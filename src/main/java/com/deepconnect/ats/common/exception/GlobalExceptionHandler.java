package com.deepconnect.ats.common.exception;

import java.io.IOException;
import java.net.SocketException;
import java.util.stream.Collectors;
import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Note that @ControllerAdvice does not apply to filters because it only works for exceptions thrown inside controllers.
 * So any errors encountered inside the filters such as JwtAuthenticationFilter are not handled automatically by GlobalExceptionHandler. 
 * This also happens because in Spring boot, filters do not automatically propagate exceptions to @ControllerAdvice.
 * As a workaround, we can manually invoke the exception handler's logic inside JwtAuthenticationFilter by declaring GlobalExceptionHandler
 * as a Component and then injecting it insde JwtAuthenticationFilter.
 */
@Component
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<ErrorRsp> methodArgInValid(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(Strings.LINE_SEPARATOR));
        return new ResponseEntity<>(ErrorRsp.of(errorMsg, HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorRsp> handleCustomException(CustomException ex, ServletRequest request) {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), locale);
        return new ResponseEntity<>(ErrorRsp.of(message, ex.getHttpStatus().value()), ex.getHttpStatus());
    }

    /**
     * In GlobalExceptionHandler, @ControllerAdvice does not apply to filters because it only works for exceptions thrown inside controllers.
    * So any errors encountered inside the filters such as JwtAuthenticationFilter are not handled automatically by GlobalExceptionHandler. 
    * This also happens because in Spring boot, filters do not automatically propagate exceptions to @ControllerAdvice.
    * As a workaround, we can manually invoke the exception handler's logic inside JwtAuthenticationFilter by declaring GlobalExceptionHandler
    * as a Component and then injecting it insde filters like JwtAuthenticationFilter.
     * @param ex
     * @param request
     * @param response
     * @throws IOException
     */
    public void setCustomExceptionInResponse(CustomException ex, ServletRequest request, ServletResponse response) throws IOException {
        Locale locale = request.getLocale();
        String message = messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), locale);
        ((HttpServletResponse) response).setStatus(ex.getHttpStatus().value());
        ((HttpServletResponse) response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        ((HttpServletResponse) response).setCharacterEncoding("UTF-8");
        // Serialize with Jackson so quotes/special characters in the message are escaped correctly
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(ErrorRsp.of(message, ex.getHttpStatus().value())));
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
