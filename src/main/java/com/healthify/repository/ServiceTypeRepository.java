package com.healthify.repository;

import com.healthify.entity.ServiceType;
import com.healthify.enums.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

    Optional<ServiceType> findByServiceName(String serviceName);

    List<ServiceCategory> findByCategory(ServiceCategory category);

    List<ServiceCategory> findByServiceNameContainingIgnoreCase(String keyword);
}
