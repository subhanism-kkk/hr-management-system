package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.common.PageResponse;
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
import az.ingress.hrms.mapper.OrderPersonLeaveMapper;
import az.ingress.hrms.repository.*;
import az.ingress.hrms.service.order.OrderPersonLeaveService;
import az.ingress.hrms.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPersonLeaveServiceImpl implements OrderPersonLeaveService {

    private final OrderPersonLeaveRepository repository;
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final OrderPersonAppointmentRepository appointmentRepository;
    private final OrderPersonLeaveLogService leaveLogService;

    private final OrderPersonLeaveMapper mapper;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public OrderPersonLeaveResponse create(OrderPersonLeaveCreateRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Leave end date cannot be before start date.");
        }

        Order order = fetchOrder(request.getOrderId());

        if (!"LEV".equalsIgnoreCase(order.getOrderType().getCode())) {
            throw new BadRequestException("Selected order is not a leave order.");
        }

        if (request.getStartDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Leave start date cannot be before the order date.");
        }

        Person person = fetchPerson(request.getPersonId());
        LeaveType leaveType = fetchLeaveType(request.getLeaveTypeId());

        if (!leaveType.getStatus().getCode().equals("ACTIVE")) {
            throw new BadRequestException(
                    "Leave type is inactive."
            );
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
                "admin"
        );

        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonLeaveResponse update(
            Integer id,
            OrderPersonLeaveUpdateRequest request
    ) {
        OrderPersonLeave entity = fetchLeave(id);

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Leave end date cannot be before start date.");
        }

        Order order = entity.getOrder();

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
                "admin"
        );

        mapper.updateEntity(entity, request);
        entity.setLeaveType(newLeaveType);

        OrderPersonLeave updatedEntity = repository.save(entity);
        return mapper.toResponse(updatedEntity);
    }

    @Override
    public OrderPersonLeaveResponse getById(Integer id) {
        return mapper.toResponse(fetchLeave(id));
    }

    @Override
    public PageResponse<OrderPersonLeaveResponse> getAll(int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<OrderPersonLeave> page =
                repository.findAll(pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

    @Override
    public PageResponse<OrderPersonLeaveResponse> getByPerson(Integer personId, int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<OrderPersonLeave> page =
                repository.findByPerson(fetchPerson(personId), pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        OrderPersonLeave entity = fetchLeave(id);

        leaveLogService.log(
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
        OrderPersonLeave entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deleted leave record not found with id: " + id));

        if (!entity.getIsDeleted()) {
            throw new BadRequestException("Leave record is not deleted.");
        }

        leaveLogService.log(
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
    public OrderPersonLeaveResponse activate(Integer id) {
        OrderPersonLeave entity = fetchLeave(id);

        if (entity.getEndDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot activate an expired leave record.");
        }

        leaveLogService.log(
                entity,
                LogAction.PATCH,
                "admin"
        );
        entity.setStatus(statusHelper.getActive());
        OrderPersonLeave saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrderPersonLeaveResponse deactivate(Integer id) {
        OrderPersonLeave entity = fetchLeave(id);

        leaveLogService.log(
                entity,
                LogAction.PATCH,
                "admin"
        );

        entity.setStatus(statusHelper.getInactive());

        OrderPersonLeave saved = repository.save(entity);
        return mapper.toResponse(saved);
    }


    private OrderPersonLeave fetchLeave(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Leave record is deleted.");
                            });
                    throw new ResourceNotFoundException("Leave record not found.");
                });
    }

    private Order fetchOrder(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseGet(() -> {
                    orderRepository.findByIdWithDeleted(orderId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Order is deleted.");
                            });
                    throw new ResourceNotFoundException("Order not found with id: " + orderId);
                });
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