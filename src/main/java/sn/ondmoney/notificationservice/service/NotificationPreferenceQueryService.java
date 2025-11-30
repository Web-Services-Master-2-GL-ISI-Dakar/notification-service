package sn.ondmoney.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.notificationservice.domain.*; // for static metamodels
import sn.ondmoney.notificationservice.domain.NotificationPreference;
import sn.ondmoney.notificationservice.repository.NotificationPreferenceRepository;
import sn.ondmoney.notificationservice.service.criteria.NotificationPreferenceCriteria;
import sn.ondmoney.notificationservice.service.dto.NotificationPreferenceDTO;
import sn.ondmoney.notificationservice.service.mapper.NotificationPreferenceMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link NotificationPreference} entities in the database.
 * The main input is a {@link NotificationPreferenceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link NotificationPreferenceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NotificationPreferenceQueryService extends QueryService<NotificationPreference> {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationPreferenceQueryService.class);

    private final NotificationPreferenceRepository notificationPreferenceRepository;

    private final NotificationPreferenceMapper notificationPreferenceMapper;

    public NotificationPreferenceQueryService(
        NotificationPreferenceRepository notificationPreferenceRepository,
        NotificationPreferenceMapper notificationPreferenceMapper
    ) {
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.notificationPreferenceMapper = notificationPreferenceMapper;
    }

    /**
     * Return a {@link Page} of {@link NotificationPreferenceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationPreferenceDTO> findByCriteria(NotificationPreferenceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<NotificationPreference> specification = createSpecification(criteria);
        return notificationPreferenceRepository.findAll(specification, page).map(notificationPreferenceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NotificationPreferenceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<NotificationPreference> specification = createSpecification(criteria);
        return notificationPreferenceRepository.count(specification);
    }

    /**
     * Function to convert {@link NotificationPreferenceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<NotificationPreference> createSpecification(NotificationPreferenceCriteria criteria) {
        Specification<NotificationPreference> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), NotificationPreference_.id),
                buildStringSpecification(criteria.getUserId(), NotificationPreference_.userId),
                buildSpecification(criteria.getSmsEnabled(), NotificationPreference_.smsEnabled),
                buildSpecification(criteria.getEmailEnabled(), NotificationPreference_.emailEnabled),
                buildSpecification(criteria.getPushEnabled(), NotificationPreference_.pushEnabled),
                buildStringSpecification(criteria.getMutedTypes(), NotificationPreference_.mutedTypes),
                buildStringSpecification(criteria.getLanguage(), NotificationPreference_.language),
                buildRangeSpecification(criteria.getUpdatedAt(), NotificationPreference_.updatedAt)
            );
        }
        return specification;
    }
}
