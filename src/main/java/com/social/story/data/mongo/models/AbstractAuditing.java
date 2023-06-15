package com.social.story.data.mongo.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;

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
public abstract class AbstractAuditing {

    @CreatedBy
    private String createdBy;

    @Indexed
    @CreatedDate
    private LocalDateTime createdDt;

    @LastModifiedBy
    private String updatedBy;

    @LastModifiedDate
    private LocalDateTime updatedDt;
}
