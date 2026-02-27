package com.sribank.userservice.interfaces.rest.controller;

import com.sribank.userservice.application.command.CreateUserProfileCommand;
import com.sribank.userservice.application.dto.UserProfileResult;
import com.sribank.userservice.application.usecase.CreateUserProfileUseCase;
import com.sribank.userservice.application.usecase.GetUserProfileUseCase;
import com.sribank.userservice.interfaces.rest.request.CreateUserProfileRequest;
import com.sribank.userservice.interfaces.rest.response.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/profiles")
public class UserProfileController {

    private final CreateUserProfileUseCase createUserProfileUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;

    public UserProfileController(CreateUserProfileUseCase createUserProfileUseCase,
                                 GetUserProfileUseCase getUserProfileUseCase) {
        this.createUserProfileUseCase = createUserProfileUseCase;
        this.getUserProfileUseCase = getUserProfileUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileResponse create(@Valid @RequestBody CreateUserProfileRequest request) {
        UserProfileResult result = createUserProfileUseCase.execute(new CreateUserProfileCommand(
                request.userId(),
                request.firstName(),
                request.lastName(),
                request.phoneNumber()
        ));
        return toResponse(result);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponse getByUserId(@PathVariable String userId) {
        UserProfileResult result = getUserProfileUseCase.execute(userId);
        return toResponse(result);
    }

    private UserProfileResponse toResponse(UserProfileResult result) {
        return new UserProfileResponse(
                result.userId(),
                result.firstName(),
                result.lastName(),
                result.phoneNumber()
        );
    }
}
