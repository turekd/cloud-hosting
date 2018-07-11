package it.dturek.cloudhosting.service.impl;

import it.dturek.cloudhosting.dao.ResourceDao;
import it.dturek.cloudhosting.domain.BreadcrumbItem;
import it.dturek.cloudhosting.domain.FilePreviewResponse;
import it.dturek.cloudhosting.domain.Sort;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.service.ApplicationService;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.service.ResourceService;
import it.dturek.cloudhosting.service.UserService;
import it.dturek.cloudhosting.util.FileUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(ResourceServiceImpl.class);

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private InternationalizationService internationalizationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Override
    public Resource createDirectory(String name, Resource parent, User user) {
        if (name == null || name.isEmpty()) {
            name = internationalizationService.getMessage("drive.directory.default_name");
        }
        Resource directory = new Resource();
        directory.setType(Resource.Type.DIRECTORY);
        directory.setName(modifyNameIfDuplicated(name.trim(), user, parent, Resource.Type.DIRECTORY));
        directory.setParent(parent);
        directory.setModificationTime(getTime());
        directory.setSize(0);
        directory.setUser(user);
        resourceDao.create(directory);
        updateDirectoryModificationTime(directory.getParent());

        return directory;
    }

    @Override
    public Resource editDirectory(Resource directory, String newName) {
        return null;
    }

    @Override
    public void delete(Resource resource, User user) {
        switch (resource.getType()) {
            case DIRECTORY:
                deleteDirectory(resource, user);
                break;
            case FILE:
                deleteFile(resource, user);
                break;
            default:
                throw new IllegalArgumentException("Unkown resource type");
        }
    }

    @Override
    public Resource find(Long id, User user) {
        return resourceDao.find(id, user);
    }

    @Override
    public List<Resource> findTopResources(Resource parent, User user, Sort sort) {
        List<Resource> sortedResources = new ArrayList<>();
        List<Resource> notSortedResources = resourceDao.findResources(parent, user, sort);

        sortedResources.addAll(notSortedResources.stream().filter(r -> Resource.Type.DIRECTORY.equals(r.getType())).collect(Collectors.toList()));
        sortedResources.addAll(notSortedResources.stream().filter(r -> Resource.Type.FILE.equals(r.getType())).collect(Collectors.toList()));

        sortedResources.forEach(r -> r.setFriendlySize(FileUtil.getFriendlySize(r.getSize())));

        return sortedResources;
    }

    @Override
    public List<Resource> loadMoreResources(Resource parent, User user, Sort sort, Integer offset) {
        List<Resource> sortedResources = new ArrayList<>();
        List<Resource> notSortedResources = resourceDao.findResources(parent, user, sort, offset);

        sortedResources.addAll(notSortedResources.stream().filter(r -> Resource.Type.DIRECTORY.equals(r.getType())).collect(Collectors.toList()));
        sortedResources.addAll(notSortedResources.stream().filter(r -> Resource.Type.FILE.equals(r.getType())).collect(Collectors.toList()));

        sortedResources.forEach(r -> r.setFriendlySize(FileUtil.getFriendlySize(r.getSize())));

        return sortedResources;
    }

    @Override
    public List<BreadcrumbItem> getBreadcrumbs(Resource directory) {
        List<BreadcrumbItem> items = new ArrayList<>();
        if (directory == null) {
            return items;
        }

        if (directory.getId() != null) {
            List<BreadcrumbItem> tempList = new ArrayList<>();
            Resource parent = directory.getParent();
            while (parent != null) {
                tempList.add(new BreadcrumbItem(parent.getId(), parent.getName()));
                parent = parent.getParent();
            }

            // Reverse
            for (int i = tempList.size() - 1; i >= 0; i--) {
                items.add(tempList.get(i));
            }

            items.add(new BreadcrumbItem(directory.getId(), directory.getName()));
        }

        return items;
    }

    @Override
    public Resource rename(Resource resource, String newName) {
        resource.setName(newName);
        resourceDao.update(resource);
        return resource;
    }

    @Override
    public Resource addFile(MultipartFile file, User user, Resource parent) {
        Resource old = resourceDao.findByName(file.getOriginalFilename(), parent, user, Resource.Type.FILE);
        boolean alreadyExists = old != null;

        // Save to db
        Resource resource;

        if (alreadyExists) {
            resource = old;
            userService.subtractUsedSpace(user, old.getSize());
            updateParentDirectoriesSizeDown(file, parent);
            resource.setModificationTime(getTime());
            resource.setSize(file.getSize());
            resource.setContentType(file.getContentType());
        } else {
            resource = new Resource();
            resource.setType(Resource.Type.FILE);
            resource.setName(modifyNameIfDuplicated(file.getOriginalFilename().trim(), user, parent, Resource.Type.FILE));
            resource.setParent(parent);
            resource.setModificationTime(getTime());
            resource.setSize(file.getSize());
            resource.setUser(user);
            resource.setContentType(file.getContentType());
            resourceDao.create(resource);
        }

        // Update user space used
        userService.addUsedSpace(user, resource.getSize());

        // Update directory size
        updateParentDirectoriesSizeUp(file, parent);

        // Update directory modification time
        updateDirectoryModificationTime(resource.getParent());

        String filename = String.valueOf(resource.getId());

        String relativePath = FileUtil.createPathToFile(resource.getId()) + File.separator + filename;
        resource.setPath(relativePath);
        resourceDao.update(resource);

        File directory = new File(applicationService.getDrivePath() + FileUtil.createPathToFile(resource.getId()));
        directory.mkdirs();

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(directory.getPath() + File.separator + filename);
            Files.write(path, bytes);
        } catch (IOException e) {
            resourceDao.delete(resource);
            LOGGER.debug("Exception during saving resource: \n {}", e);
        }

        return resource;
    }

    @Override
    public List<Resource> addFiles(MultipartFile[] files, User user, Resource parent) {
        List<Resource> resources = new ArrayList<>();
        for (MultipartFile file : files) {
            resources.add(addFile(file, user, parent));
        }
        return resources;
    }

    private Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String modifyNameIfDuplicated(String name, User user, Resource parent, Resource.Type type) {
        if (type.equals(Resource.Type.DIRECTORY)) {
            String result = name;
            Resource resource = resourceDao.findByName(name, parent, user, type);
            int i = 1;
            while (resource != null) {
                result = String.format("%s (%d)", name, i++);
                resource = resourceDao.findByName(result, parent, user, type);
            }

            return result;
        }
        return name;
    }

    @Override
    public boolean hasUserEnoughSpaceToUploadFile(User user, long size) {
        long availableSpace = user.getSpaceAvailable();
        long usedSpace = user.getSpaceUsed();
        return usedSpace + size <= availableSpace;
    }

    @Override
    public FilePreviewResponse getFilePreviewResponse(Resource file) {
        FilePreviewResponse filePreviewResponse = new FilePreviewResponse();
        filePreviewResponse.setName(file.getName());
        if (applicationService.getReadableMimeTypes().contains(file.getContentType())) {
            try {
                filePreviewResponse.setContent(new String(Files.readAllBytes(Paths.get(getFilePath(file).toUri()))));
            } catch (IOException e) {
                LOGGER.debug(e);
            }
        }
        filePreviewResponse.setMimeType(file.getContentType());
        return filePreviewResponse;
    }

    @Override
    public Path getFilePath(Resource file) {
        String absolutePath =
                applicationService.getDrivePath()
                        + FileUtil.createPathToFile(file.getId())
                        + File.separator
                        + file.getId();
        return Paths.get(absolutePath);
    }

    @Override
    public void deleteAll(Long[] resourceIds, User user) {
        for (Long resourceId : resourceIds) {
            Resource resource = find(resourceId, user);
            if (resource != null) {
                delete(resource, user);
            }
        }
    }

    @Override
    public List<Resource> search(User user, String name) {
        List<Resource> resources = resourceDao.search(user, name);
        resources.forEach(r -> r.setFriendlySize(FileUtil.getFriendlySize(r.getSize())));
        return resources;
    }

    @Override
    public int countResources(Resource directory, User user) {
        return resourceDao.countResources(directory, user);
    }

    private void deleteFile(Resource resource, User user) {
        File file = new File(getFilePath(resource).toUri());
        boolean deleted = file.delete();
        if (deleted) {
            long size = resource.getSize();
            resourceDao.delete(resource);
            userService.subtractUsedSpace(user, size);
            updateDirectoryModificationTime(resource.getParent());
        } else {
            LOGGER.info("ERROR: Could not delete " + file.getPath());
        }
    }

    private void deleteDirectory(Resource resource, User user) {
        List<Resource> files = new ArrayList<>();
        List<Resource> directories = new ArrayList<>();

        collectChildrenResources(resourceDao.findResources(resource, user), user, files, directories);

        // Delete children's files
        for (int i = files.size() - 1; i >= 0; i--) {
            deleteFile(files.get(i), user);
        }

        // Delete children's directories
        for (int i = directories.size() - 1; i >= 0; i--) {
            resourceDao.delete(directories.get(i));
        }

        resourceDao.delete(resource);
        updateDirectoryModificationTime(resource.getParent());
    }

    private void updateDirectoryModificationTime(Resource resource) {
        if (resource != null) {
            resource.setModificationTime(getTime());
            resourceDao.update(resource);
        }
    }

    private void collectChildrenResources(List<Resource> children, User user, List<Resource> files, List<Resource> directories) {
        for (Resource child : children) {
            if (child.getType().equals(Resource.Type.DIRECTORY)) {
                directories.add(child);
            } else {
                files.add(child);
            }
            collectChildrenResources(resourceDao.findResources(child, user), user, files, directories);
        }
    }

    private void updateParentDirectoriesSizeUp(MultipartFile file, Resource parent) {
        while (parent != null) {
            parent.setSize(parent.getSize() + file.getSize());
            resourceDao.update(parent);
            parent = parent.getParent();
        }
    }

    private void updateParentDirectoriesSizeDown(MultipartFile file, Resource parent) {
        while (parent != null) {
            parent.setSize(parent.getSize() - file.getSize());
            resourceDao.update(parent);
            parent = parent.getParent();
        }
    }

}
