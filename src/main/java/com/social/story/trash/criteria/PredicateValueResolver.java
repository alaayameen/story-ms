package com.social.story.trash.criteria;

import com.social.swagger.model.contentmanagement.StringFilter;


public class PredicateValueResolver {

    public void resolveValue(Object filter) {
        if ((filter instanceof StringFilter) && ((StringFilter) filter).getOperation() == StringFilter.OperationEnum.LIKE) {
            String val = ((StringFilter) filter).getValue();
            val = "%".concat(val).concat("%");
            ((StringFilter) filter).setValue(val);
        }
    }
}
