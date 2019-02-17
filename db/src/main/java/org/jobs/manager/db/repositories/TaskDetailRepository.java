package org.jobs.manager.db.repositories;

import org.jobs.manager.db.entities.TaskDetailEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TaskDetailRepository extends ReactiveCrudRepository<TaskDetailEntity, String> {

    @Query("select d from TaskDetailEntity d where "
            + " (d.taskId = :taskId) ")
    Flux<TaskDetailEntity> findByTaskId(@Param("taskId") String taskId);
}
