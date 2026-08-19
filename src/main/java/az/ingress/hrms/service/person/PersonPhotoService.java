package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.PersonPhotoSearchCriteria;
import az.ingress.hrms.dto.personPhoto.PersonPhotoCreateRequest;
import az.ingress.hrms.dto.personPhoto.PersonPhotoResponse;
import az.ingress.hrms.dto.personPhoto.PersonPhotoUpdateRequest;
import org.springframework.data.domain.Pageable;


public interface PersonPhotoService {

    PersonPhotoResponse create(PersonPhotoCreateRequest request);

    PersonPhotoResponse update(Integer id, PersonPhotoUpdateRequest request);

    PersonPhotoResponse getById(Integer id);

    PageResponse<PersonPhotoResponse> getAll(PersonPhotoSearchCriteria criteria, Pageable pageable);

    PersonPhotoResponse getMainPhoto(Integer personId);

    void setMainPhoto(Integer photoId);

    void softDelete(Integer id);

    void restore(Integer id);


    PersonPhotoResponse activate(Integer id);

    PersonPhotoResponse deactivate(Integer id);
}
