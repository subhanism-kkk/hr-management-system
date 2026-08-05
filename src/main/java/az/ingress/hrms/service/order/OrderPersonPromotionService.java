package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionCreateRequest;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionResponse;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderPersonPromotionService {

    OrderPersonPromotionResponse create(OrderPersonPromotionCreateRequest request);

    OrderPersonPromotionResponse update(Integer id, OrderPersonPromotionUpdateRequest request);

    OrderPersonPromotionResponse getById(Integer id);

    Page<OrderPersonPromotionResponse> getAll(int pageNo, int pageSize);

    Page<OrderPersonPromotionResponse> getByPerson(Integer personId, int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonPromotionResponse activate(Integer id);

    OrderPersonPromotionResponse deactivate(Integer id);
}