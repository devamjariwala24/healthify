package com.healthify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePlanDTO {

    private Long id;

    private String planName;

    private BigDecimal monthlyPremium;

    private BigDecimal deductible;

    private BigDecimal outOfPocketMax;

}