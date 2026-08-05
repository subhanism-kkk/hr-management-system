package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.order.OrderRequest;
import az.ingress.hrms.dto.order.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    OrderResponse create(OrderRequest request);

    OrderResponse getById(Integer id);

    Page<OrderResponse> getAll(int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderResponse activate(Integer id);

    OrderResponse deactivate(Integer id);

}