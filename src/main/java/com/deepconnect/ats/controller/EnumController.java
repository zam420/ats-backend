package com.deepconnect.ats.controller;

import com.deepconnect.ats.common.constant.enums.*;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.EnumsRsp;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enum")
public class EnumController {

    private <T extends BaseEnum> List<EnumsRsp> convert(T[] values) {
        return Arrays.stream(values)
                .map(EnumsRsp::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/listCurrency")
    public ApiResult<List<EnumsRsp>> listCurrency() {
        return ApiResult.success(convert(Currency.values()));
    }

    @GetMapping("/listDegree")
    public ApiResult<List<EnumsRsp>> listDegree() {
        return ApiResult.success(convert(Degree.values()));
    }

    @GetMapping("/listDepartment")
    public ApiResult<List<EnumsRsp>> listDepartment() {
        return ApiResult.success(convert(Department.values()));
    }

    @GetMapping("/listDirectSupervisorComment")
    public ApiResult<List<EnumsRsp>> listDirectSupervisorComment() {
        return ApiResult.success(convert(DirectSupervisorComment.values()));
    }

    @GetMapping("/listEnglishProficiency")
    public ApiResult<List<EnumsRsp>> listEnglishProficiency() {
        return ApiResult.success(convert(EnglishProficiency.values()));
    }

    @GetMapping("/listGender")
    public ApiResult<List<EnumsRsp>> listGender() {
        return ApiResult.success(convert(Gender.values()));
    }

    @GetMapping("/listGrade")
    public ApiResult<List<EnumsRsp>> listGrade() {
        return ApiResult.success(convert(Grade.values()));
    }

    @GetMapping("/listInterviewEvaluation")
    public ApiResult<List<EnumsRsp>> listInterviewEvaluation() {
        return ApiResult.success(convert(InterviewEvaluation.values()));
    }

    @GetMapping("/listInterviewMethod")
    public ApiResult<List<EnumsRsp>> listInterviewMethod() {
        return ApiResult.success(convert(InterviewMethod.values()));
    }

    @GetMapping("/listJobPositionLevel")
    public ApiResult<List<EnumsRsp>> listJobPositionLevel() {
        return ApiResult.success(convert(JobPositionLevel.values()));
    }

    @GetMapping("/listMaritalStatus")
    public ApiResult<List<EnumsRsp>> listMaritalStatus() {
        return ApiResult.success(convert(MaritalStatus.values()));
    }

    @GetMapping("/listPoliticalStatus")
    public ApiResult<List<EnumsRsp>> listPoliticalStatus() {
        return ApiResult.success(convert(PoliticalStatus.values()));
    }

    @GetMapping("/listRecruitmentProjectCategory")
    public ApiResult<List<EnumsRsp>> listRecruitmentProjectCategory() {
        return ApiResult.success(convert(RecruitmentProjectCategory.values()));
    }

    @GetMapping("/listWorkExperience")
    public ApiResult<List<EnumsRsp>> listWorkExperience() {
        return ApiResult.success(convert(WorkExperience.values()));
    }
}
