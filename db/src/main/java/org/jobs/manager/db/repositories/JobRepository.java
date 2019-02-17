package org.jobs.manager.db.repositories;

import org.jobs.manager.db.entities.JobEntity;
import org.jobs.manager.db.entities.TaskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
public interface JobRepository extends ReactiveCrudRepository<JobEntity, String> {

    @Query("select j from JobEntity j where "
            + " (j.jobId = :jobId) ")
    Flux<JobEntity> findByJobId(@Param("jobId") String jobId);

    @Query("select j from JobEntity j where "
            + " (j.taskId = :taskId) ")
    Flux<JobEntity> findByTaskId(@Param("taskId") String taskId);

}
