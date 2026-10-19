package com.deepconnect.ats.service.resumeparser;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.model.CandidateEducationHistory;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.repository.JobPositionRepository;
import com.deepconnect.ats.recruitmentproject.repo.RecruitmentProjectRepository;
import com.deepconnect.ats.service.mail.ResumeEmail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OfficialEmailResumeParser extends AbstractResumeParser {
    public OfficialEmailResumeParser(JobPositionRepository jobPositions, RecruitmentProjectRepository projects) { super(jobPositions, projects); }

    public List<com.deepconnect.ats.dto.CandidateResumeSaveReq> parse(ResumeEmail mail) {
        String text = (mail.body() == null ? "" : mail.body()).replaceAll("(?i)<br\\s*/?>|</p>|</tr>|</div>", "\n");
        text = org.jsoup.Jsoup.parse(text).wholeText();
        boolean english = text.contains("Job name") && text.contains("Country / Region") && text.contains("Preferred Job Location");
        List<String> lines = text.lines().toList();
        String[] labelsZh = {"应聘岗位","姓名","性别","出生日期","籍贯","国家 / 地区","城市","期望工作地点","邮箱","电话","上传简历","最高学历","最高学历毕业院校","开始日期","结束日期","专业","专业成绩排名","第一学历毕业院校","开始日期","结束日期","专业","专业成绩排名","最近看了什么有趣的书？请描述推荐理由","如何看待人生的意义"};
        String[] labelsEn = {"Job name","Name","Gender","Date of Birth","Native Place","Country / Region","City","Preferred Job Location","Email","Phone","Upload Resume","Highest Education Level","Name of Institution","Start Date","End Date","Major / Field of Study","Professional Ranking","Name of Institution","Start Date","End Date","Major / Field of Study","Professional Ranking","What interesting book have you read recently? Please describe why you recommend it.","How do you view the meaning of life?"};
        String[] keys = {"job","name","gender","birth","native","place","city","location","email","phone","resume","degree1","university1","from1","to1","major1","rank1","university2","from2","to2","major2","rank2","qa_book","qa_life"};        java.util.Map<String,String> values = new java.util.LinkedHashMap<>();
        String cursor = null;
        java.util.Map<String,Integer> labelOccurrences = new java.util.HashMap<>();
        for (String line : lines) {
            String s=line.trim(); if(s.isEmpty()) continue;
            int colon=s.indexOf(':');
            if(colon>=0){
                String label=s.substring(0,colon).trim();
                String[] labels=english?labelsEn:labelsZh;
                java.util.List<Integer> matches = new java.util.ArrayList<>();
                for(int i=0;i<labels.length;i++) if(labels[i].equals(label)) matches.add(i);
                if (!matches.isEmpty()) {
                    int seen = labelOccurrences.getOrDefault(label, 0);
                    cursor = keys[matches.get(Math.min(seen, matches.size() - 1))];
                    labelOccurrences.put(label, seen + 1);
                }
                String v=s.substring(colon+1).trim();
                if(cursor!=null&&!v.isEmpty()) values.merge(cursor,v,(a,b)->a+"\n"+b);
            } else if(cursor!=null) values.merge(cursor,s,(a,b)->a+"\n"+b);
        }
        String title=values.get("job");
        if(title==null) throw new InternalServerException("Not found: applying position");
        List<com.deepconnect.ats.model.JobPosition> jobs=matchingJobs(title,mail.mailbox());
        List<CandidateEducationHistory> education = new ArrayList<>();
        addEducation(education, values, "1", false);
        addEducation(education, values, "2", true);
        List<com.deepconnect.ats.dto.CandidateResumeSaveReq> result=new ArrayList<>();
        for(var job:jobs){
            String place=join(values.get("place"),values.get("city"));
            var req=form(mail,values.get("name"),values.get("gender"),null,values.get("phone"),place,values.get("email"),values.get("location"),null,null,null,title,1,1);
            CandidateResume resume=req.getCandidateResume();
            resume.setApplyingPositionId(job.getId());
            resume.setBirthDate(date(values.get("birth")));
            resume.setAttachmentLinks(clean(values.get("resume")));
            List<java.util.Map<String,Object>> qa=new ArrayList<>();
            if(values.containsKey("qa_book")) qa.add(java.util.Map.of("question","最近看了什么有趣的书？请描述推荐理由","answer",values.get("qa_book")));
            if(values.containsKey("qa_life")) qa.add(java.util.Map.of("question","如何看待人生的意义","answer",values.get("qa_life")));
            resume.setExtendedQa(qa);
            req.setEducationHistories(education.stream().map(this::copy).toList());
            result.add(req);
        }
        return result;
    }

    private void addEducation(List<CandidateEducationHistory> out, java.util.Map<String,String> v, String suffix, boolean primary) {
        String university=v.get("university"+suffix), degree=v.get("degree"+suffix), major=v.get("major"+suffix);
        if(university==null&&degree==null&&major==null)return;
        CandidateEducationHistory edu=new CandidateEducationHistory(); edu.setUniversity(clean(university)); edu.setDegree(degree(degree)); edu.setMajor(clean(major));
        edu.setDateFrom(date(v.get("from"+suffix))); edu.setDateTo(date(v.get("to"+suffix))); edu.setUpToNow(false); edu.setPerformanceRank(integer(v.get("rank"+suffix))); edu.setDataStatus(1); out.add(edu);
    }
    private String join(String a,String b){return a==null?b:b==null?a:a+" "+b;}
}
