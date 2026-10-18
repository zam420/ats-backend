package com.deepconnect.ats.dto;

import com.deepconnect.ats.model.Interview;
import com.deepconnect.ats.model.InterviewCandidate;
import com.deepconnect.ats.model.InterviewRound;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewDetailRsp {
    private Interview interview;

    @Builder.Default
    private List<InterviewRound> rounds = new ArrayList<>();

    @Builder.Default
    private List<InterviewCandidate> candidates = new ArrayList<>();
}
