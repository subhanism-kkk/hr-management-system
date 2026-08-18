package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.PersonSearchCriteria;
import az.ingress.hrms.dto.person.PersonRequest;
import az.ingress.hrms.dto.person.PersonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;

public interface PersonService {

    PersonResponse create(PersonRequest request);

    PersonResponse update(Integer id, PersonRequest request);

    PersonResponse getById(Integer id);

    PageResponse<PersonResponse> getAll(PersonSearchCriteria criteria, Pageable pageable);

    void softDelete(Integer id);

    void restore(Integer id);

    PersonResponse activate(Integer id);

    PersonResponse deactivate(Integer id);
}
