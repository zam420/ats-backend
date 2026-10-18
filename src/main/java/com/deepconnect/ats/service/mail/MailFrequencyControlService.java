package com.deepconnect.ats.service.mail;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.model.AtsEmailCounter;
import com.deepconnect.ats.model.AtsEmailRecord;
import com.deepconnect.ats.repository.AtsEmailCounterRepository;
import com.deepconnect.ats.repository.AtsEmailRecordRepository;
import com.deepconnect.ats.repository.TaskRecordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MailFrequencyControlService {
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private final AtsEmailCounterRepository counterRepository;
    private final AtsEmailRecordRepository emailRepository;
    private final TaskRecordRepository taskRecordRepository;
    @PersistenceContext private EntityManager entityManager;
    @Value("${ats.email.frequency.daily-total:-1}") private int dailyLimit;
    @Value("${ats.email.frequency.generic:}") private String genericFrequency;
    @Value("${ats.email.frequency.per-domain:}") private String domainFrequency;

    @Transactional
    public void check(List<AtsEmailRecord> mails) {
        int count = mails == null ? 0 : mails.size();
        if (dailyLimit < 0) return;
        if (count > dailyLimit) throw new InternalServerException("Email daily limit exceeded: limit " + dailyLimit + ", remaining 0");
        LocalDateTime now = LocalDateTime.now(ZONE);
        List<String> domains = new ArrayList<>();
        for (AtsEmailRecord mail : mails) {
            Set<String> unique = new HashSet<>();
            if (mail.getTo() != null) for (String address : mail.getTo()) unique.add(domain(address));
            domains.addAll(unique);
        }

        for (int attempt = 0; attempt < 5; attempt++) {
            String tag = LocalDate.now(ZONE).toString();
            AtsEmailCounter counter = counterRepository.findByCounterTag(tag).orElse(null);
            int sentToday = counter == null ? 0 : counter.getMailCount();
            int totalRemaining = dailyLimit - sentToday;
            if (totalRemaining < count) throw new InternalServerException("Email daily limit exceeded: limit " + dailyLimit + ", remaining " + totalRemaining);

            Frequency generic = parse(genericFrequency);
            if (generic.limit >= 0) {
                int sent = countSince(now, generic.seconds, null);
                if (generic.limit - sent < count) throw new InternalServerException("Email generic frequency limit exceeded: limit " + generic.limit);
            }
            Frequency perDomain = parse(domainFrequency);
            if (perDomain.limit >= 0) {
                for (String target : domains) {
                    int sent = countSince(now, perDomain.seconds, target);
                    long upcoming = domains.stream().filter(target::equals).count();
                    if (perDomain.limit - sent < upcoming) throw new InternalServerException("Email domain frequency limit exceeded: limit " + perDomain.limit);
                }
            }

            if (increment(tag, counter, count)) return;
            try { Thread.sleep(50); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new InternalServerException("Email frequency control interrupted"); }
        }
        throw new InternalServerException("System busy while checking email frequency limits");
    }

    private int countSince(LocalDateTime now, double seconds, String domain) {
        LocalDateTime start = now.minusNanos((long) (seconds * 1_000_000_000d));
        int count = 0;
        for (AtsEmailRecord record : emailRepository.findAll()) {
            if (record.getCreatedAt() == null || record.getCreatedAt().isBefore(start)) continue;
            if (domain == null || (record.getTo() != null && record.getTo().stream().anyMatch(a -> a.toLowerCase(Locale.ROOT).endsWith(domain)))) count++;
        }
        return count;
    }

    private boolean increment(String tag, AtsEmailCounter counter, int add) {
        if (counter != null) {
            int oldCount = counter.getMailCount();
            return entityManager.createQuery("update AtsEmailCounter c set c.mailCount = :next where c.id = :id and c.mailCount = :old")
                    .setParameter("next", oldCount + add).setParameter("id", counter.getId()).setParameter("old", oldCount)
                    .executeUpdate() > 0;
        }
        taskRecordRepository.lockFirstByTaskId("mail_counter_init");
        if (counterRepository.findByCounterTag(tag).isPresent()) return false;
        try {
            counterRepository.saveAndFlush(AtsEmailCounter.builder().counterTag(tag).mailCount(add).build());
            return true;
        } catch (DataAccessException e) { return false; }
    }

    private Frequency parse(String value) {
        if (value == null || value.isBlank()) return new Frequency(-1, -1);
        Matcher match = Pattern.compile("(\\d+)\\s*/\\s*(\\d+)\\s*(ms|min|s|h|d|wk)").matcher(value.trim());
        if (!match.matches()) throw new IllegalArgumentException("invalid setting: " + value);
        double scale = switch (match.group(3)) { case "ms" -> 0.001; case "s" -> 1d; case "min" -> 60d; case "h" -> 3600d; case "d" -> 86400d; case "wk" -> 604800d; default -> throw new IllegalArgumentException("invalid setting: " + value); };
        return new Frequency(Integer.parseInt(match.group(1)), Long.parseLong(match.group(2)) * scale);
    }

    private String domain(String address) {
        if (address == null) return "";
        int at = address.lastIndexOf('@');
        return at < 0 ? "" : address.substring(at + 1).toLowerCase(Locale.ROOT);
    }
    private record Frequency(int limit, double seconds) {}
}
