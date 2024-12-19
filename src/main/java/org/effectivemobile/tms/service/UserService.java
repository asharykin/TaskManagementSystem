package org.effectivemobile.tms.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.effectivemobile.tms.dto.user.UserAuthRequestDto;
import org.effectivemobile.tms.dto.user.UserResponseDto;
import org.effectivemobile.tms.entity.User;
import org.effectivemobile.tms.mapper.UserMapper;
import org.effectivemobile.tms.repository.UserRepository;
import org.effectivemobile.tms.util.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponseDto> getAll(Integer page, Integer size) {
        User user = getCurrentUser();
        if (user.getRole() == Role.ADMIN) {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.findAll(pageable);
            return userPage.stream().map(userMapper::entityToResponseDto).toList();
        }
        throw new AccessDeniedException("Вы не являетесь администратором");
    }

    public UserResponseDto getById(Long id) {
        User user = getCurrentUser();
        if (user.getRole() == Role.ADMIN || user.getId().equals(id)) {
            return userRepository.findById(id).map(userMapper::entityToResponseDto).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        }
        throw new AccessDeniedException("Вы не являетесь администратором или пользователем с данным ID");
    }

    @Transactional
    public UserResponseDto create(UserAuthRequestDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new EntityExistsException("Пользователь с таким именем уже существует");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(Role.USER);
        userRepository.save(user);
        return userMapper.entityToResponseDto(user);
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return loadUserByUsername(username);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким именем не найден"));
    }
}
