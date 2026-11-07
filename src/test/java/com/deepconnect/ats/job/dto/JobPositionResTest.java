package com.deepconnect.ats.job.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.deepconnect.ats.job.model.JobPosition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.Test;

class JobPositionResTest {
    @Test
    void includesHiringManagersAsPublicIdsInResponse() {
        JobPosition position = new JobPosition();
        position.setHiringManagers(List.of("manager1", "manager2"));

        JobPositionRes response = JobPositionRes.from(position);
        assertEquals(List.of("manager1", "manager2"), response.getHiringManagers());
        ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        assertEquals("manager1", mapper.valueToTree(response).get("hiring_managers").get(0).asText());
    }
}
