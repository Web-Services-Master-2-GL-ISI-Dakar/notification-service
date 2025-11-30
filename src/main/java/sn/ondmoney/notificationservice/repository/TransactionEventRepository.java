package sn.ondmoney.notificationservice.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.ondmoney.notificationservice.domain.TransactionEvent;

/**
 * Spring Data JPA repository for the TransactionEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionEventRepository extends JpaRepository<TransactionEvent, Long> {}
