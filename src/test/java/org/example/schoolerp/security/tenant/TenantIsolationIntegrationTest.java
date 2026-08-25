package org.example.schoolerp.security.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


import org.example.schoolerp.identity.entity.*;
import org.example.schoolerp.identity.repos.UserRepository;
import org.example.schoolerp.organization.Organization;
import org.example.schoolerp.testsupport.TenantFixtures;
import org.example.schoolerp.testsupport.TenantTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;


@SpringBootTest
public class TenantIsolationIntegrationTest  extends TenantTestSupport{
   
    @Autowired private TenantFixtures tenantFixtures;
    @Autowired private UserRepository userRepository;
    

    private Organization orgA;
    private Organization orgB;

    private TenantFixtures.TenantFixture tenantA;
    private TenantFixtures.TenantFixture tenantB;


    

    @BeforeEach
    void setup() {
        orgA = tenantFixtures.createOrg("A");
        orgB = tenantFixtures.createOrg("B");

        tenantA = asTenant(orgA.getId(), () -> tenantFixtures.createOrgWithUser(orgA, "TA", "TAAA"));
        tenantB = asTenant(orgB.getId(), () -> tenantFixtures.createOrgWithUser(orgB, "TB", "TBBB"));
    }

    @AfterEach
    void cleanupTenantContext() {
        asTenantVoid(tenantA.orgId(), () -> tenantFixtures.deleteTenantData());
        asTenantVoid(tenantB.orgId(), () -> tenantFixtures.deleteTenantData());

        newTx().executeWithoutResult(state -> tenantFixtures.deleteAllOrganizations());
    }

    @Test
    void teanantA_shouldOnlySeeTenantAUsers() {
        var users = asTenant(tenantA.orgId(), () -> userRepository.findAll());
        assertThat(users).isNotEmpty().allMatch(u -> u.getOrganizationId().equals(tenantA.orgId()));
    }
    
    @Test
    void tenantA_shouldNotSeeTenantBUsers() {
        var users = asTenant(tenantA.orgId(), () -> userRepository.findAll());
        assertThat(users).noneMatch(u -> u.getOrganizationId().equals(tenantB.orgId()));
    }

    @Test
    void noTenantContext_shouldSeeNoUsers() {
        TenantContext.clear();
        var users = newTx().execute(status -> userRepository.findAll());
        assertThat(users).isEmpty();
    }

    @Test
    void noTenantContext_shouldRejectWritesWithAssignedTenant() {
        TenantContext.clear();
        assertThatThrownBy(() ->
            newTx().executeWithoutResult(status -> 
                userRepository.save(new User("orphan", orgA))
            )
        ).isInstanceOf(DataIntegrityViolationException.class);  
    }
}
