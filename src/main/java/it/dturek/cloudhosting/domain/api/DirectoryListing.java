package it.dturek.cloudhosting.domain.api;

import it.dturek.cloudhosting.domain.BreadcrumbItem;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.util.FileUtil;

import java.util.List;

public class DirectoryListing {

    private Resource directory;
    private List<Resource> resources;
    private List<BreadcrumbItem> breadcrumbs;
    private User user;
    private int total;

    public DirectoryListing(Resource directory, List<Resource> resources, List<BreadcrumbItem> breadcrumbs, User user, int total) {
        this.directory = directory;
        this.resources = resources;
        this.breadcrumbs = breadcrumbs;
        this.user = user;
        this.total = total;
    }

    public Resource getDirectory() {
        return directory;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public List<BreadcrumbItem> getBreadcrumbs() {
        return breadcrumbs;
    }

    public String getSpaceUsed() {
        return FileUtil.getFriendlySize(user.getSpaceUsed());
    }

    public String getSpaceAvailable() {
        return FileUtil.getFriendlySize(user.getSpaceAvailable());
    }

    public int getTotal() {
        return total;
    }

    public int getSpaceUsedPercentages() {
        double divide = (double) user.getSpaceUsed() / (double) user.getSpaceAvailable();
        return (int) Math.round(divide * 100);
    }
}
