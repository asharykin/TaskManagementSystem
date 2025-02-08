package org.effectivemobile.tms.service;

import javax.persistence.EntityExistsException;
import org.effectivemobile.tms.dto.user.UserAuthRequestDto;
import org.effectivemobile.tms.dto.user.UserResponseDto;
import org.effectivemobile.tms.entity.User;
import org.effectivemobile.tms.mapper.UserMapper;
import org.effectivemobile.tms.repository.UserRepository;
import org.effectivemobile.tms.util.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private User admin;

    private User user1;

    private User user2;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        user1 = new User();
        user1.setId(2L);
        user1.setUsername("user1");
        user1.setRole(Role.USER);

        user2 = new User();
        user2.setId(3L);
        user2.setUsername("user2");
        user2.setRole(Role.USER);
    }

    private void mockUserAuthentication() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user1.getUsername());
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void mockAdminAuthentication() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(admin.getUsername());
        when(userRepository.findByUsername(admin.getUsername())).thenReturn(Optional.of(admin));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    @Test
    void getAll_ShouldSucceed_WhenAdmin() {
        mockAdminAuthentication();

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user2.getId());
        userResponseDto.setUsername(user2.getUsername());

        List<User> users = List.of(admin, user1, user2);
        Page<User> userPage = new PageImpl<>(users);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.entityToResponseDto(any(User.class))).thenReturn(userResponseDto);

        List<UserResponseDto> result = userService.getAll(0, 10);

        assertEquals(3, result.size());
    }

    @Test
    void getAll_ShouldThrowAccessDenied_WhenUser() {
        mockUserAuthentication();

        assertThrows(AccessDeniedException.class, () -> userService.getAll(0, 10));
    }

    @Test
    void getById_ShouldSucceed_WhenAdmin() {
        mockAdminAuthentication();

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user2.getId());
        userResponseDto.setUsername(user2.getUsername());

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(userMapper.entityToResponseDto(user2)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getById(user2.getId());

        assertEquals(user2.getId(), result.getId());
    }

    @Test
    void getById_ShouldSucceed_WhenUserFetchesThemself() {
        mockAdminAuthentication();

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user1.getId());
        userResponseDto.setUsername(user1.getUsername());

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(userMapper.entityToResponseDto(user1)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getById(user1.getId());

        assertEquals(user1.getId(), result.getId());
    }

    @Test
    void getById_ShouldThrowAccessDenied_WhenUserFetchesAnotherUser() {
        mockUserAuthentication();

        assertThrows(AccessDeniedException.class, () -> userService.getById(user2.getId()));
    }

    @Test
    void create_ShouldSucceed_WhenUsernameIsUnique() {
        UserAuthRequestDto requestDto = new UserAuthRequestDto();
        requestDto.setUsername("newUser");
        requestDto.setPassword("password");

        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Optional.empty());

        User user = new User();
        user.setId(4L);
        user.setUsername(requestDto.getUsername());
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.entityToResponseDto(any(User.class))).thenReturn(new UserResponseDto(user.getId(), user.getUsername(), user.getRole()));

        UserResponseDto result = userService.create(requestDto);

        assertEquals(requestDto.getUsername(), result.getUsername());
    }

    @Test
    void create_ShouldThrowEntityExistsException_WhenUsernameIsNotUnique() {
        UserAuthRequestDto requestDto = new UserAuthRequestDto();
        requestDto.setUsername("user1");

        when(userRepository.findByUsername(requestDto.getUsername())).thenReturn(Optional.of(user1));

        assertThrows(EntityExistsException.class, () -> userService.create(requestDto));
    }
}
