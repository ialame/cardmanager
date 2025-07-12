package com.pcagrade.mason.jpa.revision;

import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;

public record RevisionDTO<T>(
		int number,
		LocalDateTime modificationDate,
		RevisionMetadata.RevisionType type,
		String author,
		String message,
		T data
) {

	public static <T> RevisionDTO<T> of(Revision<Integer, T> revision) {
		var metadata = revision.getMetadata();
		var revisionInfo = metadata.getDelegate() instanceof RevisionInfo info ? info : null;

		return new RevisionDTO<>(
				metadata.getRequiredRevisionNumber(),
				LocalDateTime.ofInstant(metadata.getRequiredRevisionInstant(), ZoneId.systemDefault()),
				metadata.getRevisionType(),
				revisionInfo != null ? revisionInfo.getAuthor() : "",
				revisionInfo != null ? revisionInfo.getMessage() : "",
				revision.getEntity()
		);
	}

	public <U> RevisionDTO<U> map(Function<T, U> mapper) {
		return new RevisionDTO<>(number, modificationDate, type, author, message, mapper.apply(data));
	}
}
