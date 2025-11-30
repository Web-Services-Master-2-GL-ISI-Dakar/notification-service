package sn.ondmoney.notificationservice.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.notificationservice.domain.NotificationPreference;
import sn.ondmoney.notificationservice.service.dto.NotificationPreferenceDTO;

/**
 * Mapper for the entity {@link NotificationPreference} and its DTO {@link NotificationPreferenceDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationPreferenceMapper extends EntityMapper<NotificationPreferenceDTO, NotificationPreference> {}
