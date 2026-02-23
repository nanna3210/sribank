package com.sribank.authservice.application.command;

public record RegisterCommand(String username, String email, String password) {
}
