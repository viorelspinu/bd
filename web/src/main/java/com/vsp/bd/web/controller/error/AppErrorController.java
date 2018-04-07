package com.vsp.bd.web.controller.error;

import java.time.Instant;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.vsp.bd.domain.Log;
import com.vsp.bd.domain.LogRepository;

@Controller
public class AppErrorController implements ErrorController {

	private static final String PATH = "/error";

	@Autowired
	private ErrorAttributes errorAttributes;

	@Autowired
	private LogRepository logRepository;

	@RequestMapping(value = PATH)
	public String error(HttpServletRequest request, HttpServletResponse response) {
		String errorMessage = getErrorAttributes(request).toString();

		try {
			logRepository.save(new Log(Log.ERROR_TYPE, Instant.now() + ": " + errorMessage));
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return "redirect:/";
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		return errorAttributes.getErrorAttributes(requestAttributes, true);
	}

}
