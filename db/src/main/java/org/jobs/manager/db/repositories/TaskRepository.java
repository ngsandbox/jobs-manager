package org.jobs.manager.db.repositories;

import org.jobs.manager.db.entities.TaskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public interface TaskRepository extends ReactiveCrudRepository<TaskEntity, String> {

    @Query("select t from TaskEntity t where "
            + " (t.strategyCode = :strategyCode) ")
    @EntityGraph(value = "TaskEntity.details", type = FETCH)
    Mono<TaskEntity> findByStrategy(@Param("strategyCode") String strategyCode);

    @Query("select t from TaskEntity t " +
            " where t.schedule.active and t.schedule.startDate <= :date" +
            " order by t.schedule.priority desc ")
    @EntityGraph(value = "TaskEntity.schedule", type = FETCH)
    Flux<TaskEntity> findConsumersBooks(@Nullable @Param("date") LocalDateTime date, Pageable pageable);
}
