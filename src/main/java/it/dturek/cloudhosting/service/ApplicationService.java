package it.dturek.cloudhosting.service;

import java.util.List;

public interface ApplicationService {

    long getAvailableSpacePerUser();

    String getDrivePath();

    String getBaseUrl();

    String getCdnUrl();

    boolean isRegistrationEnabled();

    List<String> getReadableMimeTypes();

}
