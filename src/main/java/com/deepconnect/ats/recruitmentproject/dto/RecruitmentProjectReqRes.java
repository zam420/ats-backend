package com.deepconnect.ats.recruitmentproject.dto;
import java.util.List;
import com.deepconnect.ats.recruitmentproject.model.RecruitmentProject;
import lombok.Data;

@Data
public class RecruitmentProjectReqRes {
    
    private String id;
    private String name;
    private Integer status;
    private List<String> projectOwners;

    public static RecruitmentProjectReqRes from(RecruitmentProject entity) {
        RecruitmentProjectReqRes res = new RecruitmentProjectReqRes();
        res.setId(entity.getPublicId());
        res.setName(entity.getName());
        res.setStatus(entity.getStatus());
        res.setProjectOwners(entity.getProjectOwners());
        return res;
    }

    public static List<RecruitmentProjectReqRes> fromList(List<RecruitmentProject> entities) {
        return entities.stream().map(RecruitmentProjectReqRes::from).toList();
    }
}
