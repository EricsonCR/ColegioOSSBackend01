package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.ForgotPasswordRequest;
import upc.colegioossbackend01.dto.request.LoginRequest;
import upc.colegioossbackend01.dto.request.RefreshTokenRequest;
import upc.colegioossbackend01.dto.request.RegisterRequest;
import upc.colegioossbackend01.dto.request.ResetPasswordRequest;
import upc.colegioossbackend01.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    String register(RegisterRequest request);

    String forgotPassword(ForgotPasswordRequest request);

    String resetPassword(ResetPasswordRequest request);
}