package com.social.story.trash.criteria;

import com.querydsl.core.BooleanBuilder;

public interface ICriteriaBuilder {
    BooleanBuilder build(Object criteria);
}
