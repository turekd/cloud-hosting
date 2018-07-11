package it.dturek.cloudhosting.controller.api;

import it.dturek.cloudhosting.domain.FilePreviewResponse;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.service.ResourceService;
import it.dturek.cloudhosting.service.SecurityService;
import it.dturek.cloudhosting.util.FileUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/file")
public class FileRestController {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(FileRestController.class);

    private static final int FILES_UPLOAD_LIMIT = 10;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private InternationalizationService internationalizationService;

    @PostMapping
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile[] files, HttpServletRequest request, HttpServletResponse response) {
        User user = securityService.getUser();

        Resource parent;
        try {
            Long parentId = Long.valueOf(request.getParameter("parent_id"));
            parent = resourceService.find(parentId, user);
        } catch (NumberFormatException exception) {
            parent = null;
        }

        if (files.length == 0) {
            return new ResponseEntity<>(internationalizationService.getMessage("drive.upload.error.no_file"),
                    HttpStatus.BAD_REQUEST);
        }

        if (files.length > FILES_UPLOAD_LIMIT) {
            return new ResponseEntity<>(internationalizationService.getMessage("drive.upload.error.limit", FILES_UPLOAD_LIMIT),
                    HttpStatus.BAD_REQUEST);
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return new ResponseEntity<>(internationalizationService.getMessage("drive.upload.error.no_file"),
                        HttpStatus.BAD_REQUEST);
            }

            if (!resourceService.hasUserEnoughSpaceToUploadFile(user, file.getSize())) {
                String message = internationalizationService.getMessage("drive.upload.error.space",
                        file.getOriginalFilename(),
                        FileUtil.getFriendlySize(file.getSize()),
                        FileUtil.getFriendlySize(user.getSpaceAvailable() - user.getSpaceUsed()));
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }
        }

        resourceService.addFiles(files, user, parent);
        return new ResponseEntity<>(internationalizationService.getMessage("drive.upload.success.message", files.length),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public FilePreviewResponse getFile(@PathVariable("id") Long fileId, HttpServletResponse response) {
        User user = securityService.getUser();
        Resource file = resourceService.find(fileId, user);

        if (file == null || file.getType().equals(Resource.Type.DIRECTORY)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        return resourceService.getFilePreviewResponse(file);
    }

}
