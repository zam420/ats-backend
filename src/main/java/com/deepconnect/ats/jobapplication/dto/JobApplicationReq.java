package com.deepconnect.ats.jobapplication.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class JobApplicationReq {
    @NotBlank(message = "{field.jobApplication.firstName}{validation.notBlank}")
    @Length(max = 100, message = "{field.jobApplication.firstName}{validation.lengthMax}")
    private String firstName;

    @NotBlank(message = "{field.jobApplication.lastName}{validation.notBlank}")
    @Length(max = 100, message = "{field.jobApplication.lastName}{validation.lengthMax}")
    private String lastName;

    @Length(max = 100, message = "{field.jobApplication.preferredName}{validation.lengthMax}")
    private String preferredName;

    @NotBlank(message = "{field.jobApplication.email}{validation.notBlank}")
    @Email(message = "{field.jobApplication.email}{validation.email}")
    @Length(max = 254, message = "{field.jobApplication.email}{validation.lengthMax}")
    private String email;

    @NotBlank(message = "{field.jobApplication.countryCode}{validation.notBlank}")
    @Length(max = 10, message = "{field.jobApplication.countryCode}{validation.lengthMax}")
    private String countryCode;

    @NotBlank(message = "{field.jobApplication.phoneNumber}{validation.notBlank}")
    @Length(max = 40, message = "{field.jobApplication.phoneNumber}{validation.lengthMax}")
    private String phoneNumber;

    @NotBlank(message = "{field.jobApplication.currentLocation}{validation.notBlank}")
    @Length(max = 200, message = "{field.jobApplication.currentLocation}{validation.lengthMax}")
    private String currentLocation;
    
    @NotNull(message = "{field.jobApplication.answers}{validation.notBlank}")
    @Size(max = 40, message = "{field.jobApplication.answers}{validation.maxSize}")
    @Valid
    private List<@NotNull(message = "{field.jobApplication.answer}{validation.notBlank}") Answer> answers;

    @Data
    public static class Answer {
        @NotBlank(message = "{field.jobApplication.questionId}{validation.notBlank}")
        private String questionId;
        @NotNull(message = "{field.jobApplication.values}{validation.notBlank}")
        @Size(max = 20, message = "{field.jobApplication.values}{validation.maxSize}")
        private List<@NotBlank(message = "{field.jobApplication.answerValue}{validation.notBlank}")
                @Length(max = 10000, message = "{field.jobApplication.answerValue}{validation.lengthMax}") String> values;
    }
}
