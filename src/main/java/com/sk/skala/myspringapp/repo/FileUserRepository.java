package com.sk.skala.myspringapp.repo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.sk.skala.myspringapp.model.User;
import com.sk.skala.myspringapp.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class FileUserRepository implements UserRepository {

    private final Path dataFile;
    private final Map<Long, User> userMap = new ConcurrentHashMap<>();
    private long idSeq = 1L;

    public FileUserRepository(@Value("${users.file.path:users.json}") String dataFilePath) {
        this.dataFile = Paths.get(dataFilePath);
        try {
            loadFromFile();
            log.info("FileUserRepository 초기화 완료. 파일: {}", dataFile.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("사용자 파일 로드 실패: " + dataFile.toAbsolutePath(), e);
        }
    }

    private void loadFromFile() throws IOException {
        if (dataFile.getParent() != null) {
            Files.createDirectories(dataFile.getParent());
        }

        if (!Files.exists(dataFile)) {
            saveToFile();
            log.info("users.json 새로 생성됨: {}", dataFile.toAbsolutePath());
            return;
        }

        if (Files.size(dataFile) == 0) {
            log.warn("users.json 이 비어있음: {}", dataFile.toAbsolutePath());
            return;
        }

        byte[] bytes = Files.readAllBytes(dataFile);
        List<User> users = JsonUtil.mapper().readValue(
                bytes,
                JsonUtil.mapper().getTypeFactory().constructCollectionType(List.class, User.class));

        userMap.clear();
        if (users != null) {
            for (User user : users) {
                if (user != null && user.getId() != null) {
                    userMap.put(user.getId(), user);
                }
            }
        }

        if (!userMap.isEmpty()) {
            idSeq = userMap.keySet().stream().mapToLong(Long::longValue).max().orElse(0L) + 1;
        }

        log.info("파일 로드 완료. 사용자 수: {}, 다음 ID: {}", userMap.size(), idSeq);
    }

    private void saveToFile() throws IOException {
        List<User> users = new ArrayList<>(userMap.values());
        byte[] json = JsonUtil.mapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(users);
        Files.write(dataFile, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        log.info("users.json 저장 완료. 사용자 수: {}", users.size());
    }

    @Override
    public Collection<User> findAll() {
        log.info("findAll() 호출됨");
        return new ArrayList<>(userMap.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        log.info("findById() 호출됨 → id={}", id);
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public synchronized User create(User user) {
        log.info("create() 호출됨 → name={}, email={}", user.getName(), user.getEmail());
        Long id = (user.getId() == null) ? idSeq++ : user.getId();

        if (userMap.containsKey(id)) {
            throw new IllegalStateException("이미 존재하는 사용자 ID입니다: " + id);
        }

        User newUser = new User(id, user.getName(), user.getEmail(), user.getHobbies());
        userMap.put(id, newUser);

        try {
            saveToFile();
        } catch (IOException e) {
            userMap.remove(id);
            throw new RuntimeException("파일 저장에 실패했습니다", e);
        }

        return newUser;
    }

    @Override
    public synchronized User update(Long id, User user) {
        log.info("update() 호출됨 → id={}", id);

        if (!userMap.containsKey(id)) {
            throw new NoSuchElementException("사용자를 찾을 수 없습니다: " + id);
        }

        User updatedUser = new User(id, user.getName(), user.getEmail(), user.getHobbies());
        userMap.put(id, updatedUser);

        try {
            saveToFile();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다", e);
        }

        return updatedUser;
    }

    @Override
    public synchronized void delete(Long id) {
        log.info("delete() 호출됨 → id={}", id);
        userMap.remove(id);
        try {
            saveToFile();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다", e);
        }
    }
}
