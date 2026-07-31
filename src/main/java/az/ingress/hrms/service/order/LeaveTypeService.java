package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.leaveType.LeaveTypeCreateRequest;
import az.ingress.hrms.dto.leaveType.LeaveTypeResponse;
import az.ingress.hrms.dto.leaveType.LeaveTypeUpdateRequest;

import java.util.List;

public interface LeaveTypeService {

    LeaveTypeResponse create(LeaveTypeCreateRequest request);

    LeaveTypeResponse update(Integer id, LeaveTypeUpdateRequest request);

    LeaveTypeResponse getById(Integer id);

    LeaveTypeResponse getByCode(String code);

    List<LeaveTypeResponse> getAll();

    void softDelete(Integer id);

    void restore(Integer id);
}