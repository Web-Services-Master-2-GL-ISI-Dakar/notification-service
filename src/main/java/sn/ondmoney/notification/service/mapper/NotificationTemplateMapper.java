package sn.ondmoney.notification.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.notification.domain.NotificationTemplate;
import sn.ondmoney.notification.service.dto.NotificationTemplateDTO;

/**
 * Mapper for the entity {@link NotificationTemplate} and its DTO {@link NotificationTemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationTemplateMapper extends EntityMapper<NotificationTemplateDTO, NotificationTemplate> {}
