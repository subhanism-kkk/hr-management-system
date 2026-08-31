package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonDismissalSearchCriteria;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalCreateRequest;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalResponse;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonDismissal;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.order.orderPerson.dismissal.OrderPersonDismissalLogService;
import az.ingress.hrms.mapper.order.OrderPersonDismissalMapper;
import az.ingress.hrms.repository.order.OrderPersonAppointmentRepository;
import az.ingress.hrms.repository.order.OrderPersonDismissalRepository;
import az.ingress.hrms.repository.person.PersonRepository;
import az.ingress.hrms.service.order.OrderPersonDismissalService;
import az.ingress.hrms.specification.order.OrderPersonDismissalSpecification;
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
public class OrderPersonDismissalServiceImpl implements OrderPersonDismissalService {

    private final OrderPersonDismissalRepository repository;
    private final PersonRepository personRepository;
    private final OrderPersonAppointmentRepository appointmentRepository;
    private final OrderPersonDismissalLogService dismissalLogService;

    private final OrderPersonDismissalMapper mapper;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public OrderPersonDismissalResponse create(
            Order order,
            OrderPersonDismissalCreateRequest request
    ) {

        if (request.getDismissalDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Dismissal date cannot be before the order date.");
        }

        Person person = fetchPerson(request.getPersonId());

        OrderPersonAppointment appointment = appointmentRepository
                .findByPersonIdAndIsClosedFalse(person.getId())
                .orElseThrow(() -> new BadRequestException("Person with ID " + person.getId() + " does not have an active appointment to dismiss."));

        if (request.getDismissalDate().isBefore(appointment.getStartDate())) {
            throw new BadRequestException("Dismissal date cannot be before the appointment start date.");
        }

        OrderPersonDismissal entity = mapper.toEntity(request);
        entity.setOrder(order);
        entity.setPerson(person);
        entity.setStatus(statusHelper.getActive());

        OrderPersonDismissal savedEntity = repository.save(entity);

        // Update appointment status to closed/inactive
        appointment.setDismissalOrder(order);
        appointment.setEndDate(request.getDismissalDate());
        appointment.setIsClosed(true);
        appointment.setStatus(statusHelper.getInactive());

        appointmentRepository.save(appointment);

        dismissalLogService.log(
                savedEntity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonDismissalResponse update(
            Order order,
            OrderPersonDismissalUpdateRequest request
    ) {

        OrderPersonDismissal entity = fetchDismissalByOrderAndPerson(order.getId(), request.getPersonId());

        if (request.getDismissalDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Dismissal date cannot be before the order date.");
        }

        OrderPersonAppointment appointment = appointmentRepository
                .findByPersonIdAndIsClosedFalse(entity.getPerson().getId())
                .orElseGet(() -> appointmentRepository.findByDismissalOrderIdAndPersonId(order.getId(), entity.getPerson().getId())
                        .orElseThrow(() -> new BadRequestException("Person does not have an associated appointment.")));

        if (request.getDismissalDate().isBefore(appointment.getStartDate())) {
            throw new BadRequestException("Dismissal date cannot be before the appointment start date.");
        }

        dismissalLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(entity, request);

        appointment.setDismissalOrder(order);
        appointment.setEndDate(request.getDismissalDate());

        appointmentRepository.save(appointment);

        OrderPersonDismissal updatedEntity = repository.save(entity);

        return mapper.toResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void activate(Order order) {

        List<OrderPersonDismissal> dismissals = repository.findAllByOrderId(order.getId());

        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonDismissal entity : dismissals) {

            dismissalLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setStatus(statusHelper.getActive());
        }

        repository.saveAll(dismissals);
    }

    @Override
    @Transactional
    public void deactivate(Order order) {

        List<OrderPersonDismissal> dismissals = repository.findAllByOrderId(order.getId());

        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonDismissal entity : dismissals) {

            dismissalLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setStatus(statusHelper.getInactive());
        }

        repository.saveAll(dismissals);
    }

    @Override
    @Transactional
    public void softDelete(Order order) {

        List<OrderPersonDismissal> dismissals = repository.findAllByOrderId(order.getId());

        LocalDateTime now = LocalDateTime.now();
        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonDismissal entity : dismissals) {

            dismissalLogService.log(
                    entity,
                    LogAction.DELETE,
                    username
            );

            entity.setIsDeleted(true);
            entity.setDeletedAt(now);
            entity.setDeletedBy(username);
        }

        repository.saveAll(dismissals);
    }

    @Override
    @Transactional
    public void restore(Order order) {

        List<OrderPersonDismissal> dismissals = repository.findAllByOrderIdWithDeleted(order.getId());

        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonDismissal entity : dismissals) {

            if (!Boolean.TRUE.equals(entity.getIsDeleted())) {
                continue;
            }

            dismissalLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setIsDeleted(false);
            entity.setDeletedAt(null);
            entity.setDeletedBy(null);
        }

        repository.saveAll(dismissals);
    }

    @Override
    public PageResponse<OrderPersonDismissalResponse> getAll(
            OrderPersonDismissalSearchCriteria criteria,
            Pageable pageable
    ) {

        Specification<OrderPersonDismissal> specification = OrderPersonDismissalSpecification.build(criteria);
        Page<OrderPersonDismissal> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

    @Override
    public List<OrderPersonDismissalResponse> getByOrderId(Integer orderId) {
        return repository.findByOrderIdAndIsDeletedFalse(orderId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private Person fetchPerson(Integer personId) {

        return personRepository.findById(personId)
                .orElseGet(() -> {
                    personRepository.findByIdWithDeleted(personId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Person is deleted.");
                            });
                    throw new ResourceNotFoundException("Person not found with id: " + personId);
                });
    }

    private OrderPersonDismissal fetchDismissalByOrderAndPerson(Integer orderId, Integer personId) {

        return repository.findByOrderIdAndPersonId(orderId, personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dismissal record not found for person ID " + personId + " in order ID " + orderId
                ));
    }
}