package org.example.schoolerp.security.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.example.schoolerp.security.UserDetailsServiceImpl;
import org.example.schoolerp.security.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class JwtAuthFilterTenantContextTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);
    private final JwtAuthFilter filter = new JwtAuthFilter(jwtService, userDetailsService);

    @AfterEach
    void clearContext() {
        TenantContext.clear();
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void validToken_setsTenantContextBeforeLoadingUser_thenClearsAfterChain() throws Exception {
        UUID orgId = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer faketoken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("faketoken")).thenReturn("alice");
        when(jwtService.extractOrganizationId("faketoken")).thenReturn(orgId);
        when(jwtService.isValid("faketoken", "alice")).thenReturn(true);

        UserDetails alice = new User("alice", "irrelevant", List.of());

        // The critical assertion: by the time loadUserByUsername is called,
        // TenantContext must ALREADY be set to orgId. This directly verifies
        // the ordering fix (extract + set BEFORE loadUserByUsername).
        when(userDetailsService.loadUserByUsername("alice")).thenAnswer(invocation -> {
            assertThat(TenantContext.get()).isEqualTo(orgId);
            return alice;
        });

        FilterChain chain = mock(FilterChain.class);
        doAnswer(inv -> {
            // Tenant should also still be set while the rest of the chain runs
            assertThat(TenantContext.get()).isEqualTo(orgId);
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        // And cleared once the filter is fully done
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void chainThrows_tenantContextStillClearedAfterward() throws Exception {
        UUID orgId = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer faketoken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("faketoken")).thenReturn("alice");
        when(jwtService.extractOrganizationId("faketoken")).thenReturn(orgId);
        when(jwtService.isValid("faketoken", "alice")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("alice"))
            .thenReturn(new User("alice", "irrelevant", List.of()));

        FilterChain chain = mock(FilterChain.class);
        doThrow(new ServletException("downstream boom")).when(chain).doFilter(request, response);

        try {
            filter.doFilterInternal(request, response, chain);
        } catch (ServletException expected) {
            // expected -- we only care that the finally block still ran
        }

        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void loadUserByUsernameThrows_tenantContextStillClearedAfterward() throws Exception {
        // Covers the case where the user lookup itself fails (e.g. deleted account,
        // DB error) after TenantContext was already set -- context must not leak.
        UUID orgId = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer faketoken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("faketoken")).thenReturn("alice");
        when(jwtService.extractOrganizationId("faketoken")).thenReturn(orgId);
        when(userDetailsService.loadUserByUsername("alice"))
            .thenThrow(new RuntimeException("db exploded"));

        FilterChain chain = mock(FilterChain.class);

        try {
            filter.doFilterInternal(request, response, chain);
        } catch (RuntimeException expected) {
            // expected
        }

        verifyNoInteractions(chain);
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void noAuthHeader_neverTouchesTenantContext() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(); // no Authorization header
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(TenantContext.get()).isNull();
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void nonBearerAuthHeader_neverTouchesTenantContext() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic somebase64stuff");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(TenantContext.get()).isNull();
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void invalidToken_doesNotAuthenticate_contextClearedAfterward() throws Exception {
        UUID orgId = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer badtoken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("badtoken")).thenReturn("alice");
        when(jwtService.extractOrganizationId("badtoken")).thenReturn(orgId);
        when(jwtService.isValid("badtoken", "alice")).thenReturn(false); // invalid
        when(userDetailsService.loadUserByUsername("alice"))
            .thenReturn(new User("alice", "irrelevant", List.of()));

        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void tokenWithNullOrgClaim_resolvesToNoTenant_notStaleThreadTenant() throws Exception {
        // Simulates a malformed/legacy token with no organization_id claim.
        // TenantContext.set(null) must behave like "no tenant," not silently
        // reuse a stale tenant left on this thread by a previous request.
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer badtoken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("badtoken")).thenReturn("bob");
        when(jwtService.extractOrganizationId("badtoken")).thenReturn(null);
        when(jwtService.isValid("badtoken", "bob")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("bob"))
            .thenReturn(new User("bob", "irrelevant", List.of()));

        FilterChain chain = mock(FilterChain.class);
        doAnswer(inv -> {
            assertThat(TenantContext.get()).isNull(); // must NOT be some stale prior tenant
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);
    }
}