package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveCreateRequest;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveResponse;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveUpdateRequest;

import java.util.List;

public interface OrderPersonLeaveService {

    OrderPersonLeaveResponse create(OrderPersonLeaveCreateRequest request);

    OrderPersonLeaveResponse update(Integer id, OrderPersonLeaveUpdateRequest request);

    OrderPersonLeaveResponse getById(Integer id);

    List<OrderPersonLeaveResponse> getAll();

    List<OrderPersonLeaveResponse> getByPerson(Integer personId);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonLeaveResponse activate(Integer id);

    OrderPersonLeaveResponse deactivate(Integer id);
}