package com.pcagrade.mason.jpa.revision;

import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import com.pcagrade.mason.transaction.author.AuthorNameResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.envers.RevisionListener;
import org.springframework.context.annotation.Lazy;

public class MasonRevisionListener implements RevisionListener {

    private static final Logger LOGGER = LogManager.getLogger(MasonRevisionListener.class);

    private final RevisionMessageService revisionMessageService;
    private final AuthorNameResolver authorNameResolver;

    public MasonRevisionListener(RevisionMessageService revisionMessageService, @Lazy AuthorNameResolver authorNameResolver) {
        this.revisionMessageService = revisionMessageService;
        this.authorNameResolver = authorNameResolver;
    }

    @Override
    public void newRevision(Object revisionEntity) {
        if (revisionEntity instanceof RevisionInfo revisionInfo) {
            var message = revisionMessageService.getMessage();

            if (StringUtils.isNotBlank(message)) {
                revisionInfo.setMessage(message);
            } else {
                LOGGER.warn("A new revision was created but no message was set");
            }

            var name = authorNameResolver.resolveAuthorName();

            if (StringUtils.isNotBlank(name)) {
                revisionInfo.setAuthor(name);
            } else {
                LOGGER.trace("A new revision was created but no author was set");
            }

        }
    }
}
