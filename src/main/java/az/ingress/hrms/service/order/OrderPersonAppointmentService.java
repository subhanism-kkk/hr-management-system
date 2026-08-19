package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonAppointmentSearchCriteria;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentCreateRequest;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentResponse;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentUpdateRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OrderPersonAppointmentService {
    OrderPersonAppointmentResponse create(
            OrderPersonAppointmentCreateRequest request
    );

    OrderPersonAppointmentResponse update(
            Integer id,
            OrderPersonAppointmentUpdateRequest request
    );

    OrderPersonAppointmentResponse getById(Integer id);

    PageResponse<OrderPersonAppointmentResponse> getAll(OrderPersonAppointmentSearchCriteria criteria, Pageable pageable);

    void dismiss(
            Integer appointmentId,
            Integer dismissalOrderId,
            LocalDate dismissalDate
    );

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonAppointmentResponse activate(Integer id);

    OrderPersonAppointmentResponse deactivate(Integer id);
}
