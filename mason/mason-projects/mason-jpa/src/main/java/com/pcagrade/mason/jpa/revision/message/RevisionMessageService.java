package com.pcagrade.mason.jpa.revision.message;

import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class RevisionMessageService {

    private static final Logger LOGGER = LogManager.getLogger(RevisionMessageService.class);

    public void addMessage(String format, Object... args) {
        addMessage(new MessageFormat(format).format(args));
    }

    public void addMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        LOGGER.trace("Adding revision message: {}", message);

        var messages = getMessages();

        if (messages == null) {
            TransactionSynchronizationManager.bindResource(this, new ArrayList<>(List.of(message)));
        } else {
            messages.add(message);
        }
    }
    public String getMessage() {
        var messages = getMessages();

        if (CollectionUtils.isNotEmpty(messages)) {
            TransactionSynchronizationManager.unbindResource(this);
            return String.join("\n", messages);
        }
        return "";
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private List<String> getMessages() {
        return (List<String>) TransactionSynchronizationManager.getResource(this);
    }

}
