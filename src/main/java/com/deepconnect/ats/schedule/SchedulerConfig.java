package com.deepconnect.ats.schedule;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Java equivalent of Django ats/schedule_config.py (APScheduler BackgroundScheduler, only started for runserver).
 * Disable with ats.scheduler.enabled=false. Every job class must carry @Lazy(false) because the application
 * uses spring.main.lazy-initialization=true, which would otherwise never instantiate the @Scheduled beans.
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "ats.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulerConfig {

    /** Zone used by every cron expression (Django TIME_ZONE). */
    public static final String ZONE = "Asia/Shanghai";
}
