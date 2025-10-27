package com.healthify.dto;


import com.healthify.enums.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeDTO {

    private Long id;

    private String serviceName;

    private String description;

    private ServiceCategory category;


}
