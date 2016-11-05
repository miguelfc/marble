package org.marble.commons.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// TODO divide for each particular exception
	@ExceptionHandler({ InvalidTopicException.class, InvalidExecutionException.class, InvalidTwitterApiKeyException.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ModelAndView handleInvalidTopicException(HttpServletRequest request, Exception e) {
        ModelAndView modelAndView = new ModelAndView("not_found");
        log.error("Exception Occured. URL: <" + request.getRequestURL() + ">, error: " + e.getMessage(), e);
        return modelAndView;
    }
	
	@ExceptionHandler({ Exception.class })
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleGlobalException(HttpServletRequest request, Exception e) {
		ModelAndView modelAndView = new ModelAndView("error");
		log.error("Exception Occured. URL: <" + request.getRequestURL() + ">, error: " + e.getMessage(), e);
		return modelAndView;
	}
}