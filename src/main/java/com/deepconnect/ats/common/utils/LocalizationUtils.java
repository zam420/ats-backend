package com.deepconnect.ats.common.utils;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.deepconnect.ats.common.enums.LocalizedStringType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalizationUtils {

    private final MessageSource messageSource;

    // Retrieve `Accept-Language` header from the current request
    public static Locale getUserLocale() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            // The servlet container parses Accept-Language and returns the highest-priority locale (e.g. en_US)
            return attributes.getRequest().getLocale();
        }
        return Locale.US;// Defaults to en_US
    }

    public String getLocalizedString(LocalizedStringType localizedStringType){
        Locale locale = getUserLocale();
        return messageSource.getMessage(localizedStringType.getKey(), null, locale);
    }
    
}


