package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonPromotionSearchCriteria;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionCreateRequest;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionResponse;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonPromotion;
import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.order.orderPerson.promotion.OrderPersonPromotionLogService;
import az.ingress.hrms.mapper.order.OrderPersonPromotionMapper;
import az.ingress.hrms.repository.order.OrderPersonAppointmentRepository;
import az.ingress.hrms.repository.order.OrderPersonPromotionRepository;
import az.ingress.hrms.repository.organization.PositionRepository;
import az.ingress.hrms.repository.organization.StaffingPlanRepository;
import az.ingress.hrms.repository.person.PersonRepository;
import az.ingress.hrms.service.order.OrderPersonPromotionService;
import az.ingress.hrms.specification.order.OrderPersonPromotionSpecification;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPersonPromotionServiceImpl implements OrderPersonPromotionService {

    private final OrderPersonPromotionRepository repository;
    private final PersonRepository personRepository;
    private final PositionRepository positionRepository;
    private final OrderPersonAppointmentRepository appointmentRepository;
    private final StaffingPlanRepository staffingPlanRepository;
    private final OrderPersonPromotionLogService orderPersonPromotionLogService;

    private final OrderPersonPromotionMapper mapper;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public OrderPersonPromotionResponse create(
            Order order,
            OrderPersonPromotionCreateRequest request
    ) {
        if (request.getOldPositionId().equals(request.getNewPositionId())) {
            throw new BadRequestException("New position cannot be identical to the current old position.");
        }

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Effective date cannot be before the order date.");
        }

        Person person = fetchPerson(request.getPersonId());
        Position oldPosition = fetchPosition(request.getOldPositionId());
        Position newPosition = fetchPosition(request.getNewPositionId());

        OrderPersonAppointment appointment = appointmentRepository
                .findByPersonIdAndIsClosedFalse(person.getId())
                .orElseThrow(() -> new BadRequestException("Person does not have an active appointment."));

        if (!appointment.getStaffingPlan().getPosition().getId().equals(oldPosition.getId())) {
            throw new BadRequestException("Old position does not match employee's current position.");
        }

        Integer currentStructureId = appointment.getStaffingPlan().getStructure().getId();

        StaffingPlan newStaffingPlan = staffingPlanRepository
                .findByStructureIdAndPositionId(currentStructureId, newPosition.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No staffing plan exists for the new position in the current structure."
                ));

        appointment.setStaffingPlan(newStaffingPlan);
        appointmentRepository.save(appointment);

        OrderPersonPromotion entity = mapper.toEntity(request);
        entity.setOrder(order);
        entity.setPerson(person);
        entity.setOldPosition(oldPosition);
        entity.setNewPosition(newPosition);
        entity.setStatus(statusHelper.getActive());

        OrderPersonPromotion savedEntity = repository.save(entity);

        orderPersonPromotionLogService.log(
                savedEntity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonPromotionResponse update(
            Order order,
            OrderPersonPromotionUpdateRequest request
    ) {
        OrderPersonPromotion entity = repository.findByOrderIdAndPersonId(order.getId(), request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No promotion record found for person ID " + request.getPersonId() + " under order ID " + order.getId()
                ));

        Position newPosition = fetchPosition(request.getNewPositionId());

        if (entity.getOldPosition().getId().equals(newPosition.getId())) {
            throw new BadRequestException("New position cannot be identical to the old position.");
        }

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Effective date cannot be before the order date.");
        }

        orderPersonPromotionLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(entity, request);
        entity.setNewPosition(newPosition);

        OrderPersonPromotion updatedEntity = repository.save(entity);

        return mapper.toResponse(updatedEntity);
    }

    @Override
    public PageResponse<OrderPersonPromotionResponse> getAll(
            OrderPersonPromotionSearchCriteria criteria,
            Pageable pageable
    ) {
        Specification<OrderPersonPromotion> specification = OrderPersonPromotionSpecification.build(criteria);
        Page<OrderPersonPromotion> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }


    @Override
    public List<OrderPersonPromotionResponse> getByOrderId(Integer orderId) {
        return repository.findByOrderIdAndIsDeletedFalse(orderId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void softDelete(Order order) {
        List<OrderPersonPromotion> promotions = repository.findAllByOrderId(order.getId());

        LocalDateTime now = LocalDateTime.now();
        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonPromotion entity : promotions) {
            orderPersonPromotionLogService.log(
                    entity,
                    LogAction.DELETE,
                    username
            );

            entity.setIsDeleted(true);
            entity.setDeletedAt(now);
            entity.setDeletedBy(username);
        }

        repository.saveAll(promotions);
    }

    @Override
    @Transactional
    public void restore(Order order) {
        List<OrderPersonPromotion> promotions = repository.findAllByOrderIdWithDeleted(order.getId());

        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonPromotion entity : promotions) {
            if (!Boolean.TRUE.equals(entity.getIsDeleted())) {
                continue;
            }

            orderPersonPromotionLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setIsDeleted(false);
            entity.setDeletedAt(null);
            entity.setDeletedBy(null);
        }

        repository.saveAll(promotions);
    }

    @Override
    @Transactional
    public void activate(Order order) {
        List<OrderPersonPromotion> promotions = repository.findAllByOrderId(order.getId());

        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonPromotion entity : promotions) {
            orderPersonPromotionLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setStatus(statusHelper.getActive());
        }

        repository.saveAll(promotions);
    }

    @Override
    @Transactional
    public void deactivate(Order order) {
        List<OrderPersonPromotion> promotions = repository.findAllByOrderId(order.getId());

        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonPromotion entity : promotions) {
            orderPersonPromotionLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setStatus(statusHelper.getInactive());
        }

        repository.saveAll(promotions);
    }

    private OrderPersonPromotion fetchPromotionByOrderAndPerson(Integer orderId, Integer personId) {
        return repository.findByOrderIdAndPersonId(orderId, personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Promotion record not found for person ID " + personId + " in order ID " + orderId
                ));
    }

    private Person fetchPerson(Integer personId) {
        Person person = personRepository.findById(personId)
                .orElseGet(() -> {
                    personRepository.findByIdWithDeleted(personId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Person is deleted.");
                            });
                    throw new ResourceNotFoundException("Person not found with id: " + personId);
                });

        // Enforce active status check
        if (!statusHelper.getActive().equals(person.getStatus())) {
            throw new BadRequestException("Cannot create or modify promotion order for an inactive person (ID: " + personId + ").");
        }

        return person;
    }

    private Position fetchPosition(Integer positionId) {
        return positionRepository.findById(positionId)
                .orElseGet(() -> {
                    positionRepository.findByIdWithDeleted(positionId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Position is deleted.");
                            });
                    throw new ResourceNotFoundException("Position not found with id: " + positionId);
                });
    }
}