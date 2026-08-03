package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoCreateRequest;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoResponse;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoUpdateRequest;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonPersonalInfo;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.person.personPersonalInfo.PersonPersonalInfoLogService;
import az.ingress.hrms.mapper.PersonPersonalInfoMapper;
import az.ingress.hrms.repository.PersonPersonalInfoRepository;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonPersonalInfoService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PersonPersonalInfoServiceImpl implements PersonPersonalInfoService {

    private final PersonPersonalInfoRepository repository;
    private final PersonRepository personRepository;
    private final PersonPersonalInfoMapper mapper;
    private final StatusHelper statusHelper;
    private final PersonPersonalInfoLogService personalInfoLogService;

    @Override
    @Transactional
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

        personalInfoLogService.log(
                entity,
                LogAction.POST,
                "admin"
        );

        return mapper.toResponse(entity);

    }

    @Override
    @Transactional
    public PersonPersonalInfoResponse update(Integer id
            , PersonPersonalInfoUpdateRequest request) {

        PersonPersonalInfo entity =fetchPersonPersonalInfo(id);

        personalInfoLogService.log(
                entity,
                LogAction.PUT,
                "admin"
        );

        mapper.updateEntity(entity, request);

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public PersonPersonalInfoResponse getById(Integer id) {
        PersonPersonalInfo entity =fetchPersonPersonalInfo(id);


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
    @Transactional
    public void softDelete(Integer id) {

        PersonPersonalInfo entity =fetchPersonPersonalInfo(id);

        personalInfoLogService.log(
                entity,
                LogAction.DELETE,
                "admin"
        );

        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setDeletedBy("SYSTEM");

        repository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        PersonPersonalInfo entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Personal information not found."
                ));

        personalInfoLogService.log(
                entity,
                LogAction.PATCH,
                "admin"
        );

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }

    @Override
    @Transactional
    public PersonPersonalInfoResponse activate(Integer id) {

        PersonPersonalInfo entity = fetchPersonPersonalInfo(id);

        personalInfoLogService.log(
                entity,
                LogAction.PATCH,
                "admin"
        );

        entity.setStatus(statusHelper.getActive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PersonPersonalInfoResponse deactivate(Integer id) {

        PersonPersonalInfo entity = fetchPersonPersonalInfo(id);

        personalInfoLogService.log(
                entity,
                LogAction.PATCH,
                "admin"
        );

        entity.setStatus(statusHelper.getInactive());

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    private PersonPersonalInfo fetchPersonPersonalInfo(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(s -> {
                                throw new DeletedResourceException(
                                        "Person Personal Info is deleted."
                                );
                            });
                    throw new ResourceNotFoundException(
                            "Person Personal Info not found.");
                });
    }
}
