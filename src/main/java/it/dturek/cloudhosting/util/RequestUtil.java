package it.dturek.cloudhosting.util;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

    public static boolean isAjax(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader);
    }

}
