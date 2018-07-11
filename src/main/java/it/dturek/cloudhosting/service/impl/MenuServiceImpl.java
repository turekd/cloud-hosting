package it.dturek.cloudhosting.service.impl;

import it.dturek.cloudhosting.domain.PageType;
import it.dturek.cloudhosting.service.MenuService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class MenuServiceImpl implements MenuService {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(MenuServiceImpl.class);

    private static final Map<String, PageType> MAP;

    static {
        MAP = new HashMap<>();
        MAP.put("/drive", PageType.DRIVE);
        MAP.put("/my-account", PageType.MY_ACCOUNT);
        MAP.put("/login", PageType.LOGIN);
        MAP.put("/register", PageType.REGISTER);
    }

    @Override
    public void setPageType(HttpServletRequest request) {
        request.setAttribute("pageType", MAP.get(request.getServletPath()));
    }
}
