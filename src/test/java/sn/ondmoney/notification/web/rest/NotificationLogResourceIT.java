package sn.ondmoney.notification.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.ondmoney.notification.domain.NotificationLogAsserts.*;
import static sn.ondmoney.notification.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import sn.ondmoney.notification.IntegrationTest;
import sn.ondmoney.notification.domain.NotificationLog;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationStatus;
import sn.ondmoney.notification.domain.enumeration.NotificationType;
import sn.ondmoney.notification.repository.NotificationLogRepository;
import sn.ondmoney.notification.repository.search.NotificationLogSearchRepository;
import sn.ondmoney.notification.service.dto.NotificationLogDTO;
import sn.ondmoney.notification.service.mapper.NotificationLogMapper;

/**
 * Integration tests for the {@link NotificationLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationLogResourceIT {

    private static final String DEFAULT_EVENT_REF = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_REF = "BBBBBBBBBB";

    private static final Instant DEFAULT_EVENT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EVENT_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_RECIPIENT = "AAAAAAAAAA";
    private static final String UPDATED_RECIPIENT = "BBBBBBBBBB";

    private static final NotificationType DEFAULT_NOTIFICATION_TYPE = NotificationType.OTP_REQUEST;
    private static final NotificationType UPDATED_NOTIFICATION_TYPE = NotificationType.EMAIL_VERIFICATION_REQUEST;

    private static final NotificationStatus DEFAULT_NOTIFICATION_STATUS = NotificationStatus.PENDING;
    private static final NotificationStatus UPDATED_NOTIFICATION_STATUS = NotificationStatus.SENT;

    private static final NotificationChannel DEFAULT_NOTIFICATION_CHANNEL = NotificationChannel.SMS;
    private static final NotificationChannel UPDATED_NOTIFICATION_CHANNEL = NotificationChannel.EMAIL;

    private static final String DEFAULT_PAYLOAD = "AAAAAAAAAA";
    private static final String UPDATED_PAYLOAD = "BBBBBBBBBB";

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_EXTERNAL_EVENT_REF = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_EVENT_REF = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final Integer DEFAULT_RETRY_COUNT = 0;
    private static final Integer UPDATED_RETRY_COUNT = 1;

    private static final Instant DEFAULT_FAILED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FAILED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/notification-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/notification-logs/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Autowired
    private NotificationLogMapper notificationLogMapper;

    @Autowired
    private NotificationLogSearchRepository notificationLogSearchRepository;

    @Autowired
    private MockMvc restNotificationLogMockMvc;

    private NotificationLog notificationLog;

    private NotificationLog insertedNotificationLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationLog createEntity() {
        return new NotificationLog()
            .eventRef(DEFAULT_EVENT_REF)
            .eventTime(DEFAULT_EVENT_TIME)
            .userId(DEFAULT_USER_ID)
            .recipient(DEFAULT_RECIPIENT)
            .notificationType(DEFAULT_NOTIFICATION_TYPE)
            .notificationStatus(DEFAULT_NOTIFICATION_STATUS)
            .notificationChannel(DEFAULT_NOTIFICATION_CHANNEL)
            .payload(DEFAULT_PAYLOAD)
            .sentAt(DEFAULT_SENT_AT)
            .externalEventRef(DEFAULT_EXTERNAL_EVENT_REF)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .retryCount(DEFAULT_RETRY_COUNT)
            .failedAt(DEFAULT_FAILED_AT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationLog createUpdatedEntity() {
        return new NotificationLog()
            .eventRef(UPDATED_EVENT_REF)
            .eventTime(UPDATED_EVENT_TIME)
            .userId(UPDATED_USER_ID)
            .recipient(UPDATED_RECIPIENT)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationStatus(UPDATED_NOTIFICATION_STATUS)
            .notificationChannel(UPDATED_NOTIFICATION_CHANNEL)
            .payload(UPDATED_PAYLOAD)
            .sentAt(UPDATED_SENT_AT)
            .externalEventRef(UPDATED_EXTERNAL_EVENT_REF)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .retryCount(UPDATED_RETRY_COUNT)
            .failedAt(UPDATED_FAILED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        notificationLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotificationLog != null) {
            notificationLogRepository.delete(insertedNotificationLog);
            notificationLogSearchRepository.delete(insertedNotificationLog);
            insertedNotificationLog = null;
        }
    }

    @Test
    void createNotificationLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);
        var returnedNotificationLogDTO = om.readValue(
            restNotificationLogMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(notificationLogDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificationLogDTO.class
        );

        // Validate the NotificationLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotificationLog = notificationLogMapper.toEntity(returnedNotificationLogDTO);
        assertNotificationLogUpdatableFieldsEquals(returnedNotificationLog, getPersistedNotificationLog(returnedNotificationLog));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedNotificationLog = returnedNotificationLog;
    }

    @Test
    void createNotificationLogWithExistingId() throws Exception {
        // Create the NotificationLog with an existing ID
        notificationLog.setId("existing_id");
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEventRefIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        // set the field null
        notificationLog.setEventRef(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkRecipientIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        // set the field null
        notificationLog.setRecipient(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNotificationTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        // set the field null
        notificationLog.setNotificationType(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNotificationStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        // set the field null
        notificationLog.setNotificationStatus(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNotificationChannelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        // set the field null
        notificationLog.setNotificationChannel(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSentAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        // set the field null
        notificationLog.setSentAt(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllNotificationLogs() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.save(notificationLog);

        // Get all the notificationLogList
        restNotificationLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationLog.getId())))
            .andExpect(jsonPath("$.[*].eventRef").value(hasItem(DEFAULT_EVENT_REF)))
            .andExpect(jsonPath("$.[*].eventTime").value(hasItem(DEFAULT_EVENT_TIME.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].recipient").value(hasItem(DEFAULT_RECIPIENT)))
            .andExpect(jsonPath("$.[*].notificationType").value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].notificationStatus").value(hasItem(DEFAULT_NOTIFICATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].notificationChannel").value(hasItem(DEFAULT_NOTIFICATION_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD)))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].externalEventRef").value(hasItem(DEFAULT_EXTERNAL_EVENT_REF)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].failedAt").value(hasItem(DEFAULT_FAILED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    void getNotificationLog() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.save(notificationLog);

        // Get the notificationLog
        restNotificationLogMockMvc
            .perform(get(ENTITY_API_URL_ID, notificationLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationLog.getId()))
            .andExpect(jsonPath("$.eventRef").value(DEFAULT_EVENT_REF))
            .andExpect(jsonPath("$.eventTime").value(DEFAULT_EVENT_TIME.toString()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.recipient").value(DEFAULT_RECIPIENT))
            .andExpect(jsonPath("$.notificationType").value(DEFAULT_NOTIFICATION_TYPE.toString()))
            .andExpect(jsonPath("$.notificationStatus").value(DEFAULT_NOTIFICATION_STATUS.toString()))
            .andExpect(jsonPath("$.notificationChannel").value(DEFAULT_NOTIFICATION_CHANNEL.toString()))
            .andExpect(jsonPath("$.payload").value(DEFAULT_PAYLOAD))
            .andExpect(jsonPath("$.sentAt").value(DEFAULT_SENT_AT.toString()))
            .andExpect(jsonPath("$.externalEventRef").value(DEFAULT_EXTERNAL_EVENT_REF))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.retryCount").value(DEFAULT_RETRY_COUNT))
            .andExpect(jsonPath("$.failedAt").value(DEFAULT_FAILED_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getNonExistingNotificationLog() throws Exception {
        // Get the notificationLog
        restNotificationLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingNotificationLog() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.save(notificationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLogSearchRepository.save(notificationLog);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());

        // Update the notificationLog
        NotificationLog updatedNotificationLog = notificationLogRepository.findById(notificationLog.getId()).orElseThrow();
        updatedNotificationLog
            .eventRef(UPDATED_EVENT_REF)
            .eventTime(UPDATED_EVENT_TIME)
            .userId(UPDATED_USER_ID)
            .recipient(UPDATED_RECIPIENT)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationStatus(UPDATED_NOTIFICATION_STATUS)
            .notificationChannel(UPDATED_NOTIFICATION_CHANNEL)
            .payload(UPDATED_PAYLOAD)
            .sentAt(UPDATED_SENT_AT)
            .externalEventRef(UPDATED_EXTERNAL_EVENT_REF)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .retryCount(UPDATED_RETRY_COUNT)
            .failedAt(UPDATED_FAILED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(updatedNotificationLog);

        restNotificationLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationLogDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationLogToMatchAllProperties(updatedNotificationLog);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<NotificationLog> notificationLogSearchList = Streamable.of(notificationLogSearchRepository.findAll()).toList();
                NotificationLog testNotificationLogSearch = notificationLogSearchList.get(searchDatabaseSizeAfter - 1);

                assertNotificationLogAllPropertiesEquals(testNotificationLogSearch, updatedNotificationLog);
            });
    }

    @Test
    void putNonExistingNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        notificationLog.setId(UUID.randomUUID().toString());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationLogDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        notificationLog.setId(UUID.randomUUID().toString());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        notificationLog.setId(UUID.randomUUID().toString());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateNotificationLogWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.save(notificationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationLog using partial update
        NotificationLog partialUpdatedNotificationLog = new NotificationLog();
        partialUpdatedNotificationLog.setId(notificationLog.getId());

        partialUpdatedNotificationLog
            .eventTime(UPDATED_EVENT_TIME)
            .userId(UPDATED_USER_ID)
            .recipient(UPDATED_RECIPIENT)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationStatus(UPDATED_NOTIFICATION_STATUS)
            .payload(UPDATED_PAYLOAD)
            .sentAt(UPDATED_SENT_AT)
            .externalEventRef(UPDATED_EXTERNAL_EVENT_REF)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .failedAt(UPDATED_FAILED_AT);

        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationLog.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationLog))
            )
            .andExpect(status().isOk());

        // Validate the NotificationLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotificationLog, notificationLog),
            getPersistedNotificationLog(notificationLog)
        );
    }

    @Test
    void fullUpdateNotificationLogWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.save(notificationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationLog using partial update
        NotificationLog partialUpdatedNotificationLog = new NotificationLog();
        partialUpdatedNotificationLog.setId(notificationLog.getId());

        partialUpdatedNotificationLog
            .eventRef(UPDATED_EVENT_REF)
            .eventTime(UPDATED_EVENT_TIME)
            .userId(UPDATED_USER_ID)
            .recipient(UPDATED_RECIPIENT)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationStatus(UPDATED_NOTIFICATION_STATUS)
            .notificationChannel(UPDATED_NOTIFICATION_CHANNEL)
            .payload(UPDATED_PAYLOAD)
            .sentAt(UPDATED_SENT_AT)
            .externalEventRef(UPDATED_EXTERNAL_EVENT_REF)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .retryCount(UPDATED_RETRY_COUNT)
            .failedAt(UPDATED_FAILED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationLog.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationLog))
            )
            .andExpect(status().isOk());

        // Validate the NotificationLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationLogUpdatableFieldsEquals(
            partialUpdatedNotificationLog,
            getPersistedNotificationLog(partialUpdatedNotificationLog)
        );
    }

    @Test
    void patchNonExistingNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        notificationLog.setId(UUID.randomUUID().toString());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationLogDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        notificationLog.setId(UUID.randomUUID().toString());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        notificationLog.setId(UUID.randomUUID().toString());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteNotificationLog() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.save(notificationLog);
        notificationLogRepository.save(notificationLog);
        notificationLogSearchRepository.save(notificationLog);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the notificationLog
        restNotificationLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationLog.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchNotificationLog() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.save(notificationLog);
        notificationLogSearchRepository.save(notificationLog);

        // Search the notificationLog
        restNotificationLogMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + notificationLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationLog.getId())))
            .andExpect(jsonPath("$.[*].eventRef").value(hasItem(DEFAULT_EVENT_REF)))
            .andExpect(jsonPath("$.[*].eventTime").value(hasItem(DEFAULT_EVENT_TIME.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].recipient").value(hasItem(DEFAULT_RECIPIENT)))
            .andExpect(jsonPath("$.[*].notificationType").value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].notificationStatus").value(hasItem(DEFAULT_NOTIFICATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].notificationChannel").value(hasItem(DEFAULT_NOTIFICATION_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].payload").value(hasItem(DEFAULT_PAYLOAD.toString())))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].externalEventRef").value(hasItem(DEFAULT_EXTERNAL_EVENT_REF)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].failedAt").value(hasItem(DEFAULT_FAILED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return notificationLogRepository.count();
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

    protected NotificationLog getPersistedNotificationLog(NotificationLog notificationLog) {
        return notificationLogRepository.findById(notificationLog.getId()).orElseThrow();
    }

    protected void assertPersistedNotificationLogToMatchAllProperties(NotificationLog expectedNotificationLog) {
        assertNotificationLogAllPropertiesEquals(expectedNotificationLog, getPersistedNotificationLog(expectedNotificationLog));
    }

    protected void assertPersistedNotificationLogToMatchUpdatableProperties(NotificationLog expectedNotificationLog) {
        assertNotificationLogAllUpdatablePropertiesEquals(expectedNotificationLog, getPersistedNotificationLog(expectedNotificationLog));
    }
}
