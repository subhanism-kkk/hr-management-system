package az.ingress.hrms.service.organization;

import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.dto.structure.StructureResponse;

import java.util.List;

public interface StructureService {

    StructureResponse create(StructureRequest request);

    StructureResponse update(
            Integer id,
            StructureRequest request
    );

    StructureResponse getById(Integer id);

    List<StructureResponse> getAll();

    List<StructureResponse> getRootStructures();

    List<StructureResponse> getChildren(Integer parentId);

    void softDelete(Integer id);

    void restore(Integer id);

    StructureResponse activate(Integer id);

    StructureResponse deactivate(Integer id);

}
