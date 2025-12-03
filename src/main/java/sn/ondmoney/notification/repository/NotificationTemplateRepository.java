package sn.ondmoney.notification.repository;

import java.nio.channels.FileChannel;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sn.ondmoney.notification.domain.NotificationTemplate;

/**
 * Spring Data MongoDB repository for the NotificationTemplate entity.
 */
@Repository
public interface NotificationTemplateRepository extends MongoRepository<NotificationTemplate, String> {
    NotificationTemplate findByTemplateCode(String templateCode);

    Optional<NotificationTemplate> findOneByTemplateCode(String templateCode);

    boolean existsByTemplateCode(String templateCode);
}
