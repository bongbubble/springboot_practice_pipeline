package com.sk.skala.myspringapp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sk.skala.myspringapp.model.User;
import com.sk.skala.myspringapp.repo.FileUserRepository;
import com.sk.skala.myspringapp.repo.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    // 생성자 주입
    public UserService(FileUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 이름 필터는 Service 계층에서 처리 (선택적)
    public List<User> findAll(Optional<String> name) {
        Collection<User> all = userRepository.findAll();
        if (name.isPresent()) {
            String search = name.get();
            return all.stream()
                    .filter(u -> u.getName() != null && u.getName().equalsIgnoreCase(search))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>(all);
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public User create(User user) {
        // id가 없으면 Repository가 자동 생성
        return userRepository.create(user);
    }

    public Optional<User> update(long id, User updated) {
        try {
            return Optional.of(userRepository.update(id, updated));
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    // 기존 boolean 시그니처 유지
    public boolean delete(long id) {
        Optional<User> existed = userRepository.findById(id);
        userRepository.delete(id); // 존재하지 않아도 무시
        return existed.isPresent();
    }
}
