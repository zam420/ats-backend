package com.deepconnect.ats.common.enums;

public enum AccessType {
    SYSTEM_ADMIN,
    TENANT_ADMIN,  //both write and read for all
    WRITE,  //both write and read, only for the owner or specific users
    READ,   //read only
    NONE
    ;
}