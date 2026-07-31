package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferCreateRequest;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferResponse;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferUpdateRequest;

import java.util.List;

public interface OrderPersonTransferService {

    OrderPersonTransferResponse create(OrderPersonTransferCreateRequest request);

    OrderPersonTransferResponse update(Integer id, OrderPersonTransferUpdateRequest request);

    OrderPersonTransferResponse getById(Integer id);

    List<OrderPersonTransferResponse> getAll();

    List<OrderPersonTransferResponse> getByPerson(Integer personId);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonTransferResponse activate(Integer id);

    OrderPersonTransferResponse deactivate(Integer id);
}