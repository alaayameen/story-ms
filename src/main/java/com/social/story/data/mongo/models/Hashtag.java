package com.social.story.data.mongo.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author ayameen
 *
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Document(collection = "hashtag")
public class Hashtag extends AbstractAuditing {
	
	@Id
	private String id;

	@Indexed
	private String text;

}
