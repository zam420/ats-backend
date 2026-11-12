package com.deepconnect.ats.tenant.dto;

import lombok.Data;

import java.util.List;

import com.deepconnect.ats.tenant.model.Tenant;

@Data
public class TenantRes {
    private String id;
    private String name;
    private String description;
    private String ownerId;
    
    public static TenantRes from(Tenant entity) {
        TenantRes res = new TenantRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setDescription(entity.getDescription());
        res.setOwnerId(entity.getOwnerPublicId());
        return res;
    }

    public static List<TenantRes> fromList(List<Tenant> entities) {
        return entities.stream().map(TenantRes::from).toList();
    }
}
