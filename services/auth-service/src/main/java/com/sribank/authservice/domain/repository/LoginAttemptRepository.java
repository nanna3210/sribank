package com.sribank.authservice.domain.repository;

import com.sribank.authservice.domain.model.LoginAttempt;

public interface LoginAttemptRepository {

    LoginAttempt save(LoginAttempt loginAttempt);
}
