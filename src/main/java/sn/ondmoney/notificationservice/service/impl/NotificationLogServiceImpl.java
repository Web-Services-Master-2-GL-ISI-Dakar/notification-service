package sn.ondmoney.notificationservice.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.notificationservice.domain.NotificationLog;
import sn.ondmoney.notificationservice.repository.NotificationLogRepository;
import sn.ondmoney.notificationservice.service.NotificationLogService;
import sn.ondmoney.notificationservice.service.dto.NotificationLogDTO;
import sn.ondmoney.notificationservice.service.mapper.NotificationLogMapper;

/**
 * Service Implementation for managing {@link sn.ondmoney.notificationservice.domain.NotificationLog}.
 */
@Service
@Transactional
public class NotificationLogServiceImpl implements NotificationLogService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationLogServiceImpl.class);

    private final NotificationLogRepository notificationLogRepository;

    private final NotificationLogMapper notificationLogMapper;

    public NotificationLogServiceImpl(NotificationLogRepository notificationLogRepository, NotificationLogMapper notificationLogMapper) {
        this.notificationLogRepository = notificationLogRepository;
        this.notificationLogMapper = notificationLogMapper;
    }

    @Override
    public NotificationLogDTO save(NotificationLogDTO notificationLogDTO) {
        LOG.debug("Request to save NotificationLog : {}", notificationLogDTO);
        NotificationLog notificationLog = notificationLogMapper.toEntity(notificationLogDTO);
        notificationLog = notificationLogRepository.save(notificationLog);
        return notificationLogMapper.toDto(notificationLog);
    }

    @Override
    public NotificationLogDTO update(NotificationLogDTO notificationLogDTO) {
        LOG.debug("Request to update NotificationLog : {}", notificationLogDTO);
        NotificationLog notificationLog = notificationLogMapper.toEntity(notificationLogDTO);
        notificationLog = notificationLogRepository.save(notificationLog);
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
            .map(notificationLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationLogDTO> findOne(Long id) {
        LOG.debug("Request to get NotificationLog : {}", id);
        return notificationLogRepository.findById(id).map(notificationLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete NotificationLog : {}", id);
        notificationLogRepository.deleteById(id);
    }
}
