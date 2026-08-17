package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveCreateRequest;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveResponse;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveUpdateRequest;


public interface OrderPersonLeaveService {

    OrderPersonLeaveResponse create(OrderPersonLeaveCreateRequest request);

    OrderPersonLeaveResponse update(Integer id, OrderPersonLeaveUpdateRequest request);

    OrderPersonLeaveResponse getById(Integer id);

    PageResponse<OrderPersonLeaveResponse> getAll(int pageNo, int pageSize);

    PageResponse<OrderPersonLeaveResponse> getByPerson(Integer personId, int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonLeaveResponse activate(Integer id);

    OrderPersonLeaveResponse deactivate(Integer id);
}