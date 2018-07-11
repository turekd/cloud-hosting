package it.dturek.cloudhosting.controller;

import it.dturek.cloudhosting.domain.Sort;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.service.ApplicationService;
import it.dturek.cloudhosting.service.ResourceService;
import it.dturek.cloudhosting.service.SecurityService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/drive")
public class DriveController {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(DriveController.class);

    private static final List<String> SORT_COLUMNS = Arrays.asList("name", "modification_time", "size");
    private static final List<String> SORT_ORDERS = Arrays.asList("ASC", "DESC");

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ApplicationService applicationService;

    @ModelAttribute("sort")
    protected Sort getSort(HttpServletRequest request) {
        Sort sort = new Sort();
        sort.setColumn("name");
        sort.setOrder("ASC");

        String column = request.getParameter("sortColumn");
        if (column != null && SORT_COLUMNS.contains(column)) {
            sort.setColumn(column);
        }
        String order = request.getParameter("sortOrder");

        if (order != null && SORT_ORDERS.contains(order)) {
            sort.setOrder(order);
        }

        return sort;
    }

    @GetMapping
    public String list(Model model, @RequestParam(name = "dir", required = false) Long directoryId, @ModelAttribute("sort") Sort sort) {
        User user = securityService.getUser();

        Resource directory;
        if (directoryId == null) {
            directory = new Resource();
        } else {
            directory = resourceService.find(directoryId, user);
        }
        model.addAttribute("directory", directory);
        if (directory != null) {
            model.addAttribute("breadcrumbs", resourceService.getBreadcrumbs(directory));
        }
        model.addAttribute("readableMimeTypes", applicationService.getReadableMimeTypes());
        return "drive/list";
    }

}
