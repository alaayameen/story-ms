package com.social.story.data.mongo.models;

import com.social.swagger.called.user.model.PublicUserInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @author ayameen
 *
 */
@Getter
@Setter
@SuperBuilder
@Document("story")
@NoArgsConstructor
public class Story extends AbstractAuditingWithDeleteFields {

    @Id
    @Field(name = "id", targetType = FieldType.OBJECT_ID)
    String id;

    @Field("caption")
    String caption;

    @Indexed
    @Field(name =  "userId", targetType = FieldType.OBJECT_ID)
    String userId;

    @Field(name = "mediaDetails")
    MediaDetails mediaDetails;

    @DBRef
    List<Hashtag> hashtags;

    List<String> seenByIds;

    @Transient
    List<PublicUserInfo> seenBy = null;

    Boolean seen;

    @Transient
    PublicUserInfo publicUserInfo;

    private Integer usersSeenNumber;

    private Boolean isOwnerUserActive;

    private Boolean isOwnerAccountMarkedForDelete;

	@Indexed(name = "expireAt", expireAfterSeconds = 0)
	private Date expireAt;
}
