package com.deepconnect.ats.dto;

import lombok.Data;

@Data
public class QuickSaveResumeReq {
    private Long id;
    private String notes;
    private String privacyNotes;
}
