package org.jobs.manager.db.repositories;

import org.jobs.manager.db.model.JobHistoryEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public interface JobHistoryRepository extends CrudRepository<JobHistoryEntity, String>,
        JpaSpecificationExecutor<JobHistoryEntity> {

    @Query("select j from JobHistoryEntity j where j.jobId = :jobId")
    List<JobHistoryEntity> findByJobId(@Param("jobId") String jobId);

    @Query("select j from JobHistoryEntity j where  (j.taskId = :taskId) ")
    @EntityGraph(value = "JobHistoryEntity.full", type = FETCH)
    List<JobHistoryEntity> findByTaskId(@Param("taskId") String taskId);

}
