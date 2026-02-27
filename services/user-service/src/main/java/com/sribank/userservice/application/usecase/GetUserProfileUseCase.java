package com.sribank.userservice.application.usecase;

import com.sribank.userservice.application.dto.UserProfileResult;
import com.sribank.userservice.domain.exception.UserProfileNotFoundException;
import com.sribank.userservice.domain.model.UserProfile;
import com.sribank.userservice.domain.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class GetUserProfileUseCase {

    private final UserProfileRepository userProfileRepository;

    public GetUserProfileUseCase(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfileResult execute(String userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for userId: " + userId));

        return new UserProfileResult(
                userProfile.getUserId(),
                userProfile.getFirstName(),
                userProfile.getLastName(),
                userProfile.getPhoneNumber()
        );
    }
}
