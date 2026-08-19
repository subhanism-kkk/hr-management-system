package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonTransferSearchCriteria;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferCreateRequest;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferResponse;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferUpdateRequest;
import org.springframework.data.domain.Pageable;


public interface OrderPersonTransferService {

    OrderPersonTransferResponse create(OrderPersonTransferCreateRequest request);

    OrderPersonTransferResponse update(Integer id, OrderPersonTransferUpdateRequest request);

    OrderPersonTransferResponse getById(Integer id);

    PageResponse<OrderPersonTransferResponse> getAll(OrderPersonTransferSearchCriteria criteria, Pageable pageable);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonTransferResponse activate(Integer id);

    OrderPersonTransferResponse deactivate(Integer id);
}