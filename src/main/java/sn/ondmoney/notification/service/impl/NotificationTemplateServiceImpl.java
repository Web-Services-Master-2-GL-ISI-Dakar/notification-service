package sn.ondmoney.notification.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sn.ondmoney.notification.domain.NotificationTemplate;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationLanguage;
import sn.ondmoney.notification.domain.enumeration.NotificationType;
import sn.ondmoney.notification.repository.NotificationTemplateRepository;
import sn.ondmoney.notification.repository.search.NotificationTemplateSearchRepository;
import sn.ondmoney.notification.service.NotificationTemplateService;
import sn.ondmoney.notification.service.dto.NotificationTemplateDTO;
import sn.ondmoney.notification.service.mapper.NotificationTemplateMapper;

/**
 * Service Implementation for managing {@link sn.ondmoney.notification.domain.NotificationTemplate}.
 */
@Service
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationTemplateServiceImpl.class);

    private final NotificationTemplateRepository notificationTemplateRepository;

    private final NotificationTemplateMapper notificationTemplateMapper;

    private final NotificationTemplateSearchRepository notificationTemplateSearchRepository;

    public NotificationTemplateServiceImpl(
        NotificationTemplateRepository notificationTemplateRepository,
        NotificationTemplateMapper notificationTemplateMapper,
        NotificationTemplateSearchRepository notificationTemplateSearchRepository
    ) {
        this.notificationTemplateRepository = notificationTemplateRepository;
        this.notificationTemplateMapper = notificationTemplateMapper;
        this.notificationTemplateSearchRepository = notificationTemplateSearchRepository;
    }

    @Override
    public NotificationTemplateDTO save(NotificationTemplateDTO notificationTemplateDTO) {
        LOG.debug("Request to save NotificationTemplate : {}", notificationTemplateDTO);
        NotificationTemplate notificationTemplate = notificationTemplateMapper.toEntity(notificationTemplateDTO);
        notificationTemplate = notificationTemplateRepository.save(notificationTemplate);
        notificationTemplateSearchRepository.index(notificationTemplate);
        return notificationTemplateMapper.toDto(notificationTemplate);
    }

    @Override
    public NotificationTemplateDTO update(NotificationTemplateDTO notificationTemplateDTO) {
        LOG.debug("Request to update NotificationTemplate : {}", notificationTemplateDTO);
        NotificationTemplate notificationTemplate = notificationTemplateMapper.toEntity(notificationTemplateDTO);
        notificationTemplate = notificationTemplateRepository.save(notificationTemplate);
        notificationTemplateSearchRepository.index(notificationTemplate);
        return notificationTemplateMapper.toDto(notificationTemplate);
    }

    @Override
    public Optional<NotificationTemplateDTO> partialUpdate(NotificationTemplateDTO notificationTemplateDTO) {
        LOG.debug("Request to partially update NotificationTemplate : {}", notificationTemplateDTO);

        return notificationTemplateRepository
            .findById(notificationTemplateDTO.getId())
            .map(existingNotificationTemplate -> {
                notificationTemplateMapper.partialUpdate(existingNotificationTemplate, notificationTemplateDTO);

                return existingNotificationTemplate;
            })
            .map(notificationTemplateRepository::save)
            .map(savedNotificationTemplate -> {
                notificationTemplateSearchRepository.index(savedNotificationTemplate);
                return savedNotificationTemplate;
            })
            .map(notificationTemplateMapper::toDto);
    }

    @Override
    public Page<NotificationTemplateDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all NotificationTemplates");
        return notificationTemplateRepository.findAll(pageable).map(notificationTemplateMapper::toDto);
    }

    @Override
    public Optional<NotificationTemplateDTO> findOne(String id) {
        LOG.debug("Request to get NotificationTemplate : {}", id);
        return notificationTemplateRepository.findById(id).map(notificationTemplateMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete NotificationTemplate : {}", id);
        notificationTemplateRepository.deleteById(id);
        notificationTemplateSearchRepository.deleteFromIndexById(id);
    }

    @Override
    public Page<NotificationTemplateDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of NotificationTemplates for query {}", query);
        return notificationTemplateSearchRepository.search(query, pageable).map(notificationTemplateMapper::toDto);
    }

    /**
     * Combine les composants pour former le templateCode et récupère le modèle actif.
     * * @param eventType Le type métier de la notification (Ex: OTP_REQUEST).
     * @param channel Le canal (Ex: SMS).
     * @param lang La langue (Ex: FR).
     * @param version La version (Ex: V1).
     * @return Le DTO du modèle trouvé.
     */
    @Override
    public Optional<NotificationTemplateDTO> findActiveTemplateByCompositeKey(
        NotificationType eventType,
        NotificationChannel channel,
        NotificationLanguage lang,
        int version
    ) {
        // Find in elasticsearch first
        // 1. COMBINAISON DE LA CLÉ (La logique métier principale)
        // Format attendu: EVENT_TYPE_CHANNEL_LANG_VERSION
        String templateCode = String.format("%s_%s_%s_V%d", eventType.name(), channel.name(), lang.name(), version);
        LOG.debug("Template code: {}", templateCode);

        NotificationTemplate notificationTemplate = notificationTemplateSearchRepository.findNotificationTemplateByTemplateCode(
            templateCode
        );
        if (notificationTemplate != null) {
            LOG.debug("Found template in Elasticsearch for code: {}", templateCode);
            return Optional.of(notificationTemplateMapper.toDto(notificationTemplate));
        }

        // 2. Appel au Repository (recherche par la clé unique)
        notificationTemplate = notificationTemplateRepository.findOneByTemplateCode(templateCode).orElse(null);
        if (notificationTemplate == null) {
            return Optional.empty();
        }
        LOG.debug("Found template in database for code: {}", templateCode);
        // Index it in Elasticsearch
        notificationTemplateSearchRepository.index(notificationTemplate);
        return Optional.ofNullable(notificationTemplateMapper.toDto(notificationTemplate));
    }
}
