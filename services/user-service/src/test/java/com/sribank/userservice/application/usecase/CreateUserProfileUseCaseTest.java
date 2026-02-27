package com.sribank.userservice.application.usecase;

import com.sribank.userservice.application.command.CreateUserProfileCommand;
import com.sribank.userservice.application.dto.UserProfileResult;
import com.sribank.userservice.domain.exception.UserProfileAlreadyExistsException;
import com.sribank.userservice.domain.model.UserProfile;
import com.sribank.userservice.domain.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserProfileUseCaseTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private CreateUserProfileUseCase createUserProfileUseCase;

    @Test
    void executeThrowsWhenProfileExists() {
        when(userProfileRepository.existsByUserId("u1")).thenReturn(true);

        assertThrows(UserProfileAlreadyExistsException.class, () ->
                createUserProfileUseCase.execute(new CreateUserProfileCommand("u1", "Sai", "K", "+911234567890")));

        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void executeCreatesProfileWhenNotExists() {
        when(userProfileRepository.existsByUserId("u1")).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(UserProfile.restore("p1", "u1", "Sai", "K", "+911234567890", Instant.now(), Instant.now()));

        UserProfileResult result = createUserProfileUseCase.execute(
                new CreateUserProfileCommand("u1", "Sai", "K", "+911234567890"));

        assertEquals("u1", result.userId());
        assertEquals("Sai", result.firstName());
    }
}
