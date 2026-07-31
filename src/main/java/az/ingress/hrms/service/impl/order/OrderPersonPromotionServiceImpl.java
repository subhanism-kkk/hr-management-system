package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionCreateRequest;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionResponse;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonPromotion;
import az.ingress.hrms.entity.organization.Position;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.mapper.OrderPersonPromotionMapper;
import az.ingress.hrms.repository.*;
import az.ingress.hrms.service.order.OrderPersonPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderPersonPromotionServiceImpl implements OrderPersonPromotionService {

    private final OrderPersonPromotionRepository repository;
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final PositionRepository positionRepository;
    private final OrderPersonAppointmentRepository appointmentRepository;

    private final OrderPersonPromotionMapper mapper;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public OrderPersonPromotionResponse create(OrderPersonPromotionCreateRequest request) {
        if (request.getOldPositionId().equals(request.getNewPositionId())) {
            throw new BadRequestException("New position cannot be identical to the current old position.");
        }

        Order order = fetchOrder(request.getOrderId());

        if (!order.getOrderType().getCode().equals("PRO")) {
            throw new BadRequestException(
                    "Selected order is not a promotion order."
            );
        }

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException(
                    "Effective date cannot be before the order date."
            );
        }

        Person person = fetchPerson(request.getPersonId());
        Position oldPosition = fetchPosition(request.getOldPositionId());
        Position newPosition = fetchPosition(request.getNewPositionId());

        OrderPersonAppointment appointment =
                appointmentRepository
                        .findByPersonIdAndIsClosedFalse(person.getId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Person does not have an active appointment."
                                ));

        if (!appointment.getStaffingPlan()
                .getPosition()
                .getId()
                .equals(oldPosition.getId())) {

            throw new BadRequestException(
                    "Old position does not match employee's current position."
            );
        }



        OrderPersonPromotion entity = mapper.toEntity(request);
        entity.setOrder(order);
        entity.setPerson(person);
        entity.setOldPosition(oldPosition);
        entity.setNewPosition(newPosition);
        entity.setStatus(statusHelper.getActive());

        OrderPersonPromotion savedEntity = repository.save(entity);
        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public OrderPersonPromotionResponse update(
            Integer id,
            OrderPersonPromotionUpdateRequest request
    ) {

        OrderPersonPromotion entity = fetchPromotion(id);

        Position newPosition = fetchPosition(request.getNewPositionId());

        if (entity.getOldPosition().getId().equals(newPosition.getId())) {
            throw new BadRequestException(
                    "New position cannot be identical to the old position."
            );
        }


        Order order = entity.getOrder();

        if (request.getEffectiveDate().isBefore(order.getOrderDate())) {
            throw new BadRequestException(
                    "Effective date cannot be before the order date."
            );
        }

        mapper.updateEntity(entity, request);

        entity.setNewPosition(newPosition);

        OrderPersonPromotion updatedEntity = repository.save(entity);

        return mapper.toResponse(updatedEntity);
    }

    @Override
    public OrderPersonPromotionResponse getById(Integer id) {
        return mapper.toResponse(fetchPromotion(id));
    }

    @Override
    public List<OrderPersonPromotionResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<OrderPersonPromotionResponse> getByPerson(Integer personId) {
        fetchPerson(personId);
        return repository.findByPersonId(personId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        OrderPersonPromotion entity = fetchPromotion(id);

        entity.setDeletedBy("SYSTEM");
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());

        repository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        OrderPersonPromotion entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deleted promotion record not found with id: " + id));

        if (!entity.getIsDeleted()) {
            throw new BadRequestException(
                    "Promotion is not deleted."
            );
        }

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);


        repository.save(entity);
    }

    @Override
    @Transactional
    public OrderPersonPromotionResponse activate(Integer id) {
        OrderPersonPromotion entity = fetchPromotion(id);
        entity.setStatus(statusHelper.getActive());

        OrderPersonPromotion saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrderPersonPromotionResponse deactivate(Integer id) {
        OrderPersonPromotion entity = fetchPromotion(id);
        entity.setStatus(statusHelper.getInactive());

        OrderPersonPromotion saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    // =========================================================================
    // Private Helper Fetch Methods
    // =========================================================================

    private OrderPersonPromotion fetchPromotion(Integer id) {

        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Promotion record is deleted."
                                );
                            });
                    throw new ResourceNotFoundException(
                            "Promotion not found."
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
