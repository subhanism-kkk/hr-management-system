package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonLeaveSearchCriteria;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveCreateRequest;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveResponse;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveUpdateRequest;
import az.ingress.hrms.entity.lookup.LeaveType;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonLeave;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.order.orderPerson.leave.OrderPersonLeaveLogService;
import az.ingress.hrms.mapper.order.OrderPersonLeaveMapper;
import az.ingress.hrms.repository.LeaveTypeRepository;
import az.ingress.hrms.repository.order.OrderPersonAppointmentRepository;
import az.ingress.hrms.repository.order.OrderPersonLeaveRepository;
import az.ingress.hrms.repository.person.PersonRepository;
import az.ingress.hrms.service.order.OrderPersonLeaveService;
import az.ingress.hrms.specification.order.OrderPersonLeaveSpecification;
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
public class OrderPersonLeaveServiceImpl implements OrderPersonLeaveService {

    private final OrderPersonLeaveRepository repository;
    private final PersonRepository personRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final OrderPersonAppointmentRepository appointmentRepository;
    private final OrderPersonLeaveLogService leaveLogService;

    private final OrderPersonLeaveMapper mapper;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public OrderPersonLeaveResponse create(
            Order order,
            OrderPersonLeaveCreateRequest request
    ) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Leave end date cannot be before start date.");
        }

        if (request.getStartDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Leave start date cannot be before the order date.");
        }

        Person person = fetchPerson(request.getPersonId());
        LeaveType leaveType = fetchLeaveType(request.getLeaveTypeId());

        if (!"ACTIVE".equals(leaveType.getStatus().getCode())) {
            throw new BadRequestException("Leave type is inactive.");
        }

        OrderPersonAppointment appointment = appointmentRepository
                .findByPersonIdAndIsClosedFalse(person.getId())
                .orElseThrow(() -> new BadRequestException("Person does not have an active appointment."));

        if (request.getStartDate().isBefore(appointment.getStartDate())) {
            throw new BadRequestException("Leave start date cannot be before the appointment start date.");
        }

        if (repository.existsOverlappingLeave(person.getId(), request.getStartDate(), request.getEndDate())) {
            throw new BadRequestException("Person already has an active leave during this period.");
        }

        OrderPersonLeave entity = mapper.toEntity(request);
        entity.setOrder(order);
        entity.setPerson(person);
        entity.setLeaveType(leaveType);
        entity.setStatus(statusHelper.getActive());

        OrderPersonLeave savedEntity = repository.save(entity);

        leaveLogService.log(
                savedEntity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonLeaveResponse update(
            Order order,
            OrderPersonLeaveUpdateRequest request
    ) {
        OrderPersonLeave entity = fetchLeaveByOrderAndPerson(order.getId(), request.getPersonId());

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Leave end date cannot be before start date.");
        }

        if (request.getStartDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Leave start date cannot be before the order date.");
        }

        OrderPersonAppointment appointment = appointmentRepository
                .findByPersonIdAndIsClosedFalse(entity.getPerson().getId())
                .orElseThrow(() -> new BadRequestException("Person does not have an active appointment."));

        if (request.getStartDate().isBefore(appointment.getStartDate())) {
            throw new BadRequestException("Leave start date cannot be before the appointment start date.");
        }

        if (repository.existsOverlappingLeaveExcludingId(
                entity.getPerson().getId(),
                request.getStartDate(),
                request.getEndDate(),
                entity.getId())) {
            throw new BadRequestException("Updated leave period overlaps with another existing active leave.");
        }

        LeaveType newLeaveType = fetchLeaveType(request.getLeaveTypeId());

        leaveLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(entity, request);
        entity.setLeaveType(newLeaveType);

        OrderPersonLeave updatedEntity = repository.save(entity);
        return mapper.toResponse(updatedEntity);
    }

    @Override
    public List<OrderPersonLeaveResponse> getByOrderId(Integer orderId) {
        return repository.findByOrderIdAndIsDeletedFalse(orderId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<OrderPersonLeaveResponse> getAll(
            OrderPersonLeaveSearchCriteria criteria,
            Pageable pageable
    ) {
        Specification<OrderPersonLeave> specification = OrderPersonLeaveSpecification.build(criteria);
        Page<OrderPersonLeave> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }



    @Override
    @Transactional
    public void softDelete(Order order) {
        List<OrderPersonLeave> leaves = repository.findAllByOrderId(order.getId());

        LocalDateTime now = LocalDateTime.now();
        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonLeave entity : leaves) {
            leaveLogService.log(
                    entity,
                    LogAction.DELETE,
                    username
            );

            entity.setIsDeleted(true);
            entity.setDeletedAt(now);
            entity.setDeletedBy(username);
        }

        repository.saveAll(leaves);
    }

    @Override
    @Transactional
    public void restore(Order order) {
        List<OrderPersonLeave> leaves = repository.findAllByOrderIdWithDeleted(order.getId());

        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonLeave entity : leaves) {
            if (!Boolean.TRUE.equals(entity.getIsDeleted())) {
                continue;
            }

            leaveLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setIsDeleted(false);
            entity.setDeletedAt(null);
            entity.setDeletedBy(null);
        }

        repository.saveAll(leaves);
    }

    @Override
    @Transactional
    public void activate(Order order) {
        List<OrderPersonLeave> leaves = repository.findAllByOrderId(order.getId());

        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonLeave entity : leaves) {
            leaveLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setStatus(statusHelper.getActive());
        }

        repository.saveAll(leaves);
    }

    @Override
    @Transactional
    public void deactivate(Order order) {
        List<OrderPersonLeave> leaves = repository.findAllByOrderId(order.getId());

        String username = SecurityUtils.getCurrentUsername();

        for (OrderPersonLeave entity : leaves) {
            leaveLogService.log(
                    entity,
                    LogAction.PATCH,
                    username
            );

            entity.setStatus(statusHelper.getInactive());
        }

        repository.saveAll(leaves);
    }

    private OrderPersonLeave fetchLeaveByOrderAndPerson(Integer orderId, Integer personId) {
        return repository.findByOrderIdAndPersonId(orderId, personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave record not found for person ID " + personId + " in order ID " + orderId
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
            throw new BadRequestException("Cannot create or modify leave order for an inactive person (ID: " + personId + ").");
        }

        return person;
    }

    private LeaveType fetchLeaveType(Integer leaveTypeId) {
        return leaveTypeRepository.findById(leaveTypeId)
                .orElseGet(() -> {
                    leaveTypeRepository.findByIdWithDeleted(leaveTypeId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Leave type is deleted.");
                            });
                    throw new ResourceNotFoundException("Leave type not found with id: " + leaveTypeId);
                });
    }
}