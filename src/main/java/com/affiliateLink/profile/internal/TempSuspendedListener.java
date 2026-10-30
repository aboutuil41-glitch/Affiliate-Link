package com.affiliateLink.profile.internal;

import com.affiliateLink.profile.ProfileSuspended;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class TempSuspendedListener {
    private static final Logger log = LoggerFactory.getLogger(TempSuspendedListener.class);

    @ApplicationModuleListener
    void on(ProfileSuspended event) {
        log.info("Profile suspended: {}", event.profileId());
    }
}
