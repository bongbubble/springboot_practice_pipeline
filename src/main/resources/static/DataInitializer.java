package com.skala.springbootsample.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skala.springbootsample.domain.User;
import com.skala.springbootsample.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("데이터베이스가 비어있어 users.json에서 데이터를 로드합니다.");
            try {
                ClassPathResource resource = new ClassPathResource("users.json");
                InputStream inputStream = resource.getInputStream();
                List<User> users = objectMapper.readValue(inputStream, new TypeReference<List<User>>() {
                });
                userRepository.saveAll(users);
                log.info("{}명의 사용자를 성공적으로 로드했습니다.", users.size());
            } catch (Exception e) {
                log.error("users.json 파일 로드 중 오류가 발생했습니다.", e);
            }
        }
    }
}