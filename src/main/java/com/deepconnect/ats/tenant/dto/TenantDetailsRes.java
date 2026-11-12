package com.deepconnect.ats.tenant.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.tenant.model.Tenant;

@Data
public class TenantDetailsRes {
    private String id;
    private String name;
    private String description;
    private String ownerId;
    
    public static TenantDetailsRes from(Tenant entity) {
        TenantDetailsRes res = new TenantDetailsRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<TenantDetailsRes> fromList(List<Tenant> entities) {
        return entities.stream().map(TenantDetailsRes::from).toList();
    }
}
