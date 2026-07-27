package az.ingress.hrms.controller;

import az.ingress.hrms.service.auth.StatusService;
import az.ingress.hrms.dto.status.StatusRequest;
import az.ingress.hrms.dto.status.StatusResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/status")
public class StatusController {

    private final StatusService service;


    @PostMapping
    public StatusResponse create(
            @Valid @RequestBody StatusRequest request
    ) {
        return service.create(request);
    }


    @PutMapping("/{id}")
    public StatusResponse update(
            @PathVariable Integer id,
            @Valid @RequestBody StatusRequest request) {

        return service.update(id, request);
    }


    @GetMapping("/{id}")
    public StatusResponse getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping
    public List<StatusResponse> getAll() {
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

