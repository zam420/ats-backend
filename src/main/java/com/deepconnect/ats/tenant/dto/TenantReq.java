package com.deepconnect.ats.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class TenantReq {

    private String id;

    @Length(max = 100, message = "{field.common.name}{validation.lengthMax}")
    @NotBlank(message = "{field.common.name}{validation.notBlank}")
    private String name;

    @Length(max = 300, message = "{field.common.description}{validation.lengthMax}")
    private String description;

}
