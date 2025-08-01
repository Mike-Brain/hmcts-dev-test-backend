package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import uk.gov.hmcts.reform.dev.model.Task;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;
import uk.gov.hmcts.reform.dev.enumerations.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllTasksWhenEmpty() throws Exception {
        when(taskRepository.findAllByOrderByIdAsc()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/task/getAllTasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetAllTasksWithData() throws Exception {
        Task task = createTask();
        List<Task> tasks = Collections.singletonList(task);
        when(taskRepository.findAllByOrderByIdAsc()).thenReturn(tasks);

        mockMvc.perform(get("/task/getAllTasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void testAddTaskSuccess() throws Exception {
        Task newTask = createTask();
        when(taskRepository.save(any(Task.class))).thenReturn(newTask);

        mockMvc.perform(post("/task/addTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void testAddTaskValidationError() throws Exception {
        Task invalidTask = Task.builder()
                .description("No title")
                .status(TaskStatus.NOT_STARTED)
                .dueDate(LocalDate.of(2024, 12, 31))
                .build();

        mockMvc.perform(post("/task/addTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void testGetTaskByIdFound() throws Exception {
        Task task = createTask();
        when(taskRepository.findTaskById(1)).thenReturn(task);

        mockMvc.perform(get("/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        when(taskRepository.findTaskById(1)).thenReturn(null);

        mockMvc.perform(get("/task/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTaskSuccess() throws Exception {
        Task existingTask = createTask();
        Task updateData = createTask();
        updateData.setTitle("Updated Task");
        
        when(taskRepository.findTaskById(1)).thenReturn(existingTask);
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        mockMvc.perform(put("/task/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @Test
    void testUpdateTaskNotFound() throws Exception {
        Task updateData = createTask();
        when(taskRepository.findTaskById(1)).thenReturn(null);

        mockMvc.perform(put("/task/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTaskSuccess() throws Exception {
        when(taskRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(delete("/task/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTaskNotFound() throws Exception {
        when(taskRepository.existsById(1)).thenReturn(false);

        mockMvc.perform(delete("/task/1"))
                .andExpect(status().isNotFound());
    }

    private Task createTask() {
        return Task.builder()
                .id(1)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.NOT_STARTED)
                .dueDate(LocalDate.of(2024, 12, 31))
                .createdDate(LocalDateTime.now())
                .build();
    }
}
