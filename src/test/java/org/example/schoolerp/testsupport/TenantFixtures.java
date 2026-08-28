package org.example.schoolerp.testsupport;

import java.util.UUID;

import org.example.schoolerp.identity.entity.AuthAccount;
import org.example.schoolerp.identity.entity.User;
import org.example.schoolerp.identity.repos.AuthAccountRepository;
import org.example.schoolerp.identity.repos.UserRepository;
import org.example.schoolerp.organization.Organization;
import org.example.schoolerp.organization.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TenantFixtures {
   
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private UserRepository userRepository;;
    @Autowired private AuthAccountRepository authAccountRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public record TenantFixture(UUID orgId, Organization org, User user, AuthAccount authAccount) {}
    
    public Organization createOrg(String name) {
        return organizationRepository.save(new Organization(name));
    }

    /**
     * Creates an loginable user. Caller is responsible for running this
     * inside asTenant(...)/newTx() with TenantContext already set.
     * (org creation is tenant-native) followed by the correct tenant.
     */
    public TenantFixture createUser(Organization org, String username, String rawPassword) {
        User user = userRepository.save(new User(username));
        AuthAccount authAccount = new AuthAccount(user, passwordEncoder.encode(rawPassword));
        authAccountRepository.save(authAccount);
        return new TenantFixture(org.getId(), org, user, authAccount);
    }

    /**
     * Deletes the User/AuthAccount rows that belong to ONE tenant(Set by the caller).
     * Must be called witha fresh REQUIRES_NEW transaction wrapping it
     *  
     */
    public void deleteTenantData() {
            authAccountRepository.deleteAll();
            userRepository.deleteAll();
    }

    /**
     * Deletes all organizations, Safe to call with no tenant context set
     */
    public void deleteAllOrganizations() {
        organizationRepository.deleteAll();
    }
}
