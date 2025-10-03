package com.sk.skala.myspringapp.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sk.skala.myspringapp.model.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserFileUtil {
    private static final String FILE_PATH = "users.json"; // 외부 파일로 둘 경우

    public static List<User> loadUsers() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return JsonUtil.mapper().readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            throw new RuntimeException("사용자 JSON 로드 실패", e);
        }
    }

    public static void saveUsers(List<User> users) {
        try {
            JsonUtil.mapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), users);
        } catch (IOException e) {
            throw new RuntimeException("사용자 JSON 저장 실패", e);
        }
    }
}
