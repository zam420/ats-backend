package com.deepconnect.ats.dto;

import com.deepconnect.ats.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuplicationCheckBody {
    private User user;
    private Long resumeId;
}
