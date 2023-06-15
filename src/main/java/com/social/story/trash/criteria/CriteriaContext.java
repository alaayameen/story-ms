package com.social.story.trash.criteria;

import com.querydsl.core.BooleanBuilder;

public class CriteriaContext {

    private final ICriteriaBuilder criteriaBuilder;

    public CriteriaContext(ICriteriaBuilder criteriaBuilder){
        this.criteriaBuilder = criteriaBuilder;
    }

    public BooleanBuilder execute(Object obj){
        return criteriaBuilder.build(obj);
    }
}
