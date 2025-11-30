package sn.ondmoney.notificationservice.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.notificationservice.domain.TransactionEvent;
import sn.ondmoney.notificationservice.repository.TransactionEventRepository;
import sn.ondmoney.notificationservice.service.TransactionEventService;
import sn.ondmoney.notificationservice.service.dto.TransactionEventDTO;
import sn.ondmoney.notificationservice.service.mapper.TransactionEventMapper;

/**
 * Service Implementation for managing {@link sn.ondmoney.notificationservice.domain.TransactionEvent}.
 */
@Service
@Transactional
public class TransactionEventServiceImpl implements TransactionEventService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionEventServiceImpl.class);

    private final TransactionEventRepository transactionEventRepository;

    private final TransactionEventMapper transactionEventMapper;

    public TransactionEventServiceImpl(
        TransactionEventRepository transactionEventRepository,
        TransactionEventMapper transactionEventMapper
    ) {
        this.transactionEventRepository = transactionEventRepository;
        this.transactionEventMapper = transactionEventMapper;
    }

    @Override
    public TransactionEventDTO save(TransactionEventDTO transactionEventDTO) {
        LOG.debug("Request to save TransactionEvent : {}", transactionEventDTO);
        TransactionEvent transactionEvent = transactionEventMapper.toEntity(transactionEventDTO);
        transactionEvent = transactionEventRepository.save(transactionEvent);
        return transactionEventMapper.toDto(transactionEvent);
    }

    @Override
    public TransactionEventDTO update(TransactionEventDTO transactionEventDTO) {
        LOG.debug("Request to update TransactionEvent : {}", transactionEventDTO);
        TransactionEvent transactionEvent = transactionEventMapper.toEntity(transactionEventDTO);
        transactionEvent = transactionEventRepository.save(transactionEvent);
        return transactionEventMapper.toDto(transactionEvent);
    }

    @Override
    public Optional<TransactionEventDTO> partialUpdate(TransactionEventDTO transactionEventDTO) {
        LOG.debug("Request to partially update TransactionEvent : {}", transactionEventDTO);

        return transactionEventRepository
            .findById(Long.valueOf(transactionEventDTO.getTransactionId()))
            .map(existingTransactionEvent -> {
                transactionEventMapper.partialUpdate(existingTransactionEvent, transactionEventDTO);

                return existingTransactionEvent;
            })
            .map(transactionEventRepository::save)
            .map(transactionEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionEventDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TransactionEvents");
        return transactionEventRepository.findAll(pageable).map(transactionEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionEventDTO> findOne(Long id) {
        LOG.debug("Request to get TransactionEvent : {}", id);
        return transactionEventRepository.findById(id).map(transactionEventMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TransactionEvent : {}", id);
        transactionEventRepository.deleteById(id);
    }
}
