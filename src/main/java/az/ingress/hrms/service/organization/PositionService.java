package az.ingress.hrms.service.organization;


import az.ingress.hrms.dto.position.PositionRequest;
import az.ingress.hrms.dto.position.PositionResponse;

import java.util.List;

public interface PositionService {
    PositionResponse create(PositionRequest request);

    PositionResponse update(Integer id, PositionRequest request);

    PositionResponse getById(Integer id);

    List<PositionResponse> getAll();

    void softDelete(Integer id);

    void restore(Integer id);

}
