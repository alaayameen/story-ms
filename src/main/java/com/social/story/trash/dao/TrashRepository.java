package com.social.story.trash.dao;

import com.social.story.trash.model.Trash;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author ayameen
 *
 */
@Repository
public interface TrashRepository extends MongoRepository<Trash, String>, QuerydslPredicateExecutor<Trash> {
    List<Trash> findByUserIdAndCommandAndEntityName(String userId, String command, String entityName);
    List<Trash> findByEntityIdInAndCommandAndEntityName(List<String> ids, String command, String entityName);
    List<Trash> findByPostIdAndCommand(String postId, String deleteCommand);
    List<Trash> findByVideoIdAndCommand(String postId, String deleteCommand);
    List<Trash> findByParentIdInAndCommandAndEntityName(List<String> ids, String command, String entityName);
}
