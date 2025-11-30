package sn.ondmoney.notificationservice.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.notificationservice.domain.UserProfile;
import sn.ondmoney.notificationservice.repository.UserProfileRepository;
import sn.ondmoney.notificationservice.service.UserProfileService;
import sn.ondmoney.notificationservice.service.dto.UserProfileDTO;
import sn.ondmoney.notificationservice.service.mapper.UserProfileMapper;

/**
 * Service Implementation for managing {@link sn.ondmoney.notificationservice.domain.UserProfile}.
 */
@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public UserProfileDTO save(UserProfileDTO userProfileDTO) {
        LOG.debug("Request to save UserProfile : {}", userProfileDTO);
        UserProfile userProfile = userProfileMapper.toEntity(userProfileDTO);
        userProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toDto(userProfile);
    }

    @Override
    public UserProfileDTO update(UserProfileDTO userProfileDTO) {
        LOG.debug("Request to update UserProfile : {}", userProfileDTO);
        UserProfile userProfile = userProfileMapper.toEntity(userProfileDTO);
        userProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toDto(userProfile);
    }

    @Override
    public Optional<UserProfileDTO> partialUpdate(UserProfileDTO userProfileDTO) {
        LOG.debug("Request to partially update UserProfile : {}", userProfileDTO);

        return userProfileRepository
            .findById(userProfileDTO.getId())
            .map(existingUserProfile -> {
                userProfileMapper.partialUpdate(existingUserProfile, userProfileDTO);
                return existingUserProfile;
            })
            .map(userProfileRepository::save)
            .map(userProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserProfiles");
        return userProfileRepository.findAll(pageable).map(userProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileDTO> findOne(Long id) {
        LOG.debug("Request to get UserProfile : {}", id);
        return userProfileRepository.findById(id).map(userProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileDTO> findByUserId(String userId) {
        LOG.debug("Request to get UserProfile by userId : {}", userId);
        return userProfileRepository.findByUserId(userId).map(userProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(String userId) {
        LOG.debug("Request to check if UserProfile exists for userId : {}", userId);
        return userProfileRepository.findByUserId(userId).isPresent();
    }

    @Override
    public void updatePhoneNumber(String userId, String phoneNumber, boolean verified) {
        LOG.debug("Request to update phone number for userId : {}", userId);

        Optional<UserProfileDTO> profileOpt = findByUserId(userId);
        if (profileOpt.isPresent()) {
            UserProfileDTO profile = profileOpt.get();
            profile.setPhoneNumber(phoneNumber);
            profile.setPhoneVerified(verified);
            save(profile);
        } else {
            LOG.warn("UserProfile not found for userId: {}", userId);
        }
    }

    @Override
    public void updateEmail(String userId, String email, boolean verified) {
        LOG.debug("Request to update email for userId : {}", userId);

        Optional<UserProfileDTO> profileOpt = findByUserId(userId);
        if (profileOpt.isPresent()) {
            UserProfileDTO profile = profileOpt.get();
            profile.setEmail(email);
            profile.setEmailVerified(verified);
            save(profile);
        } else {
            LOG.warn("UserProfile not found for userId: {}", userId);
        }
    }

    @Override
    public void updateDeviceToken(String userId, String deviceToken) {
        LOG.debug("Request to update device token for userId : {}", userId);

        Optional<UserProfileDTO> profileOpt = findByUserId(userId);
        if (profileOpt.isPresent()) {
            UserProfileDTO profile = profileOpt.get();
            profile.setDeviceToken(deviceToken);
            save(profile);
        } else {
            LOG.warn("UserProfile not found for userId: {}", userId);
        }
    }

    @Override
    public UserProfileDTO createDefaultProfile(String userId, String phoneNumber, String email) {
        LOG.debug("Request to create default profile for userId : {}", userId);

        UserProfileDTO profile = new UserProfileDTO();
        profile.setUserId(userId);
        profile.setPhoneNumber(phoneNumber);
        profile.setEmail(email);
        profile.setPhoneVerified(false);
        profile.setEmailVerified(false);

        return save(profile);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UserProfile : {}", id);
        userProfileRepository.deleteById(id);
    }

    @Override
    public void deleteByUserId(String userId) {
        LOG.debug("Request to delete UserProfile by userId : {}", userId);
        userProfileRepository.findByUserId(userId).ifPresent(profile -> userProfileRepository.deleteById(profile.getId()));
    }
}
