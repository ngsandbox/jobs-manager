package org.jobs.manager.db.repositories;

import org.jobs.manager.db.entities.ScheduleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ScheduleRepository extends ReactiveCrudRepository<ScheduleEntity, String> {


    @Query("select s from ScheduleEntity s where "
            + " (s.taskId = :taskId) ")
    Flux<ScheduleEntity> findByTaskId(@Param("taskId") String taskId);

}
