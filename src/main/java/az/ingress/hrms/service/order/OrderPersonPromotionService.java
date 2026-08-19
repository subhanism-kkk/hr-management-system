package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonPromotionSearchCriteria;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionCreateRequest;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionResponse;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionUpdateRequest;
import org.springframework.data.domain.Pageable;


public interface OrderPersonPromotionService {

    OrderPersonPromotionResponse create(OrderPersonPromotionCreateRequest request);

    OrderPersonPromotionResponse update(Integer id, OrderPersonPromotionUpdateRequest request);

    OrderPersonPromotionResponse getById(Integer id);

    PageResponse<OrderPersonPromotionResponse> getAll(OrderPersonPromotionSearchCriteria criteria, Pageable pageable);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonPromotionResponse activate(Integer id);

    OrderPersonPromotionResponse deactivate(Integer id);
}