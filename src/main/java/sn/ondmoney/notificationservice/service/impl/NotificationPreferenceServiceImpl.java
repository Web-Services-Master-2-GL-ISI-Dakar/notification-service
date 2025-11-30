package sn.ondmoney.notificationservice.service.impl;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.notificationservice.domain.NotificationPreference;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;
import sn.ondmoney.notificationservice.repository.NotificationPreferenceRepository;
import sn.ondmoney.notificationservice.service.NotificationPreferenceService;
import sn.ondmoney.notificationservice.service.dto.NotificationPreferenceDTO;
import sn.ondmoney.notificationservice.service.mapper.NotificationPreferenceMapper;

/**
 * Service Implementation for managing {@link sn.ondmoney.notificationservice.domain.NotificationPreference}.
 */
@Service
@Transactional
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationPreferenceServiceImpl.class);

    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationPreferenceMapper notificationPreferenceMapper;

    public NotificationPreferenceServiceImpl(
        NotificationPreferenceRepository notificationPreferenceRepository,
        NotificationPreferenceMapper notificationPreferenceMapper
    ) {
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.notificationPreferenceMapper = notificationPreferenceMapper;
    }

    @Override
    public NotificationPreferenceDTO save(NotificationPreferenceDTO notificationPreferenceDTO) {
        LOG.debug("Request to save NotificationPreference : {}", notificationPreferenceDTO);

        // Mettre à jour la date de modification
        notificationPreferenceDTO.setUpdatedAt(Instant.now());

        NotificationPreference notificationPreference = notificationPreferenceMapper.toEntity(notificationPreferenceDTO);
        notificationPreference = notificationPreferenceRepository.save(notificationPreference);
        return notificationPreferenceMapper.toDto(notificationPreference);
    }

    @Override
    public NotificationPreferenceDTO update(NotificationPreferenceDTO notificationPreferenceDTO) {
        LOG.debug("Request to update NotificationPreference : {}", notificationPreferenceDTO);
        return save(notificationPreferenceDTO);
    }

    @Override
    public Optional<NotificationPreferenceDTO> partialUpdate(NotificationPreferenceDTO notificationPreferenceDTO) {
        LOG.debug("Request to partially update NotificationPreference : {}", notificationPreferenceDTO);

        return notificationPreferenceRepository
            .findById(notificationPreferenceDTO.getId())
            .map(existingNotificationPreference -> {
                notificationPreferenceMapper.partialUpdate(existingNotificationPreference, notificationPreferenceDTO);
                return existingNotificationPreference;
            })
            .map(notificationPreferenceRepository::save)
            .map(notificationPreferenceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationPreferenceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all NotificationPreferences");
        return notificationPreferenceRepository.findAll(pageable).map(notificationPreferenceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationPreferenceDTO> findOne(Long id) {
        LOG.debug("Request to get NotificationPreference : {}", id);
        return notificationPreferenceRepository.findById(id).map(notificationPreferenceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationPreferenceDTO> findByUserId(String userId) {
        LOG.debug("Request to get NotificationPreference by userId : {}", userId);
        return notificationPreferenceRepository.findByUserId(userId).map(notificationPreferenceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(String userId) {
        LOG.debug("Request to check if NotificationPreference exists for userId : {}", userId);
        return notificationPreferenceRepository.findByUserId(userId).isPresent();
    }

    @Override
    public NotificationPreferenceDTO createDefaultPreferences(String userId) {
        LOG.debug("Request to create default preferences for userId : {}", userId);

        NotificationPreferenceDTO preference = new NotificationPreferenceDTO();
        preference.setUserId(userId);
        preference.setSmsEnabled(true);
        preference.setEmailEnabled(true);
        preference.setPushEnabled(true);
        preference.setLanguage("fr");
        preference.setMutedTypes("[]");
        preference.setUpdatedAt(Instant.now());

        return save(preference);
    }

    @Override
    public void setSmsEnabled(String userId, boolean enabled) {
        LOG.debug("Request to set SMS enabled={} for userId : {}", enabled, userId);

        Optional<NotificationPreferenceDTO> prefOpt = findByUserId(userId);
        if (prefOpt.isPresent()) {
            NotificationPreferenceDTO pref = prefOpt.get();
            pref.setSmsEnabled(enabled);
            save(pref);
        } else {
            LOG.warn("NotificationPreference not found for userId: {}", userId);
        }
    }

    @Override
    public void setEmailEnabled(String userId, boolean enabled) {
        LOG.debug("Request to set Email enabled={} for userId : {}", enabled, userId);

        Optional<NotificationPreferenceDTO> prefOpt = findByUserId(userId);
        if (prefOpt.isPresent()) {
            NotificationPreferenceDTO pref = prefOpt.get();
            pref.setEmailEnabled(enabled);
            save(pref);
        } else {
            LOG.warn("NotificationPreference not found for userId: {}", userId);
        }
    }

    @Override
    public void setPushEnabled(String userId, boolean enabled) {
        LOG.debug("Request to set Push enabled={} for userId : {}", enabled, userId);

        Optional<NotificationPreferenceDTO> prefOpt = findByUserId(userId);
        if (prefOpt.isPresent()) {
            NotificationPreferenceDTO pref = prefOpt.get();
            pref.setPushEnabled(enabled);
            save(pref);
        } else {
            LOG.warn("NotificationPreference not found for userId: {}", userId);
        }
    }

    @Override
    public void muteNotificationType(String userId, NotificationType type) {
        LOG.debug("Request to mute notification type {} for userId : {}", type, userId);

        Optional<NotificationPreferenceDTO> prefOpt = findByUserId(userId);
        if (prefOpt.isPresent()) {
            NotificationPreferenceDTO pref = prefOpt.get();
            Set<String> mutedTypes = parseMutedTypes(pref.getMutedTypes());
            mutedTypes.add(type.name());
            pref.setMutedTypes(convertMutedTypesToJson(mutedTypes));
            save(pref);
        } else {
            LOG.warn("NotificationPreference not found for userId: {}", userId);
        }
    }

    @Override
    public void unmuteNotificationType(String userId, NotificationType type) {
        LOG.debug("Request to unmute notification type {} for userId : {}", type, userId);

        Optional<NotificationPreferenceDTO> prefOpt = findByUserId(userId);
        if (prefOpt.isPresent()) {
            NotificationPreferenceDTO pref = prefOpt.get();
            Set<String> mutedTypes = parseMutedTypes(pref.getMutedTypes());
            mutedTypes.remove(type.name());
            pref.setMutedTypes(convertMutedTypesToJson(mutedTypes));
            save(pref);
        } else {
            LOG.warn("NotificationPreference not found for userId: {}", userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTypeMuted(String userId, NotificationType type) {
        Optional<NotificationPreferenceDTO> prefOpt = findByUserId(userId);
        if (prefOpt.isPresent()) {
            Set<String> mutedTypes = parseMutedTypes(prefOpt.get().getMutedTypes());
            return mutedTypes.contains(type.name());
        }
        return false;
    }

    @Override
    public void setLanguage(String userId, String language) {
        LOG.debug("Request to set language={} for userId : {}", language, userId);

        Optional<NotificationPreferenceDTO> prefOpt = findByUserId(userId);
        if (prefOpt.isPresent()) {
            NotificationPreferenceDTO pref = prefOpt.get();
            pref.setLanguage(language);
            save(pref);
        } else {
            LOG.warn("NotificationPreference not found for userId: {}", userId);
        }
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete NotificationPreference : {}", id);
        notificationPreferenceRepository.deleteById(id);
    }

    @Override
    public void deleteByUserId(String userId) {
        LOG.debug("Request to delete NotificationPreference by userId : {}", userId);
        notificationPreferenceRepository.findByUserId(userId).ifPresent(pref -> notificationPreferenceRepository.deleteById(pref.getId()));
    }

    // Méthodes utilitaires privées
    private Set<String> parseMutedTypes(String mutedTypesJson) {
        Set<String> result = new HashSet<>();

        if (mutedTypesJson == null || mutedTypesJson.isEmpty()) {
            return result;
        }

        try {
            String cleaned = mutedTypesJson.replace("[", "").replace("]", "").replace("\"", "");
            if (!cleaned.isEmpty()) {
                String[] types = cleaned.split(",");
                for (String type : types) {
                    result.add(type.trim());
                }
            }
        } catch (Exception e) {
            LOG.error("Error parsing muted types", e);
        }

        return result;
    }

    private String convertMutedTypesToJson(Set<String> mutedTypes) {
        if (mutedTypes == null || mutedTypes.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        int i = 0;
        for (String type : mutedTypes) {
            json.append("\"").append(type).append("\"");
            if (i < mutedTypes.size() - 1) {
                json.append(",");
            }
            i++;
        }
        json.append("]");

        return json.toString();
    }
}
