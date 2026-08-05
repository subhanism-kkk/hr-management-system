package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentCreateRequest;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentResponse;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentUpdateRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface OrderPersonAppointmentService {
    OrderPersonAppointmentResponse create(
            OrderPersonAppointmentCreateRequest request
    );

    OrderPersonAppointmentResponse update(
            Integer id,
            OrderPersonAppointmentUpdateRequest request
    );

    OrderPersonAppointmentResponse getById(Integer id);

    Page<OrderPersonAppointmentResponse> getAll(int pageNo, int pageSize);

    Page<OrderPersonAppointmentResponse> getByPerson(
            Integer personId, int pageNo, int pageSize
    );

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
