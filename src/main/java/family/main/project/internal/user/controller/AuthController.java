package family.main.project.internal.user.controller;

import com.nimbusds.jose.JOSEException;
import family.main.project.common.model.response.ApiResponse;
import family.main.project.internal.user.dto.request.LoginRequest;
import family.main.project.internal.user.dto.request.UserSignUpRequest;
import family.main.project.internal.user.dto.response.AuthResponse;
import family.main.project.internal.user.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/public/signup")
    ApiResponse<AuthResponse> signUp(@RequestBody UserSignUpRequest request){
        return ApiResponse.<AuthResponse>builder()
                .message("signup: "+request.getUsername())
                .result(authService.signUp(request))
                .build();
    }

    @PostMapping("/public/login")
    ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) throws JOSEException {
        return ApiResponse.<AuthResponse>builder()
                .message("login: " + request.getUsername())
                .result(authService.login(request))
                .build();
    }
}
