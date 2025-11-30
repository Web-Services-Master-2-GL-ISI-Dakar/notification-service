package sn.ondmoney.notificationservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.ondmoney.notificationservice.domain.NotificationTemplate;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByTypeAndLanguage(NotificationType type, String language);

    Optional<NotificationTemplate> findByTemplateCode(String templateCode);
}
