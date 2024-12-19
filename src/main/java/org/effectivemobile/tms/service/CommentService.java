package org.effectivemobile.tms.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.effectivemobile.tms.dto.comment.CommentRequestDto;
import org.effectivemobile.tms.dto.comment.CommentResponseDto;
import org.effectivemobile.tms.entity.Comment;
import org.effectivemobile.tms.entity.Task;
import org.effectivemobile.tms.entity.User;
import org.effectivemobile.tms.mapper.CommentMapper;
import org.effectivemobile.tms.repository.CommentRepository;
import org.effectivemobile.tms.repository.TaskRepository;
import org.effectivemobile.tms.util.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;

    public List<CommentResponseDto> getAll(Long taskId, Integer page, Integer size) {
        User user = userService.getCurrentUser();
        Task task = getTask(taskId);
        if (user.getRole() == Role.ADMIN || user.equals(task.getExecutor())) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> comments = commentRepository.findAllByTaskId(taskId, pageable);
            return comments.stream().map(commentMapper::entityToResponseDto).toList();
        }
        throw new AccessDeniedException("Вы не являетесь исполнителем задачи или администратором");
    }

    @Transactional
    public CommentResponseDto create(Long taskId, CommentRequestDto dto) {
        User user = userService.getCurrentUser();
        Task task = getTask(taskId);
        if (user.getRole() == Role.ADMIN || user.equals(task.getExecutor())) {
            Comment comment = new Comment();
            comment.setContent(dto.getContent());
            comment.setTask(task);
            comment.setAuthor(user);
            commentRepository.save(comment);
            return commentMapper.entityToResponseDto(comment);
        }
        throw new AccessDeniedException("Вы не являетесь исполнителем задачи или администратором");
    }

    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));
    }
}
