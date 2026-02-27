package com.sribank.userservice.application.usecase;

import com.sribank.userservice.application.command.CreateUserProfileCommand;
import com.sribank.userservice.application.dto.UserProfileResult;
import com.sribank.userservice.domain.exception.UserProfileAlreadyExistsException;
import com.sribank.userservice.domain.model.UserProfile;
import com.sribank.userservice.domain.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateUserProfileUseCase {

    private final UserProfileRepository userProfileRepository;

    public CreateUserProfileUseCase(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfileResult execute(CreateUserProfileCommand command) {
        if (userProfileRepository.existsByUserId(command.userId())) {
            throw new UserProfileAlreadyExistsException("User profile already exists for userId: " + command.userId());
        }

        UserProfile saved = userProfileRepository.save(UserProfile.create(
                command.userId(),
                command.firstName(),
                command.lastName(),
                command.phoneNumber()
        ));

        return new UserProfileResult(
                saved.getUserId(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getPhoneNumber()
        );
    }
}
