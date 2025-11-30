package sn.ondmoney.notificationservice.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.ondmoney.notificationservice.domain.NotificationRequest;

/**
 * Spring Data JPA repository for the NotificationRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRequestRepository extends JpaRepository<NotificationRequest, Long> {}
