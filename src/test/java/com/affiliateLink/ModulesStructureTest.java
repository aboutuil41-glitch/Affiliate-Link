package com.affiliateLink;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulesStructureTest {

    @Test
    void shouldRespectModuleBoundaries() {
        ApplicationModules.of(AffiliateLinkApplication.class).verify();
    }
}