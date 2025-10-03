package com.sk.skala.myspringapp.controller;

import java.util.List;
import java.util.Optional;

import com.sk.skala.myspringapp.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.myspringapp.model.User;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 생성자 주입 (@Autowired 생략 가능)
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 모든 사용자 조회 및 이름 필터
    @GetMapping("/users")
    public List<User> getAllUsers(@RequestParam Optional<String> name) {
        return userService.findAll(name);
    }

    // 특정 사용자 조회
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        log.info("getUserById called with id: {}", id);
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 사용자 생성
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("createUser called with user: {}", user);
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    // 사용자 수정
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User updatedUser) {
        return userService.update(id, updatedUser)
                .map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 사용자 삭제
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        return userService.delete(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Valid 문제 발생 시 처리하는 Exception Handler 정의
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        System.out.println("[MethodArgumentNotValidException ExceptionalHandler] Validation Error: ");

        return ResponseEntity.status(500).body("SK000" + ex.getMessage());
    }

    /**
     * 그 외 모든 예외를 처리하는 Exception Handler 정의
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        System.out.println("[RunTimeException ExceptionalHandler] Runtime Error: ");

        return ResponseEntity.status(500).body(ex.getMessage());

    }
}