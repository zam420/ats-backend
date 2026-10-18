package com.deepconnect.ats.service.resumeparser;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.CandidateResumeSaveReq;
import com.deepconnect.ats.model.CandidateEducationHistory;
import com.deepconnect.ats.model.CandidateProjectExperience;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.CandidateWorkExperience;
import com.deepconnect.ats.model.JobPosition;
import com.deepconnect.ats.model.RecruitmentProject;
import com.deepconnect.ats.repository.RecruitmentProjectRepository;
import com.deepconnect.ats.repository.JobPositionRepository;
import com.deepconnect.ats.service.mail.ResumeEmail;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class AbstractResumeParser {
    protected final JobPositionRepository jobPositions;
    protected final RecruitmentProjectRepository projects;
    protected AbstractResumeParser(JobPositionRepository jobPositions, RecruitmentProjectRepository projects) { this.jobPositions = jobPositions; this.projects = projects; }

    protected Document html(ResumeEmail email) {
        return Parser.htmlParser().parseInput(email.body() == null ? "" : email.body(), "");
    }

    protected List<Element> elements(Element node, String expression) {
        if (expression == null || expression.isBlank()) return List.of();
        try { return node.selectXpath(expression); }
        catch (RuntimeException ignored) { return List.of(); }
    }

    protected List<String> xpath(Document doc, String expression) {
        if (expression == null || expression.isBlank()) return List.of();
        try {
            List<Node> nodes = doc.selectXpath(expression, Node.class);
            List<String> result = new ArrayList<>();
            for (Node node : nodes) {
                String value = node instanceof TextNode text ? text.getWholeText() :
                        node instanceof Element element ? element.text() : node.toString();
                if (value != null) result.add(value.trim());
            }
            return result;
        } catch (RuntimeException ignored) { return List.of(); }
    }

    protected List<Element> elements(Document doc, String expression) {
        if (expression == null || expression.isBlank()) return List.of();
        try { return doc.selectXpath(expression); }
        catch (RuntimeException ignored) { return List.of(); }
    }

    protected String first(Document doc, String path) {
        List<String> values = xpath(doc, path);
        return values.isEmpty() ? null : String.join("", values).trim();
    }

    protected String first(Element node, String path) {
        List<String> values = xpath(node, path);
        return values.isEmpty() ? null : String.join("", values).trim();
    }

    protected List<String> xpath(Element node, String expression) {
        if (expression == null || expression.isBlank()) return List.of();
        try {
            List<Node> nodes = node.selectXpath(expression, Node.class);
            List<String> result = new ArrayList<>();
            for (Node n : nodes) result.add((n instanceof TextNode t ? t.getWholeText() : n instanceof Element e ? e.text() : n.toString()).trim());
            return result;
        } catch (RuntimeException ignored) { return List.of(); }
    }

    protected List<JobPosition> matchingJobs(String title, String mailbox) {
        if (title == null || title.isBlank()) return List.of();
        List<JobPosition> found = jobPositions.findAll().stream()
                .filter(p -> p.getStatus() == null || p.getStatus() != 2)
                .filter(p -> projects.findById(p.getProjectId()).map(RecruitmentProject::getStatus).orElse(1) == 0)
                .filter(p -> mailbox.equalsIgnoreCase(p.getResumeInbox()))
                .filter(p -> title.trim().equalsIgnoreCase(p.getTitle().trim())).toList();
        if (found.isEmpty()) throw new InternalServerException("Not found: applying position");
        return found;
    }

    protected CandidateResumeSaveReq form(ResumeEmail email, String name, String gender, String age,
                                           String phone, String address, String emailAddress, String expectedLocation,
                                           String salary, String assessment, String notes, String subjectTitle,
                                           Integer source, Integer sourceInfo) {
        CandidateResume resume = new CandidateResume();
        resume.setName(clean(name));
        resume.setGender(gender != null && gender.contains("女") ? 1 : gender != null && gender.contains("男") ? 0 : null);
        resume.setAge(integer(age));
        resume.setPhoneNumber(clean(phone));
        resume.setEmail(clean(emailAddress));
        resume.setCurrentPlace(clean(address));
        resume.setExpectedWorkLocation(clean(expectedLocation));
        resume.setExpectedSalary(clean(salary));
        resume.setSelfAssessment(clean(assessment));
        resume.setNotes(clean(notes));
        resume.setAppliedAt(email.sentAt() == null ? LocalDateTimeNow.now() : email.sentAt().atZoneSameInstant(ZoneId.of("Asia/Shanghai")).toLocalDateTime());
        resume.setDataStatus(1);
        resume.setRecruitmentSource(source);
        resume.setRecruitmentInfoFrom(sourceInfo);
        return saveReq(resume, List.of(), List.of(), List.of());
    }

    protected CandidateResumeSaveReq assignPosition(CandidateResumeSaveReq base, JobPosition position) {
        base.getCandidateResume().setApplyingPositionId(position.getId());
        return base;
    }

    protected CandidateWorkExperience copy(CandidateWorkExperience x) { CandidateWorkExperience y=new CandidateWorkExperience(); y.setDateFrom(x.getDateFrom()); y.setDateTo(x.getDateTo()); y.setUpToNow(x.getUpToNow()); y.setCompanyName(x.getCompanyName()); y.setTitle(x.getTitle()); y.setMajorAchievement(x.getMajorAchievement()); y.setWorkYears(x.getWorkYears()); y.setDataStatus(x.getDataStatus()); return y; }
    protected CandidateEducationHistory copy(CandidateEducationHistory x) { CandidateEducationHistory y=new CandidateEducationHistory(); y.setDateFrom(x.getDateFrom()); y.setDateTo(x.getDateTo()); y.setUpToNow(x.getUpToNow()); y.setUniversity(x.getUniversity()); y.setSchool(x.getSchool()); y.setLabName(x.getLabName()); y.setDegree(x.getDegree()); y.setMajor(x.getMajor()); y.setPerformanceRank(x.getPerformanceRank()); y.setDataStatus(x.getDataStatus()); return y; }
    protected CandidateProjectExperience copy(CandidateProjectExperience x) { CandidateProjectExperience y=new CandidateProjectExperience(); y.setDateFrom(x.getDateFrom()); y.setDateTo(x.getDateTo()); y.setUpToNow(x.getUpToNow()); y.setProjectName(x.getProjectName()); y.setProjectRole(x.getProjectRole()); y.setProjectDesc(x.getProjectDesc()); y.setMajorAchievement(x.getMajorAchievement()); y.setDataStatus(x.getDataStatus()); return y; }

    protected CandidateResumeSaveReq saveReq(CandidateResume resume, List<CandidateWorkExperience> work,
                                              List<CandidateEducationHistory> education,
                                              List<CandidateProjectExperience> projects) {
        CandidateResumeSaveReq req = new CandidateResumeSaveReq();
        req.setCandidateResume(resume);
        req.setWorkExperiences(work);
        req.setEducationHistories(education);
        req.setProjectExperiences(projects);
        return req;
    }

    protected String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    protected Integer integer(String value) {
        if (value == null) return null;
        Matcher m = Pattern.compile("\\d+").matcher(value);
        return m.find() ? Integer.valueOf(m.group()) : null;
    }
    protected LocalDate date(String value) {
        if (value == null || value.isBlank()) return null;
        String v = value.trim().replace('/', '.').replace('-', '.');
        Matcher m = Pattern.compile("(\\d{4})[.]?(\\d{1,2})?(?:[.]?(\\d{1,2}))?").matcher(v);
        if (m.find()) {
            try { return LocalDate.of(Integer.parseInt(m.group(1)), m.group(2) == null ? 1 : Integer.parseInt(m.group(2)), m.group(3) == null ? 1 : Integer.parseInt(m.group(3))); }
            catch (RuntimeException ignored) { return null; }
        }
        try { return LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH).withLocale(Locale.ENGLISH)); }
        catch (DateTimeParseException ignored) { return null; }
    }
    protected Integer calculateYears(LocalDate from, LocalDate to) { if (from == null || to == null) return null; if (to.getYear() == from.getYear()) return to.getMonthValue() - from.getMonthValue() >= 6 ? 1 : 0; int years = to.getYear() - from.getYear() - 1; int monthDiff = 24 - (to.getMonthValue() + from.getMonthValue()); return years + Math.floorDiv(monthDiff, 6); }
    protected boolean current(String value) { return value != null && (value.contains("至今") || value.toLowerCase(Locale.ROOT).contains("present")); }
    protected String[] interval(String value) { return value == null ? new String[]{"", ""} : value.split("[-—至到]", 2); }
    protected Integer degree(String value) {
        if (value == null) return null;
        if (value.contains("中专") || value.toLowerCase(Locale.ROOT).contains("secondary")) return 0;
        if (value.contains("大专") || value.toLowerCase(Locale.ROOT).contains("associate")) return 1;
        if (value.contains("本科") || value.toLowerCase(Locale.ROOT).contains("bachelor")) return 2;
        if (value.contains("硕士") || value.toLowerCase(Locale.ROOT).contains("master")) return 3;
        if (value.contains("博士") || value.toLowerCase(Locale.ROOT).contains("phd") || value.toLowerCase(Locale.ROOT).contains("doctor")) return 4;
        return null;
    }
    private static final class LocalDateTimeNow { static java.time.LocalDateTime now() { return java.time.LocalDateTime.now(); } }
}
