package az.ingress.hrms.service.auth;

import az.ingress.hrms.dto.leaveType.LeaveTypeCreateRequest;
import az.ingress.hrms.dto.leaveType.LeaveTypeResponse;
import az.ingress.hrms.dto.leaveType.LeaveTypeUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LeaveTypeService {

    LeaveTypeResponse create(LeaveTypeCreateRequest request);

    LeaveTypeResponse update(Integer id, LeaveTypeUpdateRequest request);

    LeaveTypeResponse getById(Integer id);

    LeaveTypeResponse getByCode(String code);

    Page<LeaveTypeResponse> getAll(int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);
}