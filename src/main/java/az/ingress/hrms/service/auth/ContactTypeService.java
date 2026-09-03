package az.ingress.hrms.service.auth;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.contactType.ContactTypeRequest;
import az.ingress.hrms.dto.contactType.ContactTypeResponse;
import az.ingress.hrms.dto.criteria.ContactTypeSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactTypeService {
    ContactTypeResponse create(ContactTypeRequest request);
    ContactTypeResponse update(Integer id, ContactTypeRequest request);
    ContactTypeResponse getById(Integer id);
    PageResponse<ContactTypeResponse> getAll(ContactTypeSearchCriteria criteria, Pageable pageable);
    List<ContactTypeResponse> getActiveOptions();
    void activate(Integer id);
    void deactivate(Integer id);
    void softDelete(Integer id);
    void restore(Integer id);
}