package az.ingress.hrms.service.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.StructureSearchCriteria;
import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.dto.structure.StructureResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public interface StructureService {

    StructureResponse create(StructureRequest request);

    StructureResponse update(
            Integer id,
            StructureRequest request
    );

    StructureResponse getById(Integer id);

    PageResponse<StructureResponse> getAll(StructureSearchCriteria criteria, Pageable pageable);

//    PageResponse<StructureResponse> getRootStructures(int pageNo, int pageSize);
//
//    PageResponse<StructureResponse> getChildren(Integer parentId, int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    StructureResponse activate(Integer id);

    StructureResponse deactivate(Integer id);

}
