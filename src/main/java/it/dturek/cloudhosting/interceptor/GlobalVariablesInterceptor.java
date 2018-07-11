package it.dturek.cloudhosting.interceptor;

import it.dturek.cloudhosting.service.ApplicationService;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class GlobalVariablesInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    @Lazy
    private InternationalizationService internationalizationService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (!RequestUtil.isAjax(request)) {
            request.setAttribute("cdnUrl", applicationService.getCdnUrl());
            request.setAttribute("baseUrl", applicationService.getBaseUrl());
            request.setAttribute("siteName", internationalizationService.getMessage("site.name"));
        }
    }
}
