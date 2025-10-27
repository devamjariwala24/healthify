package com.healthify.controller;

import com.healthify.dto.ServiceTypeDTO;
import com.healthify.service.ServiceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-types")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;


    // CREATE: POST /api/service-type
    @PostMapping
    public ResponseEntity<ServiceTypeDTO> createServiceType(@RequestBody ServiceTypeDTO serviceTypeDTO) {
        ServiceTypeDTO created = serviceTypeService.createServiceType(serviceTypeDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<ServiceTypeDTO>> getAllServiceTypes() {
        List<ServiceTypeDTO> serviceType = serviceTypeService.getAllServiceTypes();
        return ResponseEntity.ok(serviceType);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> getServiceTypeById(@PathVariable Long id) {
        ServiceTypeDTO serviceType = serviceTypeService.getServiceTypeById(id);
        return ResponseEntity.ok(serviceType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> updateServiceType(
            @PathVariable Long id,
            @RequestBody ServiceTypeDTO serviceDTO) {
        ServiceTypeDTO updated = serviceTypeService.updateServiceType(id, serviceDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceType(@PathVariable Long id) {
        serviceTypeService.deleteServiceType(id);
        return ResponseEntity.noContent().build();
    }

}
