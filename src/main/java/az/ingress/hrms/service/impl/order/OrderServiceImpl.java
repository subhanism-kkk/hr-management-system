package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.order.order.OrderLogService;
import az.ingress.hrms.mapper.OrderMapper;
import az.ingress.hrms.repository.OrderRepository;
import az.ingress.hrms.repository.OrderTypeRepository;
import az.ingress.hrms.service.generator.OrderNumberGenerator;
import az.ingress.hrms.service.order.OrderService;
import az.ingress.hrms.dto.order.OrderRequest;
import az.ingress.hrms.dto.order.OrderResponse;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.lookup.OrderType;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderNumberGenerator generator;
    private final OrderTypeRepository orderTypeRepository;
    private final StatusHelper statusHelper;
    private final OrderLogService orderLogService;


    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        OrderType orderType = orderTypeRepository.findById(request.getOrderTypeId())
                .orElseGet(() -> {
                    orderTypeRepository.findByIdWithDeleted(request.getOrderTypeId())
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Order type is deleted");
                            });

                    throw new ResourceNotFoundException("Order type not found.");
                });

        Order order =
                Order.builder()
                        .orderType(orderType)
                        .orderNumber(
                                generator.generate(orderType)
                        )
                        .build();

        repository.save(order);

        orderLogService.log(
                order,
                LogAction.POST,
                "admin"
        );

        return mapper.toResponse(order);
    }

    @Override
    public OrderResponse getById(Integer id) {
        Order order = fetchOrder(id);

        return mapper.toResponse(order);

    }

    @Override
    public List<OrderResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        Order order = fetchOrder(id);

        orderLogService.log(
                order,
                LogAction.DELETE,
                "admin"
        );

        order.setIsDeleted(true);
        order.setDeletedAt(LocalDateTime.now());
        order.setDeletedBy("SYSTEM");

        repository.save(order);
    }

    @Override
    @Transactional
    public void restore(Integer id) {

        Order order =
                repository.findByIdWithDeleted(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found."
                                ));

        if (!Boolean.TRUE.equals(order.getIsDeleted())) {
            throw new IllegalStateException("Order is not deleted.");
        }

        orderLogService.log(
                order,
                LogAction.PATCH,
                "admin"
        );

        order.setIsDeleted(false);
        order.setDeletedAt(null);
        order.setDeletedBy(null);

        repository.save(order);
    }

    @Override
    @Transactional
    public OrderResponse activate(Integer id) {
        Order entity = fetchOrder(id);

        orderLogService.log(
                entity,
                LogAction.PATCH,
                "admin"
        );

        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public OrderResponse deactivate(Integer id) {
        Order entity = fetchOrder(id);

        orderLogService.log(
                entity,
                LogAction.PATCH,
                "admin"
        );

        entity.setStatus(statusHelper.getInactive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }


    private Order fetchOrder(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Order is deleted");
                            });

                    throw new ResourceNotFoundException("Order not found.");
                });
    }
}
