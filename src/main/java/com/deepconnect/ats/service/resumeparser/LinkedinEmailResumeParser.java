package com.deepconnect.ats.service.resumeparser;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.CandidateWorkExperience;
import com.deepconnect.ats.model.JobPosition;
import com.deepconnect.ats.repository.JobPositionRepository;
import com.deepconnect.ats.repository.RecruitmentProjectRepository;
import com.deepconnect.ats.service.mail.ResumeEmail;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LinkedinEmailResumeParser extends AbstractResumeParser {
    private final String residence, skills, current, past, questionRows, qaRows, question, answer;
    public LinkedinEmailResumeParser(JobPositionRepository jobPositions, RecruitmentProjectRepository projects,
        @Value("${ats.xpath.linkedin.current-residence-path}") String residence,
        @Value("${ats.xpath.linkedin.screening-questions-skills-path}") String skills,
        @Value("${ats.xpath.linkedin.work-experiences.current-experience}") String current,
        @Value("${ats.xpath.linkedin.work-experiences.past-experience}") String past,
        @Value("${ats.xpath.linkedin.screening-qualifications.root-path}") String questionRows,
        @Value("${ats.xpath.linkedin.screening-qualifications.qa-path}") String qaRows,
        @Value("${ats.xpath.linkedin.screening-qualifications.question-path}") String question,
        @Value("${ats.xpath.linkedin.screening-qualifications.answer-path}") String answer) {
        super(jobPositions, projects); this.residence=residence; this.skills=skills; this.current=current; this.past=past;
        this.questionRows=questionRows; this.qaRows=qaRows; this.question=question; this.answer=answer;
    }

    public List<com.deepconnect.ats.dto.CandidateResumeSaveReq> parse(ResumeEmail mail) {
        String[] subject=(mail.subject()==null?"":mail.subject()).trim().split("\\s+from\\s+",2);
        if(subject.length<2) throw new InternalServerException("Invalid LinkedIn application subject: "+mail.subject());
        String title=subject[0].replace("New application:","").trim();
        String candidate=subject[1].trim();
        List<JobPosition> jobs=matchingJobs(title,mail.mailbox());
        Document doc=html(mail);
        List<CandidateWorkExperience> work=parseWork(doc);
        List<Map<String,Object>> skillList=new ArrayList<>();
        for(String skill:xpath(doc,skills)) if(!skill.isBlank()) skillList.add(Map.of("skillName",skill.trim()));
        List<Map<String,Object>> qa=parseQa(doc);
        List<com.deepconnect.ats.dto.CandidateResumeSaveReq> result=new ArrayList<>();
        for(JobPosition job:jobs){
            var req=form(mail,candidate,null,null,null,first(doc,residence),null,null,null,null,null,title,5,15);
            CandidateResume resume=req.getCandidateResume(); resume.setApplyingPositionId(job.getId());
            resume.setSkills(skillList); resume.setExtendedQa(qa); req.setWorkExperiences(work.stream().map(this::copy).toList()); result.add(req);
        }
        return result;
    }

    private List<CandidateWorkExperience> parseWork(Document doc) {
        List<CandidateWorkExperience> result=new ArrayList<>();
        List<String> descriptions=new ArrayList<>(xpath(doc,current)); descriptions.addAll(xpath(doc,past));
        Pattern pattern=Pattern.compile("^(.*?)\\s+at\\s+(.*?)·\\s*(.*)$");
        for(String description:descriptions){
            Matcher m=pattern.matcher(description.trim()); if(!m.matches()) continue;
            CandidateWorkExperience experience=new CandidateWorkExperience();
            experience.setTitle(clean(m.group(1))); experience.setCompanyName(clean(m.group(2)));
            String[] range=m.group(3).split("-",2); experience.setDateFrom(range.length>0?date(range[0]):null);
            boolean now=range.length>1&&range[1].toLowerCase(Locale.ROOT).contains("present");
            experience.setUpToNow(now); experience.setDateTo(now||range.length<2?null:date(range[1])); experience.setWorkYears(calculateYears(experience.getDateFrom(), experience.getDateTo())); experience.setDataStatus(1); result.add(experience);
        }
        return result;
    }

    private List<Map<String,Object>> parseQa(Document doc) {
        List<Map<String,Object>> result=new ArrayList<>();
        for(org.jsoup.nodes.Element row:elements(doc,questionRows)) {
            List<org.jsoup.nodes.Element> pairs=elements(row,qaRows); if(pairs.size()<2) continue;
            String q=first(pairs.get(0),question), a=first(pairs.get(1),answer);
            if(q!=null&&a!=null) result.add(Map.of("question",q,"answer",a));
        }
        return result;
    }
}
