package com.healthify.entity;

import com.healthify.enums.ServiceCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String serviceName;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ServiceCategory category;
}
