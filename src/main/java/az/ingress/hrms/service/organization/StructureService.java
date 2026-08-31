package az.ingress.hrms.service.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.StructureSearchCriteria;
import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.dto.structure.StructureResponse;
import az.ingress.hrms.entity.order.Order;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;


public interface StructureService {

    StructureResponse create(Order order,
                             StructureRequest request);

    StructureResponse update(
            Order order,
            StructureRequest request
    );

    List<StructureResponse> getByOrderId(Integer orderId);

    PageResponse<StructureResponse> getAll(StructureSearchCriteria criteria, Pageable pageable);

    void softDelete(Order order);

    void restore(Order order);

    List<StructureResponse> activate(Order order);

    List<StructureResponse> deactivate(Order order);

}
