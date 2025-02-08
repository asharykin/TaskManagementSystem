package org.effectivemobile.tms.service;

import javax.persistence.EntityNotFoundException;
import org.effectivemobile.tms.dto.task.TaskCreateRequestDto;
import org.effectivemobile.tms.dto.task.TaskResponseDto;
import org.effectivemobile.tms.dto.task.TaskUpdateRequestDto;
import org.effectivemobile.tms.dto.task.TaskUpdateStatusRequestDto;
import org.effectivemobile.tms.entity.Task;
import org.effectivemobile.tms.entity.User;
import org.effectivemobile.tms.mapper.TaskMapper;
import org.effectivemobile.tms.repository.TaskRepository;
import org.effectivemobile.tms.repository.UserRepository;
import org.effectivemobile.tms.util.enums.Priority;
import org.effectivemobile.tms.util.enums.Role;
import org.effectivemobile.tms.util.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserService userService;

    private User admin;

    private User user;
    
    private Task task;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        user = new User();
        user.setId(2L);
        user.setUsername("user");
        user.setRole(Role.USER);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Status.WAITING);
        task.setPriority(Priority.LOW);
        task.setExecutor(user);
        task.setAuthor(admin);
    }

    @Test
    void create_ShouldSucceed_WhenAdminCreatesTask() {
        TaskCreateRequestDto dto = new TaskCreateRequestDto();
        dto.setTitle("New Task");
        dto.setDescription("Task Description");
        dto.setExecutorId(user.getId());

        Task task = new Task();
        task.setId(2L);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(Status.WAITING);
        task.setPriority(Priority.LOW);
        task.setExecutor(user);
        task.setAuthor(admin);

        TaskResponseDto expectedResponseDto = new TaskResponseDto();
        expectedResponseDto.setId(task.getId());
        expectedResponseDto.setTitle(task.getTitle());
        expectedResponseDto.setDescription(task.getDescription());
        expectedResponseDto.setStatus(task.getStatus());
        expectedResponseDto.setPriority(task.getPriority());
        expectedResponseDto.setExecutorId(task.getExecutor().getId());
        expectedResponseDto.setAuthorId(task.getAuthor().getId());

        when(userService.getCurrentUser()).thenReturn(admin);
        when(userRepository.findById(dto.getExecutorId())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.entityToResponseDto(any(Task.class))).thenReturn(expectedResponseDto);

        TaskResponseDto result = taskService.create(dto);

        assertEquals(expectedResponseDto.getId(), result.getId());
        assertEquals(expectedResponseDto.getTitle(), result.getTitle());
        assertEquals(expectedResponseDto.getDescription(), result.getDescription());
        assertEquals(expectedResponseDto.getStatus(), result.getStatus());
        assertEquals(expectedResponseDto.getPriority(), result.getPriority());
        assertEquals(expectedResponseDto.getExecutorId(), result.getExecutorId());
        assertEquals(expectedResponseDto.getAuthorId(), result.getAuthorId());
    }


    @Test
    void create_ShouldThrowException_WhenUserCreatesTask() {
        TaskCreateRequestDto dto = new TaskCreateRequestDto();
        dto.setTitle("New Task");
        dto.setDescription("Task Description");

        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(AccessDeniedException.class, () -> taskService.create(dto));
        verify(taskRepository, never()).save(any(Task.class));
    }


    @Test
    void update_ShouldSucceed_WhenAdminUpdatesExistingTask() {
        TaskUpdateRequestDto dto = new TaskUpdateRequestDto();
        dto.setTitle("Updated Task");

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(admin);
        when(taskMapper.entityToResponseDto(any(Task.class))).thenReturn(new TaskResponseDto());

        TaskResponseDto result = taskService.update(task.getId(), dto);

        assertNotNull(result);
        assertEquals("Updated Task", task.getTitle());
    }

    @Test
    void update_ShouldThrowException_WhenAdminUpdatesNonExistingTask() {
        TaskUpdateRequestDto dto = new TaskUpdateRequestDto();
        dto.setTitle("Updated Task");

        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());
        when(userService.getCurrentUser()).thenReturn(admin);

        assertThrows(EntityNotFoundException.class, () -> taskService.update(task.getId(), dto));
    }

    @Test
    void update_ShouldThrowException_WhenUserUpdatesTask() {
        TaskUpdateRequestDto dto = new TaskUpdateRequestDto();
        dto.setTitle("Updated Task");

        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(AccessDeniedException.class, () -> taskService.update(task.getId(), dto));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void delete_ShouldSucceed_WhenAdminDeletesExistingTask() {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        taskService.delete(task.getId());
        verify(taskRepository).delete(task);
    }

    @Test
    void delete_ShouldThrowException_WhenAdminDeletesNonExistingTask() {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.delete(task.getId()));
    }

    @Test
    void delete_ShouldThrowException_WhenUserDeletesTask() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class, () -> taskService.delete(task.getId()));
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateStatus_ShouldSucceed_WhenAdminUpdatesStatus() {
        TaskUpdateStatusRequestDto dto = new TaskUpdateStatusRequestDto();
        dto.setStatus(Status.IN_PROGRESS);

        when(userService.getCurrentUser()).thenReturn(admin);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));


        TaskResponseDto expectedResponseDto = new TaskResponseDto();
        expectedResponseDto.setStatus(Status.IN_PROGRESS);

        when(taskMapper.entityToResponseDto(task)).thenReturn(expectedResponseDto);

        TaskResponseDto result = taskService.updateStatus(task.getId(), dto);

        assertEquals(Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void updateStatus_ShouldSucceed_WhenUserUpdatesTheirTaskStatus() {
        TaskUpdateStatusRequestDto dto = new TaskUpdateStatusRequestDto();
        dto.setStatus(Status.COMPLETED);

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        TaskResponseDto expectedResponseDto = new TaskResponseDto();
        expectedResponseDto.setStatus(Status.COMPLETED);

        when(taskMapper.entityToResponseDto(task)).thenReturn(expectedResponseDto);

        TaskResponseDto result = taskService.updateStatus(task.getId(), dto);

        assertEquals(Status.COMPLETED, result.getStatus());
    }

    @Test
    void updateStatus_ShouldThrowException_WhenUserUpdatesNotTheirTaskStatus() {
        Task anotherTask = new Task();
        anotherTask.setId(2L);
        anotherTask.setExecutor(new User());

        TaskUpdateStatusRequestDto dto = new TaskUpdateStatusRequestDto();
        dto.setStatus(Status.COMPLETED);

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(anotherTask.getId())).thenReturn(Optional.of(anotherTask));

        assertThrows(AccessDeniedException.class, () -> taskService.updateStatus(anotherTask.getId(), dto));
    }

    @Test
    void getById_ShouldSucceed_WhenAdminGetsTask() {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskMapper.entityToResponseDto(task)).thenReturn(new TaskResponseDto());

        TaskResponseDto result = taskService.getById(task.getId());

        assertNotNull(result);
    }

    @Test
    void getById_ShouldSucceed_WhenUserGetsTheirTask() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskMapper.entityToResponseDto(task)).thenReturn(new TaskResponseDto());

        TaskResponseDto result = taskService.getById(task.getId());

        assertNotNull(result);
    }

    @Test
    void getById_ShouldThrowException_WhenUserGetsNotTheirTask() {
        Task anotherTask = new Task();
        anotherTask.setId(2L);
        anotherTask.setExecutor(new User());

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(anotherTask.getId())).thenReturn(Optional.of(anotherTask));

        assertThrows(AccessDeniedException.class, () -> taskService.getById(anotherTask.getId()));
    }

    @Test
    void getAll_ShouldSucceed_WhenAdminGetsAllTasks() {
        Page<Task> tasksPage = new PageImpl<>(List.of(task));

        when(userService.getCurrentUser()).thenReturn(admin);
        when(taskRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(tasksPage);

        List<TaskResponseDto> result = taskService.getAll(0, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAll_ShouldSucceed_WhenUserGetsTheirTasks() {
        Page<Task> tasksPage = new PageImpl<>(List.of(task));

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(tasksPage);

        List<TaskResponseDto> result = taskService.getAll(0, 10, user.getId(), null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
