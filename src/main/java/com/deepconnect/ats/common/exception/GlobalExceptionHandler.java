package com.deepconnect.ats.common.exception;

import java.net.SocketException;
import java.util.stream.Collectors;
import java.util.Locale;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.transaction.TransactionSystemException;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


/* 
In GlobalExceptionHandler, @ControllerAdvice does not apply to filters because it only works for exceptions thrown inside controllers.
* So any errors encountered inside the filters such as JwtAuthenticationFilter are not handled automatically by GlobalExceptionHandler. 
* This also happens because in Spring boot, filters do not automatically propagate exceptions to @ControllerAdvice.
* To solve this, we use HandlerExceptionResolver. An example can be found in JwtAuthFilter.java
*/
@Slf4j 
@Component
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<ErrorRsp> methodArgInValid(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(Strings.LINE_SEPARATOR));
        return new ResponseEntity<>(ErrorRsp.of(errorMsg, HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoggedCustomException.class)
    public ResponseEntity<ErrorRsp> handleLoggedCustomException(LoggedCustomException ex, ServletRequest request) {
        String message = messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), request.getLocale());
        log.error("Application error | Reason: {} | URI: {} | IP: {} | Info: {}", 
            message, 
            ((HttpServletRequest) request).getRequestURI(), 
            getClientIpAddress((HttpServletRequest) request),
            ex.getLogMessage()
            //,ex     uncomment the following if we want to log the full stack trace of the exception
        );
        return new ResponseEntity<>(ErrorRsp.of(message, ex.getHttpStatus().value()), ex.getHttpStatus());
    }

    //This Spring built-in exception is thrown when a permission check fails on annotations like @PreAuthorize("hasAuthority('iptsa')")
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorRsp> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String message = messageSource.getMessage("exception.accessDenied", null, request.getLocale());
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        String username = (loggedInUser != null) ? loggedInUser.getUsername() : "ANONYMOUS";
        log.warn("Access denied | Reason: {} | URI: {} | IP: {} | User: {} | Info: {}", 
            message,
            ((HttpServletRequest) request).getRequestURI(), 
            getClientIpAddress(request), 
            username,
            ex.getMessage()
        );

        return new ResponseEntity<>(ErrorRsp.of(message, HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorRsp> handleCustomException(CustomException ex, ServletRequest request) {
        String message = messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), request.getLocale());
        return new ResponseEntity<>(ErrorRsp.of(message, ex.getHttpStatus().value()), ex.getHttpStatus());
    }
    
    @ExceptionHandler(ValidationRangeException.class)
    public ResponseEntity<ErrorRsp> handleValidationRangeException(ValidationRangeException ex, HttpServletRequest request) {
        Locale locale = request.getLocale();
        String fieldName = messageSource.getMessage(ex.getFieldKey(), null, locale);
        String message = messageSource.getMessage(ex.getMessageKey(), ArrayUtils.addFirst(ex.getArgs(), fieldName), locale);
        return new ResponseEntity<>(ErrorRsp.of(message, ex.getHttpStatus().value()), ex.getHttpStatus());
    }

    @ExceptionHandler(SocketException.class)
    public ResponseEntity<ErrorRsp> handleSocketException(SocketException ex, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorRsp.of(ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method to handle standard IPs and proxy headers (like Nginx / Cloudflare / AWS ELB)
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain a comma-separated list of IPs if behind multiple proxies. 
            // The first one is the original client's IP.
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRsp> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Application error | Reason: {} | URI: {} | IP: {} | Info: {}", 
            ex.getMessage(), 
            request.getRequestURI(), 
            getClientIpAddress(request),
            ex.getMessage()
            ,ex     //uncomment the following if we want to log the full stack trace of the exception
        );
        return new ResponseEntity<>(ErrorRsp.of(ex.getMessage() != null ? ex.getMessage() : "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /*
    
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResult<?> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return ApiResult.fail(ApiResult.FORBIDDEN_CODE, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResult<?> handleSpringSecurityAccessDenied(AccessDeniedException e) {
        log.warn("Spring Security Access denied: {}", e.getMessage());
        if (AuthService.getLoggedInUser() == null) {
            return ApiResult.fail(ApiResult.UNAUTHORIZED_CODE, "Unauthorized");
        }
        return ApiResult.fail(ApiResult.FORBIDDEN_CODE, "Access denied");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<?> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Validation error: {}", msg);
        return ApiResult.fail(ApiResult.PARAMS_ERROR_CODE, msg);
    }

    @ExceptionHandler(BindException.class)
    public ApiResult<?> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Bind error: {}", msg);
        return ApiResult.fail(ApiResult.PARAMS_ERROR_CODE, msg);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return ApiResult.fail(ApiResult.PARAMS_ERROR_CODE, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<?> handleGeneralException(Exception e) {
        log.error("Unhandled internal server error", e);
        return ApiResult.fail(ApiResult.FAILED_CODE, e.getMessage() != null ? e.getMessage() : "Internal server error");
    }
    
    */
    
}
