package family.main.project.internal.user.service;

import family.main.project.common.exception.AppException;
import family.main.project.common.exception.ErrorCode;
import family.main.project.internal.user.dto.request.ProfileUpdateRequest;
import family.main.project.internal.user.dto.response.ProfileResponse;
import family.main.project.internal.user.entity.UserProfile;
import family.main.project.internal.user.mapper.ProfileMapper;
import family.main.project.internal.user.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class ProfileService {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;


    @Cacheable(value = {"profile"}, keyGenerator = "simpleKeyGenerator")
    public ProfileResponse get(String userId){
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(()->new AppException(ErrorCode.PROFILE_NO_EXISTS));

        return profileMapper.toProfileResponse(profile);
    }


    @CacheEvict(value = {"profile"}, allEntries = true)
    public ProfileResponse update(String userId, ProfileUpdateRequest request){
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(()->new AppException(ErrorCode.PROFILE_NO_EXISTS));

        profileMapper.updateToProfile(request, profile);
        return profileMapper.toProfileResponse(profileRepository.save(profile));
    }
}
