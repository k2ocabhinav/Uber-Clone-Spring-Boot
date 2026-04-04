package com.github.k2ocabhinav.ubercloneapp.testdata;

import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.Role;

import java.util.Set;

public class UserTestBuilder {
    private Long id = 1L;
    private String firstName = "Test";
    private String lastName = "User";
    private String email = "test@example.com";
    private String password = "password123";
    private String phoneNumber = "+1234567890";
    private Boolean active = true;
    private Set<Role> roles = Set.of(Role.RIDER);

    public UserTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserTestBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserTestBuilder withName(String name) {
        String[] parts = name.split(" ", 2);
        this.firstName = parts[0];
        this.lastName = parts.length > 1 ? parts[1] : "";
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestBuilder withRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public UserTestBuilder withRole(Role role) {
        this.roles = Set.of(role);
        return this;
    }

    public User build() {
        return User.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .active(active)
                .roles(roles)
                .build();
    }

    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }

    public static UserTestBuilder aRider() {
        return aUser().withRole(Role.RIDER);
    }

    public static UserTestBuilder aDriver() {
        return aUser().withRole(Role.DRIVER);
    }

    public static UserTestBuilder anAdmin() {
        return aUser().withRole(Role.ADMIN);
    }
}
