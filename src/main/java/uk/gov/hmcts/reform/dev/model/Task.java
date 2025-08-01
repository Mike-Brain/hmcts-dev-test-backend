package uk.gov.hmcts.reform.dev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import uk.gov.hmcts.reform.dev.enumerations.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private LocalDateTime createdDate;
}
