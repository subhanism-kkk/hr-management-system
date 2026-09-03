package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonTransferSearchCriteria;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferCreateRequest;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferResponse;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonTransfer;
import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.entity.organization.Structure;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.order.orderPerson.transfer.OrderPersonTransferLogService;
import az.ingress.hrms.mapper.order.OrderPersonTransferMapper;
import az.ingress.hrms.repository.order.OrderPersonAppointmentRepository;
import az.ingress.hrms.repository.order.OrderPersonTransferRepository;
import az.ingress.hrms.repository.organization.PositionRepository;
import az.ingress.hrms.repository.organization.StaffingPlanRepository;
import az.ingress.hrms.repository.organization.StructureRepository;
import az.ingress.hrms.repository.person.PersonRepository;
import az.ingress.hrms.service.order.OrderPersonTransferService;
import az.ingress.hrms.specification.order.OrderPersonTransferSpecification;
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
public class OrderPersonTransferServiceImpl implements OrderPersonTransferService {

    private final OrderPersonTransferRepository repository;
    private final PersonRepository personRepository;
    private final StructureRepository structureRepository;
    private final PositionRepository positionRepository;
    private final OrderPersonAppointmentRepository appointmentRepository;
    private final StaffingPlanRepository staffingPlanRepository;
    private final OrderPersonTransferLogService transferLogService;

    private final OrderPersonTransferMapper mapper;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public OrderPersonTransferResponse create(
            Order order,
            OrderPersonTransferCreateRequest request
    ) {
        if (request.getOldStructureId().equals(request.getNewStructureId()) &&
                request.getOldPositionId().equals(request.getNewPositionId())) {
            throw new BadRequestException(
                    "Target structure and position cannot be identical to current structure and position."
            );
        }

        if (!"TRF".equals(order.getOrderType().getCode())) {
            throw new BadRequestException("Selected order is not a transfer order.");
        }

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Effective date cannot be before the order date.");
        }

        Person person = fetchPerson(request.getPersonId());
        Structure oldStructure = fetchStructure(request.getOldStructureId());
        Structure newStructure = fetchStructure(request.getNewStructureId());
        Position oldPosition = fetchPosition(request.getOldPositionId());
        Position newPosition = fetchPosition(request.getNewPositionId());

