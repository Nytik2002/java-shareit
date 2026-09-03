package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

//Сервис для работы с пользователями
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Создание нового пользователя
    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new RuntimeException("Name is required");
        }

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.save(user);

        return UserMapper.toUserDto(savedUser);
    }

    //Обновление пользователя
    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDto.getEmail() != null) {
            userRepository.findByEmail(userDto.getEmail())
                    .filter(user -> !user.getId().equals(id))
                    .ifPresent(user -> {
                        throw new RuntimeException("Email already exists");
                    });

            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        User updatedUser = userRepository.save(existingUser);

        return UserMapper.toUserDto(updatedUser);
    }

    //Получение пользователя по ID
    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserMapper.toUserDto(user);
    }

    //Получение списка всех пользователей
    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    //Удаление пользователя
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}