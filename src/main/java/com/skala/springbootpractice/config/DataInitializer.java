package com.skala.springbootpractice.config;

import com.skala.springbootpractice.domain.Region;
import com.skala.springbootpractice.domain.User;
import com.skala.springbootpractice.repo.RegionRepository;
import com.skala.springbootpractice.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("초기 데이터 설정 시작");

        // 지역 데이터 생성
        Region seoul = regionRepository.save(new Region("서울"));
        Region busan = regionRepository.save(new Region("부산"));
        Region daegu = regionRepository.save(new Region("대구"));

        // 사용자 데이터 생성
        userRepository.save(new User("alice", "alice@example.com", seoul));
        userRepository.save(new User("bob", "bob@example.com", busan));
        userRepository.save(new User("charlie", "charlie@example.com", daegu));

        log.info("초기 데이터 설정 완료");
    }
}