        StaffingPlan staffingPlan = staffingPlanRepository
                .findByStructureIdAndPositionId(newStructure.getId(), newPosition.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No staffing plan exists for the selected structure and position."
                ));

        OrderPersonAppointment appointment = appointmentRepository
                .findByPersonIdAndIsClosedFalse(person.getId())
                .orElseThrow(() -> new BadRequestException("Person does not have an active appointment."));

        if (!appointment.getStaffingPlan().getStructure().getId().equals(oldStructure.getId())) {
            throw new BadRequestException("Old structure does not match employee's current structure.");
        }

        if (!appointment.getStaffingPlan().getPosition().getId().equals(oldPosition.getId())) {
            throw new BadRequestException("Old position does not match employee's current position.");
        }

        long activeEmployees = appointmentRepository.countByStaffingPlanIdAndIsClosedFalse(staffingPlan.getId());

        if (activeEmployees >= staffingPlan.getCapacity()) {
            throw new BadRequestException("Target staffing plan is already full.");
        }

        appointment.setStaffingPlan(staffingPlan);
        appointmentRepository.save(appointment);

        OrderPersonTransfer entity = mapper.toEntity(request);
        entity.setOrder(order);
        entity.setPerson(person);
        entity.setOldStructure(oldStructure);
        entity.setNewStructure(newStructure);
        entity.setOldPosition(oldPosition);
        entity.setNewPosition(newPosition);
        entity.setStatus(statusHelper.getActive());

        OrderPersonTransfer savedEntity = repository.save(entity);

        transferLogService.log(
                savedEntity,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonTransferResponse update(
            Order order,
            OrderPersonTransferUpdateRequest request
    ) {
        OrderPersonTransfer entity = fetchTransferByOrderAndPerson(order.getId(), request.getPersonId());

        Structure newStructure = fetchStructure(request.getNewStructureId());
        Position newPosition = fetchPosition(request.getNewPositionId());

        if (entity.getOldStructure().getId().equals(newStructure.getId()) &&
                entity.getOldPosition().getId().equals(newPosition.getId())) {
            throw new BadRequestException(
                    "New structure and position cannot be identical to the old structure and position."
            );
        }

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException("Effective date cannot be before the order date.");
        }

        transferLogService.log(
                entity,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(entity, request);

        entity.setNewStructure(newStructure);
        entity.setNewPosition(newPosition);

        OrderPersonTransfer updatedEntity = repository.save(entity);

        return mapper.toResponse(updatedEntity);
    }

    @Override
    public List<OrderPersonTransferResponse> getByOrderId(Integer orderId) {
        return repository.findByOrderIdAndIsDeletedFalse(orderId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<OrderPersonTransferResponse> getAll(OrderPersonTransferSearchCriteria criteria, Pageable pageable) {
        Specification<OrderPersonTransfer> specification = OrderPersonTransferSpecification.build(criteria);
        Page<OrderPersonTransfer> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    public void softDelete(Order order) {
        List<OrderPersonTransfer> entities = repository.findAllByOrderId(order.getId());

        if (entities.isEmpty()) {
            return;
        }

        String currentUsername = SecurityUtils.getCurrentUsername();
        LocalDateTime now = LocalDateTime.now();

        for (OrderPersonTransfer entity : entities) {
            transferLogService.log(
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
        List<OrderPersonTransfer> entities = repository.findAllByOrderIdWithDeleted(order.getId());

        if (entities.isEmpty()) {
            return;
        }

        String currentUsername = SecurityUtils.getCurrentUsername();

        for (OrderPersonTransfer entity : entities) {
            if (Boolean.TRUE.equals(entity.getIsDeleted())) {
                transferLogService.log(
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
        List<OrderPersonTransfer> entities = fetchTransfersByOrderId(order.getId());

        String currentUsername = SecurityUtils.getCurrentUsername();

        for (OrderPersonTransfer entity : entities) {
            transferLogService.log(
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
        List<OrderPersonTransfer> entities = fetchTransfersByOrderId(order.getId());

        String currentUsername = SecurityUtils.getCurrentUsername();

        for (OrderPersonTransfer entity : entities) {
            transferLogService.log(
                    entity,
                    LogAction.PATCH,
                    currentUsername
            );

            entity.setStatus(statusHelper.getInactive());
        }

        repository.saveAll(entities);
    }

    private OrderPersonTransfer fetchTransfer(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Transfer record is deleted.");
                            });
                    throw new ResourceNotFoundException("Transfer not found.");
                });
    }

    private OrderPersonTransfer fetchTransferByOrderAndPerson(Integer orderId, Integer personId) {
        return repository.findByOrderIdAndPersonId(orderId, personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transfer record not found for person ID " + personId + " under order ID " + orderId
                ));
    }

    private List<OrderPersonTransfer> fetchTransfersByOrderId(Integer orderId) {
        List<OrderPersonTransfer> entities = repository.findAllByOrderId(orderId);

        if (entities.isEmpty()) {
            List<OrderPersonTransfer> deletedEntities = repository.findAllByOrderIdWithDeleted(orderId);
            if (!deletedEntities.isEmpty()) {
                throw new DeletedResourceException(
                        "Transfer records for order ID " + orderId + " are deleted."
                );
            }

            throw new ResourceNotFoundException(
                    "No transfer records found for order ID: " + orderId
            );
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


        if (!statusHelper.getActive().equals(person.getStatus())) {
            throw new BadRequestException("Cannot create or modify transfer order for an inactive person (ID: " + personId + ").");
        }

        return person;
    }

    private Structure fetchStructure(Integer structureId) {
        return structureRepository.findById(structureId)
                .orElseGet(() -> {
                    structureRepository.findByIdWithDeleted(structureId)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Structure is deleted.");
                            });
                    throw new ResourceNotFoundException("Structure not found with id: " + structureId);
                });
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