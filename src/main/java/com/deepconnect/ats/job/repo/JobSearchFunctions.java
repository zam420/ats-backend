package com.deepconnect.ats.job.repo;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

public class JobSearchFunctions implements FunctionContributor {
    @Override
    public void contributeFunctions(FunctionContributions contributions) {
        var types = contributions.getTypeConfiguration().getBasicTypeRegistry();
        contributions.getFunctionRegistry().registerPattern("ats_match_title",
            "match (?1) against (?2 in natural language mode)", types.resolve(StandardBasicTypes.DOUBLE));
        contributions.getFunctionRegistry().registerPattern("ats_json_member",
            "json_contains(?1, json_array(?2))", types.resolve(StandardBasicTypes.INTEGER));
    }
}
