package com.sk.skala.myspringapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;
import java.util.Objects;

import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class User {
    @Positive
    private Long id;

    @NotBlank
    private String name;

    @Email
    private String email;

    @Size(min = 1, max =10)
    private List<String> hobbies; // 취미

    @JsonCreator
    public User(@JsonProperty("id") Long id,
                @JsonProperty("name") String name,
                @JsonProperty("email") String email,
                @JsonProperty("hobbies") List<String> hobbies) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hobbies = hobbies;
    }

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name){ this.name = name; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public List<String> getHobbies(){ return hobbies; }
    public void setHobbies(List<String> hobbies){ this.hobbies = hobbies; }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode(){ return Objects.hash(id); }
}
