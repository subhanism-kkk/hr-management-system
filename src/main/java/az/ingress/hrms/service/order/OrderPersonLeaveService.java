package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonLeaveSearchCriteria;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveCreateRequest;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveResponse;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveUpdateRequest;
import org.springframework.data.domain.Pageable;


public interface OrderPersonLeaveService {

    OrderPersonLeaveResponse create(OrderPersonLeaveCreateRequest request);

    OrderPersonLeaveResponse update(Integer id, OrderPersonLeaveUpdateRequest request);

    OrderPersonLeaveResponse getById(Integer id);

    PageResponse<OrderPersonLeaveResponse> getAll(OrderPersonLeaveSearchCriteria criteria, Pageable pageable);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonLeaveResponse activate(Integer id);

    OrderPersonLeaveResponse deactivate(Integer id);
}