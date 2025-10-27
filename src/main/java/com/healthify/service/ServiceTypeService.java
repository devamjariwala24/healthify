package com.healthify.service;

import com.healthify.dto.ServiceTypeDTO;
import com.healthify.entity.ServiceType;
import com.healthify.exception.ResourceNotFoundException;
import com.healthify.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    // CREATE
    public ServiceTypeDTO createServiceType(ServiceTypeDTO serviceDTO) {
        ServiceType service = convertToEntity(serviceDTO);
        ServiceType savedServiceType = serviceTypeRepository.save(service);
        return convertToDTO(savedServiceType);
    }

    // READ ALL
    public List<ServiceTypeDTO> getAllServiceTypes() {
        return serviceTypeRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ ONE
    public ServiceTypeDTO getServiceTypeById(Long id) {
        ServiceType service = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service Type not found with id: " + id
                ));
        return convertToDTO(service);
    }

    // UPDATE
    public ServiceTypeDTO updateServiceType(Long id, ServiceTypeDTO serviceTypeDTO) {
        ServiceType existingService = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service Type not found with id: " + id
                ));

        existingService.setServiceName(serviceTypeDTO.getServiceName());
        existingService.setDescription(serviceTypeDTO.getDescription());
        existingService.setCategory(serviceTypeDTO.getCategory());

        ServiceType updatedService = serviceTypeRepository.save(existingService);
        return convertToDTO(updatedService);
    }

    // DELETE
    public void deleteServiceType(Long id) {
        if (!serviceTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Service Type not found with id: " + id
            );
        }
        serviceTypeRepository.deleteById(id);
    }

    // Helper: Convert Entity to DTO
    private ServiceTypeDTO convertToDTO(ServiceType serviceType) {
        ServiceTypeDTO dto = new ServiceTypeDTO();
        dto.setId(serviceType.getId());
        dto.setServiceName(serviceType.getServiceName());
        dto.setCategory(serviceType.getCategory());
        dto.setDescription(serviceType.getDescription());
        return dto;
    }

    // Helper: Convert DTO to Entity
    private ServiceType convertToEntity(ServiceTypeDTO dto) {
        ServiceType service = new ServiceType();
        service.setId(dto.getId());
        service.setServiceName(dto.getServiceName());
        service.setCategory(dto.getCategory());
        service.setDescription(dto.getDescription());
        return service;
    }
}