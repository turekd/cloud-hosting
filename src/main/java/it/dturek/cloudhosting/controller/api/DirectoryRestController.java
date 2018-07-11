package it.dturek.cloudhosting.controller.api;

import it.dturek.cloudhosting.domain.BreadcrumbItem;
import it.dturek.cloudhosting.domain.Sort;
import it.dturek.cloudhosting.domain.api.DirectoryListing;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.service.ResourceService;
import it.dturek.cloudhosting.service.SecurityService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/directory")
public class DirectoryRestController {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(DirectoryRestController.class);

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private InternationalizationService internationalizationService;

    @ModelAttribute("sort")
    protected Sort getSort(HttpServletRequest request) {
        Sort sort = new Sort("name", "ASC");
        if (request.getParameter("sortColumn") != null) {
            sort.setColumn(request.getParameter("sortColumn"));
        }
        if (request.getParameter("sortDir") != null) {
            sort.setOrder(request.getParameter("sortDir"));
        }
        return sort;
    }

    @GetMapping("/details/{directoryId}")
    public DirectoryListing list(@PathVariable("directoryId") Long directoryId, @ModelAttribute("sort") Sort sort) {
        User user = securityService.getUser();
        List<Resource> resources;
        Resource directory;
        Integer total;

        if (directoryId == Resource.ROOT_ID) {
            directory = new Resource();
            resources = resourceService.findTopResources(null, user, sort);
            total = resourceService.countResources(null, user);
        } else {
            directory = resourceService.find(directoryId, user);
            resources = resourceService.findTopResources(directory, user, sort);
            total = resourceService.countResources(directory, user);
        }

        List<BreadcrumbItem> breadcrumbs = resourceService.getBreadcrumbs(directory);

        return new DirectoryListing(directory, resources, breadcrumbs, user, total);
    }

    @GetMapping("/more/{directoryId}")
    public List<Resource> loadMore(@PathVariable("directoryId") Long directoryId, @ModelAttribute("sort") Sort sort,
                                   @RequestParam("offset") Integer offset) {
        User user = securityService.getUser();
        List<Resource> resources;
        Resource directory;

        if (directoryId == Resource.ROOT_ID) {
            directory = null;
        } else {
            directory = resourceService.find(directoryId, user);
        }

        resources = resourceService.loadMoreResources(directory, user, sort, offset);

        return resources;
    }

    @PostMapping
    public ResponseEntity<String> create(HttpServletRequest request) {
        String name = request.getParameter("name");
        User user = securityService.getUser();

        Resource parent;
        try {
            Long parentId = Long.valueOf(request.getParameter("parent_id"));
            parent = resourceService.find(parentId, user);
        } catch (NumberFormatException exception) {
            parent = null;
        }

        Resource directory = resourceService.createDirectory(name, parent, user);
        String message = internationalizationService.getMessage("drive.directory.success.message", directory.getName());
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
