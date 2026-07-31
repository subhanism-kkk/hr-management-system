package az.ingress.hrms.service.impl.order;

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
import az.ingress.hrms.mapper.OrderPersonTransferMapper;
import az.ingress.hrms.repository.*;
import az.ingress.hrms.service.order.OrderPersonTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPersonTransferServiceImpl implements OrderPersonTransferService {

    private final OrderPersonTransferRepository repository;
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final StructureRepository structureRepository;
    private final PositionRepository positionRepository;
    private final OrderPersonAppointmentRepository appointmentRepository;
    private final StaffingPlanRepository staffingPlanRepository;

    private final OrderPersonTransferMapper mapper;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public OrderPersonTransferResponse create(OrderPersonTransferCreateRequest request) {
        if (request.getOldStructureId().equals(request.getNewStructureId()) &&
                request.getOldPositionId().equals(request.getNewPositionId())) {
            throw new BadRequestException(
                    "Target structure and position cannot be identical to current structure and position.");
        }

        Order order = fetchOrder(request.getOrderId());

        if (!order.getOrderType().getCode().equals("TRF")) {
            throw new BadRequestException(
                    "Selected order is not a transfer order."
            );
        }

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException(
                    "Effective date cannot be before the order date."
            );
        }

        Person person = fetchPerson(request.getPersonId());
        Structure oldStructure = fetchStructure(request.getOldStructureId());
        Structure newStructure = fetchStructure(request.getNewStructureId());
        Position oldPosition = fetchPosition(request.getOldPositionId());
        Position newPosition = fetchPosition(request.getNewPositionId());

        StaffingPlan staffingPlan =
                staffingPlanRepository
                        .findByStructureIdAndPositionId(
                                newStructure.getId(),
                                newPosition.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No staffing plan exists for the selected structure and position."
                                ));

        OrderPersonAppointment appointment =
                appointmentRepository
                        .findByPersonIdAndIsClosedFalse(person.getId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Person does not have an active appointment."
                                ));

        if (!appointment.getStaffingPlan()
                .getStructure()
                .getId()
                .equals(oldStructure.getId())) {

            throw new BadRequestException(
                    "Old structure does not match employee's current structure."
            );
        }

        if (!appointment.getStaffingPlan()
                .getPosition()
                .getId()
                .equals(oldPosition.getId())) {

            throw new BadRequestException(
                    "Old position does not match employee's current position."
            );
        }



        long activeEmployees =
                appointmentRepository
                        .countByStaffingPlanIdAndIsClosedFalse(
                                staffingPlan.getId()
                        );

        if (activeEmployees >= staffingPlan.getCapacity()) {

            throw new BadRequestException(
                    "Target staffing plan is already full."
            );
        }


        OrderPersonTransfer entity = mapper.toEntity(request);
        entity.setOrder(order);
        entity.setPerson(person);
        entity.setOldStructure(oldStructure);
        entity.setNewStructure(newStructure);
        entity.setOldPosition(oldPosition);
        entity.setNewPosition(newPosition);
        entity.setStatus(statusHelper.getActive());

        OrderPersonTransfer savedEntity = repository.save(entity);
        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonTransferResponse update(
            Integer id,
            OrderPersonTransferUpdateRequest request
    ) {
        OrderPersonTransfer entity = fetchTransfer(id);

        Structure newStructure = fetchStructure(request.getNewStructureId());
        Position newPosition = fetchPosition(request.getNewPositionId());

        if (entity.getOldStructure().getId().equals(newStructure.getId()) &&
                entity.getOldPosition().getId().equals(newPosition.getId())) {
            throw new BadRequestException(
                    "New structure and position cannot be identical to the old structure and position."
            );
        }

        Order order = entity.getOrder();

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException(
                    "Effective date cannot be before the order date."
            );
        }

        mapper.updateEntity(entity, request);

        entity.setNewStructure(newStructure);
        entity.setNewPosition(newPosition);

        OrderPersonTransfer updatedEntity = repository.save(entity);

        return mapper.toResponse(updatedEntity);
    }

    @Override
    public OrderPersonTransferResponse getById(Integer id) {
        return mapper.toResponse(fetchTransfer(id));
    }

    @Override
    public List<OrderPersonTransferResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<OrderPersonTransferResponse> getByPerson(Integer personId) {
        fetchPerson(personId);
        return repository.findByPersonId(personId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        OrderPersonTransfer entity = fetchTransfer(id);

        entity.setDeletedBy("SYSTEM");
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());

        repository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        OrderPersonTransfer entity = repository.findDeletedById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Deleted transfer record not found with id: " + id));

        if (!entity.getIsDeleted()) {
            throw new BadRequestException(
                    "Transfer is not deleted."
            );
        }

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }

    @Override
    @Transactional
    public OrderPersonTransferResponse activate(Integer id) {
        OrderPersonTransfer entity = fetchTransfer(id);
        entity.setStatus(statusHelper.getActive());

        OrderPersonTransfer saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrderPersonTransferResponse deactivate(Integer id) {
        OrderPersonTransfer entity = fetchTransfer(id);

        OrderPersonTransfer saved = repository.save(entity);
        return mapper.toResponse(saved);
    }


    private OrderPersonTransfer fetchTransfer(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findDeletedById(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Transfer record is deleted."
                                );
                            });
                    throw new ResourceNotFoundException(
                            "Transfer not found."
                    );
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