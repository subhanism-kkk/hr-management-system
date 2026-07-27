package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.mapper.OrderMapper;
import az.ingress.hrms.repository.OrderRepository;
import az.ingress.hrms.repository.OrderTypeRepository;
import az.ingress.hrms.service.generator.OrderNumberGenerator;
import az.ingress.hrms.service.order.OrderService;
import az.ingress.hrms.dto.order.OrderRequest;
import az.ingress.hrms.dto.order.OrderResponse;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.OrderType;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderNumberGenerator generator;
    private final OrderTypeRepository orderTypeRepository;


    @Override
    public OrderResponse create(OrderRequest request) {
        OrderType orderType =
                orderTypeRepository.findById(request.getOrderTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order type not found."
                                ));
        Order order =
                Order.builder()
                        .orderType(orderType)
                        .orderNumber(
                                generator.generate(orderType)
                        )
                        .build();

        repository.save(order);


        return mapper.toResponse(order);
    }

    @Override
    public OrderResponse getById(Integer id) {
        Order order = repository.findById(id)
                .orElseGet(() -> {

                    repository.findByIdWithDeleted(id)
                            .ifPresent(o -> {
                                throw new DeletedResourceException(
                                        "Order has been deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Order not found."
                    );

                });
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
    public void softDelete(Integer id) {

        Order order = repository.findById(id)
                .orElseGet(() -> {

                    repository.findByIdWithDeleted(id)
                            .ifPresent(o -> {
                                throw new DeletedResourceException(
                                        "Order has been deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Order not found."
                    );

                });


        order.setIsDeleted(true);
        order.setDeletedAt(LocalDateTime.now());
        order.setDeletedBy("SYSTEM");

        repository.save(order);
    }

    @Override
    public void restore(Integer id) {

        Order order =
                repository.findByIdWithDeleted(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found."
                                ));

        order.setIsDeleted(false);
        order.setDeletedAt(null);
        order.setDeletedBy(null);

        repository.save(order);
    }
}
