package com.affiliateLink;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

public class ModularityTests {


    @Test
    void displayModules() {
        ApplicationModules.of(AffiliateLinkApplication.class).forEach(System.out::println);
    }

    @Test
    void verifyModules() {
        ApplicationModules.of(AffiliateLinkApplication.class).verify();
    }
}
