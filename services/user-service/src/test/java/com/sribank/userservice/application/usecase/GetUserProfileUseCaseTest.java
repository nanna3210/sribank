package com.sribank.userservice.application.usecase;

import com.sribank.userservice.application.dto.UserProfileResult;
import com.sribank.userservice.domain.exception.UserProfileNotFoundException;
import com.sribank.userservice.domain.model.UserProfile;
import com.sribank.userservice.domain.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProfileUseCaseTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private GetUserProfileUseCase getUserProfileUseCase;

    @Test
    void executeThrowsWhenProfileNotFound() {
        when(userProfileRepository.findByUserId("u1")).thenReturn(Optional.empty());
        assertThrows(UserProfileNotFoundException.class, () -> getUserProfileUseCase.execute("u1"));
    }

    @Test
    void executeReturnsProfileWhenFound() {
        when(userProfileRepository.findByUserId("u1"))
                .thenReturn(Optional.of(UserProfile.restore("p1", "u1", "Sai", "K", "+911234567890", Instant.now(), Instant.now())));

        UserProfileResult result = getUserProfileUseCase.execute("u1");

        assertEquals("u1", result.userId());
        assertEquals("Sai", result.firstName());
    }
}
