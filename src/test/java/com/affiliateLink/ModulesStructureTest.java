package com.affiliateLink;
class ModulesStructureTest {

    @Test
    void shouldRespectModuleBoundaries() {
        ApplicationModules.of(AffiliateLinkApplication.class).verify();
    }
}