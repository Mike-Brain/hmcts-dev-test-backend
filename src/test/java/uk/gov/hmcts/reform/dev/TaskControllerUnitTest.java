package uk.gov.hmcts.reform.dev;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import uk.gov.hmcts.reform.dev.controllers.TaskController;
import uk.gov.hmcts.reform.dev.model.Task;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;
import uk.gov.hmcts.reform.dev.enumerations.TaskStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskController taskController;

    @Test
    void testGetAllTasksWhenEmpty() {
        when(taskRepository.findAllByOrderByIdAsc()).thenReturn(Collections.emptyList());

        ResponseEntity<Iterable<Task>> result = taskController.getAllTasks();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(((List<Task>) result.getBody()).isEmpty());
    }

    @Test
    void testGetAllTasksWithData() {
        Task task = createTask();
        when(taskRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(task));

        ResponseEntity<Iterable<Task>> result = taskController.getAllTasks();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(Arrays.asList(task), result.getBody());
    }

    @Test
    void testAddTaskSuccess() {
        Task newTask = createTask();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(taskRepository.save(newTask)).thenReturn(newTask);

        ResponseEntity<?> result = taskController.addTask(newTask, bindingResult);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(newTask, result.getBody());
    }

    @Test
    void testAddTaskValidationError() {
        Task invalidTask = Task.builder().build();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(
            new FieldError("task", "title", "Title is required")
        ));

        ResponseEntity<?> result = taskController.addTask(invalidTask, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void testGetTaskByIdFound() {
        Task task = createTask();
        when(taskRepository.findTaskById(1)).thenReturn(task);

        ResponseEntity<Task> result = taskController.getTaskById(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(task, result.getBody());
    }

    @Test
    void testGetTaskByIdNotFound() {
        when(taskRepository.findTaskById(1)).thenReturn(null);

        ResponseEntity<Task> result = taskController.getTaskById(1);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testUpdateTaskSuccess() {
        Task existingTask = createTask();
        Task updateData = createTask();
        updateData.setTitle("Updated Title");
        
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(taskRepository.findTaskById(1)).thenReturn(existingTask);
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        ResponseEntity<?> result = taskController.updateTask(1, updateData, bindingResult);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Updated Title", ((Task) result.getBody()).getTitle());
    }

    @Test
    void testUpdateTaskNotFound() {
        Task updateData = createTask();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(taskRepository.findTaskById(1)).thenReturn(null);

        ResponseEntity<?> result = taskController.updateTask(1, updateData, bindingResult);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testDeleteTaskSuccess() {
        when(taskRepository.existsById(1)).thenReturn(true);

        ResponseEntity<Void> result = taskController.deleteTaskById(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void testDeleteTaskNotFound() {
        when(taskRepository.existsById(1)).thenReturn(false);

        ResponseEntity<Void> result = taskController.deleteTaskById(1);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    private Task createTask() {
        return Task.builder()
                .id(1)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.NOT_STARTED)
                .dueDate(LocalDate.of(2024, 12, 31))
                .build();
    }
}

