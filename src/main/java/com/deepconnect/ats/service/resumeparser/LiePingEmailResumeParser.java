package com.deepconnect.ats.service.resumeparser;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.model.CandidateEducationHistory;
import com.deepconnect.ats.model.CandidateProjectExperience;
import com.deepconnect.ats.model.CandidateWorkExperience;
import com.deepconnect.ats.model.JobPosition;
import com.deepconnect.ats.repository.JobPositionRepository;
import com.deepconnect.ats.recruitmentproject.repo.RecruitmentProjectRepository;
import com.deepconnect.ats.service.mail.ResumeEmail;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LiePingEmailResumeParser extends AbstractResumeParser {
    private final String name, phone, gender, email, age, employment, selfAssessment, salary, expectedLocation, appliedPosition, notes;
    private final String workSection, projectSection, educationSection, assessmentSection;
    private final String workInterval, workCompany, workTitle, workAchievement;
    private final String projectInterval, projectName, projectRole, projectCompany, projectDesc, projectResponsibility, projectAchievement;
    private final String educationInterval, educationUniversity, educationMajor, educationDegree;

    public LiePingEmailResumeParser(JobPositionRepository jobPositions, RecruitmentProjectRepository projects,
        @Value("${ats.xpath.lie-ping.base-info.name}") String name,
        @Value("${ats.xpath.lie-ping.base-info.phone-number}") String phone,
        @Value("${ats.xpath.lie-ping.base-info.gender}") String gender,
        @Value("${ats.xpath.lie-ping.base-info.email}") String email,
        @Value("${ats.xpath.lie-ping.base-info.age}") String age,
        @Value("${ats.xpath.lie-ping.base-info.employment-status}") String employment,
        @Value("${ats.xpath.lie-ping.base-info.self-assessment}") String selfAssessment,
        @Value("${ats.xpath.lie-ping.base-info.expected-salary}") String salary,
        @Value("${ats.xpath.lie-ping.base-info.expected-work-locations}") String expectedLocation,
        @Value("${ats.xpath.lie-ping.base-info.applied-position}") String appliedPosition,
        @Value("${ats.xpath.lie-ping.base-info.notes}") String notes,
        @Value("${ats.xpath.lie-ping.key-node.work-experience-tr}") String workSection,
        @Value("${ats.xpath.lie-ping.key-node.project-experience-tr}") String projectSection,
        @Value("${ats.xpath.lie-ping.key-node.edu-experience-tr}") String educationSection,
        @Value("${ats.xpath.lie-ping.key-node.self-assessment-tr}") String assessmentSection,
        @Value("${ats.xpath.lie-ping.work-experience.time-interval}") String workInterval,
        @Value("${ats.xpath.lie-ping.work-experience.company}") String workCompany,
        @Value("${ats.xpath.lie-ping.work-experience.title}") String workTitle,
        @Value("${ats.xpath.lie-ping.work-experience.major-achievement}") String workAchievement,
        @Value("${ats.xpath.lie-ping.project-experience.time-interval}") String projectInterval,
        @Value("${ats.xpath.lie-ping.project-experience.project-name}") String projectName,
        @Value("${ats.xpath.lie-ping.project-experience.project-role}") String projectRole,
        @Value("${ats.xpath.lie-ping.project-experience.belongs-company}") String projectCompany,
        @Value("${ats.xpath.lie-ping.project-experience.project-desc}") String projectDesc,
        @Value("${ats.xpath.lie-ping.project-experience.project-responsibility}") String projectResponsibility,
        @Value("${ats.xpath.lie-ping.project-experience.major-achievement}") String projectAchievement,
        @Value("${ats.xpath.lie-ping.edu-experience.time-interval}") String educationInterval,
        @Value("${ats.xpath.lie-ping.edu-experience.university}") String educationUniversity,
        @Value("${ats.xpath.lie-ping.edu-experience.major}") String educationMajor,
        @Value("${ats.xpath.lie-ping.edu-experience.degree}") String educationDegree) {
        super(jobPositions, projects);
        this.name=name; this.phone=phone; this.gender=gender; this.email=email; this.age=age; this.employment=employment;
        this.selfAssessment=selfAssessment; this.salary=salary; this.expectedLocation=expectedLocation; this.appliedPosition=appliedPosition; this.notes=notes;
        this.workSection=workSection; this.projectSection=projectSection; this.educationSection=educationSection; this.assessmentSection=assessmentSection;
        this.workInterval=workInterval; this.workCompany=workCompany; this.workTitle=workTitle; this.workAchievement=workAchievement;
        this.projectInterval=projectInterval; this.projectName=projectName; this.projectRole=projectRole; this.projectCompany=projectCompany;
        this.projectDesc=projectDesc; this.projectResponsibility=projectResponsibility; this.projectAchievement=projectAchievement;
        this.educationInterval=educationInterval; this.educationUniversity=educationUniversity; this.educationMajor=educationMajor; this.educationDegree=educationDegree;
    }

    public List<com.deepconnect.ats.dto.CandidateResumeSaveReq> parse(ResumeEmail mail) {
        Document doc = html(mail);
        String pos = first(doc, appliedPosition);
        Matcher matcher = Pattern.compile("【(.*?)】").matcher(pos == null ? "" : pos);
        if (!matcher.find()) throw new InternalServerException("Not found: applying position");
        String title = matcher.group(1).trim();
        int suffix = title.lastIndexOf('_');
        if (suffix >= 0) title = title.substring(0, suffix).trim();
        List<JobPosition> jobs = matchingJobs(title, mail.mailbox());

        String assessment = first(doc, selfAssessment);
        List<CandidateWorkExperience> work = parseWork(rowsBetween(doc, workSection, projectSection, educationSection, assessmentSection));
        List<CandidateProjectExperience> projects = parseProjects(rowsBetween(doc, projectSection, educationSection, assessmentSection));
        List<CandidateEducationHistory> education = parseEducation(rowsBetween(doc, educationSection, assessmentSection));
        String emp = first(doc, employment);
        String[] status = {"在职".equals(emp) || (emp != null && emp.contains("在职")) ? "0" : emp != null && emp.contains("离职") ? "1" : null};
        List<com.deepconnect.ats.dto.CandidateResumeSaveReq> result = new ArrayList<>();
        for (JobPosition job : jobs) {
            var req = form(mail, first(doc,name), first(doc,gender), first(doc,age), discardDesensitized(first(doc,phone)), null,
                    discardDesensitized(first(doc,email)), first(doc,expectedLocation), first(doc,salary), assessment, first(doc,notes), title, 0, 17);
            req.getCandidateResume().setApplyingPositionId(job.getId());
            req.getCandidateResume().setEmploymentStatus(integer(status[0]));
            req.setWorkExperiences(work.stream().map(this::copy).toList()); req.setProjectExperiences(projects.stream().map(this::copy).toList()); req.setEducationHistories(education.stream().map(this::copy).toList());
            result.add(req);
        }
        return result;
    }

    private List<Element> rowsBetween(Document doc, String startPath, String... endPaths) {
        List<Element> rows = elements(doc, "//tr");
        List<Element> startNodes = elements(doc, startPath);
        if (startNodes.isEmpty()) return List.of();
        int start = rows.indexOf(startNodes.get(0));
        if (start < 0) return List.of();
        int end = rows.size();
        for (String path : endPaths) {
            List<Element> boundary = elements(doc, path);
            if (!boundary.isEmpty()) { int idx = rows.indexOf(boundary.get(0)); if (idx > start && idx < end) end = idx; }
        }
        if (end == rows.size()) end = rows.size();
        return rows.subList(Math.min(start + 1, end), end);
    }

    private List<CandidateWorkExperience> parseWork(List<Element> rows) {
        List<CandidateWorkExperience> out = new ArrayList<>();
        for (Element row : rows) {
            String company=first(row,workCompany), title=first(row,workTitle), achievement=first(row,workAchievement);
            if (empty(company) && empty(title) && empty(achievement)) continue;
            CandidateWorkExperience e = new CandidateWorkExperience();
            dateRange(first(row,workInterval), e::setDateFrom, e::setDateTo, e::setUpToNow);
            e.setCompanyName(clean(company)); e.setTitle(clean(title)); e.setMajorAchievement(clean(achievement)); e.setDataStatus(1); out.add(e);
        }
        return out;
    }
    private List<CandidateProjectExperience> parseProjects(List<Element> rows) {
        List<CandidateProjectExperience> out=new ArrayList<>();
        for(Element row:rows){ String n=first(row,projectName), role=first(row,projectRole), desc=first(row,projectDesc);
            if(empty(n)&&empty(role)&&empty(desc)) continue;
            CandidateProjectExperience e=new CandidateProjectExperience();
            dateRange(first(row,projectInterval),e::setDateFrom,e::setDateTo,e::setUpToNow);
            e.setProjectName(clean(n)); e.setProjectRole(stripLabel(role,"- 项目职务："));
            String company=first(row,projectCompany); String responsibility=first(row,projectResponsibility); String achievement=first(row,projectAchievement);
            e.setProjectDesc(clean(desc));
            e.setMajorAchievement(joinNonblank(responsibility == null ? null : "项目职责：\n"+responsibility, achievement == null ? null : "项目业绩：\n"+achievement)); e.setDataStatus(1); out.add(e);
        } return out;
    }
    private List<CandidateEducationHistory> parseEducation(List<Element> rows) {
        List<CandidateEducationHistory> out=new ArrayList<>();
        for(Element row:rows){ String uni=first(row,educationUniversity), major=first(row,educationMajor), deg=first(row,educationDegree);
            if(empty(uni)&&empty(major)&&empty(deg)) continue;
            CandidateEducationHistory e=new CandidateEducationHistory(); dateRange(first(row,educationInterval),e::setDateFrom,e::setDateTo,e::setUpToNow);
            e.setUniversity(clean(uni)); e.setMajor(stripLabel(major,"- 专业：")); e.setDegree(degree(deg)); e.setDataStatus(1); out.add(e);
        } return out;
    }
    private void dateRange(String value, java.util.function.Consumer<java.time.LocalDate> from, java.util.function.Consumer<java.time.LocalDate> to, java.util.function.Consumer<Boolean> current) {
        String[] parts=interval(value); from.accept(date(parts[0])); boolean now=current(value) || current(parts.length>1?parts[1]:""); current.accept(now); to.accept(now||parts.length<2?null:date(parts[1]));
    }
    private boolean empty(String x){return x==null||x.isBlank();}
    private String discardDesensitized(String value){return value!=null&&value.trim().equals("******")?null:clean(value);}
    private String stripLabel(String value,String label){return value==null?null:value.replace(label,"").trim();}
    private String joinNonblank(String... values){StringBuilder b=new StringBuilder(); for(String v:values) if(v!=null&&!v.isBlank()){if(b.length()>0)b.append('\n');b.append(v);}return b.length()==0?null:b.toString();}
}
