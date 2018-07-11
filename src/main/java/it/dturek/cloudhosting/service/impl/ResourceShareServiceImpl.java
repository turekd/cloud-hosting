package it.dturek.cloudhosting.service.impl;

import it.dturek.cloudhosting.dao.ResourceShareDao;
import it.dturek.cloudhosting.domain.ResourceShareSuccess;
import it.dturek.cloudhosting.domain.ResourceShareType;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.ResourceShare;
import it.dturek.cloudhosting.service.ApplicationService;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.service.ResourceShareService;
import it.dturek.cloudhosting.util.Md5Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;

@Service
public class ResourceShareServiceImpl implements ResourceShareService {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(ResourceShareServiceImpl.class);

    private static final long HOUR = 3600;
    private static final long DAY = 86400;
    private static final long WEEK = 604800;

    @Autowired
    private ResourceShareDao resourceShareDao;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private InternationalizationService internationalizationService;

    @Override
    public ResourceShareSuccess add(Resource resource, ResourceShareType type) {
        ResourceShare resourceShare = new ResourceShare();
        resourceShare.setResource(resource);
        resourceShare.setCreatedAt(new Timestamp(Calendar.getInstance().getTime().getTime()));
        resourceShare.setType(type);
        resourceShare.setKey(createKey(resource));
        resourceShareDao.add(resourceShare);

        ResourceShareSuccess resourceShareSuccess = new ResourceShareSuccess();
        resourceShareSuccess.setResourceShare(resourceShare);
        resourceShareSuccess.setMessage(getSuccessMessage(resourceShare, getUrl(resourceShare)));
        return resourceShareSuccess;
    }

    @Override
    public ResourceShare findByKey(String key) {
        return resourceShareDao.findByKey(key);
    }

    @Override
    public boolean isShareable(ResourceShare resourceShare) {
        if (resourceShare == null) {
            return false;
        }
        switch (resourceShare.getType()) {
            case ONCE:
                return true;
            case TEMPORARILY_HOUR:
                return checkIsValidTime(resourceShare, HOUR);
            case TEMPORARILY_DAY:
                return checkIsValidTime(resourceShare, DAY);
            case TEMPORARILY_WEEK:
                return checkIsValidTime(resourceShare, WEEK);
            default:
                throw new IllegalArgumentException();
        }
    }

    private boolean checkIsValidTime(ResourceShare resourceShare, long secondsToAdd) {
        long now = Calendar.getInstance().getTime().getTime();
        return resourceShare.getCreatedAt().getTime() + secondsToAdd <= now;
    }

    @Override
    public void handleAfterShare(ResourceShare resourceShare) {
        if (resourceShare.getType().equals(ResourceShareType.ONCE)) {
            resourceShareDao.delete(resourceShare);
        }
    }

    private String createKey(Resource resource) {
        return Md5Util.getHash(System.currentTimeMillis() + "+" + resource.getId());
    }

    private String getUrl(ResourceShare resourceShare) {
        return applicationService.getBaseUrl()
                + internationalizationService.getMessage("share.url")
                + "/"
                + resourceShare.getKey();
    }

    private String getSuccessMessage(ResourceShare resourceShare, String url) {
        switch (resourceShare.getType()) {
            case ONCE:
                return internationalizationService.getMessage("share.success.once", url);
            case TEMPORARILY_HOUR:
                return internationalizationService.getMessage("share.success.hour", url);
            case TEMPORARILY_DAY:
                return internationalizationService.getMessage("share.success.day", url);
            case TEMPORARILY_WEEK:
                return internationalizationService.getMessage("share.success.week", url);
        }
        throw new IllegalArgumentException();
    }
}
