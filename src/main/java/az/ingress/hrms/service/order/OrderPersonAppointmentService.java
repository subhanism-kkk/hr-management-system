package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonAppointmentSearchCriteria;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentCreateRequest;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentResponse;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderPersonAppointmentService {

    OrderPersonAppointmentResponse create(
            Order order,
            OrderPersonAppointmentCreateRequest request
    );

    OrderPersonAppointmentResponse update(
            Order order,
            OrderPersonAppointmentUpdateRequest request
    );

    void activate(Order order);

    void deactivate(Order order);

    void softDelete(Order order);

    void restore(Order order);

    PageResponse<OrderPersonAppointmentResponse> getAll(
            OrderPersonAppointmentSearchCriteria criteria,
            Pageable pageable
    );

    List<OrderPersonAppointmentResponse> getByOrderId(Integer orderId);
}