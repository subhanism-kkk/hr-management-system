package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalCreateRequest;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalResponse;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderPersonDismissalService {

    OrderPersonDismissalResponse create(OrderPersonDismissalCreateRequest request);

    OrderPersonDismissalResponse update(Integer id, OrderPersonDismissalUpdateRequest request);

    OrderPersonDismissalResponse getById(Integer id);

    Page<OrderPersonDismissalResponse> getAll(int pageNo, int pageSize);

    Page<OrderPersonDismissalResponse> getByPerson(Integer personId,int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

//    OrderPersonDismissalResponse activate(Integer id);
//
//    OrderPersonDismissalResponse deactivate(Integer id);
}