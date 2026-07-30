package az.ingress.hrms.helper;

import az.ingress.hrms.entity.lookup.Status;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusHelper {

    private final StatusRepository repository;

    public Status getActive() {
        return repository.findByCode("ACTIVE")
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ACTIVE status not found."
                        ));
    }

    public Status getInactive() {
        return repository.findByCode("INACTIVE")
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "INACTIVE status not found."
                        ));
    }
}