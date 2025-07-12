package com.pcagrade.mason.transaction.author;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class AuthorNameResolver {

    private static final Logger LOGGER = LogManager.getLogger(AuthorNameResolver.class);

    private final List<IAuthorNameProvider> authorNameProviders;

    public AuthorNameResolver(@Nullable List<IAuthorNameProvider> authorNameProviders) {
        if (CollectionUtils.isEmpty(authorNameProviders)) {
            LOGGER.warn("AuthorNameResolver was created with an empty list of IAuthorNameProvider instances. Author name resolution will always return an empty string.");
            this.authorNameProviders = Collections.emptyList();
        } else {
            this.authorNameProviders = authorNameProviders;
        }

    }

    @Nonnull
    public String resolveAuthorName() {
        for (IAuthorNameProvider authorNameProvider : authorNameProviders) {
            String authorName = authorNameProvider.getAuthorName();

            if (StringUtils.isNotBlank(authorName)) {
                return authorName;
            }
        }
        return "";
    }

}
