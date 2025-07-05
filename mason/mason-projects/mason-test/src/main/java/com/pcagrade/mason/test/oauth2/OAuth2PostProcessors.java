package com.pcagrade.mason.test.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.Arrays;

public class OAuth2PostProcessors {

    private OAuth2PostProcessors() {}

    public static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtWithRoles(String... authorities) {
        return SecurityMockMvcRequestPostProcessors.jwt().authorities(Arrays.stream(authorities)
                .<GrantedAuthority>map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toList());
    }
}
