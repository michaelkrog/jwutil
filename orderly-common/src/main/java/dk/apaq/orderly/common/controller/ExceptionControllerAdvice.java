package dk.apaq.orderly.common.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import dk.apaq.orderly.common.errors.InvalidArgumentException;
import dk.apaq.orderly.common.errors.InvalidRequestException;
import dk.apaq.orderly.common.errors.ResourceNotFoundException;
import dk.apaq.orderly.common.errors.RestError;
import dk.apaq.orderly.common.errors.RestErrorWithParam;
import javax.validation.ConstraintViolation;
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice()
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler  {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
    private static final String FALLBACK_ERROR_MESSAGE = "Internal error occured. This incident has been reported to the System Administrators for further investigation.";

    private ConstraintViolationException resolveConstraintViolation(Throwable t) {
        if (t.getCause() instanceof ConstraintViolationException) {
            return (ConstraintViolationException) t.getCause();
        } else if (t.getCause() == null) {
            return null;
        } else {
            return resolveConstraintViolation(t.getCause());
        }
    }

    private RestErrorWithParam handleConstraintViolation(ConstraintViolationException ex) {
        LOG.debug("Error sent to client", ex);
        ConstraintViolation cv = ex.getConstraintViolations().isEmpty() ? null : ex.getConstraintViolations().iterator().next();
        String param = cv == null ? null : cv.getPropertyPath().toString();
        String message = cv == null ? "A parameter was not correctly specified." : cv.getMessage();

        if (cv == null) {
            LOG.warn("ConstraintViolationException without ConstraintViolation.", ex);
        }
        return new RestErrorWithParam(RestError.ErrorType.InvalidRequestError, message, param);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RestError onException(Exception ex) {
        ConstraintViolationException violationException = resolveConstraintViolation(ex);
        if (violationException == null) {
            LOG.error("Error sent to client", ex);
            return new RestError(RestError.ErrorType.ApiError, FALLBACK_ERROR_MESSAGE);
        } else {
            return handleConstraintViolation(violationException);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public RestErrorWithParam onException(ConstraintViolationException ex) {
        return handleConstraintViolation(ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ResponseBody
    public RestError onException(AccessDeniedException ex) {
        LOG.debug("Error sent to client [message="+ex.getMessage()+"]");
        return new RestError(RestError.ErrorType.InvalidRequestError, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public RestError onException(ResourceNotFoundException ex) {
        LOG.debug("NOT_FOUND sent to client [message="+ex.getMessage()+"]");
        return new RestError(RestError.ErrorType.InvalidRequestError, ex.getMessage());
    }

    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public RestErrorWithParam onException(InvalidArgumentException ex) {
        LOG.debug("BAD_REQUEST sent to client", ex);
        return new RestErrorWithParam(RestError.ErrorType.InvalidRequestError, ex.getMessage(), ex.getParameter());
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public RestError onException(InvalidRequestException ex) {
        LOG.debug("BAD_REQUEST sent to client", ex);
        return new RestError(RestError.ErrorType.InvalidRequestError, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.debug("BAD_REQUEST sent to client", ex);
        FieldError error = ex.getFieldError();
        String message = "'" + error.getRejectedValue() + "' is not valid for this parameter.";
        RestError restError = new RestErrorWithParam(RestError.ErrorType.InvalidRequestError, message, error.getField());
        return new ResponseEntity(restError, headers, HttpStatus.BAD_REQUEST);
    }
    
    

    @ExceptionHandler(HttpMediaTypeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public RestError onException(HttpMediaTypeException ex) {
        LOG.debug("BAD_REQUEST sent to client", ex);
        return new RestError(RestError.ErrorType.InvalidRequestError, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.debug("BAD_REQUEST sent to client", ex);
        RestError error = new RestError(RestError.ErrorType.InvalidRequestError, ex.getMessage());
        return new ResponseEntity(error, headers, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.debug("BAD_REQUEST sent to client", ex);
        RestError error = null;
        if (ex.getCause() instanceof JsonParseException) {
            JsonParseException parseException = (JsonParseException) ex.getCause();
            error = new RestError(RestError.ErrorType.InvalidRequestError,
                    "Invalid JSON at line " + parseException.getLocation().getLineNr() + ", column "
                    + parseException.getLocation().getColumnNr() + ".");
        }

        if (ex.getCause() instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException upe = (UnrecognizedPropertyException) ex.getCause();
            error = new RestErrorWithParam(RestError.ErrorType.InvalidRequestError,
                    "The property does not apply to this resource", upe.getPropertyName());
        }

        if (ex.getCause() instanceof JsonMappingException) {
            JsonMappingException mappingException = (JsonMappingException) ex.getCause();
            List<JsonMappingException.Reference> refs = mappingException.getPath();
            error = new RestErrorWithParam(RestError.ErrorType.InvalidRequestError,
                    mappingException.getOriginalMessage(), refs.isEmpty() ? null : refs.get(0).getFieldName());
        }
        
        if(error == null) {
            error = new RestError(RestError.ErrorType.InvalidRequestError, "Message body was expected but is missing.");
        }
        
        return new ResponseEntity(error, headers, HttpStatus.BAD_REQUEST);
    }
    
    
    
}
