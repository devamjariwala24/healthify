package com.healthify.service;

import com.healthify.dto.InsurancePlanDTO;
import com.healthify.entity.InsurancePlan;
import com.healthify.exception.ResourceNotFoundException;
import com.healthify.repository.InsurancePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InsurancePlanService {

    private final InsurancePlanRepository planRepository;

    public InsurancePlanDTO createPlan(InsurancePlanDTO planDTO) {
        InsurancePlan plan = convertToEntity(planDTO);
        InsurancePlan savedPlan = planRepository.save(plan);
        return convertToDTO(savedPlan);
    }

    public List<InsurancePlanDTO> getAllPlans() {
        return planRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InsurancePlanDTO getPlanById(Long id) {
        InsurancePlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Insurance Plan not found with id: " + id
                ));
        return convertToDTO(plan);
    }

    public InsurancePlanDTO updatePlan(Long id, InsurancePlanDTO planDTO) {
        InsurancePlan existingPlan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Insurance Plan not found with id: " + id
                ));

        existingPlan.setPlanName(planDTO.getPlanName());
        existingPlan.setDeductible(planDTO.getDeductible());
        existingPlan.setMonthlyPremium(planDTO.getMonthlyPremium());
        existingPlan.setOutOfPocketMax(planDTO.getOutOfPocketMax());

        InsurancePlan updatedPlan = planRepository.save(existingPlan);
        return convertToDTO(updatedPlan);
    }

    public void deletePlan(Long id) {
        if (!planRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Insurance Plan not found with id: " + id
            );
        }
        planRepository.deleteById(id);
    }

    // Helper: Convert Entity to DTO
    private InsurancePlanDTO convertToDTO(InsurancePlan plan) {
        InsurancePlanDTO dto = new InsurancePlanDTO();
        dto.setId(plan.getId());
        dto.setPlanName(plan.getPlanName());
        dto.setMonthlyPremium(plan.getMonthlyPremium());
        dto.setDeductible(plan.getDeductible());
        dto.setOutOfPocketMax(plan.getOutOfPocketMax());
        return dto;
    }

    // Helper: Convert DTO to Entity
    private InsurancePlan convertToEntity(InsurancePlanDTO dto) {
        InsurancePlan plan = new InsurancePlan();
        plan.setId(dto.getId());
        plan.setPlanName(dto.getPlanName());
        plan.setMonthlyPremium(dto.getMonthlyPremium());
        plan.setDeductible(dto.getDeductible());
        plan.setOutOfPocketMax(dto.getOutOfPocketMax());
        return plan;
    }


}