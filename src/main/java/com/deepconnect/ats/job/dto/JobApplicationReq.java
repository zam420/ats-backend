package com.deepconnect.ats.job.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobApplicationReq {
    @NotBlank @Size(max = 100) private String firstName;
    @NotBlank @Size(max = 100) private String lastName;
    @Size(max = 100) private String preferredName;
    @NotBlank @Email @Size(max = 254) private String email;
    @NotBlank @Size(max = 10) private String countryCode;
    @NotBlank @Size(max = 40) private String phoneNumber;
    @NotBlank @Size(max = 200) private String currentLocation;
    @NotNull @Size(max = 40) @Valid private List<Answer> answers;

    @Data
    public static class Answer {
        @NotBlank private String questionId;
        @NotNull @Size(max = 20) private List<@NotBlank @Size(max = 10000) String> values;
    }
}
