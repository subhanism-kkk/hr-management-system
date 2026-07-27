package az.ingress.hrms.service.auth;

import az.ingress.hrms.dto.contactType.ContactTypeRequest;
import az.ingress.hrms.dto.contactType.ContactTypeResponse;

import java.util.List;

public interface ContactTypeService {

    ContactTypeResponse create(ContactTypeRequest request);

    ContactTypeResponse update(Integer id, ContactTypeRequest request);

    ContactTypeResponse getById(Integer id);

    List<ContactTypeResponse> getAll();

    void softDelete(Integer id);

    void restore(Integer id);
}
