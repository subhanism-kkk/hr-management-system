package az.ingress.hrms.service.impl.order;

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
import az.ingress.hrms.mapper.OrderPersonSalaryMapper;
import az.ingress.hrms.repository.OrderPersonSalaryRepository;
import az.ingress.hrms.repository.OrderRepository;
import az.ingress.hrms.repository.StaffingPlanRepository;
import az.ingress.hrms.service.order.OrderPersonSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPersonSalaryServiceImpl implements OrderPersonSalaryService {

    private final OrderPersonSalaryRepository repository;
    private final OrderRepository orderRepository;
    private final StaffingPlanRepository staffingPlanRepository;
    private final OrderPersonSalaryLogService salaryLogService;

    private final OrderPersonSalaryMapper mapper;
    private final StatusHelper statusHelper;


    @Override
    @Transactional
    public OrderPersonSalaryResponse create(
            OrderPersonSalaryCreateRequest request
    ) {

        Order order = fetchOrder(request.getOrderId());

        if (!"SAL".equals(order.getOrderType().getCode())) {
            throw new BadRequestException(
                    "Selected order is not a salary order."
            );
        }

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException(
                    "Effective date cannot be before the order date."
            );
        }

        StaffingPlan staffingPlan =
                fetchStaffingPlan(request.getStaffingPlanId());

        BigDecimal oldSalary = staffingPlan.getSalary();

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
                "admin"
        );

        return mapper.toResponse(savedEntity);
    }


    @Override
    @Transactional
    public OrderPersonSalaryResponse update(
            Integer id,
            OrderPersonSalaryUpdateRequest request
    ) {

        OrderPersonSalary entity = fetchSalary(id);

        StaffingPlan staffingPlan = entity.getStaffingPlan();

        BigDecimal currentSalary = staffingPlan.getSalary();


        if (request.getNewSalary().compareTo(currentSalary) <= 0) {
            throw new BadRequestException(
                    "New salary must be greater than current salary."
            );
        }


        Order order = entity.getOrder();

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException(
                    "Effective date cannot be before the order date."
            );
        }


        salaryLogService.log(
                entity,
                LogAction.PUT,
                "admin"
        );

        entity.setOldSalary(currentSalary);
        entity.setNewSalary(request.getNewSalary());
        entity.setEffectiveDate(request.getEffectiveDate());


        staffingPlan.setSalary(request.getNewSalary());

        staffingPlanRepository.save(staffingPlan);

        OrderPersonSalary updatedEntity = repository.save(entity);

        return mapper.toResponse(updatedEntity);
    }


    @Override
    public OrderPersonSalaryResponse getById(Integer id) {

        return mapper.toResponse(
                fetchSalary(id)
        );
    }


    @Override
    public Page<OrderPersonSalaryResponse> getAll(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("id").ascending());
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }


    @Override
    public Page<OrderPersonSalaryResponse> getByStaffingPlan(
            Integer staffingPlanId,
            int pageNo, int pageSize
    ) {

        fetchStaffingPlan(staffingPlanId);
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("id").ascending());
        return repository
                .findByStaffingPlanId(staffingPlanId, pageable)
                .map(mapper::toResponse);
    }


    @Override
    @Transactional
    public void softDelete(Integer id) {

        OrderPersonSalary entity = fetchSalary(id);

        salaryLogService.log(
                entity,
                LogAction.DELETE,
                "admin"
        );

        entity.setDeletedBy("SYSTEM");
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());

        repository.save(entity);
    }


    @Override
    @Transactional
    public void restore(Integer id) {

        OrderPersonSalary entity =
                repository.findByIdWithDeleted(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Deleted salary record not found."
                                )
                        );

        if (!Boolean.TRUE.equals(entity.getIsDeleted())) {
            throw new BadRequestException(
                    "Salary record is not deleted."
            );
        }

        salaryLogService.log(
                entity,
                LogAction.PATCH,
                "admin"
        );

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }


    @Override
    @Transactional
    public OrderPersonSalaryResponse activate(Integer id) {

        OrderPersonSalary entity = fetchSalary(id);

        salaryLogService.log(
                entity,
                LogAction.PATCH,
                "admin"
        );

        entity.setStatus(
                statusHelper.getActive()
        );

        OrderPersonSalary saved =
                repository.save(entity);

        return mapper.toResponse(saved);
    }


    @Override
    @Transactional
    public OrderPersonSalaryResponse deactivate(Integer id) {

        OrderPersonSalary entity = fetchSalary(id);

        salaryLogService.log(
                entity,
                LogAction.PATCH,
                "admin"
        );

        entity.setStatus(
                statusHelper.getInactive()
        );

        OrderPersonSalary saved =
                repository.save(entity);

        return mapper.toResponse(saved);
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


    private Order fetchOrder(Integer orderId) {

        return orderRepository.findById(orderId)
                .orElseGet(() -> {

                    orderRepository.findByIdWithDeleted(orderId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Order is deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Order not found with id: " + orderId
                    );
                });
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
                            "Staffing plan not found with id: "
                                    + staffingPlanId
                    );
                });
    }
}