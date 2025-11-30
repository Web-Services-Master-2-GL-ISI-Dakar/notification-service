package sn.ondmoney.notificationservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.ondmoney.notificationservice.domain.UserProfile;

/**
 * Repository Spring Data JPA pour l'entité UserProfile
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    /**
     * Trouve un profil utilisateur par son userId
     *
     * @param userId l'id de l'utilisateur
     * @return le profil si trouvé
     */
    Optional<UserProfile> findByUserId(String userId);

    /**
     * Trouve un profil par numéro de téléphone
     *
     * @param phoneNumber le numéro de téléphone
     * @return le profil si trouvé
     */
    Optional<UserProfile> findByPhoneNumber(String phoneNumber);

    /**
     * Trouve un profil par email
     *
     * @param email l'email
     * @return le profil si trouvé
     */
    Optional<UserProfile> findByEmail(String email);
}
