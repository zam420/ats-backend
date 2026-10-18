package com.deepconnect.ats.service.resumeparser;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.CandidateResumeSaveReq;
import com.deepconnect.ats.service.mail.ResumeEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeEmailParserService {
    private final LiePingEmailResumeParser liePingParser;
    private final OfficialEmailResumeParser officialParser;
    private final LinkedinEmailResumeParser linkedinParser;

    public List<CandidateResumeSaveReq> parse(ResumeEmail mail) {
        if (mail.senderName() != null && mail.senderName().contains("猎聘") &&
                mail.subject() != null && mail.subject().contains("来自猎聘的候选人")) return liePingParser.parse(mail);
        if (mail.senderName() != null && mail.senderName().contains("乐鑫")) return officialParser.parse(mail);
        if ("jobs-listings@linkedin.com".equalsIgnoreCase(mail.senderAddress()) &&
                mail.subject() != null && mail.subject().contains("New application")) return linkedinParser.parse(mail);
        throw new InternalServerException("can not found corresponding parser for mail: " + mail.subject() + " from " + mail.senderAddress());
    }
}
