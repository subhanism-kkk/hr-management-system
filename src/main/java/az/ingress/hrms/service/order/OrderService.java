package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.order.OrderRequest;
import az.ingress.hrms.dto.order.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse create(OrderRequest request);

    OrderResponse getById(Integer id);

    List<OrderResponse> getAll();

    void softDelete(Integer id);

    void restore(Integer id);

    OrderResponse activate(Integer id);

    OrderResponse deactivate(Integer id);

}