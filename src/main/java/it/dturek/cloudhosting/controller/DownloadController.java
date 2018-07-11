package it.dturek.cloudhosting.controller;

import it.dturek.cloudhosting.domain.jpa.ResourceShare;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.service.ResourceService;
import it.dturek.cloudhosting.service.ResourceShareService;
import it.dturek.cloudhosting.service.SecurityService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;

@Controller
public class DownloadController {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(DownloadController.class);

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ResourceShareService resourceShareService;

    @Autowired
    private InternationalizationService internationalizationService;

    @RequestMapping("/download/{id}")
    @ResponseBody
    ResponseEntity<Resource> download(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        User user = securityService.getUser();
        it.dturek.cloudhosting.domain.jpa.Resource resource = resourceService.find(id, user);
        return download(resource, response);
    }

    @RequestMapping("/share/{key}")
    @ResponseBody
    ResponseEntity<Resource> share(@PathVariable("key") String key, HttpServletResponse response, RedirectAttributes redirectAttributes) throws IOException {
        ResourceShare resourceShare = resourceShareService.findByKey(key);

        if (!resourceShareService.isShareable(resourceShare)) {
            redirectAttributes.addFlashAttribute("error", internationalizationService.getMessage("share.invalid_url"));
            response.sendRedirect("/downloadError");
            return null;
        }

        ResponseEntity<Resource> result = download(resourceShare.getResource(), response);
        resourceShareService.handleAfterShare(resourceShare);
        return result;
    }

    @RequestMapping("/downloadError")
    public String downloadError(Model model){
        return "download/error";
    }

    private ResponseEntity<Resource> download(it.dturek.cloudhosting.domain.jpa.Resource resource, HttpServletResponse response) throws IOException {
        if (resource == null) {
            response.sendRedirect("/downloadError");
            return null;
        }

        Path file = resourceService.getFilePath(resource);
        Resource fileResource = new UrlResource(file.toUri());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getName() + "\"")
                .body(fileResource);
    }

}
