package org.example.datalabelingtool.domain.users.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.groups.entity.Group;
import org.example.datalabelingtool.domain.groups.repository.GroupRepository;
import org.example.datalabelingtool.domain.users.dto.UserCreateRequestDto;
import org.example.datalabelingtool.domain.users.dto.UserResponseDto;
import org.example.datalabelingtool.domain.users.dto.UserUpdateRequestDto;
import org.example.datalabelingtool.domain.users.entity.User;
import org.example.datalabelingtool.domain.users.entity.UserRole;
import org.example.datalabelingtool.domain.users.repository.UserRepository;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;

    @Value("${app.admin.code}")
    private String adminCode;

    public UserResponseDto createUser(UserCreateRequestDto requestDto) {
        String dtoAdminCode = requestDto.getAdminCode();
        UserRole role = UserRole.USER;

        if (dtoAdminCode != null && dtoAdminCode.equals(adminCode)) {
            role = UserRole.ADMIN;
        }

        String uuid = UUID.randomUUID().toString();

        User user = User.builder()
                .id(uuid)
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(role)
                .isActive(true)
                .build();

        userRepository.save(user);

        return toResponseDto(user);
    }

    public UserResponseDto getUserById(String id) {
        User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        return toResponseDto(user);
    }

    public DataResponseDto getAllUsers() {
        List<UserResponseDto> userResponseDtoList = userRepository.findAllByIsActiveTrue().stream()
                .map(this::toResponseDto).toList();
        return new DataResponseDto(userResponseDtoList);
    }

    @Transactional
    public UserResponseDto updateUser(String id, UserUpdateRequestDto requestDto) {
        String newUsername = requestDto.getNewUsername();
        String newPassword = requestDto.getNewPassword();

        User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        if(StringUtils.hasText(newUsername)) user.updateUsername(newUsername);
        if(StringUtils.hasText(newPassword)) user.updatePassword(passwordEncoder.encode(newPassword));

        return toResponseDto(user);
    }

    @Transactional
    public void deleteUser(@Valid String id) {
        User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        user.updateIsActive(false);
    }

    private UserResponseDto toResponseDto(User user) {
        List<Group> groupList = groupRepository.findByUserId(user.getId());

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .groupIds(groupList.stream().map(Group::getId).collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
