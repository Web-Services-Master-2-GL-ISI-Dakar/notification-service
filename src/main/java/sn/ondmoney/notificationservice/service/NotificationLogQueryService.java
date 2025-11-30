package sn.ondmoney.notificationservice.service;

import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.notificationservice.domain.NotificationLog;
import sn.ondmoney.notificationservice.repository.NotificationLogRepository;
import sn.ondmoney.notificationservice.service.criteria.NotificationLogCriteria;
import sn.ondmoney.notificationservice.service.dto.NotificationLogDTO;
import sn.ondmoney.notificationservice.service.mapper.NotificationLogMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link NotificationLog} entities in the database.
 * The main input is a {@link NotificationLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link NotificationLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NotificationLogQueryService extends QueryService<NotificationLog> {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationLogQueryService.class);

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationLogMapper notificationLogMapper;

    public NotificationLogQueryService(NotificationLogRepository notificationLogRepository, NotificationLogMapper notificationLogMapper) {
        this.notificationLogRepository = notificationLogRepository;
        this.notificationLogMapper = notificationLogMapper;
    }

    /**
     * Return a {@link Page} of {@link NotificationLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationLogDTO> findByCriteria(NotificationLogCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<NotificationLog> specification = createSpecification(criteria);
        return notificationLogRepository.findAll(specification, page).map(notificationLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NotificationLogCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<NotificationLog> specification = createSpecification(criteria);
        return notificationLogRepository.count(specification);
    }

    /**
     * Function to convert {@link NotificationLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<NotificationLog> createSpecification(NotificationLogCriteria criteria) {
        Specification<NotificationLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), root -> root.get("id")));
            }
            if (criteria.getNotificationId() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getNotificationId(), root -> root.get("notificationId"))
                );
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserId(), root -> root.get("userId")));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), root -> root.get("type")));
            }
            if (criteria.getChannel() != null) {
                specification = specification.and(buildSpecification(criteria.getChannel(), root -> root.get("channel")));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), root -> root.get("status")));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), root -> root.get("message")));
            }
            if (criteria.getRecipient() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRecipient(), root -> root.get("recipient")));
            }
            if (criteria.getTimestamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimestamp(), root -> root.get("timestamp")));
            }
            if (criteria.getSentAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSentAt(), root -> root.get("sentAt")));
            }
            if (criteria.getRetryCount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRetryCount(), root -> root.get("retryCount")));
            }
            if (criteria.getChannelResults() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getChannelResults(), root -> root.get("channelResults"))
                );
            }
            if (criteria.getAction() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAction(), root -> root.get("action")));
            }
            if (criteria.getDetails() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDetails(), root -> root.get("details")));
            }
        }
        return specification;
    }
}
