package com.deepconnect.ats.schedule;

import com.deepconnect.ats.service.O365AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Lazy(false)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ats.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class O365Schedule {
    private final O365AuthService o365AuthService;

    @Scheduled(cron = "0 0 1 * * *", zone = SchedulerConfig.ZONE)
    @Transactional
    public void refreshO365Auth() {
        log.info("======> o365_schedule()");
        o365AuthService.refresh();
    }
}
