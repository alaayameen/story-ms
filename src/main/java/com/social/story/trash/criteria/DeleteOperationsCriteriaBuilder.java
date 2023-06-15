package com.social.story.trash.criteria;

import com.google.common.base.Strings;
import com.social.story.trash.model.QTrash;
import com.social.story.trash.utils.Constants;
import com.social.swagger.model.contentmanagement.DeleteOperationsCriteria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;

public class DeleteOperationsCriteriaBuilder extends PredicateValueResolver implements ICriteriaBuilder {

    @Override
    public BooleanBuilder build(Object criteriaObject) {
        DeleteOperationsCriteria criteria = (DeleteOperationsCriteria) criteriaObject;

        QTrash trash = QTrash.trash;
        BooleanBuilder builder = new BooleanBuilder();

        if (criteriaObject == null) {
            return builder;
        }

        builder.and(
                Expressions.predicate(
                        Ops.EQ,
                        trash.entityName,
                        Expressions.constant(Constants.CONTENT_TYPE.STORY.name())
                )
        );

        if (criteria.getCommand() != null) {
            resolveValue(criteria.getCommand());
            builder.and(
                    Expressions.predicate(
                            Constants.predicateOperators().get(criteria.getCommand().getOperation().toString()),
                            trash.command,
                            Expressions.constant(criteria.getCommand().getValue())
                    )
            );
        }

        if (criteria.getDeletedBy() != null) {
            resolveValue(criteria.getDeletedBy());
            builder.and(
                    Expressions.predicate(
                            Constants.predicateOperators().get(criteria.getDeletedBy().getOperation().toString()),
                            trash.deletedBy,
                            Expressions.constant(criteria.getDeletedBy().getValue())
                    )
            );
        }

        if (criteria.getReportId() != null && !Strings.isNullOrEmpty(criteria.getReportId().getValue())) {
            resolveValue(criteria.getReportId());
            builder.and(
                    Expressions.predicate(
                            Constants.predicateOperators().get(criteria.getReportId().getOperation().toString()),
                            trash.reportingId,
                            Expressions.constant(criteria.getReportId().getValue())
                    )
            );
        }

        if (criteria.getUserId() != null) {
            resolveValue(criteria.getUserId());
            builder.and(
                    Expressions.predicate(
                            Constants.predicateOperators().get(criteria.getUserId().getOperation().toString()),
                            trash.userId,
                            Expressions.constant(criteria.getUserId().getValue())
                    )
            );
        }

        if (criteria.getDeletedDate() != null) {
            builder.and(
                    Expressions.predicate(
                            Constants.predicateOperators().get(criteria.getDeletedDate().getOperation().toString()),
                            trash.deletedDate,
                            Expressions.constant(criteria.getDeletedDate().getValue())
                    )
            );
        }
        return builder;
    }
}
