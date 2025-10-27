package com.healthify.controller;

import com.healthify.dto.InsurancePlanDTO;
import com.healthify.service.InsurancePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance-plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InsurancePlanController {

    private final InsurancePlanService planService;

    // CREATE: POST /api/insurance-plans
    @PostMapping
    public ResponseEntity<InsurancePlanDTO> createPlan(@RequestBody InsurancePlanDTO planDTO) {
        InsurancePlanDTO created = planService.createPlan(planDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InsurancePlanDTO>> getAllPlans() {
        List<InsurancePlanDTO> plans = planService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsurancePlanDTO> getPlanById(@PathVariable Long id) {
        InsurancePlanDTO plan = planService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsurancePlanDTO> updatePlan(
            @PathVariable Long id,
            @RequestBody InsurancePlanDTO planDTO) {
        InsurancePlanDTO updated = planService.updatePlan(id, planDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}