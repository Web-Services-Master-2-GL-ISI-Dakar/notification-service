package sn.ondmoney.notificationservice.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.notificationservice.domain.UserProfile;
import sn.ondmoney.notificationservice.service.dto.UserProfileDTO;

/**
 * Mapper for the entity {@link UserProfile} and its DTO {@link UserProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserProfileMapper extends EntityMapper<UserProfileDTO, UserProfile> {}
