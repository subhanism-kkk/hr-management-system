package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoCreateRequest;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoResponse;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoUpdateRequest;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonPersonalInfo;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.mapper.PersonPersonalInfoMapper;
import az.ingress.hrms.repository.PersonPersonalInfoRepository;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonPersonalInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonPersonalInfoServiceImpl implements PersonPersonalInfoService {

    private final PersonPersonalInfoRepository repository;
    private final PersonRepository personRepository;
    private final PersonPersonalInfoMapper mapper;

    @Override
    public PersonPersonalInfoResponse create(PersonPersonalInfoCreateRequest request) {
        Person person = personRepository.findById(request.getPersonId()).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found"));

        if (repository.existsByPerson(person)) {
            throw new DuplicateResourceException(
                    " This person already has personal information.");
        }

        String finCode = request.getFinCode().trim().toUpperCase();

        if (repository.existsByFinCode(finCode)) {
            throw new DuplicateResourceException(
                    "FIN code already exists."
            );
        }

        PersonPersonalInfo entity = mapper.toEntity(request);

        entity.setPerson(person);

        entity.setFinCode(finCode);

        repository.save(entity);
        return mapper.toResponse(entity);

    }

    @Override
    public PersonPersonalInfoResponse update(Integer id
            , PersonPersonalInfoUpdateRequest request) {
        PersonPersonalInfo entity = repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Personal information is deleted.");
                            });


                    throw new ResourceNotFoundException(
                            "Personal information not found.");
                });

        mapper.updateEntity(entity, request);

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public PersonPersonalInfoResponse getById(Integer id) {
        PersonPersonalInfo entity = repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Personal information is deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Personal information not found."
                    );
                });

        return mapper.toResponse(entity);
    }

    @Override
    public List<PersonPersonalInfoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void softDelete(Integer id) {

        PersonPersonalInfo entity = repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Personal information is deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Personal information not found."
                    );
                });
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setDeletedBy("SYSTEM");

        repository.save(entity);


    }

    @Override
    public void restore(Integer id) {
        PersonPersonalInfo entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Personal information not found."
                ));

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }
}
