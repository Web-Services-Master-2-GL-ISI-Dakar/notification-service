package sn.ondmoney.notificationservice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.ondmoney.notificationservice.domain.NotificationLogAsserts.*;
import static sn.ondmoney.notificationservice.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import sn.ondmoney.notificationservice.domain.Notification;
import sn.ondmoney.notificationservice.domain.NotificationLog;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationStatus;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;
import sn.ondmoney.notificationservice.repository.NotificationLogRepository;
import sn.ondmoney.notificationservice.service.dto.NotificationLogDTO;
import sn.ondmoney.notificationservice.service.mapper.NotificationLogMapper;

/**
 * Integration tests for the {@link NotificationLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationLogResourceIT {

    private static final Long DEFAULT_NOTIFICATION_ID = 1L;
    private static final Long UPDATED_NOTIFICATION_ID = 2L;
    private static final Long SMALLER_NOTIFICATION_ID = 1L - 1L;

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final NotificationType DEFAULT_TYPE = NotificationType.TRANSACTION_SENT;
    private static final NotificationType UPDATED_TYPE = NotificationType.TRANSACTION_RECEIVED;

    private static final NotificationChannel DEFAULT_CHANNEL = NotificationChannel.SMS;
    private static final NotificationChannel UPDATED_CHANNEL = NotificationChannel.EMAIL;

    private static final NotificationStatus DEFAULT_STATUS = NotificationStatus.PENDING;
    private static final NotificationStatus UPDATED_STATUS = NotificationStatus.SENT;

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_RECIPIENT = "AAAAAAAAAA";
    private static final String UPDATED_RECIPIENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_RETRY_COUNT = 1;
    private static final Integer UPDATED_RETRY_COUNT = 2;
    private static final Integer SMALLER_RETRY_COUNT = 1 - 1;

    private static final String DEFAULT_CHANNEL_RESULTS = "AAAAAAAAAA";
    private static final String UPDATED_CHANNEL_RESULTS = "BBBBBBBBBB";

    private static final String DEFAULT_ACTION = "AAAAAAAAAA";
    private static final String UPDATED_ACTION = "BBBBBBBBBB";

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/notification-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Autowired
    private NotificationLogMapper notificationLogMapper;

    @Autowired
    private EntityManager em;

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
            .notificationId(DEFAULT_NOTIFICATION_ID)
            .userId(DEFAULT_USER_ID)
            .type(DEFAULT_TYPE)
            .channel(DEFAULT_CHANNEL)
            .status(DEFAULT_STATUS)
            .message(DEFAULT_MESSAGE)
            .recipient(DEFAULT_RECIPIENT)
            .timestamp(DEFAULT_TIMESTAMP)
            .sentAt(DEFAULT_SENT_AT)
            .retryCount(DEFAULT_RETRY_COUNT)
            .channelResults(DEFAULT_CHANNEL_RESULTS)
            .action(DEFAULT_ACTION)
            .details(DEFAULT_DETAILS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationLog createUpdatedEntity() {
        return new NotificationLog()
            .notificationId(UPDATED_NOTIFICATION_ID)
            .userId(UPDATED_USER_ID)
            .type(UPDATED_TYPE)
            .channel(UPDATED_CHANNEL)
            .status(UPDATED_STATUS)
            .message(UPDATED_MESSAGE)
            .recipient(UPDATED_RECIPIENT)
            .timestamp(UPDATED_TIMESTAMP)
            .sentAt(UPDATED_SENT_AT)
            .retryCount(UPDATED_RETRY_COUNT)
            .channelResults(UPDATED_CHANNEL_RESULTS)
            .action(UPDATED_ACTION)
            .details(UPDATED_DETAILS);
    }

    @BeforeEach
    void initTest() {
        notificationLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotificationLog != null) {
            notificationLogRepository.delete(insertedNotificationLog);
            insertedNotificationLog = null;
        }
    }

    @Test
    @Transactional
    void createNotificationLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
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

        insertedNotificationLog = returnedNotificationLog;
    }

    @Test
    @Transactional
    void createNotificationLogWithExistingId() throws Exception {
        // Create the NotificationLog with an existing ID
        notificationLog.setId(1L);
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNotificationIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setNotificationId(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setUserId(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setType(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChannelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setChannel(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setStatus(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationLog.setTimestamp(null);

        // Create the NotificationLog, which fails.
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        restNotificationLogMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotificationLogs() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList
        restNotificationLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].notificationId").value(hasItem(DEFAULT_NOTIFICATION_ID.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].recipient").value(hasItem(DEFAULT_RECIPIENT)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].channelResults").value(hasItem(DEFAULT_CHANNEL_RESULTS)))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)));
    }

    @Test
    @Transactional
    void getNotificationLog() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get the notificationLog
        restNotificationLogMockMvc
            .perform(get(ENTITY_API_URL_ID, notificationLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationLog.getId().intValue()))
            .andExpect(jsonPath("$.notificationId").value(DEFAULT_NOTIFICATION_ID.intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.channel").value(DEFAULT_CHANNEL.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.recipient").value(DEFAULT_RECIPIENT))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.sentAt").value(DEFAULT_SENT_AT.toString()))
            .andExpect(jsonPath("$.retryCount").value(DEFAULT_RETRY_COUNT))
            .andExpect(jsonPath("$.channelResults").value(DEFAULT_CHANNEL_RESULTS))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS));
    }

    @Test
    @Transactional
    void getNotificationLogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        Long id = notificationLog.getId();

        defaultNotificationLogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultNotificationLogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultNotificationLogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByNotificationIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where notificationId equals to
        defaultNotificationLogFiltering(
            "notificationId.equals=" + DEFAULT_NOTIFICATION_ID,
            "notificationId.equals=" + UPDATED_NOTIFICATION_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByNotificationIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where notificationId in
        defaultNotificationLogFiltering(
            "notificationId.in=" + DEFAULT_NOTIFICATION_ID + "," + UPDATED_NOTIFICATION_ID,
            "notificationId.in=" + UPDATED_NOTIFICATION_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByNotificationIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where notificationId is not null
        defaultNotificationLogFiltering("notificationId.specified=true", "notificationId.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByNotificationIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where notificationId is greater than or equal to
        defaultNotificationLogFiltering(
            "notificationId.greaterThanOrEqual=" + DEFAULT_NOTIFICATION_ID,
            "notificationId.greaterThanOrEqual=" + UPDATED_NOTIFICATION_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByNotificationIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where notificationId is less than or equal to
        defaultNotificationLogFiltering(
            "notificationId.lessThanOrEqual=" + DEFAULT_NOTIFICATION_ID,
            "notificationId.lessThanOrEqual=" + SMALLER_NOTIFICATION_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByNotificationIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where notificationId is less than
        defaultNotificationLogFiltering(
            "notificationId.lessThan=" + UPDATED_NOTIFICATION_ID,
            "notificationId.lessThan=" + DEFAULT_NOTIFICATION_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByNotificationIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where notificationId is greater than
        defaultNotificationLogFiltering(
            "notificationId.greaterThan=" + SMALLER_NOTIFICATION_ID,
            "notificationId.greaterThan=" + DEFAULT_NOTIFICATION_ID
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where userId equals to
        defaultNotificationLogFiltering("userId.equals=" + DEFAULT_USER_ID, "userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where userId in
        defaultNotificationLogFiltering("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID, "userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where userId is not null
        defaultNotificationLogFiltering("userId.specified=true", "userId.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByUserIdContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where userId contains
        defaultNotificationLogFiltering("userId.contains=" + DEFAULT_USER_ID, "userId.contains=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByUserIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where userId does not contain
        defaultNotificationLogFiltering("userId.doesNotContain=" + UPDATED_USER_ID, "userId.doesNotContain=" + DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where type equals to
        defaultNotificationLogFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where type in
        defaultNotificationLogFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where type is not null
        defaultNotificationLogFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByChannelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where channel equals to
        defaultNotificationLogFiltering("channel.equals=" + DEFAULT_CHANNEL, "channel.equals=" + UPDATED_CHANNEL);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByChannelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where channel in
        defaultNotificationLogFiltering("channel.in=" + DEFAULT_CHANNEL + "," + UPDATED_CHANNEL, "channel.in=" + UPDATED_CHANNEL);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByChannelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where channel is not null
        defaultNotificationLogFiltering("channel.specified=true", "channel.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where status equals to
        defaultNotificationLogFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where status in
        defaultNotificationLogFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where status is not null
        defaultNotificationLogFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where message equals to
        defaultNotificationLogFiltering("message.equals=" + DEFAULT_MESSAGE, "message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where message in
        defaultNotificationLogFiltering("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE, "message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where message is not null
        defaultNotificationLogFiltering("message.specified=true", "message.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where message contains
        defaultNotificationLogFiltering("message.contains=" + DEFAULT_MESSAGE, "message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where message does not contain
        defaultNotificationLogFiltering("message.doesNotContain=" + UPDATED_MESSAGE, "message.doesNotContain=" + DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRecipientIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where recipient equals to
        defaultNotificationLogFiltering("recipient.equals=" + DEFAULT_RECIPIENT, "recipient.equals=" + UPDATED_RECIPIENT);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRecipientIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where recipient in
        defaultNotificationLogFiltering("recipient.in=" + DEFAULT_RECIPIENT + "," + UPDATED_RECIPIENT, "recipient.in=" + UPDATED_RECIPIENT);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRecipientIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where recipient is not null
        defaultNotificationLogFiltering("recipient.specified=true", "recipient.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRecipientContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where recipient contains
        defaultNotificationLogFiltering("recipient.contains=" + DEFAULT_RECIPIENT, "recipient.contains=" + UPDATED_RECIPIENT);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRecipientNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where recipient does not contain
        defaultNotificationLogFiltering("recipient.doesNotContain=" + UPDATED_RECIPIENT, "recipient.doesNotContain=" + DEFAULT_RECIPIENT);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByTimestampIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where timestamp equals to
        defaultNotificationLogFiltering("timestamp.equals=" + DEFAULT_TIMESTAMP, "timestamp.equals=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByTimestampIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where timestamp in
        defaultNotificationLogFiltering("timestamp.in=" + DEFAULT_TIMESTAMP + "," + UPDATED_TIMESTAMP, "timestamp.in=" + UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByTimestampIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where timestamp is not null
        defaultNotificationLogFiltering("timestamp.specified=true", "timestamp.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsBySentAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where sentAt equals to
        defaultNotificationLogFiltering("sentAt.equals=" + DEFAULT_SENT_AT, "sentAt.equals=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllNotificationLogsBySentAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where sentAt in
        defaultNotificationLogFiltering("sentAt.in=" + DEFAULT_SENT_AT + "," + UPDATED_SENT_AT, "sentAt.in=" + UPDATED_SENT_AT);
    }

    @Test
    @Transactional
    void getAllNotificationLogsBySentAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where sentAt is not null
        defaultNotificationLogFiltering("sentAt.specified=true", "sentAt.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRetryCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where retryCount equals to
        defaultNotificationLogFiltering("retryCount.equals=" + DEFAULT_RETRY_COUNT, "retryCount.equals=" + UPDATED_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRetryCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where retryCount in
        defaultNotificationLogFiltering(
            "retryCount.in=" + DEFAULT_RETRY_COUNT + "," + UPDATED_RETRY_COUNT,
            "retryCount.in=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRetryCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where retryCount is not null
        defaultNotificationLogFiltering("retryCount.specified=true", "retryCount.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRetryCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where retryCount is greater than or equal to
        defaultNotificationLogFiltering(
            "retryCount.greaterThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.greaterThanOrEqual=" + UPDATED_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRetryCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where retryCount is less than or equal to
        defaultNotificationLogFiltering(
            "retryCount.lessThanOrEqual=" + DEFAULT_RETRY_COUNT,
            "retryCount.lessThanOrEqual=" + SMALLER_RETRY_COUNT
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRetryCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where retryCount is less than
        defaultNotificationLogFiltering("retryCount.lessThan=" + UPDATED_RETRY_COUNT, "retryCount.lessThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByRetryCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where retryCount is greater than
        defaultNotificationLogFiltering("retryCount.greaterThan=" + SMALLER_RETRY_COUNT, "retryCount.greaterThan=" + DEFAULT_RETRY_COUNT);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByChannelResultsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where channelResults equals to
        defaultNotificationLogFiltering(
            "channelResults.equals=" + DEFAULT_CHANNEL_RESULTS,
            "channelResults.equals=" + UPDATED_CHANNEL_RESULTS
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByChannelResultsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where channelResults in
        defaultNotificationLogFiltering(
            "channelResults.in=" + DEFAULT_CHANNEL_RESULTS + "," + UPDATED_CHANNEL_RESULTS,
            "channelResults.in=" + UPDATED_CHANNEL_RESULTS
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByChannelResultsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where channelResults is not null
        defaultNotificationLogFiltering("channelResults.specified=true", "channelResults.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByChannelResultsContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where channelResults contains
        defaultNotificationLogFiltering(
            "channelResults.contains=" + DEFAULT_CHANNEL_RESULTS,
            "channelResults.contains=" + UPDATED_CHANNEL_RESULTS
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByChannelResultsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where channelResults does not contain
        defaultNotificationLogFiltering(
            "channelResults.doesNotContain=" + UPDATED_CHANNEL_RESULTS,
            "channelResults.doesNotContain=" + DEFAULT_CHANNEL_RESULTS
        );
    }

    @Test
    @Transactional
    void getAllNotificationLogsByActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where action equals to
        defaultNotificationLogFiltering("action.equals=" + DEFAULT_ACTION, "action.equals=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where action in
        defaultNotificationLogFiltering("action.in=" + DEFAULT_ACTION + "," + UPDATED_ACTION, "action.in=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where action is not null
        defaultNotificationLogFiltering("action.specified=true", "action.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByActionContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where action contains
        defaultNotificationLogFiltering("action.contains=" + DEFAULT_ACTION, "action.contains=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByActionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where action does not contain
        defaultNotificationLogFiltering("action.doesNotContain=" + UPDATED_ACTION, "action.doesNotContain=" + DEFAULT_ACTION);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByDetailsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where details equals to
        defaultNotificationLogFiltering("details.equals=" + DEFAULT_DETAILS, "details.equals=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByDetailsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where details in
        defaultNotificationLogFiltering("details.in=" + DEFAULT_DETAILS + "," + UPDATED_DETAILS, "details.in=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByDetailsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where details is not null
        defaultNotificationLogFiltering("details.specified=true", "details.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationLogsByDetailsContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where details contains
        defaultNotificationLogFiltering("details.contains=" + DEFAULT_DETAILS, "details.contains=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByDetailsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        // Get all the notificationLogList where details does not contain
        defaultNotificationLogFiltering("details.doesNotContain=" + UPDATED_DETAILS, "details.doesNotContain=" + DEFAULT_DETAILS);
    }

    @Test
    @Transactional
    void getAllNotificationLogsByNotificationIsEqualToSomething() throws Exception {
        Notification notification;
        if (TestUtil.findAll(em, Notification.class).isEmpty()) {
            notificationLogRepository.saveAndFlush(notificationLog);
            notification = NotificationResourceIT.createEntity();
        } else {
            notification = TestUtil.findAll(em, Notification.class).get(0);
        }
        em.persist(notification);
        em.flush();
        notificationLog.setNotification(notification);
        notificationLogRepository.saveAndFlush(notificationLog);
        Long notificationId = notification.getId();
        // Get all the notificationLogList where notification equals to notificationId
        defaultNotificationLogShouldBeFound("notificationId.equals=" + notificationId);

        // Get all the notificationLogList where notification equals to (notificationId + 1)
        defaultNotificationLogShouldNotBeFound("notificationId.equals=" + (notificationId + 1));
    }

    private void defaultNotificationLogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultNotificationLogShouldBeFound(shouldBeFound);
        defaultNotificationLogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationLogShouldBeFound(String filter) throws Exception {
        restNotificationLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].notificationId").value(hasItem(DEFAULT_NOTIFICATION_ID.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].channel").value(hasItem(DEFAULT_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].recipient").value(hasItem(DEFAULT_RECIPIENT)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].sentAt").value(hasItem(DEFAULT_SENT_AT.toString())))
            .andExpect(jsonPath("$.[*].retryCount").value(hasItem(DEFAULT_RETRY_COUNT)))
            .andExpect(jsonPath("$.[*].channelResults").value(hasItem(DEFAULT_CHANNEL_RESULTS)))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)));

        // Check, that the count call also returns 1
        restNotificationLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationLogShouldNotBeFound(String filter) throws Exception {
        restNotificationLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotificationLog() throws Exception {
        // Get the notificationLog
        restNotificationLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotificationLog() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationLog
        NotificationLog updatedNotificationLog = notificationLogRepository.findById(notificationLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotificationLog are not directly saved in db
        em.detach(updatedNotificationLog);
        updatedNotificationLog
            .notificationId(UPDATED_NOTIFICATION_ID)
            .userId(UPDATED_USER_ID)
            .type(UPDATED_TYPE)
            .channel(UPDATED_CHANNEL)
            .status(UPDATED_STATUS)
            .message(UPDATED_MESSAGE)
            .recipient(UPDATED_RECIPIENT)
            .timestamp(UPDATED_TIMESTAMP)
            .sentAt(UPDATED_SENT_AT)
            .retryCount(UPDATED_RETRY_COUNT)
            .channelResults(UPDATED_CHANNEL_RESULTS)
            .action(UPDATED_ACTION)
            .details(UPDATED_DETAILS);
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
    }

    @Test
    @Transactional
    void putNonExistingNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

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
    }

    @Test
    @Transactional
    void putWithIdMismatchNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

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
    }

    @Test
    @Transactional
    void partialUpdateNotificationLogWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationLog using partial update
        NotificationLog partialUpdatedNotificationLog = new NotificationLog();
        partialUpdatedNotificationLog.setId(notificationLog.getId());

        partialUpdatedNotificationLog
            .notificationId(UPDATED_NOTIFICATION_ID)
            .type(UPDATED_TYPE)
            .channel(UPDATED_CHANNEL)
            .status(UPDATED_STATUS)
            .recipient(UPDATED_RECIPIENT)
            .timestamp(UPDATED_TIMESTAMP)
            .sentAt(UPDATED_SENT_AT)
            .channelResults(UPDATED_CHANNEL_RESULTS)
            .details(UPDATED_DETAILS);

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
    @Transactional
    void fullUpdateNotificationLogWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationLog using partial update
        NotificationLog partialUpdatedNotificationLog = new NotificationLog();
        partialUpdatedNotificationLog.setId(notificationLog.getId());

        partialUpdatedNotificationLog
            .notificationId(UPDATED_NOTIFICATION_ID)
            .userId(UPDATED_USER_ID)
            .type(UPDATED_TYPE)
            .channel(UPDATED_CHANNEL)
            .status(UPDATED_STATUS)
            .message(UPDATED_MESSAGE)
            .recipient(UPDATED_RECIPIENT)
            .timestamp(UPDATED_TIMESTAMP)
            .sentAt(UPDATED_SENT_AT)
            .retryCount(UPDATED_RETRY_COUNT)
            .channelResults(UPDATED_CHANNEL_RESULTS)
            .action(UPDATED_ACTION)
            .details(UPDATED_DETAILS);

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
    @Transactional
    void patchNonExistingNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

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
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

        // Create the NotificationLog
        NotificationLogDTO notificationLogDTO = notificationLogMapper.toDto(notificationLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotificationLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationLog.setId(longCount.incrementAndGet());

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
    }

    @Test
    @Transactional
    void deleteNotificationLog() throws Exception {
        // Initialize the database
        insertedNotificationLog = notificationLogRepository.saveAndFlush(notificationLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notificationLog
        restNotificationLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationLog.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
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
