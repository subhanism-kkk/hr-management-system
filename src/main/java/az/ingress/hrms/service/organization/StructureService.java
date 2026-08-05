package az.ingress.hrms.service.organization;

import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.dto.structure.StructureResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StructureService {

    StructureResponse create(StructureRequest request);

    StructureResponse update(
            Integer id,
            StructureRequest request
    );

    StructureResponse getById(Integer id);

    Page<StructureResponse> getAll(int pageNo, int pageSize);

    Page<StructureResponse> getRootStructures(int pageNo, int pageSize);

    Page<StructureResponse> getChildren(Integer parentId, int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    StructureResponse activate(Integer id);

    StructureResponse deactivate(Integer id);

}
