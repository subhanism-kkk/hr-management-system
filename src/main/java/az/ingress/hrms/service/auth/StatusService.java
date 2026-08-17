package az.ingress.hrms.service.auth;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.status.StatusRequest;
import az.ingress.hrms.dto.status.StatusResponse;
import az.ingress.hrms.entity.lookup.Status;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StatusService {

    StatusResponse create(StatusRequest request);

    StatusResponse update(Integer id, StatusRequest request);

    StatusResponse getById(Integer id);

    PageResponse<StatusResponse> getAll(int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);
}