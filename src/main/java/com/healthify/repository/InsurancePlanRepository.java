package com.healthify.repository;

import com.healthify.entity.InsurancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, Long> {


    Optional<InsurancePlan> findByPlanName(String planName);

    List<InsurancePlan> findByMonthlyPremiumLessThan(BigDecimal premium);

    List<InsurancePlan>  findByDeductibleBetween (BigDecimal min, BigDecimal max);
}
