package family.main.project.internal.user.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import family.main.project.common.exception.AppException;
import family.main.project.common.exception.ErrorCode;
import family.main.project.internal.user.dto.request.LoginRequest;
import family.main.project.internal.user.dto.request.TokenRequest;
import family.main.project.internal.user.dto.request.UserSignUpRequest;
import family.main.project.internal.user.dto.response.AuthResponse;
import family.main.project.internal.user.entity.UserProfile;
import family.main.project.internal.user.entity.User;
import family.main.project.internal.user.repository.ProfileRepository;
import family.main.project.internal.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthService {

    @NonFinal
    @Value("${key.jwt.value}")
    String KEY;

    @NonFinal
    @Value("${app.time.expiryTime}")
    int expiryTime;

    UserRepository userRepository;
    ProfileRepository profileRepository;
    PasswordEncoder passwordEncoder;


    public AuthResponse signUp(UserSignUpRequest request) {
        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTS);

        User user = User.builder()
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        UserProfile profile = UserProfile.builder().build();

        return AuthResponse.builder()
                .userID(user.getId())
                .fullname(profile.getFullName())
                .build();
    }

    public AuthResponse login(LoginRequest request) throws JOSEException {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NO_EXISTS));

        boolean check = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!check)
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        String userId = user.getId();

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(()->new AppException(ErrorCode.PROFILE_NO_EXISTS));

        return AuthResponse.builder()
                .userID(userId)
                .fullname(profile.getFullName())
                .token(generate(user))
                .build();
    }

    public Boolean instropect(TokenRequest request) throws ParseException, JOSEException {
        SignedJWT jwt = SignedJWT.parse(request.getToken());
        var expiryTime = jwt.getJWTClaimsSet().getExpirationTime();
        JWSVerifier jwsVerifier = new MACVerifier(KEY.getBytes());

        boolean isVerify = jwt.verify(jwsVerifier);
        boolean isTime = expiryTime.after(Date.from(Instant.now()));

        if(!isVerify || !isTime )
            throw new AppException(ErrorCode.PARSE_TOKEN_FAIL);

        return true;
    }

    String generate(User user) throws JOSEException {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .issuer("BUNNUOCOLE")
                .subject(user.getId())
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(expiryTime,ChronoUnit.SECONDS)))
                .claim("scope", user.getRole())
                .build();

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);
        jwsObject.sign(new MACSigner(KEY.getBytes()));

        return jwsObject.serialize();
    }
}
