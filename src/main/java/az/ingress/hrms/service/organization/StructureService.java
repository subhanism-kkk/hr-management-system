package az.ingress.hrms.service.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.dto.structure.StructureResponse;

import java.time.LocalDateTime;


public interface StructureService {

    StructureResponse create(StructureRequest request);

    StructureResponse update(
            Integer id,
            StructureRequest request
    );

    StructureResponse getById(Integer id);

    PageResponse<StructureResponse> getAll(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search,
            Integer parentId,
            Boolean isClosed,
            String status,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            Boolean isRoot
    );

//    PageResponse<StructureResponse> getRootStructures(int pageNo, int pageSize);
//
//    PageResponse<StructureResponse> getChildren(Integer parentId, int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    StructureResponse activate(Integer id);

    StructureResponse deactivate(Integer id);

}
