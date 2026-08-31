package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonBonus.CreateOrderPersonBonusRequest;
import az.ingress.hrms.dto.criteria.OrderPersonBonusSearchCriteria;
import az.ingress.hrms.dto.orderPersonBonus.UpdateOrderPersonBonusRequest;
import az.ingress.hrms.dto.orderPersonBonus.OrderPersonBonusResponse;
import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.entity.order.Order;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrderPersonBonusService {

    OrderPersonBonusResponse create(
            Order order,
            CreateOrderPersonBonusRequest request);

    OrderPersonBonusResponse update(
            Order order,
            UpdateOrderPersonBonusRequest request);

    List<OrderPersonBonusResponse> getByOrderId(Integer orderId);

    PageResponse<OrderPersonBonusResponse> getAll(OrderPersonBonusSearchCriteria criteria, Pageable pageable);


//    BigDecimal calculateTotalSalary(Integer personId, LocalDate asOfDate);

    void softDelete(Order order);

    void restore(Order order);

    void activate(Order order);

    void deactivate(Order order);
}