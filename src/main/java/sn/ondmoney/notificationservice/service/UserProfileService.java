package sn.ondmoney.notificationservice.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.ondmoney.notificationservice.service.dto.UserProfileDTO;

/**
 * Service Interface for managing {@link sn.ondmoney.notificationservice.domain.UserProfile}.
 */
public interface UserProfileService {
    /**
     * Save a userProfile.
     */
    UserProfileDTO save(UserProfileDTO userProfileDTO);

    /**
     * Updates a userProfile.
     */
    UserProfileDTO update(UserProfileDTO userProfileDTO);

    /**
     * Partially updates a userProfile.
     */
    Optional<UserProfileDTO> partialUpdate(UserProfileDTO userProfileDTO);

    /**
     * Get all the userProfiles.
     */
    Page<UserProfileDTO> findAll(Pageable pageable);

    /**
     * Get the "id" userProfile.
     */
    Optional<UserProfileDTO> findOne(Long id);

    /**
     * Get userProfile by userId.
     */
    Optional<UserProfileDTO> findByUserId(String userId);

    /**
     * Check if userProfile exists for userId.
     */
    boolean existsByUserId(String userId);

    /**
     * Update phone number.
     */
    void updatePhoneNumber(String userId, String phoneNumber, boolean verified);

    /**
     * Update email.
     */
    void updateEmail(String userId, String email, boolean verified);

    /**
     * Update device token.
     */
    void updateDeviceToken(String userId, String deviceToken);

    /**
     * Create default profile.
     */
    UserProfileDTO createDefaultProfile(String userId, String phoneNumber, String email);

    /**
     * Delete the "id" userProfile.
     */
    void delete(Long id);

    /**
     * Delete by userId.
     */
    void deleteByUserId(String userId);
}
