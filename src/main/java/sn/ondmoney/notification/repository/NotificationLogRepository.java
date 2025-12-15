package sn.ondmoney.notification.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sn.ondmoney.notification.domain.NotificationLog;

/**
 * Spring Data MongoDB repository for the NotificationLog entity.
 */
@Repository
public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
    boolean existsByEventRef(String eventRef);
}
