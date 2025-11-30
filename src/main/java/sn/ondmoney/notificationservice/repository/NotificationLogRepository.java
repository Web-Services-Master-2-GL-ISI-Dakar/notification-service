package sn.ondmoney.notificationservice.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.ondmoney.notificationservice.domain.NotificationLog;

/**
 * Spring Data JPA repository for the NotificationLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long>, JpaSpecificationExecutor<NotificationLog> {}
