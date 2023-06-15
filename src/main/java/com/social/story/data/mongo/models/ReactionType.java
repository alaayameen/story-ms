/**
 * 
 */
package com.social.story.data.mongo.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author mshawahneh
 *
 */
public enum ReactionType {
	LIKE("LIKE"),
	LOVE("LOVE"),
	HAHA("HAHA"),
	WOW("WOW"),
	SAD("SAD"),
	ANGRY("ANGRY");

	private String value;

	private ReactionType(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return value;
	}

	@JsonCreator
	public static ReactionType fromValue(String text) {
		return ReactionType.valueOf(text);
	}
}
