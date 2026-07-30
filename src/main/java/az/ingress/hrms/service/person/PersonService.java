package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.person.PersonRequest;
import az.ingress.hrms.dto.person.PersonResponse;


import java.util.List;

public interface PersonService {

    PersonResponse create(PersonRequest request);

    PersonResponse update(Integer id, PersonRequest request);

    PersonResponse getById(Integer id);

    List<PersonResponse> getAll();

    void softDelete(Integer id);

    void restore(Integer id);

    PersonResponse activate(Integer id);

    PersonResponse deactivate(Integer id);
}
