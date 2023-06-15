package com.social.story.trash.utils;

import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ayameen
 *
 */
public interface Constants {

	int FETCH_BATCH_SIZE = 100;

	enum ID_TYPE {
		CONTENT_ID("CONTENT_ID"),
		USER_ID("USER_ID");

		private final String value;

		ID_TYPE(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	enum CONTENT_TYPE {
		STORY("STORY"),
		STORY_VIEW("STORY_VIEW");
        private final String value;

		CONTENT_TYPE(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public static Map<String, Operator> predicateOperators() {
		Map<String, Operator> operatorMap = new HashMap<>();
		operatorMap.put("eq", Ops.EQ);
		operatorMap.put("gt", Ops.GT);
		operatorMap.put("lt", Ops.LT);
		operatorMap.put("gte", Ops.GOE);
		operatorMap.put("lte", Ops.LOE);
		operatorMap.put("like", Ops.LIKE);
		operatorMap.put("likeIgnoreCase", Ops.LIKE_IC);
		return operatorMap;
	}

}
