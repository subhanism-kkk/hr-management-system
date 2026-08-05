package az.ingress.hrms.service.auth;

import az.ingress.hrms.dto.orderType.OrderTypeRequest;
import az.ingress.hrms.dto.orderType.OrderTypeResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderTypeService {
    OrderTypeResponse create(OrderTypeRequest request);

    OrderTypeResponse update(Integer id, OrderTypeRequest request);

    OrderTypeResponse getById(Integer id);

    Page<OrderTypeResponse> getAll(int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

}
