package org.jobs.manager.db.repositories;

import org.jobs.manager.db.model.TaskDetailEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDetailRepository extends CrudRepository<TaskDetailEntity, String>,
        JpaSpecificationExecutor<TaskDetailRepository> {

    @Query("select d from TaskDetailEntity d where "
            + " (d.taskId = :taskId) ")
    List<TaskDetailEntity> findByTaskId(@Param("taskId") String taskId);
}
