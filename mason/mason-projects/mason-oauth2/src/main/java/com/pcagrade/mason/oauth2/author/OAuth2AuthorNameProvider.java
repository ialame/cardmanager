package com.pcagrade.mason.oauth2.author;

import com.pcagrade.mason.transaction.author.IAuthorNameProvider;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuth2AuthorNameProvider implements IAuthorNameProvider {

    @Nonnull
    @Override
    public String getAuthorName() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return "";
        }

        var user = authentication.getPrincipal() instanceof OAuth2User u ? u : null;
        var name = user != null ? user.getAttribute("name") : "";

        return name instanceof String s && StringUtils.isNotBlank(s) ? s : "";
    }
}
