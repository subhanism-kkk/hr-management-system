package az.ingress.hrms.service.organization;


import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.position.PositionRequest;
import az.ingress.hrms.dto.position.PositionResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PositionService {
    PositionResponse create(PositionRequest request);

    PositionResponse update(Integer id, PositionRequest request);

    PositionResponse getById(Integer id);

    PageResponse<PositionResponse> getAll(int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    PositionResponse activate(Integer id);

    PositionResponse deactivate(Integer id);

}
