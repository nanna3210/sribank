package com.sribank.userservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public class UserProfile {

    private final String id;
    private final String userId;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final Instant createdAt;
    private final Instant updatedAt;

    private UserProfile(String id,
                        String userId,
                        String firstName,
                        String lastName,
                        String phoneNumber,
                        Instant createdAt,
                        Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserProfile create(String userId, String firstName, String lastName, String phoneNumber) {
        Instant now = Instant.now();
        return new UserProfile(UUID.randomUUID().toString(), userId, firstName, lastName, phoneNumber, now, now);
    }

    public static UserProfile restore(String id,
                                      String userId,
                                      String firstName,
                                      String lastName,
                                      String phoneNumber,
                                      Instant createdAt,
                                      Instant updatedAt) {
        return new UserProfile(id, userId, firstName, lastName, phoneNumber, createdAt, updatedAt);
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
