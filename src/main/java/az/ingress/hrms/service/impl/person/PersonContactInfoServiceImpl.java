package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.dto.personContactInfo.PersonContactInfoCreateRequest;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoResponse;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoUpdateRequest;
import az.ingress.hrms.entity.lookup.ContactType;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonContactInfo;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.mapper.PersonContactInfoMapper;
import az.ingress.hrms.repository.ContactTypeRepository;
import az.ingress.hrms.repository.PersonContactInfoRepository;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonContactInfoService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonContactInfoServiceImpl implements PersonContactInfoService {

    private final PersonContactInfoMapper mapper;
    private final PersonContactInfoRepository repository;
    private final PersonRepository personRepository;
    private final ContactTypeRepository contactTypeRepository;

    @Override
    @Transactional
    public PersonContactInfoResponse create(PersonContactInfoCreateRequest request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found."
                ));

        ContactType contactType = contactTypeRepository.findById(request.getContactTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contact Type not found."
                ));

        String contactValue = request.getContactValue().trim();

        if (repository.existsByPersonAndContactTypeAndContactValueIgnoreCase(person, contactType, contactValue)) {
            throw new DuplicateResourceException("This contact info already exists for this person.");
        }

        PersonContactInfo entity = mapper.toEntity(request);

        entity.setPerson(person);
        entity.setContactValue(contactValue);
        entity.setContactType(contactType);

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonContactInfoResponse update(
            Integer id, PersonContactInfoUpdateRequest request) {

        PersonContactInfo entity = fetchPersonContactInfo(id);


        ContactType contactType = contactTypeRepository.findById(
                        request.getContactTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contact Type not found with ID: "
                                + request.getContactTypeId())
                );


        if (
                !entity.getContactValue()
                        .equalsIgnoreCase(request.getContactValue())
                        ||
                        !entity.getContactType().getId()
                                .equals(request.getContactTypeId())
        ) {

            boolean exists =
                    repository.existsByPersonAndContactTypeAndContactValueIgnoreCase(
                            entity.getPerson(),
                            contactType,
                            request.getContactValue().trim()
                    );

            if (exists) {
                throw new DuplicateResourceException(
                        "Contact already exists."
                );
            }
        }

        mapper.updateEntity(entity, request);

        entity.setContactType(contactType);
        entity.setContactValue(request.getContactValue().trim());

        repository.save(entity);

        return mapper.toResponse(entity);

    }

    @Override
    public PersonContactInfoResponse getById(Integer id) {
        return mapper.toResponse(fetchPersonContactInfo(id));
    }

    @Override
    public List<PersonContactInfoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<PersonContactInfoResponse> getAllByPerson(Integer personId) {
        if (!personRepository.existsById(personId)) {
            throw new ResourceNotFoundException(
                    "Person with " + personId + " does not exits.");
        }

        return repository.findByPersonId(personId)
                .stream()
                .map(mapper::toResponse)
                .toList();

    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        PersonContactInfo entity = fetchPersonContactInfo(id);

        entity.setIsDeleted(true);
        entity.setDeletedBy("SYSTEM");
        entity.setDeletedAt(LocalDateTime.now());

        repository.save(entity);

    }

    @Override
    @Transactional
    public void restore(Integer id) {

        PersonContactInfo entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contact info not found."
                        ));

        if (!entity.getIsDeleted()) {
            throw new IllegalStateException(
                    "Contact info is not deleted."
            );
        }

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);

    }


    private PersonContactInfo fetchPersonContactInfo(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Person Contact info is deleted");
                            });

                    throw new ResourceNotFoundException("Person contact information not found.");
                });

    }
}
