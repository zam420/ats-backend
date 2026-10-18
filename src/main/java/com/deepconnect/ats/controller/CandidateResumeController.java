package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.dto.*;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.CandidateResumeForward;
import com.deepconnect.ats.model.ResumeOperationRecord;
import com.deepconnect.ats.service.CandidateResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
public class CandidateResumeController {

    private final CandidateResumeService candidateResumeService;

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/saveCandidateResume")
    public ApiResult<Map<String, Long>> saveCandidateResume(@RequestBody CandidateResumeSaveReq req) {
        Long id = candidateResumeService.saveCandidateResume(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/submitCandidateResume")
    public ApiResult<Map<String, Long>> submitCandidateResume(@RequestBody CandidateResumeSaveReq req) {
        Long id = candidateResumeService.saveCandidateResume(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/quickSaveResume")
    public ApiResult<Boolean> quickSaveResume(@RequestBody QuickSaveResumeReq req) {
        candidateResumeService.quickSaveResume(req);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/deleteCandidateResume")
    public ApiResult<Boolean> deleteCandidateResume(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        if (id != null) {
            candidateResumeService.deleteCandidateResume(id);
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/listCandidateResume")
    public ApiResult<AtsPageRsp<CandidateResume>> listCandidateResume(AtsPageQuery query) {
        return ApiResult.success(candidateResumeService.pageCandidateResume(query));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/getCandidateDetailById")
    public ApiResult<CandidateResumeDetailRsp> getCandidateDetailById(@RequestParam Long id) {
        return ApiResult.success(candidateResumeService.getCandidateDetailById(id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/candidateResumePage")
    public ApiResult<AtsPageRsp<CandidateResume>> candidateResumePage(AtsPageQuery query) {
        return ApiResult.success(candidateResumeService.pageCandidateResume(query));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/moveTo")
    public ApiResult<Boolean> moveTo(@RequestBody MoveToReq req) {
        candidateResumeService.moveTo(req);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/forward")
    public ApiResult<Boolean> forward(@RequestBody ForwardResumeReq req) {
        candidateResumeService.forward(req);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @PostMapping("/feedbackForward")
    public ApiResult<Boolean> feedbackForward(@RequestBody FeedbackForwardReq req) {
        candidateResumeService.feedbackForward(req);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/listForward")
    public ApiResult<List<CandidateResumeForward>>     listForward(
                @RequestParam(required = false) Long resumeId,
                @RequestParam(required = false) Integer status,
                @RequestParam(name = "forward_type", required = false) Integer forwardType) {
            return ApiResult.success(candidateResumeService.listForward(resumeId, status, forwardType));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/statusCount")
    public ApiResult<Map<String, Long>> statusCount() {
        return ApiResult.success(candidateResumeService.statusCount());
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/commentResume")
    public ApiResult<Map<String, Long>> commentResume(@RequestBody ResumeCommentReq req) {
        Long id = candidateResumeService.commentResume(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/commentResumeInBulk")
    public ApiResult<Boolean> commentResumeInBulk(@RequestBody ResumeCommentReq req) {
        candidateResumeService.commentResumeInBulk(req);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/deleteComment")
    public ApiResult<Boolean> deleteComment(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        if (id != null) {
            candidateResumeService.deleteComment(id);
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/createOffer")
    public ApiResult<Boolean> createOffer(@RequestBody OfferReq req) {
        candidateResumeService.createOffer(req);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/editOffer")
    public ApiResult<Boolean> editOffer(@RequestBody OfferReq req) {
        candidateResumeService.editOffer(req);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @PostMapping("/candidateResumeViewRecord")
    public ApiResult<Boolean> candidateResumeViewRecord(@RequestBody Map<String, Long> body) {
        Long resumeId = body.get("resumeId");
        if (resumeId == null) {
            resumeId = body.get("candidateResumeId");
        }
        if (resumeId != null) {
            candidateResumeService.recordView(resumeId);
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/listHistoricalOperations")
    public ApiResult<List<ResumeOperationRecord>>     listHistoricalOperations(
                @RequestParam(name = "candidate_resume_id", required = false) Long candidateResumeId,
                @RequestParam(required = false) Long resumeId) {
            Long id = candidateResumeId != null ? candidateResumeId : resumeId;
            if (id == null) {
                return ApiResult.success(new java.util.ArrayList<>());
            }
            return ApiResult.success(candidateResumeService.listHistoricalOperations(id));
    }
}
