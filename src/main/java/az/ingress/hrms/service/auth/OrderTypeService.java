package az.ingress.hrms.service.auth;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderTypeSearchCriteria;
import az.ingress.hrms.dto.orderType.OrderTypeRequest;
import az.ingress.hrms.dto.orderType.OrderTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderTypeService {
    OrderTypeResponse create(OrderTypeRequest request);

    OrderTypeResponse update(Integer id, OrderTypeRequest request);

    OrderTypeResponse getById(Integer id);

    PageResponse<OrderTypeResponse> getAll(OrderTypeSearchCriteria criteria, Pageable pageable);

    void softDelete(Integer id);

    void restore(Integer id);

}
