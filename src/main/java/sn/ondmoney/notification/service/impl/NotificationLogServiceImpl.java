package sn.ondmoney.notification.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sn.ondmoney.notification.domain.NotificationLog;
import sn.ondmoney.notification.repository.NotificationLogRepository;
import sn.ondmoney.notification.repository.search.NotificationLogSearchRepository;
import sn.ondmoney.notification.service.NotificationLogService;
import sn.ondmoney.notification.service.dto.NotificationLogDTO;
import sn.ondmoney.notification.service.mapper.NotificationLogMapper;

/**
 * Service Implementation for managing {@link sn.ondmoney.notification.domain.NotificationLog}.
 */
@Service
public class NotificationLogServiceImpl implements NotificationLogService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationLogServiceImpl.class);

    private final NotificationLogRepository notificationLogRepository;

    private final NotificationLogMapper notificationLogMapper;

    // Elasticsearch est optionnel en dev
    @Autowired(required = false)
    private NotificationLogSearchRepository notificationLogSearchRepository;

    public NotificationLogServiceImpl(
        NotificationLogRepository notificationLogRepository,
        NotificationLogMapper notificationLogMapper
    ) {
        this.notificationLogRepository = notificationLogRepository;
        this.notificationLogMapper = notificationLogMapper;
    }

    @Override
    public NotificationLogDTO save(NotificationLogDTO notificationLogDTO) {
        LOG.debug("Request to save NotificationLog : {}", notificationLogDTO);
        NotificationLog notificationLog = notificationLogMapper.toEntity(notificationLogDTO);
        notificationLog = notificationLogRepository.save(notificationLog);
        if (notificationLogSearchRepository != null) {
            notificationLogSearchRepository.index(notificationLog);
        }
        return notificationLogMapper.toDto(notificationLog);
    }

    @Override
    public NotificationLogDTO update(NotificationLogDTO notificationLogDTO) {
        LOG.debug("Request to update NotificationLog : {}", notificationLogDTO);
        NotificationLog notificationLog = notificationLogMapper.toEntity(notificationLogDTO);
        notificationLog = notificationLogRepository.save(notificationLog);
        if (notificationLogSearchRepository != null) {
            notificationLogSearchRepository.index(notificationLog);
        }
        return notificationLogMapper.toDto(notificationLog);
    }

    @Override
    public Optional<NotificationLogDTO> partialUpdate(NotificationLogDTO notificationLogDTO) {
        LOG.debug("Request to partially update NotificationLog : {}", notificationLogDTO);

        return notificationLogRepository
            .findById(notificationLogDTO.getId())
            .map(existingNotificationLog -> {
                notificationLogMapper.partialUpdate(existingNotificationLog, notificationLogDTO);

                return existingNotificationLog;
            })
            .map(notificationLogRepository::save)
            .map(savedNotificationLog -> {
                if (notificationLogSearchRepository != null) {
                    notificationLogSearchRepository.index(savedNotificationLog);
                }
                return savedNotificationLog;
            })
            .map(notificationLogMapper::toDto);
    }

    @Override
    public Page<NotificationLogDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all NotificationLogs");
        return notificationLogRepository.findAll(pageable).map(notificationLogMapper::toDto);
    }

    @Override
    public Optional<NotificationLogDTO> findOne(String id) {
        LOG.debug("Request to get NotificationLog : {}", id);
        return notificationLogRepository.findById(id).map(notificationLogMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete NotificationLog : {}", id);
        notificationLogRepository.deleteById(id);
        if (notificationLogSearchRepository != null) {
            notificationLogSearchRepository.deleteFromIndexById(id);
        }
    }

    @Override
    public Page<NotificationLogDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of NotificationLogs for query {}", query);
        if (notificationLogSearchRepository != null) {
            return notificationLogSearchRepository.search(query, pageable).map(notificationLogMapper::toDto);
        }
        LOG.warn("Elasticsearch not available, returning empty page for search");
        return Page.empty(pageable);
    }
}
