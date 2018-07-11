package it.dturek.cloudhosting.service;

import it.dturek.cloudhosting.domain.BreadcrumbItem;
import it.dturek.cloudhosting.domain.FilePreviewResponse;
import it.dturek.cloudhosting.domain.Sort;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.User;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface ResourceService {

    Resource createDirectory(String name, Resource parent, User user);

    Resource editDirectory(Resource directory, String newName);

    void delete(Resource resource, User user);

    Resource find(Long id, User user);

    List<Resource> findTopResources(Resource parent, User user, Sort sort);

    List<Resource> loadMoreResources(Resource parent, User user, Sort sort, Integer offset);

    List<BreadcrumbItem> getBreadcrumbs(Resource directory);

    Resource rename(Resource resource, String newName);

    Resource addFile(MultipartFile file, User user, Resource parent);

    List<Resource> addFiles(MultipartFile[] files, User user, Resource parent);

    String modifyNameIfDuplicated(String name, User user, Resource parent, Resource.Type type);

    boolean hasUserEnoughSpaceToUploadFile(User user, long size);

    FilePreviewResponse getFilePreviewResponse(Resource file);

    Path getFilePath(Resource file);

    void deleteAll(Long[] resourceIds, User user);

    List<Resource> search(User user, String name);

    int countResources(Resource directory, User user);

}
