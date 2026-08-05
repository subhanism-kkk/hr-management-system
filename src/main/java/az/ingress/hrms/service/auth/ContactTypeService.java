package az.ingress.hrms.service.auth;

import az.ingress.hrms.dto.contactType.ContactTypeRequest;
import az.ingress.hrms.dto.contactType.ContactTypeResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ContactTypeService {

    ContactTypeResponse create(ContactTypeRequest request);

    ContactTypeResponse update(Integer id, ContactTypeRequest request);

    ContactTypeResponse getById(Integer id);

    Page<ContactTypeResponse> getAll(int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);
}
