package az.ingress.hrms.controller;


import az.ingress.hrms.service.auth.ContactTypeService;
import az.ingress.hrms.dto.contactType.ContactTypeRequest;
import az.ingress.hrms.dto.contactType.ContactTypeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/contact-type")
public class ContactTypeController {

    private final ContactTypeService service;


    @PostMapping
    public ContactTypeResponse create(
            @Valid @RequestBody ContactTypeRequest request
    ) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ContactTypeResponse update(
            @PathVariable Integer id,
            @Valid @RequestBody ContactTypeRequest request) {

        return service.update(id, request);
    }


    @GetMapping("/{id}")
    public ContactTypeResponse getById(
            @PathVariable Integer id
    ) {
        return service.getById(id);
    }


    @GetMapping
    public List<ContactTypeResponse> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Integer id) {
        service.softDelete(id);
    }

    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public void restore(@PathVariable Integer id) {
        service.restore(id);
    }

}
