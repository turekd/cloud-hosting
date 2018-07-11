package it.dturek.cloudhosting.controller.api;

import it.dturek.cloudhosting.domain.ResourceShareSuccess;
import it.dturek.cloudhosting.domain.ResourceShareType;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.service.ResourceService;
import it.dturek.cloudhosting.service.ResourceShareService;
import it.dturek.cloudhosting.service.SecurityService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/resource")
public class ResourceRestController {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(ResourceRestController.class);

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private InternationalizationService internationalizationService;

    @Autowired
    private ResourceShareService resourceShareService;

    @GetMapping("/{resourceId}")
    public Resource findById(@PathVariable("resourceId") Long resourceId, HttpServletResponse response) {
        Resource resource = resourceService.find(resourceId, securityService.getUser());

        if (resource == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }

        return resource;
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<String> delete(@PathVariable("resourceId") Long resourceId, HttpServletResponse response) {
        User user = securityService.getUser();
        Resource resource = resourceService.find(resourceId, user);

        if (resource == null) {
            return new ResponseEntity<>(internationalizationService.getMessage("resource.delete.not_found"), HttpStatus.NOT_FOUND);
        }

        resourceService.delete(resource, user);

        String message = internationalizationService.getMessage(
                resource.getType().equals(Resource.Type.DIRECTORY) ? "drive.alert.directory_deleted" : "drive.alert.file_deleted",
                resource.getName()
        );
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/deleteChecked")
    public ResponseEntity massDelete(HttpServletRequest request, @RequestParam(value = "ids[]") Long[] resourceIds) {
        User user = securityService.getUser();
        resourceService.deleteAll(resourceIds, user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<Resource> search(@RequestParam("name") String name) {
        return resourceService.search(securityService.getUser(), name);
    }

    @PutMapping
    public ResponseEntity<String> rename(HttpServletRequest request, HttpServletResponse response) {
        User user = securityService.getUser();
        Long id = Long.valueOf(request.getParameter("id"));
        Resource resource = resourceService.find(id, user);
        if (resource == null) {
            return new ResponseEntity<>(internationalizationService.getMessage("drive.resource.not_found"), HttpStatus.BAD_REQUEST);
        }

        String newName = request.getParameter("name");
        String oldName = resource.getName();
        if (newName == null || newName.isEmpty()) {
            return new ResponseEntity<>(internationalizationService.getMessage("drive.modal.rename.empty_name"), HttpStatus.BAD_REQUEST);
        }

        if (newName.equals(oldName)) {
            return new ResponseEntity<>(internationalizationService.getMessage("drive.modal.rename.same_name"), HttpStatus.BAD_REQUEST);
        }

        resourceService.rename(resource, newName);

        String message = internationalizationService.getMessage(
                resource.getType().equals(Resource.Type.DIRECTORY) ? "drive.alert.directory_renamed" : "drive.alert.file_renamed"
                , oldName, newName);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/share")
    public ResponseEntity<String> share(HttpServletRequest request) {
        User user = securityService.getUser();
        Long resourceId = Long.valueOf(request.getParameter("id"));
        ResourceShareType type = ResourceShareType.fromString(request.getParameter("type"));
        Resource resource = resourceService.find(resourceId, user);

        if (type == null) {
            return new ResponseEntity<>(internationalizationService.getMessage("share.error.type"), HttpStatus.BAD_REQUEST);
        }

        if (resource == null) {
            return new ResponseEntity<>(internationalizationService.getMessage("drive.resource.not_found"), HttpStatus.BAD_REQUEST);
        }

        if (resource.getType().equals(Resource.Type.DIRECTORY)) {
            return new ResponseEntity<>(internationalizationService.getMessage("messages.onlyFilesCanBeShared"), HttpStatus.BAD_REQUEST);
        }

        ResourceShareSuccess result = resourceShareService.add(resource, type);
        return new ResponseEntity<>(result.getMessage(), HttpStatus.OK);
    }

}
