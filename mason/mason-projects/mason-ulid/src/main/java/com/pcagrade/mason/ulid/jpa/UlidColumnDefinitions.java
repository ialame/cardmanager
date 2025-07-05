package com.pcagrade.mason.ulid.jpa;

public class UlidColumnDefinitions {
    public static final String DEFINITION = " BINARY(16)";
    @Deprecated
    public static final String COMMENT = "(DC2Type:ulid)";
    @Deprecated
    public static final String NON_NULL = " BINARY(16) NOT NULL COMMENT '(DC2Type:ulid)'";
    @Deprecated
    public static final String DEFAULT_NULL = " BINARY(16) DEFAULT NULL COMMENT '(DC2Type:ulid)'";

    private UlidColumnDefinitions() { }
}
