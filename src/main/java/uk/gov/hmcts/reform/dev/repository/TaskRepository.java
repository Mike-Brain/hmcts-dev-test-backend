package uk.gov.hmcts.reform.dev.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.dev.enumerations.TaskStatus;
import uk.gov.hmcts.reform.dev.model.Task;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {

    /**
     * Find a task by its ID.
     */
    Task findTaskById(int id);

    /**
     * Find all tasks ordered by ID in ascending order.
     */
    List<Task> findAllByOrderByIdAsc();

}
