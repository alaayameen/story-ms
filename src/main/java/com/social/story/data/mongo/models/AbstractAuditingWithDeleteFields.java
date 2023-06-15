package com.social.story.data.mongo.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author ayameen
 *
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractAuditingWithDeleteFields extends AbstractAuditing {

    private String reportDeletedBy;

    private LocalDateTime reportDeletedDt;

    private String reportId;

    private Boolean isDeletedByReport;
}
