package com.healthify.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "insurance_plans")
@Data
public class InsurancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String planName;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal monthlyPremium;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal deductible;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal outOfPocketMax;

}
