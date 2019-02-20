package org.jobs.manager.db.repositories;


import org.jobs.manager.db.model.ScheduleEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<ScheduleEntity, String>,
        JpaSpecificationExecutor<ScheduleRepository> {


    @Query("select s from ScheduleEntity s where "
            + " s.task.taskId = :taskId ")
    List<ScheduleEntity> findByTaskId(@Param("taskId") String taskId);


    @Query("from ScheduleEntity as s where s.active = true and s.startDate <= :date")
    List<ScheduleEntity> findActiveTasks(@Param("date") LocalDateTime date, Pageable pageable);
}
