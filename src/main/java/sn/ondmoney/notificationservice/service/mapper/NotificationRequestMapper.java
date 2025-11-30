package sn.ondmoney.notificationservice.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.notificationservice.domain.NotificationRequest;
import sn.ondmoney.notificationservice.service.dto.NotificationRequestDTO;

/**
 * Mapper for the entity {@link NotificationRequest} and its DTO {@link NotificationRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationRequestMapper extends EntityMapper<NotificationRequestDTO, NotificationRequest> {}
