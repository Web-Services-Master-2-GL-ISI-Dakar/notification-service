package sn.ondmoney.notificationservice.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.notificationservice.domain.TransactionEvent;
import sn.ondmoney.notificationservice.service.dto.TransactionEventDTO;

/**
 * Mapper for the entity {@link TransactionEvent} and its DTO {@link TransactionEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionEventMapper extends EntityMapper<TransactionEventDTO, TransactionEvent> {}
