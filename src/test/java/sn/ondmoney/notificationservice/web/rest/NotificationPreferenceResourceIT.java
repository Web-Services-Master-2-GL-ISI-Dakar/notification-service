package sn.ondmoney.notificationservice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.ondmoney.notificationservice.domain.NotificationPreferenceAsserts.*;
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
import sn.ondmoney.notificationservice.domain.NotificationPreference;
import sn.ondmoney.notificationservice.repository.NotificationPreferenceRepository;
import sn.ondmoney.notificationservice.service.dto.NotificationPreferenceDTO;
import sn.ondmoney.notificationservice.service.mapper.NotificationPreferenceMapper;

/**
 * Integration tests for the {@link NotificationPreferenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationPreferenceResourceIT {

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SMS_ENABLED = false;
    private static final Boolean UPDATED_SMS_ENABLED = true;

    private static final Boolean DEFAULT_EMAIL_ENABLED = false;
    private static final Boolean UPDATED_EMAIL_ENABLED = true;

    private static final Boolean DEFAULT_PUSH_ENABLED = false;
    private static final Boolean UPDATED_PUSH_ENABLED = true;

    private static final String DEFAULT_MUTED_TYPES = "AAAAAAAAAA";
    private static final String UPDATED_MUTED_TYPES = "BBBBBBBBBB";

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/notification-preferences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    private NotificationPreferenceMapper notificationPreferenceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationPreferenceMockMvc;

    private NotificationPreference notificationPreference;

    private NotificationPreference insertedNotificationPreference;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationPreference createEntity() {
        return new NotificationPreference()
            .userId(DEFAULT_USER_ID)
            .smsEnabled(DEFAULT_SMS_ENABLED)
            .emailEnabled(DEFAULT_EMAIL_ENABLED)
            .pushEnabled(DEFAULT_PUSH_ENABLED)
            .mutedTypes(DEFAULT_MUTED_TYPES)
            .language(DEFAULT_LANGUAGE)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationPreference createUpdatedEntity() {
        return new NotificationPreference()
            .userId(UPDATED_USER_ID)
            .smsEnabled(UPDATED_SMS_ENABLED)
            .emailEnabled(UPDATED_EMAIL_ENABLED)
            .pushEnabled(UPDATED_PUSH_ENABLED)
            .mutedTypes(UPDATED_MUTED_TYPES)
            .language(UPDATED_LANGUAGE)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        notificationPreference = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotificationPreference != null) {
            notificationPreferenceRepository.delete(insertedNotificationPreference);
            insertedNotificationPreference = null;
        }
    }

    @Test
    @Transactional
    void createNotificationPreference() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the NotificationPreference
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);
        var returnedNotificationPreferenceDTO = om.readValue(
            restNotificationPreferenceMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(notificationPreferenceDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificationPreferenceDTO.class
        );

        // Validate the NotificationPreference in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotificationPreference = notificationPreferenceMapper.toEntity(returnedNotificationPreferenceDTO);
        assertNotificationPreferenceUpdatableFieldsEquals(
            returnedNotificationPreference,
            getPersistedNotificationPreference(returnedNotificationPreference)
        );

        insertedNotificationPreference = returnedNotificationPreference;
    }

    @Test
    @Transactional
    void createNotificationPreferenceWithExistingId() throws Exception {
        // Create the NotificationPreference with an existing ID
        notificationPreference.setId(1L);
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationPreferenceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationPreference.setUserId(null);

        // Create the NotificationPreference, which fails.
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        restNotificationPreferenceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSmsEnabledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationPreference.setSmsEnabled(null);

        // Create the NotificationPreference, which fails.
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        restNotificationPreferenceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailEnabledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationPreference.setEmailEnabled(null);

        // Create the NotificationPreference, which fails.
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        restNotificationPreferenceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPushEnabledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationPreference.setPushEnabled(null);

        // Create the NotificationPreference, which fails.
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        restNotificationPreferenceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLanguageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationPreference.setLanguage(null);

        // Create the NotificationPreference, which fails.
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        restNotificationPreferenceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotificationPreferences() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList
        restNotificationPreferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationPreference.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].smsEnabled").value(hasItem(DEFAULT_SMS_ENABLED)))
            .andExpect(jsonPath("$.[*].emailEnabled").value(hasItem(DEFAULT_EMAIL_ENABLED)))
            .andExpect(jsonPath("$.[*].pushEnabled").value(hasItem(DEFAULT_PUSH_ENABLED)))
            .andExpect(jsonPath("$.[*].mutedTypes").value(hasItem(DEFAULT_MUTED_TYPES)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getNotificationPreference() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get the notificationPreference
        restNotificationPreferenceMockMvc
            .perform(get(ENTITY_API_URL_ID, notificationPreference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationPreference.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.smsEnabled").value(DEFAULT_SMS_ENABLED))
            .andExpect(jsonPath("$.emailEnabled").value(DEFAULT_EMAIL_ENABLED))
            .andExpect(jsonPath("$.pushEnabled").value(DEFAULT_PUSH_ENABLED))
            .andExpect(jsonPath("$.mutedTypes").value(DEFAULT_MUTED_TYPES))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNotificationPreferencesByIdFiltering() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        Long id = notificationPreference.getId();

        defaultNotificationPreferenceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultNotificationPreferenceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultNotificationPreferenceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where userId equals to
        defaultNotificationPreferenceFiltering("userId.equals=" + DEFAULT_USER_ID, "userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where userId in
        defaultNotificationPreferenceFiltering("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID, "userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where userId is not null
        defaultNotificationPreferenceFiltering("userId.specified=true", "userId.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByUserIdContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where userId contains
        defaultNotificationPreferenceFiltering("userId.contains=" + DEFAULT_USER_ID, "userId.contains=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByUserIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where userId does not contain
        defaultNotificationPreferenceFiltering("userId.doesNotContain=" + UPDATED_USER_ID, "userId.doesNotContain=" + DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesBySmsEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where smsEnabled equals to
        defaultNotificationPreferenceFiltering("smsEnabled.equals=" + DEFAULT_SMS_ENABLED, "smsEnabled.equals=" + UPDATED_SMS_ENABLED);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesBySmsEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where smsEnabled in
        defaultNotificationPreferenceFiltering(
            "smsEnabled.in=" + DEFAULT_SMS_ENABLED + "," + UPDATED_SMS_ENABLED,
            "smsEnabled.in=" + UPDATED_SMS_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesBySmsEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where smsEnabled is not null
        defaultNotificationPreferenceFiltering("smsEnabled.specified=true", "smsEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByEmailEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where emailEnabled equals to
        defaultNotificationPreferenceFiltering(
            "emailEnabled.equals=" + DEFAULT_EMAIL_ENABLED,
            "emailEnabled.equals=" + UPDATED_EMAIL_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByEmailEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where emailEnabled in
        defaultNotificationPreferenceFiltering(
            "emailEnabled.in=" + DEFAULT_EMAIL_ENABLED + "," + UPDATED_EMAIL_ENABLED,
            "emailEnabled.in=" + UPDATED_EMAIL_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByEmailEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where emailEnabled is not null
        defaultNotificationPreferenceFiltering("emailEnabled.specified=true", "emailEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByPushEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where pushEnabled equals to
        defaultNotificationPreferenceFiltering("pushEnabled.equals=" + DEFAULT_PUSH_ENABLED, "pushEnabled.equals=" + UPDATED_PUSH_ENABLED);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByPushEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where pushEnabled in
        defaultNotificationPreferenceFiltering(
            "pushEnabled.in=" + DEFAULT_PUSH_ENABLED + "," + UPDATED_PUSH_ENABLED,
            "pushEnabled.in=" + UPDATED_PUSH_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByPushEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where pushEnabled is not null
        defaultNotificationPreferenceFiltering("pushEnabled.specified=true", "pushEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByMutedTypesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where mutedTypes equals to
        defaultNotificationPreferenceFiltering("mutedTypes.equals=" + DEFAULT_MUTED_TYPES, "mutedTypes.equals=" + UPDATED_MUTED_TYPES);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByMutedTypesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where mutedTypes in
        defaultNotificationPreferenceFiltering(
            "mutedTypes.in=" + DEFAULT_MUTED_TYPES + "," + UPDATED_MUTED_TYPES,
            "mutedTypes.in=" + UPDATED_MUTED_TYPES
        );
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByMutedTypesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where mutedTypes is not null
        defaultNotificationPreferenceFiltering("mutedTypes.specified=true", "mutedTypes.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByMutedTypesContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where mutedTypes contains
        defaultNotificationPreferenceFiltering("mutedTypes.contains=" + DEFAULT_MUTED_TYPES, "mutedTypes.contains=" + UPDATED_MUTED_TYPES);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByMutedTypesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where mutedTypes does not contain
        defaultNotificationPreferenceFiltering(
            "mutedTypes.doesNotContain=" + UPDATED_MUTED_TYPES,
            "mutedTypes.doesNotContain=" + DEFAULT_MUTED_TYPES
        );
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByLanguageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where language equals to
        defaultNotificationPreferenceFiltering("language.equals=" + DEFAULT_LANGUAGE, "language.equals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByLanguageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where language in
        defaultNotificationPreferenceFiltering(
            "language.in=" + DEFAULT_LANGUAGE + "," + UPDATED_LANGUAGE,
            "language.in=" + UPDATED_LANGUAGE
        );
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByLanguageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where language is not null
        defaultNotificationPreferenceFiltering("language.specified=true", "language.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByLanguageContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where language contains
        defaultNotificationPreferenceFiltering("language.contains=" + DEFAULT_LANGUAGE, "language.contains=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByLanguageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where language does not contain
        defaultNotificationPreferenceFiltering(
            "language.doesNotContain=" + UPDATED_LANGUAGE,
            "language.doesNotContain=" + DEFAULT_LANGUAGE
        );
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where updatedAt equals to
        defaultNotificationPreferenceFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where updatedAt in
        defaultNotificationPreferenceFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllNotificationPreferencesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        // Get all the notificationPreferenceList where updatedAt is not null
        defaultNotificationPreferenceFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultNotificationPreferenceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultNotificationPreferenceShouldBeFound(shouldBeFound);
        defaultNotificationPreferenceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationPreferenceShouldBeFound(String filter) throws Exception {
        restNotificationPreferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationPreference.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].smsEnabled").value(hasItem(DEFAULT_SMS_ENABLED)))
            .andExpect(jsonPath("$.[*].emailEnabled").value(hasItem(DEFAULT_EMAIL_ENABLED)))
            .andExpect(jsonPath("$.[*].pushEnabled").value(hasItem(DEFAULT_PUSH_ENABLED)))
            .andExpect(jsonPath("$.[*].mutedTypes").value(hasItem(DEFAULT_MUTED_TYPES)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restNotificationPreferenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationPreferenceShouldNotBeFound(String filter) throws Exception {
        restNotificationPreferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationPreferenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotificationPreference() throws Exception {
        // Get the notificationPreference
        restNotificationPreferenceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotificationPreference() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationPreference
        NotificationPreference updatedNotificationPreference = notificationPreferenceRepository
            .findById(notificationPreference.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedNotificationPreference are not directly saved in db
        em.detach(updatedNotificationPreference);
        updatedNotificationPreference
            .userId(UPDATED_USER_ID)
            .smsEnabled(UPDATED_SMS_ENABLED)
            .emailEnabled(UPDATED_EMAIL_ENABLED)
            .pushEnabled(UPDATED_PUSH_ENABLED)
            .mutedTypes(UPDATED_MUTED_TYPES)
            .language(UPDATED_LANGUAGE)
            .updatedAt(UPDATED_UPDATED_AT);
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(updatedNotificationPreference);

        restNotificationPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationPreferenceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isOk());

        // Validate the NotificationPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationPreferenceToMatchAllProperties(updatedNotificationPreference);
    }

    @Test
    @Transactional
    void putNonExistingNotificationPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationPreference.setId(longCount.incrementAndGet());

        // Create the NotificationPreference
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationPreferenceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotificationPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationPreference.setId(longCount.incrementAndGet());

        // Create the NotificationPreference
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotificationPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationPreference.setId(longCount.incrementAndGet());

        // Create the NotificationPreference
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationPreferenceMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotificationPreferenceWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationPreference using partial update
        NotificationPreference partialUpdatedNotificationPreference = new NotificationPreference();
        partialUpdatedNotificationPreference.setId(notificationPreference.getId());

        partialUpdatedNotificationPreference.smsEnabled(UPDATED_SMS_ENABLED).language(UPDATED_LANGUAGE).updatedAt(UPDATED_UPDATED_AT);

        restNotificationPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationPreference.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationPreference))
            )
            .andExpect(status().isOk());

        // Validate the NotificationPreference in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationPreferenceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotificationPreference, notificationPreference),
            getPersistedNotificationPreference(notificationPreference)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotificationPreferenceWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationPreference using partial update
        NotificationPreference partialUpdatedNotificationPreference = new NotificationPreference();
        partialUpdatedNotificationPreference.setId(notificationPreference.getId());

        partialUpdatedNotificationPreference
            .userId(UPDATED_USER_ID)
            .smsEnabled(UPDATED_SMS_ENABLED)
            .emailEnabled(UPDATED_EMAIL_ENABLED)
            .pushEnabled(UPDATED_PUSH_ENABLED)
            .mutedTypes(UPDATED_MUTED_TYPES)
            .language(UPDATED_LANGUAGE)
            .updatedAt(UPDATED_UPDATED_AT);

        restNotificationPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationPreference.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationPreference))
            )
            .andExpect(status().isOk());

        // Validate the NotificationPreference in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationPreferenceUpdatableFieldsEquals(
            partialUpdatedNotificationPreference,
            getPersistedNotificationPreference(partialUpdatedNotificationPreference)
        );
    }

    @Test
    @Transactional
    void patchNonExistingNotificationPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationPreference.setId(longCount.incrementAndGet());

        // Create the NotificationPreference
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationPreferenceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotificationPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationPreference.setId(longCount.incrementAndGet());

        // Create the NotificationPreference
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotificationPreference() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationPreference.setId(longCount.incrementAndGet());

        // Create the NotificationPreference
        NotificationPreferenceDTO notificationPreferenceDTO = notificationPreferenceMapper.toDto(notificationPreference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationPreferenceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationPreferenceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationPreference in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotificationPreference() throws Exception {
        // Initialize the database
        insertedNotificationPreference = notificationPreferenceRepository.saveAndFlush(notificationPreference);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notificationPreference
        restNotificationPreferenceMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationPreference.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notificationPreferenceRepository.count();
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

    protected NotificationPreference getPersistedNotificationPreference(NotificationPreference notificationPreference) {
        return notificationPreferenceRepository.findById(notificationPreference.getId()).orElseThrow();
    }

    protected void assertPersistedNotificationPreferenceToMatchAllProperties(NotificationPreference expectedNotificationPreference) {
        assertNotificationPreferenceAllPropertiesEquals(
            expectedNotificationPreference,
            getPersistedNotificationPreference(expectedNotificationPreference)
        );
    }

    protected void assertPersistedNotificationPreferenceToMatchUpdatableProperties(NotificationPreference expectedNotificationPreference) {
        assertNotificationPreferenceAllUpdatablePropertiesEquals(
            expectedNotificationPreference,
            getPersistedNotificationPreference(expectedNotificationPreference)
        );
    }
}
