package sn.ondmoney.notificationservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.ondmoney.notificationservice.domain.NotificationPreference;

/**
 * Repository Spring Data JPA pour l'entité NotificationPreference
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    /**
     * Trouve les préférences par userId
     *
     * @param userId l'id de l'utilisateur
     * @return les préférences si trouvées
     */
    Optional<NotificationPreference> findByUserId(String userId);
}
