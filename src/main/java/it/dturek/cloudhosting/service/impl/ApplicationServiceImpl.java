package it.dturek.cloudhosting.service.impl;

import it.dturek.cloudhosting.service.ApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Arrays;
import java.util.List;

@Service
@ApplicationScope
public class ApplicationServiceImpl implements ApplicationService {

    private static final String COMMA = ",";

    @Value("${drive.space-per-user}")
    private long spacePerUser;

    @Value("${drive.path}")
    private String drivePath;

    @Value("${base_url}")
    private String baseUrl;

    @Value("${cdn_url}")
    private String cdnUrl;

    @Value("${registration.enabled}")
    private boolean registrationEnabled;

    @Value("${drive.readable_mime_types}")
    private String readableMimeTypes;

    @Override
    public long getAvailableSpacePerUser() {
        return spacePerUser;
    }

    @Override
    public String getDrivePath() {
        return drivePath;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getCdnUrl() {
        return cdnUrl;
    }

    @Override
    public boolean isRegistrationEnabled() {
        return registrationEnabled;
    }

    @Override
    public List<String> getReadableMimeTypes() {
        return Arrays.asList(readableMimeTypes.split(COMMA));
    }
}
