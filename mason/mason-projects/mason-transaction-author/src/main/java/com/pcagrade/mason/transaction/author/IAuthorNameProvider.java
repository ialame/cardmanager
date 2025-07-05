package com.pcagrade.mason.transaction.author;

import jakarta.annotation.Nonnull;

@FunctionalInterface
public interface IAuthorNameProvider {

    @Nonnull
    String getAuthorName();
}
