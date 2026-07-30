package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionCreateRequest;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionResponse;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionUpdateRequest;

import java.util.List;

public interface OrderPersonPromotionService {

    OrderPersonPromotionResponse create(OrderPersonPromotionCreateRequest request);

    OrderPersonPromotionResponse update(Integer id, OrderPersonPromotionUpdateRequest request);

    OrderPersonPromotionResponse getById(Integer id);

    List<OrderPersonPromotionResponse> getAll();

    List<OrderPersonPromotionResponse> getByPerson(Integer personId);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonPromotionResponse activate(Integer id);

    OrderPersonPromotionResponse deactivate(Integer id);
}