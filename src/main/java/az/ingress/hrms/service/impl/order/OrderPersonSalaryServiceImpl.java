package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonSalarySearchCriteria;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryCreateRequest;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryResponse;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonSalary;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.order.orderPerson.salary.OrderPersonSalaryLogService;
import az.ingress.hrms.mapper.order.OrderPersonSalaryMapper;
import az.ingress.hrms.repository.order.OrderPersonSalaryRepository;
import az.ingress.hrms.repository.organization.StaffingPlanRepository;
import az.ingress.hrms.service.order.OrderPersonSalaryService;
import az.ingress.hrms.specification.order.OrderPersonSalarySpecification;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPersonSalaryServiceImpl implements OrderPersonSalaryService {

    private final OrderPersonSalaryRepository repository;
    private final StaffingPlanRepository staffingPlanRepository;
    private final OrderPersonSalaryLogService salaryLogService;

    private final OrderPersonSalaryMapper mapper;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public OrderPersonSalaryResponse create(
            Order order,
            OrderPersonSalaryCreateRequest request
    ) {

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException(
                    "Effective date cannot be before the order date."
            );
        }

        StaffingPlan staffingPlan = fetchStaffingPlan(request.getStaffingPlanId());

        BigDecimal oldSalary = staffingPlan.getSalary();

        //
        if (request.getNewSalary().compareTo(oldSalary) <= 0) {
            throw new BadRequestException(
                    "New salary must be greater than current salary."
            );
        }

        OrderPersonSalary entity = mapper.toEntity(request);

        entity.setOrder(order);
        entity.setStaffingPlan(staffingPlan);

        entity.setOldSalary(oldSalary);
        entity.setNewSalary(request.getNewSalary());

        entity.setStatus(statusHelper.getActive());

        staffingPlan.setSalary(request.getNewSalary());
        staffingPlanRepository.save(staffingPlan);

        OrderPersonSalary savedEntity = repository.save(entity);

        salaryLogService.log(
                savedEntity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonSalaryResponse update(
            Order order,
            OrderPersonSalaryUpdateRequest request
    ) {

        OrderPersonSalary entity = fetchSalaryByOrderAndStaffingPlan(order.getId(), request.getStaffingPlanId());

        StaffingPlan staffingPlan = entity.getStaffingPlan();
        BigDecimal currentSalary = staffingPlan.getSalary();

        if (request.getNewSalary().compareTo(currentSalary) <= 0) {
            throw new BadRequestException(
                    "New salary must be greater than current salary."
            );
        }

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException(
                    "Effective date cannot be before the order date."
            );
        }

        salaryLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(entity, request);

        entity.setOldSalary(currentSalary);
        entity.setNewSalary(request.getNewSalary());
        entity.setEffectiveDate(request.getEffectiveDate());

        staffingPlan.setSalary(request.getNewSalary());
        staffingPlanRepository.save(staffingPlan);

        OrderPersonSalary updatedEntity = repository.save(entity);

        return mapper.toResponse(updatedEntity);
    }

    @Override
    public List<OrderPersonSalaryResponse> getByOrderId(Integer orderId) {
        return repository.findByOrderIdAndIsDeletedFalse(orderId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<OrderPersonSalaryResponse> getAll(OrderPersonSalarySearchCriteria criteria, Pageable pageable) {
        Specification<OrderPersonSalary> specification = OrderPersonSalarySpecification.build(criteria);
        Page<OrderPersonSalary> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    public void softDelete(Order order) {

        List<OrderPersonSalary> entities = repository.findAllByOrderId(order.getId());

        if (entities.isEmpty()) {
            return;
        }

        String currentUsername = SecurityUtils.getCurrentUsername();
        LocalDateTime now = LocalDateTime.now();

        for (OrderPersonSalary entity : entities) {
            salaryLogService.log(
                    entity,
                    LogAction.DELETE,
                    currentUsername
            );

            entity.setDeletedBy(currentUsername);
            entity.setIsDeleted(true);
            entity.setDeletedAt(now);
        }

        repository.saveAll(entities);
    }

    @Override
    @Transactional
    public void restore(Order order) {

        List<OrderPersonSalary> entities = repository.findAllByOrderIdWithDeleted(order.getId());

        if (entities.isEmpty()) {
            return;
        }

        String currentUsername = SecurityUtils.getCurrentUsername();

        for (OrderPersonSalary entity : entities) {
            if (Boolean.TRUE.equals(entity.getIsDeleted())) {
                salaryLogService.log(
                        entity,
                        LogAction.PATCH,
                        currentUsername
                );

                entity.setIsDeleted(false);
                entity.setDeletedAt(null);
                entity.setDeletedBy(null);
            }
        }

        repository.saveAll(entities);
    }

    @Override
    @Transactional
    public void activate(Order order) {

        List<OrderPersonSalary> entities = fetchSalariesByOrderId(order.getId());

        String currentUsername = SecurityUtils.getCurrentUsername();

        for (OrderPersonSalary entity : entities) {
            salaryLogService.log(
                    entity,
                    LogAction.PATCH,
                    currentUsername
            );

            entity.setStatus(statusHelper.getActive());
        }

        repository.saveAll(entities);
    }

    @Override
    @Transactional
    public void deactivate(Order order) {

        List<OrderPersonSalary> entities = fetchSalariesByOrderId(order.getId());

        String currentUsername = SecurityUtils.getCurrentUsername();

        for (OrderPersonSalary entity : entities) {
            salaryLogService.log(
                    entity,
                    LogAction.PATCH,
                    currentUsername
            );

            entity.setStatus(statusHelper.getInactive());
        }

        repository.saveAll(entities);
    }

    private OrderPersonSalary fetchSalary(Integer id) {

        return repository.findById(id)
                .orElseGet(() -> {

                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Salary record is deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Salary record not found."
                    );
                });
    }

    private OrderPersonSalary fetchSalaryByOrderAndStaffingPlan(Integer orderId, Integer staffingPlanId) {
        return repository.findByOrderIdAndStaffingPlanId(orderId, staffingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salary record not found for staffing plan ID " + staffingPlanId + " under order ID " + orderId
                ));
    }

    private StaffingPlan fetchStaffingPlan(Integer staffingPlanId) {

        return staffingPlanRepository.findById(staffingPlanId)
                .orElseGet(() -> {

                    staffingPlanRepository.findByIdWithDeleted(staffingPlanId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Staffing plan is deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Staffing plan not found with id: " + staffingPlanId
                    );
                });
    }

    private List<OrderPersonSalary> fetchSalariesByOrderId(Integer orderId) {

        List<OrderPersonSalary> entities = repository.findAllByOrderId(orderId);

        if (entities.isEmpty()) {
            List<OrderPersonSalary> deletedEntities = repository.findAllByOrderIdWithDeleted(orderId);
            if (!deletedEntities.isEmpty()) {
                throw new DeletedResourceException(
                        "Salary records for order ID " + orderId + " are deleted."
                );
            }

            throw new ResourceNotFoundException(
                    "No salary records found for order ID: " + orderId
            );
        }

        return entities;
    }
}