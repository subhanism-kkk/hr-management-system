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
import az.ingress.hrms.mapper.order.OrderPersonAppointmentMapper;
import az.ingress.hrms.repository.order.OrderPersonAppointmentRepository;
import az.ingress.hrms.repository.organization.StaffingPlanRepository;
import az.ingress.hrms.repository.person.PersonRepository;
import az.ingress.hrms.service.order.OrderPersonAppointmentService;
import az.ingress.hrms.specification.order.OrderPersonAppointmentSpecification;
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

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderPersonAppointmentServiceImpl
        implements OrderPersonAppointmentService {

    private final OrderPersonAppointmentRepository repository;
    private final PersonRepository personRepository;
    private final StaffingPlanRepository staffingPlanRepository;

    private final OrderPersonAppointmentLogService appointmentLogService;
    private final OrderPersonAppointmentMapper mapper;
    private final StatusHelper statusHelper;


    @Override
    @Transactional
    public OrderPersonAppointmentResponse create(
            Order order,
            OrderPersonAppointmentCreateRequest request
    ) {

        Person person = fetchPerson(request.getPersonId());

        StaffingPlan staffingPlan =
                fetchStaffingPlan(request.getStaffingPlanId());

        if (repository.existsByPersonIdAndIsClosedFalse(
                person.getId())) {

            throw new DuplicateResourceException(
                    "Person with ID "
                            + person.getId()
                            + " already has an active appointment."
            );
        }

        long activeAppointmentsCount =
                repository.countByStaffingPlanIdAndIsClosedFalse(
                        staffingPlan.getId()
                );

        if (activeAppointmentsCount >= staffingPlan.getCapacity()) {

            throw new BadRequestException(
                    "Staffing plan capacity limit reached ("
                            + staffingPlan.getCapacity()
                            + ")."
            );
        }

        OrderPersonAppointment entity =
                mapper.toEntity(request);

        entity.setPerson(person);
        entity.setOrder(order);
        entity.setStaffingPlan(staffingPlan);
        entity.setIsClosed(false);
        entity.setStatus(statusHelper.getActive());

        OrderPersonAppointment savedEntity =
                repository.save(entity);

        appointmentLogService.log(
                savedEntity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(savedEntity);
    }




    @Override
    @Transactional
    public OrderPersonAppointmentResponse update(
            Order order,
            OrderPersonAppointmentUpdateRequest request
    ) {

        OrderPersonAppointment entity =
                fetchAppointment(order.getId(), request.getPersonId());

        StaffingPlan staffingPlan =
                fetchStaffingPlan(request.getStaffingPlanId());

        if (!entity.getStaffingPlan().getId()
                .equals(staffingPlan.getId())) {

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

        OrderPersonAppointment saved =
                repository.save(entity);

        return mapper.toResponse(saved);
    }



    @Override
    @Transactional
    public void activate(Order order) {

        List<OrderPersonAppointment> appointments =
                repository.findAllByOrderId(order.getId());

        for (OrderPersonAppointment entity : appointments) {

            appointmentLogService.log(
                    entity,
                    LogAction.PATCH,
                    SecurityUtils.getCurrentUsername()
            );

            entity.setStatus(
                    statusHelper.getActive()
            );
        }

        repository.saveAll(appointments);
    }

    @Override
    @Transactional
    public void deactivate(Order order) {

        List<OrderPersonAppointment> appointments =
                repository.findAllByOrderId(order.getId());

        for (OrderPersonAppointment entity : appointments) {

            appointmentLogService.log(
                    entity,
                    LogAction.PATCH,
                    SecurityUtils.getCurrentUsername()
            );

            entity.setStatus(
                    statusHelper.getInactive()
            );
        }

        repository.saveAll(appointments);
    }


    @Override
    @Transactional
    public void softDelete(Order order) {

        List<OrderPersonAppointment> appointments =
                repository.findAllByOrderId(order.getId());

        LocalDateTime now = LocalDateTime.now();
        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonAppointment entity : appointments) {

            appointmentLogService.log(
                    entity,
                    LogAction.DELETE,
                    username
            );

            entity.setIsDeleted(true);
            entity.setDeletedAt(now);
            entity.setDeletedBy(username);
        }

        repository.saveAll(appointments);
    }



    @Override
    @Transactional
    public void restore(Order order) {

        List<OrderPersonAppointment> appointments =
                repository.findAllByOrderIdWithDeleted(
                        order.getId()
                );

        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonAppointment entity : appointments) {

            if (!Boolean.TRUE.equals(entity.getIsDeleted())) {
                continue;
            }

            appointmentLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setIsDeleted(false);
            entity.setDeletedAt(null);
            entity.setDeletedBy(null);
        }

        repository.saveAll(appointments);
    }



    @Override
    public PageResponse<OrderPersonAppointmentResponse> getAll(
            OrderPersonAppointmentSearchCriteria criteria,
            Pageable pageable
    ) {

        Specification<OrderPersonAppointment> specification =
                OrderPersonAppointmentSpecification.build(criteria);

        Page<OrderPersonAppointment> page =
                repository.findAll(
                        specification,
                        pageable
                );

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

    @Override
    public List<OrderPersonAppointmentResponse> getByOrderId(Integer orderId) {
        return repository.findByOrderIdAndIsDeletedFalse(orderId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private OrderPersonAppointment fetchAppointment(
            Integer id
    ) {

        return repository.findById(id)
                .orElseGet(() -> {

                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Appointment is deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Appointment not found."
                    );
                });
    }



    private Person fetchPerson(Integer personId) {

        return personRepository.findById(personId)
                .orElseGet(() -> {

                    personRepository.findByIdWithDeleted(personId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Person is deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Person not found."
                    );
                });
    }


    private StaffingPlan fetchStaffingPlan(
            Integer staffingPlanId
    ) {

        return staffingPlanRepository
                .findById(staffingPlanId)
                .orElseGet(() -> {

                    staffingPlanRepository
                            .findByIdWithDeleted(staffingPlanId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "StaffingPlan is deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "StaffingPlan not found."
                    );
                });
    }

    private OrderPersonAppointment fetchAppointment(
            Integer orderId,
            Integer personId
    ) {
        return repository.findByOrderIdAndPersonId(orderId, personId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found for person ID "
                                        + personId
                                        + " in order ID "
                                        + orderId
                        )
                );
    }
}