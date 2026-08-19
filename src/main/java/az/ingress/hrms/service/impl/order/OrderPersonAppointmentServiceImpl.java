package az.ingress.hrms.service.impl.order;


import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonAppointmentSearchCriteria;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentCreateRequest;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentResponse;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.order.orderPerson.appointment.OrderPersonAppointmentLogService;
import az.ingress.hrms.mapper.OrderPersonAppointmentMapper;
import az.ingress.hrms.repository.OrderPersonAppointmentRepository;
import az.ingress.hrms.repository.OrderRepository;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.repository.StaffingPlanRepository;
import az.ingress.hrms.service.order.OrderPersonAppointmentService;
import az.ingress.hrms.specification.OrderPersonAppointmentSpecification;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderPersonAppointmentServiceImpl implements OrderPersonAppointmentService {

    private final OrderPersonAppointmentRepository repository;
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final StaffingPlanRepository staffingPlanRepository;
    private final OrderPersonAppointmentLogService appointmentLogService;

    private final OrderPersonAppointmentMapper mapper;
    private final StatusHelper statusHelper;


    @Override
    @Transactional
    public OrderPersonAppointmentResponse create(OrderPersonAppointmentCreateRequest request) {
        Person person = fetchPerson(request.getPersonId());
        Order order = fetchOrder(request.getOrderId());

        if (!order.getOrderType().getCode().equals("APT")) {
            throw new BadRequestException(
                    "Selected order is not a Appointment order."
            );
        }

        StaffingPlan staffingPlan = fetchStaffingPlan(request.getStaffingPlanId());

        if (repository.existsByPersonIdAndIsClosedFalse(person.getId())) {
            throw new DuplicateResourceException(
                    "Person with ID " + person.getId() + " already has an active appointment."
            );
        }

        long activeAppointmentsCount = repository.countByStaffingPlanIdAndIsClosedFalse(staffingPlan.getId());
        if (activeAppointmentsCount >= staffingPlan.getCapacity()) {
            throw new BadRequestException(
                    "Staffing plan capacity limit reached (" + staffingPlan.getCapacity() + ")."
            );
        }


        OrderPersonAppointment entity = mapper.toEntity(request);
        entity.setPerson(person);
        entity.setOrder(order);
        entity.setStaffingPlan(staffingPlan);
        entity.setIsClosed(false);
        entity.setStatus(statusHelper.getActive());

        OrderPersonAppointment savedEntity = repository.save(entity);

        appointmentLogService.log(
                savedEntity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonAppointmentResponse update(Integer id, OrderPersonAppointmentUpdateRequest request) {
        OrderPersonAppointment entity = fetchAppointment(id);

        StaffingPlan staffingPlan =
                fetchStaffingPlan(request.getStaffingPlanId());

        if (!entity.getStaffingPlan().getId().equals(staffingPlan.getId())) {

            long activeAppointments =
                    repository.countByStaffingPlanIdAndIsClosedFalse(
                            staffingPlan.getId()
                    );

            if (activeAppointments >= staffingPlan.getCapacity()) {
                throw new BadRequestException(
                        "Target staffing plan capacity reached."
                );
            }

            entity.setStaffingPlan(staffingPlan);
        }

        appointmentLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(entity, request);

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    public OrderPersonAppointmentResponse getById(Integer id) {
        return mapper.toResponse(fetchAppointment(id));
    }

    @Override
    public PageResponse<OrderPersonAppointmentResponse> getAll(OrderPersonAppointmentSearchCriteria criteria, Pageable pageable) {
        Specification<OrderPersonAppointment> specification = OrderPersonAppointmentSpecification.build(criteria);
        Page<OrderPersonAppointment> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    public void dismiss(Integer appointmentId, Integer dismissalOrderId, LocalDate dismissalDate) {
        OrderPersonAppointment appointment = fetchAppointment(appointmentId);


        if (Boolean.TRUE.equals(appointment.getIsClosed())) {
            throw new BadRequestException("Appointment with ID " + appointmentId + " is already closed/dismissed.");
        }

        if (dismissalDate.isBefore(appointment.getStartDate())) {
            throw new BadRequestException("Dismissal date cannot be before the appointment start date.");
        }

        Order dismissalOrder = fetchOrder(dismissalOrderId);

        if (!dismissalOrder.getOrderType()
                .getCode()
                .equals("DIS")) {

            throw new BadRequestException(
                    "Order is not a dismissal order."
            );
        }

        appointmentLogService.log(
                appointment,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        appointment.setDismissalOrder(dismissalOrder);
        appointment.setEndDate(dismissalDate);
        appointment.setIsClosed(true);
        appointment.setStatus(statusHelper.getInactive());

        repository.save(appointment);


    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        OrderPersonAppointment entity = fetchAppointment(id);

        appointmentLogService.log(
                entity,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername());

        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setDeletedBy(SecurityUtils.getCurrentUsername());

        repository.save(entity);

    }

    @Override
    @Transactional
    public void restore(Integer id) {
        OrderPersonAppointment entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deleted appointment not found with id: " + id));

        if (!entity.getIsDeleted()) {
            throw new IllegalStateException("Appointment is not deleted.");
        }

        appointmentLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);

    }

    @Override
    @Transactional
    public OrderPersonAppointmentResponse activate(Integer id) {
        OrderPersonAppointment entity = fetchAppointment(id);

        appointmentLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        entity.setStatus(statusHelper.getActive());

        OrderPersonAppointment saved = repository.save(entity);


        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrderPersonAppointmentResponse deactivate(Integer id) {
        OrderPersonAppointment entity = fetchAppointment(id);

        appointmentLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        entity.setStatus(statusHelper.getInactive());

        OrderPersonAppointment saved = repository.save(entity);


        return mapper.toResponse(saved);
    }

    private OrderPersonAppointment fetchAppointment(Integer id) {

        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {

                                throw new DeletedResourceException(
                                        "Appointment is deleted.");
                            });

                    throw new ResourceNotFoundException(
                            "Appointment not found.");
                });
    }

    private Person fetchPerson(Integer personId) {
        return personRepository.findById(personId)
                .orElseGet(() -> {
                    personRepository.findByIdWithDeleted(personId)
                            .ifPresent(e -> {

                                throw new DeletedResourceException(
                                        "person is deleted.");
                            });

                    throw new ResourceNotFoundException(
                            "person not found.");
                });
    }

    private Order fetchOrder(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseGet(() -> {
                    orderRepository.findByIdWithDeleted(orderId)
                            .ifPresent(e -> {

                                throw new DeletedResourceException(
                                        "order is deleted.");
                            });

                    throw new ResourceNotFoundException(
                            "order not found.");
                });
    }

    private StaffingPlan fetchStaffingPlan(Integer staffingPlanId) {
        return staffingPlanRepository.findById(staffingPlanId)
                .orElseGet(() -> {
                    staffingPlanRepository.findByIdWithDeleted(staffingPlanId)
                            .ifPresent(e -> {

                                throw new DeletedResourceException(
                                        "StaffingPlan is deleted.");
                            });

                    throw new ResourceNotFoundException(
                            "StaffingPlan not found.");
                });
    }

}
