package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalCreateRequest;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalResponse;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalUpdateRequest;

import java.util.List;

public interface OrderPersonDismissalService {

    OrderPersonDismissalResponse create(OrderPersonDismissalCreateRequest request);

    OrderPersonDismissalResponse update(Integer id, OrderPersonDismissalUpdateRequest request);

    OrderPersonDismissalResponse getById(Integer id);

    List<OrderPersonDismissalResponse> getAll();

    List<OrderPersonDismissalResponse> getByPerson(Integer personId);

    void softDelete(Integer id);

    void restore(Integer id);

//    OrderPersonDismissalResponse activate(Integer id);
//
//    OrderPersonDismissalResponse deactivate(Integer id);
}