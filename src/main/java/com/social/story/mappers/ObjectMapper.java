package com.social.story.mappers;

import java.util.List;

/**
 * @author ayameen
 *
 * Contract for a generic dto to entity mapper.
 *
 * @param <S> - Source type parameter.
 * @param <T> - Entity type parameter.
 */

public interface ObjectMapper<S, T> {

    T toTarget(S source);

    List <T> toTargets(List<S> sourceList);

    S toSource(T target);

    List <S> toSources(List<T> targetsList);
}
