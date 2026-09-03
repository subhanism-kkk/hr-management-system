package az.ingress.hrms.service.auth;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.LeaveTypeSearchCriteria;
import az.ingress.hrms.dto.leaveType.LeaveTypeCreateRequest;
import az.ingress.hrms.dto.leaveType.LeaveTypeResponse;
import az.ingress.hrms.dto.leaveType.LeaveTypeUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface LeaveTypeService {

    LeaveTypeResponse create(LeaveTypeCreateRequest request);

    LeaveTypeResponse update(Integer id, LeaveTypeUpdateRequest request);

    LeaveTypeResponse getById(Integer id);

    LeaveTypeResponse getByCode(String code);

    PageResponse<LeaveTypeResponse> getAll(LeaveTypeSearchCriteria criteria, Pageable pageable);

    void softDelete(Integer id);

    void restore(Integer id);

    LeaveTypeResponse activate(Integer id);

    LeaveTypeResponse deactivate(Integer id);
}