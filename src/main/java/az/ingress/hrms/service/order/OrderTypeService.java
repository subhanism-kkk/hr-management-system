package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderType.OrderTypeRequest;
import az.ingress.hrms.dto.orderType.OrderTypeResponse;

import java.util.List;

public interface OrderTypeService {
    OrderTypeResponse create(OrderTypeRequest request);

    OrderTypeResponse update(Integer id, OrderTypeRequest request);

    OrderTypeResponse getById(Integer id);

    List<OrderTypeResponse> getAll();

    void softDelete(Integer id);

    void restore(Integer id);

}
