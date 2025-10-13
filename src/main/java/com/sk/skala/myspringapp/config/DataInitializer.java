package com.sk.skala.myspringapp.config;

import java.util.List;

import com.sk.skala.myspringapp.model.User;
import com.sk.skala.myspringapp.repo.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.findAll().isEmpty()) {
            log.info("기존 사용자 데이터가 있어 초기화를 건너뜁니다.");
            return;
        }

        log.info("초기 사용자 데이터를 생성합니다.");

        userRepository.create(new User(null, "alice", "alice@example.com", List.of("reading", "hiking")));
        userRepository.create(new User(null, "bob", "bob@example.com", List.of("gaming")));
        userRepository.create(new User(null, "charlie", "charlie@example.com", List.of("music", "coding")));

        log.info("초기 사용자 데이터 생성 완료");
    }
}
