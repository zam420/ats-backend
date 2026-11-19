package com.deepconnect.ats.job.enums;
import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public enum JobPostedDateType implements LocalizedBaseEnum {

    ONE_DAY(0, "oneDay"),
    ONE_WEEK(1, "oneWeek"),
    TWO_WEEKS(2, "twoWeeks"),
    ONE_MONTH(3, "oneMonth");

    @JsonValue
    private final Integer id;

    private final String key;

    private static final String enumsKeyPrefix = "enums.jobPostedDateType.";

    JobPostedDateType(Integer id, String key) {
        this.key = enumsKeyPrefix + key;
        this.id = id;
    }

    public LocalDateTime cutoff(LocalDateTime now) {
        return switch (this) {
            case ONE_DAY -> now.minusDays(1);
            case ONE_WEEK -> now.minusWeeks(1);
            case TWO_WEEKS -> now.minusWeeks(2);
            case ONE_MONTH -> now.minusMonths(1);
        };
    }
    
}
