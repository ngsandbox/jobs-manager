package org.jobs.manager.db.repositories;

import org.jobs.manager.db.model.TaskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public interface TaskRepository extends CrudRepository<TaskEntity, String>,
        JpaSpecificationExecutor<TaskEntity> {

    @Query("select t from TaskEntity t where "
            + " (t.strategyCode = :strategyCode) ")
    @EntityGraph(value = "TaskEntity.details", type = FETCH)
    Optional<TaskEntity> findByStrategy(@Param("strategyCode") String strategyCode);

    @Query("select t from TaskEntity t where "
            + " (t.taskId = :taskId) ")
    @EntityGraph(value = "TaskEntity.all", type = FETCH)
    Optional<TaskEntity> findByTaskId(@Param("taskId") String  taskId);

    @Query("from TaskEntity t " +
            //" inner join fetch t.schedule as s " +
            " where (t.schedule.active = true and t.schedule.startDate <= :date) " +
            " order by t.schedule.priority desc ")
    @EntityGraph(value = "TaskEntity.full", type = FETCH)
    List<TaskEntity> findActiveTasks(@Param("date") LocalDateTime date, Pageable pageable);
}