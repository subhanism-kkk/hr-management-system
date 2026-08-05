package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferCreateRequest;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferResponse;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderPersonTransferService {

    OrderPersonTransferResponse create(OrderPersonTransferCreateRequest request);

    OrderPersonTransferResponse update(Integer id, OrderPersonTransferUpdateRequest request);

    OrderPersonTransferResponse getById(Integer id);

    Page<OrderPersonTransferResponse> getAll(int pageNo, int pageSize);

    Page<OrderPersonTransferResponse> getByPerson(Integer personId, int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonTransferResponse activate(Integer id);

    OrderPersonTransferResponse deactivate(Integer id);
}