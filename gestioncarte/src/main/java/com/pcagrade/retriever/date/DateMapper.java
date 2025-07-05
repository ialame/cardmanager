package com.pcagrade.retriever.date;

import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public class DateMapper {

    public LocalDate fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toLocalDate();
    }

    public LocalDateTime fromLocalDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay();
    }

}
