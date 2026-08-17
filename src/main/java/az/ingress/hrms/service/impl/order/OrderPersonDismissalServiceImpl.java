package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.common.PageResponse;
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
import az.ingress.hrms.mapper.OrderPersonDismissalMapper;
import az.ingress.hrms.repository.*;
import az.ingress.hrms.service.order.OrderPersonDismissalService;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPersonDismissalServiceImpl implements OrderPersonDismissalService {

    private final OrderPersonDismissalRepository repository;
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final OrderPersonAppointmentRepository appointmentRepository;
    private final OrderPersonDismissalLogService dismissalLogService;

    private final OrderPersonDismissalMapper mapper;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public OrderPersonDismissalResponse create(OrderPersonDismissalCreateRequest request) {
        Order order = fetchOrder(request.getOrderId());

        if (!"DIS".equalsIgnoreCase(order.getOrderType().getCode())) {
            throw new BadRequestException("Selected order is not a dismissal order.");
        }

        if (request.getDismissalDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Dismissal date cannot be before the order date.");
        }

        Person person = fetchPerson(request.getPersonId());


        OrderPersonAppointment appointment = appointmentRepository
                .findByPersonIdAndIsClosedFalse(person.getId())
                .orElseThrow(() -> new BadRequestException("Person does not have an active appointment to dismiss."));

        if (request.getDismissalDate().isBefore(appointment.getStartDate())) {
            throw new BadRequestException("Dismissal date cannot be before the appointment start date.");
        }


        OrderPersonDismissal entity = mapper.toEntity(request);
        entity.setOrder(order);
        entity.setPerson(person);
        entity.setStatus(statusHelper.getActive());

        OrderPersonDismissal savedEntity = repository.save(entity);

        appointment.setDismissalOrder(order);

        appointment.setEndDate(request.getDismissalDate());

        appointment.setIsClosed(true);

        appointment.setStatus(statusHelper.getInactive());

        appointmentRepository.save(appointment);

        dismissalLogService.log(
                entity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername());

        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonDismissalResponse update(
            Integer id,
            OrderPersonDismissalUpdateRequest request
    ) {
        OrderPersonDismissal entity = fetchDismissal(id);
        Order order = entity.getOrder();

        if (request.getDismissalDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Dismissal date cannot be before the order date.");
        }

        OrderPersonAppointment appointment = appointmentRepository
                .findByPersonIdAndIsClosedFalse(entity.getPerson().getId())
                .orElseThrow(() -> new BadRequestException("Person does not have an active appointment."));

        if (request.getDismissalDate().isBefore(appointment.getStartDate())) {
            throw new BadRequestException("Dismissal date cannot be before the appointment start date.");
        }

        dismissalLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername());

        mapper.updateEntity(entity, request);

        appointment.setDismissalOrder(order);

        appointment.setEndDate(request.getDismissalDate());

        appointmentRepository.save(appointment);

        OrderPersonDismissal updatedEntity = repository.save(entity);

        return mapper.toResponse(updatedEntity);
    }

    @Override
    public OrderPersonDismissalResponse getById(Integer id) {
        return mapper.toResponse(fetchDismissal(id));
    }

    @Override
    public PageResponse<OrderPersonDismissalResponse> getAll(int pageNo, int pageSize) {

        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<OrderPersonDismissal> page =
                repository.findAll(pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );

    }

    @Override
    public PageResponse<OrderPersonDismissalResponse> getByPerson(Integer personId, int pageNo, int pageSize) {
        Person person = fetchPerson(personId);
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<OrderPersonDismissal> page =
                repository.findByPerson(person, pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );

    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        OrderPersonDismissal entity = fetchDismissal(id);

        dismissalLogService.log(
                entity,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername());

        entity.setDeletedBy(SecurityUtils.getCurrentUsername());
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());

        repository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        OrderPersonDismissal entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deleted dismissal record not found with id: " + id));

        if (!entity.getIsDeleted()) {
            throw new BadRequestException("Dismissal record is not deleted.");
        }

        dismissalLogService.log(
                entity,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }

//    @Override
//    @Transactional
//    public OrderPersonDismissalResponse activate(Integer id) {
//        OrderPersonDismissal entity = fetchDismissal(id);
//
//    dismissalLogService.log(
//    entity,
//    LogAction.PATCH,
//            "admin"
//            );
//
//        entity.setStatus(statusHelper.getActive());
//        OrderPersonDismissal saved = repository.save(entity);
//        return mapper.toResponse(saved);
//    }
//
//    @Override
//    @Transactional
//    public OrderPersonDismissalResponse deactivate(Integer id) {
//        OrderPersonDismissal entity = fetchDismissal(id);
//
//    dismissalLogService.log(
//    entity,
//    LogAction.PATCH,
//            "admin"
//            );
//
//        entity.setStatus(statusHelper.getInactive());
//
//        OrderPersonDismissal saved = repository.save(entity);
//        return mapper.toResponse(saved);
//    }


    private OrderPersonDismissal fetchDismissal(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Dismissal record is deleted.");
                            });
                    throw new ResourceNotFoundException("Dismissal record not found.");
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
}