package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonDismissalSearchCriteria;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalCreateRequest;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalResponse;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalUpdateRequest;
import org.springframework.data.domain.Pageable;


public interface OrderPersonDismissalService {

    OrderPersonDismissalResponse create(OrderPersonDismissalCreateRequest request);

    OrderPersonDismissalResponse update(Integer id, OrderPersonDismissalUpdateRequest request);

    OrderPersonDismissalResponse getById(Integer id);

    PageResponse<OrderPersonDismissalResponse> getAll(OrderPersonDismissalSearchCriteria criteria, Pageable pageable);

    void softDelete(Integer id);

    void restore(Integer id);

//    OrderPersonDismissalResponse activate(Integer id);
//
//    OrderPersonDismissalResponse deactivate(Integer id);
}