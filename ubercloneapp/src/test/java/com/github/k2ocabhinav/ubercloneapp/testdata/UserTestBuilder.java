package com.github.k2ocabhinav.ubercloneapp.testdata;

import com.github.k2ocabhinav.ubercloneapp.entities.User;
import com.github.k2ocabhinav.ubercloneapp.entities.enums.Role;

import java.util.Set;

public class UserTestBuilder {
    private Long id = 1L;
    private String email = "test@example.com";
    private String password = "password123";
    private String firstName = "Test";
    private String lastName = "User";
    private String phoneNumber = "+1234567890";
    private Set<Role> roles = Set.of(Role.RIDER);
    private Boolean active = true;

    public UserTestBuilder withId(Long id) {
        this.id = id;
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

    public UserTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserTestBuilder withLastName(String lastName) {
        this.lastName = lastName;
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

    public UserTestBuilder active() {
        this.active = true;
        return this;
    }

    public UserTestBuilder inactive() {
        this.active = false;
        return this;
    }

    public User build() {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .roles(roles)
                .active(active)
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
