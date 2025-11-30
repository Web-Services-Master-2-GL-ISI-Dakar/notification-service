package sn.ondmoney.notificationservice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.ondmoney.notificationservice.domain.NotificationRequestAsserts.*;
import static sn.ondmoney.notificationservice.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.notificationservice.IntegrationTest;
import sn.ondmoney.notificationservice.domain.NotificationRequest;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;
import sn.ondmoney.notificationservice.domain.enumeration.Priority;
import sn.ondmoney.notificationservice.repository.NotificationRequestRepository;
import sn.ondmoney.notificationservice.service.dto.NotificationRequestDTO;
import sn.ondmoney.notificationservice.service.mapper.NotificationRequestMapper;

/**
 * Integration tests for the {@link NotificationRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationRequestResourceIT {

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_ACCOUNT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NUMBER = "BBBBBBBBBB";

    private static final NotificationType DEFAULT_TYPE = NotificationType.TRANSACTION_SENT;
    private static final NotificationType UPDATED_TYPE = NotificationType.TRANSACTION_RECEIVED;

    private static final String DEFAULT_CHANNELS = "AAAAAAAAAA";
    private static final String UPDATED_CHANNELS = "BBBBBBBBBB";

    private static final String DEFAULT_DATA = "AAAAAAAAAA";
    private static final String UPDATED_DATA = "BBBBBBBBBB";

    private static final Priority DEFAULT_PRIORITY = Priority.HIGH;
    private static final Priority UPDATED_PRIORITY = Priority.MEDIUM;

    private static final Boolean DEFAULT_IMMEDIATE = false;
    private static final Boolean UPDATED_IMMEDIATE = true;

    private static final String ENTITY_API_URL = "/api/notification-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationRequestRepository notificationRequestRepository;

    @Autowired
    private NotificationRequestMapper notificationRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationRequestMockMvc;

    private NotificationRequest notificationRequest;

    private NotificationRequest insertedNotificationRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationRequest createEntity() {
        return new NotificationRequest()
            .userId(DEFAULT_USER_ID)
            .accountNumber(DEFAULT_ACCOUNT_NUMBER)
            .type(DEFAULT_TYPE)
            .channels(DEFAULT_CHANNELS)
            .data(DEFAULT_DATA)
            .priority(DEFAULT_PRIORITY)
            .immediate(DEFAULT_IMMEDIATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationRequest createUpdatedEntity() {
        return new NotificationRequest()
            .userId(UPDATED_USER_ID)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .type(UPDATED_TYPE)
            .channels(UPDATED_CHANNELS)
            .data(UPDATED_DATA)
            .priority(UPDATED_PRIORITY)
            .immediate(UPDATED_IMMEDIATE);
    }

    @BeforeEach
    void initTest() {
        notificationRequest = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotificationRequest != null) {
            notificationRequestRepository.delete(insertedNotificationRequest);
            insertedNotificationRequest = null;
        }
    }

    @Test
    @Transactional
    void createNotificationRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the NotificationRequest
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);
        var returnedNotificationRequestDTO = om.readValue(
            restNotificationRequestMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(notificationRequestDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificationRequestDTO.class
        );

        // Validate the NotificationRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotificationRequest = notificationRequestMapper.toEntity(returnedNotificationRequestDTO);
        assertNotificationRequestUpdatableFieldsEquals(
            returnedNotificationRequest,
            getPersistedNotificationRequest(returnedNotificationRequest)
        );

        insertedNotificationRequest = returnedNotificationRequest;
    }

    @Test
    @Transactional
    void createNotificationRequestWithExistingId() throws Exception {
        // Create the NotificationRequest with an existing ID
        notificationRequest.setId(1L);
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationRequestMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationRequest.setUserId(null);

        // Create the NotificationRequest, which fails.
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        restNotificationRequestMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationRequest.setType(null);

        // Create the NotificationRequest, which fails.
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        restNotificationRequestMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChannelsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationRequest.setChannels(null);

        // Create the NotificationRequest, which fails.
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        restNotificationRequestMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriorityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationRequest.setPriority(null);

        // Create the NotificationRequest, which fails.
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        restNotificationRequestMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkImmediateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationRequest.setImmediate(null);

        // Create the NotificationRequest, which fails.
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        restNotificationRequestMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotificationRequests() throws Exception {
        // Initialize the database
        insertedNotificationRequest = notificationRequestRepository.saveAndFlush(notificationRequest);

        // Get all the notificationRequestList
        restNotificationRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].channels").value(hasItem(DEFAULT_CHANNELS)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].immediate").value(hasItem(DEFAULT_IMMEDIATE)));
    }

    @Test
    @Transactional
    void getNotificationRequest() throws Exception {
        // Initialize the database
        insertedNotificationRequest = notificationRequestRepository.saveAndFlush(notificationRequest);

        // Get the notificationRequest
        restNotificationRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, notificationRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationRequest.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.accountNumber").value(DEFAULT_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.channels").value(DEFAULT_CHANNELS))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.toString()))
            .andExpect(jsonPath("$.immediate").value(DEFAULT_IMMEDIATE));
    }

    @Test
    @Transactional
    void getNonExistingNotificationRequest() throws Exception {
        // Get the notificationRequest
        restNotificationRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotificationRequest() throws Exception {
        // Initialize the database
        insertedNotificationRequest = notificationRequestRepository.saveAndFlush(notificationRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationRequest
        NotificationRequest updatedNotificationRequest = notificationRequestRepository.findById(notificationRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotificationRequest are not directly saved in db
        em.detach(updatedNotificationRequest);
        updatedNotificationRequest
            .userId(UPDATED_USER_ID)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .type(UPDATED_TYPE)
            .channels(UPDATED_CHANNELS)
            .data(UPDATED_DATA)
            .priority(UPDATED_PRIORITY)
            .immediate(UPDATED_IMMEDIATE);
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(updatedNotificationRequest);

        restNotificationRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationRequestDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the NotificationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationRequestToMatchAllProperties(updatedNotificationRequest);
    }

    @Test
    @Transactional
    void putNonExistingNotificationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationRequest.setId(longCount.incrementAndGet());

        // Create the NotificationRequest
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationRequestDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotificationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationRequest.setId(longCount.incrementAndGet());

        // Create the NotificationRequest
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotificationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationRequest.setId(longCount.incrementAndGet());

        // Create the NotificationRequest
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationRequestMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotificationRequestWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationRequest = notificationRequestRepository.saveAndFlush(notificationRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationRequest using partial update
        NotificationRequest partialUpdatedNotificationRequest = new NotificationRequest();
        partialUpdatedNotificationRequest.setId(notificationRequest.getId());

        partialUpdatedNotificationRequest.accountNumber(UPDATED_ACCOUNT_NUMBER).channels(UPDATED_CHANNELS);

        restNotificationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationRequest.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationRequest))
            )
            .andExpect(status().isOk());

        // Validate the NotificationRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotificationRequest, notificationRequest),
            getPersistedNotificationRequest(notificationRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotificationRequestWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationRequest = notificationRequestRepository.saveAndFlush(notificationRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationRequest using partial update
        NotificationRequest partialUpdatedNotificationRequest = new NotificationRequest();
        partialUpdatedNotificationRequest.setId(notificationRequest.getId());

        partialUpdatedNotificationRequest
            .userId(UPDATED_USER_ID)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .type(UPDATED_TYPE)
            .channels(UPDATED_CHANNELS)
            .data(UPDATED_DATA)
            .priority(UPDATED_PRIORITY)
            .immediate(UPDATED_IMMEDIATE);

        restNotificationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationRequest.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationRequest))
            )
            .andExpect(status().isOk());

        // Validate the NotificationRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationRequestUpdatableFieldsEquals(
            partialUpdatedNotificationRequest,
            getPersistedNotificationRequest(partialUpdatedNotificationRequest)
        );
    }

    @Test
    @Transactional
    void patchNonExistingNotificationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationRequest.setId(longCount.incrementAndGet());

        // Create the NotificationRequest
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationRequestDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotificationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationRequest.setId(longCount.incrementAndGet());

        // Create the NotificationRequest
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotificationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationRequest.setId(longCount.incrementAndGet());

        // Create the NotificationRequest
        NotificationRequestDTO notificationRequestDTO = notificationRequestMapper.toDto(notificationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotificationRequest() throws Exception {
        // Initialize the database
        insertedNotificationRequest = notificationRequestRepository.saveAndFlush(notificationRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notificationRequest
        restNotificationRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationRequest.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notificationRequestRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected NotificationRequest getPersistedNotificationRequest(NotificationRequest notificationRequest) {
        return notificationRequestRepository.findById(notificationRequest.getId()).orElseThrow();
    }

    protected void assertPersistedNotificationRequestToMatchAllProperties(NotificationRequest expectedNotificationRequest) {
        assertNotificationRequestAllPropertiesEquals(
            expectedNotificationRequest,
            getPersistedNotificationRequest(expectedNotificationRequest)
        );
    }

    protected void assertPersistedNotificationRequestToMatchUpdatableProperties(NotificationRequest expectedNotificationRequest) {
        assertNotificationRequestAllUpdatablePropertiesEquals(
            expectedNotificationRequest,
            getPersistedNotificationRequest(expectedNotificationRequest)
        );
    }
}
