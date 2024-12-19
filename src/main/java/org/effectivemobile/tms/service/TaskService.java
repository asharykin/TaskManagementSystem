package org.effectivemobile.tms.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.effectivemobile.tms.dto.task.TaskCreateRequestDto;
import org.effectivemobile.tms.dto.task.TaskResponseDto;
import org.effectivemobile.tms.dto.task.TaskUpdateRequestDto;
import org.effectivemobile.tms.dto.task.TaskUpdateStatusRequestDto;
import org.effectivemobile.tms.entity.Task;
import org.effectivemobile.tms.entity.User;
import org.effectivemobile.tms.mapper.TaskMapper;
import org.effectivemobile.tms.repository.TaskRepository;
import org.effectivemobile.tms.repository.UserRepository;
import org.effectivemobile.tms.util.enums.Role;
import org.effectivemobile.tms.util.enums.Status;
import org.effectivemobile.tms.util.specification.TaskSpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;

    public List<TaskResponseDto> getAll(Integer page, Integer size, Long executorId, Long authorId) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        Specification<Task> spec;
        if (user.getRole() == Role.ADMIN) {
            spec = Specification.where(TaskSpecificationUtils.hasExecutor(executorId)).and(TaskSpecificationUtils.hasAuthor(authorId));
        } else {
            spec = TaskSpecificationUtils.hasExecutor(user.getId());
        }
        Page<Task> tasks = taskRepository.findAll(spec, pageable);
        return tasks.stream().map(taskMapper::entityToResponseDto).toList();
    }

    public TaskResponseDto getById(Long id) {
        User user = userService.getCurrentUser();
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));
        if (user.getRole() == Role.ADMIN || user.equals(task.getExecutor())) {
            return taskMapper.entityToResponseDto(task);
        }
        throw new AccessDeniedException("Вы не являетесь исполнителем задачи или администратором");
    }

    @Transactional
    public TaskResponseDto create(TaskCreateRequestDto dto) {
        User user = userService.getCurrentUser();
        if (user.getRole() == Role.ADMIN) {
            Task task = new Task();
            task.setTitle(dto.getTitle());
            task.setDescription(dto.getDescription());
            task.setStatus(Status.WAITING);
            task.setPriority(dto.getPriority());
            task.setAuthor(userService.getCurrentUser());
            task.setExecutor(userRepository.findById(dto.getExecutorId()).orElseThrow(() -> new EntityNotFoundException("Исполнитель не найден")));
            taskRepository.save(task);
            return taskMapper.entityToResponseDto(task);
        }
        throw new AccessDeniedException("Вы не являетесь администратором");
    }

    @Transactional
    public TaskResponseDto updateStatus(Long id, TaskUpdateStatusRequestDto dto) {
        User user = userService.getCurrentUser();
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));
        if (user.getRole() == Role.ADMIN || user.equals(task.getExecutor())) {
            task.setStatus(dto.getStatus());
            taskRepository.save(task);
            return taskMapper.entityToResponseDto(task);
        }
        throw new AccessDeniedException("Вы не являетесь исполнителем задачи или администратором");
    }

    @Transactional
    public TaskResponseDto update(Long id, TaskUpdateRequestDto dto) {
        User user = userService.getCurrentUser();
        if (user.getRole() == Role.ADMIN) {
            Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));
            Optional.ofNullable(dto.getTitle()).ifPresent(task::setTitle);
            Optional.ofNullable(dto.getDescription()).ifPresent(task::setDescription);
            Optional.ofNullable(dto.getStatus()).ifPresent(task::setStatus);
            Optional.ofNullable(dto.getPriority()).ifPresent(task::setPriority);
            Optional.ofNullable(dto.getExecutorId()).ifPresent(executorId -> {
                User executor = userRepository.findById(executorId).orElseThrow(() -> new EntityNotFoundException("Исполнитель не найден"));
                task.setExecutor(executor);
            });
            taskRepository.save(task);
            return taskMapper.entityToResponseDto(task);
        }
        throw new AccessDeniedException("Вы не являетесь администратором");
    }

    @Transactional
    public void delete(Long id) {
        User user = userService.getCurrentUser();
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));
        if (user.getRole() == Role.ADMIN) {
            taskRepository.delete(task);
            return;
        }
        throw new AccessDeniedException("Вы не являетесь администратором");
    }
}
