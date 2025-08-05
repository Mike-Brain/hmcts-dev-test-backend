package uk.gov.hmcts.reform.dev.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.model.Task;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Get all tasks ordered by ID.
     */
    @GetMapping("/getAllTasks")
    public ResponseEntity<Iterable<Task>> getAllTasks() {
        List<Task> tasks = taskRepository.findAllByOrderByIdAsc();
        if (tasks.isEmpty()) {
            log.info("No tasks found");
            return ok(emptyList());
        }
        return ok(tasks);
    }

    /**
     * Add a new task.
     */
    @PostMapping("/addTask")
    public ResponseEntity<?> addTask(@Valid @RequestBody Task newTask, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return badRequest().body(errors);
        }

        if (newTask.getCreatedDate() == null) {
            newTask.setCreatedDate(LocalDateTime.now());
        }

        try {
            Task createdTask = taskRepository.save(newTask);
            return ok(createdTask);
        } catch (Exception e) {
            log.error("Error saving task: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to save task");
            return status(500).body(error);
        }
    }

    /**
     * Get a task by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable int id) {
        Task task = taskRepository.findTaskById(id);
        if (task == null) {
            return notFound().build();
        }
        return ok(task);
    }

    /**
     * Update a task by ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable int id, 
                                         @Valid @RequestBody Task updatedTask, 
                                         BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return badRequest().body(errors);
        }

        Task existingTask = taskRepository.findTaskById(id);
        if (existingTask == null) {
            return notFound().build();
        }

        try {
            // Update the fields (all are required now due to @Valid)
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setStatus(updatedTask.getStatus());
            existingTask.setDueDate(updatedTask.getDueDate());

            Task savedTask = taskRepository.save(existingTask);
            return ok(savedTask);
        } catch (Exception e) {
            log.error("Error updating task: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update task");
            return status(500).body(error);
        }
    }

    /**
     * Delete a task by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable int id) {
        if (!taskRepository.existsById(id)) {
            return notFound().build();
        }

        taskRepository.deleteById(id);
        return noContent().build();
    }

}
