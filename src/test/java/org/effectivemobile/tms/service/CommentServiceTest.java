package org.effectivemobile.tms.service;

import jakarta.persistence.EntityNotFoundException;
import org.effectivemobile.tms.dto.comment.CommentRequestDto;
import org.effectivemobile.tms.dto.comment.CommentResponseDto;
import org.effectivemobile.tms.entity.Comment;
import org.effectivemobile.tms.entity.Task;
import org.effectivemobile.tms.entity.User;
import org.effectivemobile.tms.mapper.CommentMapper;
import org.effectivemobile.tms.repository.CommentRepository;
import org.effectivemobile.tms.repository.TaskRepository;
import org.effectivemobile.tms.util.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private CommentMapper commentMapper;

    private User admin;
    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        // Установка пользователей и задач
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
        task.setExecutor(user);
    }

    @Test
    void create_ShouldSucceed_WhenAdminCreatesCommentForAnyTask() {
        CommentRequestDto dto = new CommentRequestDto("Comment from admin");
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Comment from admin");

        when(userService.getCurrentUser()).thenReturn(admin);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.entityToResponseDto(any(Comment.class))).thenReturn(new CommentResponseDto(1L, task.getId(), "Comment from admin", admin.getId()));

        CommentResponseDto result = commentService.create(task.getId(), dto);

        assertEquals("Comment from admin", result.getContent());
        verify(commentRepository).save(any());
    }

    @Test
    void create_ShouldSucceed_WhenUserCreatesCommentForTaskTheyAreExecuting() {
        CommentRequestDto dto = new CommentRequestDto("Comment from executor");
        Comment comment = new Comment();
        comment.setId(2L);
        comment.setContent("Comment from executor");

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.entityToResponseDto(any(Comment.class))).thenReturn(new CommentResponseDto(2L, task.getId(), "Comment from executor", user.getId()));

        CommentResponseDto result = commentService.create(task.getId(), dto);

        assertEquals("Comment from executor", result.getContent());
        verify(commentRepository).save(any());
    }

    @Test
    void create_ShouldThrowAccessDeniedException_WhenUserCreatesCommentForTaskTheyAreNotExecuting() {
        // Настройка задачи с другим исполнителем
        Task anotherTask = new Task();
        anotherTask.setId(2L);
        anotherTask.setExecutor(new User()); // Другой пользователь

        CommentRequestDto dto = new CommentRequestDto("Comment from unauthorized user");

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(anotherTask.getId())).thenReturn(Optional.of(anotherTask));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            commentService.create(anotherTask.getId(), dto);
        });

        assertEquals("Вы не являетесь исполнителем задачи или администратором", exception.getMessage());
    }

    @Test
    void create_ShouldThrowEntityNotFoundException_WhenAdminCreatesCommentForNonexistentTask() {
        CommentRequestDto dto = new CommentRequestDto("Task Comment");

        when(userService.getCurrentUser()).thenReturn(admin);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.create(task.getId(), dto);
        });

        assertEquals("Задача не найдена", exception.getMessage());
    }




    @Test
    void getAll_ShouldSucceed_WhenAdminGetsCommentsForAnyTask() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Comment from admin");
        comment.setTask(task);
        comment.setAuthor(admin);

        when(userService.getCurrentUser()).thenReturn(admin);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        Page<Comment> commentsPage = new PageImpl<>(List.of(comment));
        when(commentRepository.findAllByTaskId(task.getId(), PageRequest.of(0, 10))).thenReturn(commentsPage);
        when(commentMapper.entityToResponseDto(comment)).thenReturn(new CommentResponseDto(1L, task.getId(), "Comment from admin", admin.getId()));

        List<CommentResponseDto> result = commentService.getAll(task.getId(), 0, 10);

        assertEquals(1, result.size());
        assertEquals("Comment from admin", result.get(0).getContent());
        verify(commentRepository).findAllByTaskId(task.getId(), PageRequest.of(0, 10));
    }

    @Test
    void getAll_ShouldSucceed_WhenUserGetsCommentsForTaskTheyAreExecuting() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Comment from executor");
        comment.setTask(task);
        comment.setAuthor(user);

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        Page<Comment> commentsPage = new PageImpl<>(List.of(comment));
        when(commentRepository.findAllByTaskId(task.getId(), PageRequest.of(0, 10))).thenReturn(commentsPage);
        when(commentMapper.entityToResponseDto(comment)).thenReturn(new CommentResponseDto(1L, task.getId(), "Comment from executor", user.getId()));

        List<CommentResponseDto> result = commentService.getAll(task.getId(), 0, 10);

        assertEquals(1, result.size());
        assertEquals("Comment from executor", result.get(0).getContent());
        verify(commentRepository).findAllByTaskId(task.getId(), PageRequest.of(0, 10));
    }

    @Test
    void getAll_ShouldThrowAccessDeniedException_WhenUserGetsCommentsForTaskTheyAreNotExecuting() {
        Task anotherTask = new Task();
        anotherTask.setId(2L);
        anotherTask.setExecutor(new User());

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(anotherTask.getId())).thenReturn(Optional.of(anotherTask));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            commentService.getAll(anotherTask.getId(), 0, 10);
        });

        assertEquals("Вы не являетесь исполнителем задачи или администратором", exception.getMessage());
    }

    @Test
    void getAll_ShouldThrowEntityNotFoundException_WhenAdminGetsCommentsForNonexistentTask() {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.getAll(task.getId(), 0, 10);
        });

        assertEquals("Задача не найдена", exception.getMessage());
    }
}
