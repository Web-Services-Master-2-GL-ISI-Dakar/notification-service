package sn.ondmoney.notificationservice.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.notificationservice.domain.NotificationRequest;
import sn.ondmoney.notificationservice.repository.NotificationRequestRepository;
import sn.ondmoney.notificationservice.service.NotificationRequestService;
import sn.ondmoney.notificationservice.service.dto.NotificationRequestDTO;
import sn.ondmoney.notificationservice.service.mapper.NotificationRequestMapper;

/**
 * Service Implementation for managing {@link sn.ondmoney.notificationservice.domain.NotificationRequest}.
 */
@Service
@Transactional
public class NotificationRequestServiceImpl implements NotificationRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationRequestServiceImpl.class);

    private final NotificationRequestRepository notificationRequestRepository;
    private final NotificationRequestMapper notificationRequestMapper;

    public NotificationRequestServiceImpl(
        NotificationRequestRepository notificationRequestRepository,
        NotificationRequestMapper notificationRequestMapper
    ) {
        this.notificationRequestRepository = notificationRequestRepository;
        this.notificationRequestMapper = notificationRequestMapper;
    }

    @Override
    public NotificationRequestDTO save(NotificationRequestDTO notificationRequestDTO) {
        LOG.debug("Request to save NotificationRequest : {}", notificationRequestDTO);
        NotificationRequest notificationRequest = notificationRequestMapper.toEntity(notificationRequestDTO);
        notificationRequest = notificationRequestRepository.save(notificationRequest);
        return notificationRequestMapper.toDto(notificationRequest);
    }

    @Override
    public NotificationRequestDTO update(NotificationRequestDTO notificationRequestDTO) {
        LOG.debug("Request to update NotificationRequest : {}", notificationRequestDTO);
        NotificationRequest notificationRequest = notificationRequestMapper.toEntity(notificationRequestDTO);
        notificationRequest = notificationRequestRepository.save(notificationRequest);
        return notificationRequestMapper.toDto(notificationRequest);
    }

    @Override
    public Optional<NotificationRequestDTO> partialUpdate(NotificationRequestDTO notificationRequestDTO) {
        LOG.debug("Request to partially update NotificationRequest : {}", notificationRequestDTO);

        return notificationRequestRepository
            .findById(Long.valueOf(notificationRequestDTO.getUserId()))
            .map(existingNotificationRequest -> {
                notificationRequestMapper.partialUpdate(existingNotificationRequest, notificationRequestDTO);
                return existingNotificationRequest;
            })
            .map(notificationRequestRepository::save)
            .map(notificationRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationRequestDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all NotificationRequests");
        return notificationRequestRepository.findAll(pageable).map(notificationRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationRequestDTO> findOne(Long id) {
        LOG.debug("Request to get NotificationRequest : {}", id);
        return notificationRequestRepository.findById(id).map(notificationRequestMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete NotificationRequest : {}", id);
        notificationRequestRepository.deleteById(id);
    }
}
