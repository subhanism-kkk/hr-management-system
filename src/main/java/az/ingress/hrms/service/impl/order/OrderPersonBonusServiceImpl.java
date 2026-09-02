package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonBonusSearchCriteria;
import az.ingress.hrms.dto.orderPersonBonus.CreateOrderPersonBonusRequest;
import az.ingress.hrms.dto.orderPersonBonus.OrderPersonBonusResponse;
import az.ingress.hrms.dto.orderPersonBonus.UpdateOrderPersonBonusRequest;
import az.ingress.hrms.entity.lookup.BonusType;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonBonus;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.enums.BonusCalculationType;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.mapper.order.OrderPersonBonusMapper;
import az.ingress.hrms.repository.BonusTypeRepository;
import az.ingress.hrms.repository.order.OrderPersonAppointmentRepository;
import az.ingress.hrms.repository.order.OrderPersonBonusRepository;
import az.ingress.hrms.repository.person.PersonRepository;
import az.ingress.hrms.service.order.OrderPersonBonusService;
import az.ingress.hrms.specification.order.OrderPersonBonusSpecification;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPersonBonusServiceImpl implements OrderPersonBonusService {

    private final OrderPersonBonusRepository bonusRepository;
    private final PersonRepository personRepository;
    private final BonusTypeRepository bonusTypeRepository;
    private final OrderPersonAppointmentRepository appointmentRepository;

    private final OrderPersonBonusMapper mapper;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public OrderPersonBonusResponse create(Order order, CreateOrderPersonBonusRequest request) {
        validateDates(order.getOrderDate(), request.getStartDate(), request.getEndDate());

        Person person = fetchPerson(request.getPersonId());
        BonusType bonusType = fetchBonusType(request.getBonusTypeId());

        BigDecimal finalAmount = calculateFinalAmount(
                request.getPersonId(),
                request.getCalculationType(),
                request.getAmount(),
                request.getStartDate()
        );

        OrderPersonBonus entity = mapper.toEntity(request);
        entity.setOrder(order);
        entity.setPerson(person);
        entity.setBonusType(bonusType);
        entity.setAmount(finalAmount);
        entity.setStatus(statusHelper.getActive());

        OrderPersonBonus savedEntity = bonusRepository.save(entity);
        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonBonusResponse update(Order order, UpdateOrderPersonBonusRequest request) {
        OrderPersonBonus entity = fetchBonusByOrderAndPerson(order.getId(), request.getPersonId());

        validateDates(order.getOrderDate(), request.getStartDate(), request.getEndDate());

        BonusType bonusType = fetchBonusType(request.getBonusTypeId());

        BigDecimal finalAmount = calculateFinalAmount(
                request.getPersonId(),
                request.getCalculationType(),
                request.getAmount(),
                request.getStartDate()
        );

        mapper.updateEntity(entity, request);

        entity.setBonusType(bonusType);
        entity.setAmount(finalAmount);

        OrderPersonBonus updatedEntity = bonusRepository.save(entity);
        return mapper.toResponse(updatedEntity);
    }



    @Override
    public List<OrderPersonBonusResponse> getByOrderId(Integer orderId) {
        return bonusRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<OrderPersonBonusResponse> getAll(OrderPersonBonusSearchCriteria criteria, Pageable pageable) {
        Specification<OrderPersonBonus> specification = OrderPersonBonusSpecification.build(criteria);
        Page<OrderPersonBonus> page = bonusRepository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

//    @Override
//    public BigDecimal calculateTotalSalary(Integer personId, LocalDate asOfDate) {
//        OrderPersonAppointment appointment = appointmentRepository
//                .findActiveAppointment(personId.longValue(), asOfDate)
//                .orElseThrow(() -> new ResourceNotFoundException("No active appointment found for person id: " + personId));
//
//        BigDecimal baseSalary = appointment.getStaffingPlan().getSalary();
//        List<OrderPersonBonus> activeBonuses = bonusRepository.findActiveBonuses(personId.longValue(), asOfDate);
//
//        BigDecimal fixedBonus = activeBonuses.stream()
//                .filter(b -> b.getCalculationType() == BonusCalculationType.FIXED)
//                .map(OrderPersonBonus::getAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal percentageBonus = activeBonuses.stream()
//                .filter(b -> b.getCalculationType() == BonusCalculationType.PERCENTAGE)
//                .map(b -> baseSalary.multiply(b.getAmount())
//                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        return baseSalary.add(fixedBonus).add(percentageBonus);
//    }

    @Override
    @Transactional
    public void softDelete(Order order) {
        List<OrderPersonBonus> entities = bonusRepository.findAllByOrderId(order.getId());

        if (entities.isEmpty()) {
            return;
        }

        String currentUsername = SecurityUtils.getCurrentUsername();
        LocalDateTime now = LocalDateTime.now();

        for (OrderPersonBonus entity : entities) {
            entity.setDeletedBy(currentUsername);
            entity.setIsDeleted(true);
            entity.setDeletedAt(now);
        }

        bonusRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void restore(Order order) {
        List<OrderPersonBonus> entities = bonusRepository.findAllByOrderIdWithDeleted(order.getId());

        if (entities.isEmpty()) {
            return;
        }

        for (OrderPersonBonus entity : entities) {
            if (Boolean.TRUE.equals(entity.getIsDeleted())) {
                entity.setIsDeleted(false);
                entity.setDeletedAt(null);
                entity.setDeletedBy(null);
            }
        }

        bonusRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void activate(Order order) {
        List<OrderPersonBonus> entities = fetchBonusesByOrderId(order.getId());

        for (OrderPersonBonus entity : entities) {
            entity.setStatus(statusHelper.getActive());
        }

        bonusRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void deactivate(Order order) {
        List<OrderPersonBonus> entities = fetchBonusesByOrderId(order.getId());

        for (OrderPersonBonus entity : entities) {
            entity.setStatus(statusHelper.getInactive());
        }

        bonusRepository.saveAll(entities);
    }

    private OrderPersonBonus fetchBonus(Long id) {
        return bonusRepository.findById(id)
                .orElseGet(() -> {
                    bonusRepository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Bonus record is deleted.");
                            });
                    throw new ResourceNotFoundException("Bonus record not found with id: " + id);
                });
    }

    private OrderPersonBonus fetchBonusByOrderAndPerson(Integer orderId, Integer personId) {
        return bonusRepository.findByOrderIdAndPersonId(orderId, personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bonus record not found for person ID " + personId + " under order ID " + orderId
                ));
    }

    private List<OrderPersonBonus> fetchBonusesByOrderId(Integer orderId) {
        List<OrderPersonBonus> entities = bonusRepository.findAllByOrderId(orderId);

        if (entities.isEmpty()) {
            List<OrderPersonBonus> deletedEntities = bonusRepository.findAllByOrderIdWithDeleted(orderId);
            if (!deletedEntities.isEmpty()) {
                throw new DeletedResourceException("Bonus records for order ID " + orderId + " are deleted.");
            }
            throw new ResourceNotFoundException("No bonus records found for order ID: " + orderId);
        }

        return entities;
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
            throw new BadRequestException("Cannot create or modify bonus order for an inactive person (ID: " + personId + ").");
        }

        return person;
    }

    private BonusType fetchBonusType(Integer bonusTypeId) {
        return bonusTypeRepository.findById(bonusTypeId)
                .orElseGet(() -> {
                    bonusTypeRepository.findByIdWithDeleted(bonusTypeId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Bonus type is deleted.");
                            });
                    throw new ResourceNotFoundException("Bonus type not found with id: " + bonusTypeId);
                });
    }


    private void validateDates(LocalDate orderDate, LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(orderDate)) {
            throw new BadRequestException("Bonus start date cannot be before the order date.");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException("Bonus end date cannot be before start date.");
        }
    }

    private BigDecimal calculateFinalAmount(
            Integer personId,
            BonusCalculationType calculationType,
            BigDecimal amount,
            LocalDate startDate
    ) {
        if (calculationType != BonusCalculationType.PERCENTAGE) {
            return amount;
        }

        OrderPersonAppointment appointment = appointmentRepository
                .findActiveAppointment(personId.longValue(), startDate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active appointment found to calculate percentage bonus for person ID: " + personId
                ));

        BigDecimal baseSalary = appointment.getStaffingPlan().getSalary();

        return baseSalary.multiply(amount)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}