package family.main.project.internal.user.repository;

import family.main.project.internal.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile,String> {
    Optional<UserProfile> findByUserId(String userId);
}
