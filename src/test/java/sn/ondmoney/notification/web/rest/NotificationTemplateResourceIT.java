package sn.ondmoney.notification.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.ondmoney.notification.domain.NotificationTemplateAsserts.*;
import static sn.ondmoney.notification.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import sn.ondmoney.notification.domain.NotificationTemplate;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationLanguage;
import sn.ondmoney.notification.domain.enumeration.NotificationTemplateType;
import sn.ondmoney.notification.domain.enumeration.NotificationType;
import sn.ondmoney.notification.repository.NotificationTemplateRepository;
import sn.ondmoney.notification.repository.search.NotificationTemplateSearchRepository;
import sn.ondmoney.notification.service.dto.NotificationTemplateDTO;
import sn.ondmoney.notification.service.mapper.NotificationTemplateMapper;

/**
 * Integration tests for the {@link NotificationTemplateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationTemplateResourceIT {

    private static final String DEFAULT_TEMPLATE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TEMPLATE_CODE = "BBBBBBBBBB";

    private static final NotificationChannel DEFAULT_NOTIFICATION_CHANNEL = NotificationChannel.SMS;
    private static final NotificationChannel UPDATED_NOTIFICATION_CHANNEL = NotificationChannel.EMAIL;

    private static final NotificationLanguage DEFAULT_NOTIFICATION_LANGUAGE = NotificationLanguage.FR;
    private static final NotificationLanguage UPDATED_NOTIFICATION_LANGUAGE = NotificationLanguage.EN;

    private static final NotificationType DEFAULT_NOTIFICATION_TYPE = NotificationType.OTP_REQUEST;
    private static final NotificationType UPDATED_NOTIFICATION_TYPE = NotificationType.EMAIL_VERIFICATION_REQUEST;

    private static final NotificationTemplateType DEFAULT_NOTIFICATION_TEMPLATE_TYPE = NotificationTemplateType.HTML;
    private static final NotificationTemplateType UPDATED_NOTIFICATION_TEMPLATE_TYPE = NotificationTemplateType.TEXT;

    private static final String DEFAULT_SUBJECT_TEMPLATE = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_TEMPLATE = "BBBBBBBBBB";

    private static final String DEFAULT_BODY_TEMPLATE = "AAAAAAAAAA";
    private static final String UPDATED_BODY_TEMPLATE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/notification-templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/notification-templates/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    @Autowired
    private NotificationTemplateMapper notificationTemplateMapper;

    @Autowired
    private NotificationTemplateSearchRepository notificationTemplateSearchRepository;

    @Autowired
    private MockMvc restNotificationTemplateMockMvc;

    private NotificationTemplate notificationTemplate;

    private NotificationTemplate insertedNotificationTemplate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationTemplate createEntity() {
        return new NotificationTemplate()
            .templateCode(DEFAULT_TEMPLATE_CODE)
            .notificationChannel(DEFAULT_NOTIFICATION_CHANNEL)
            .notificationLanguage(DEFAULT_NOTIFICATION_LANGUAGE)
            .notificationType(DEFAULT_NOTIFICATION_TYPE)
            .notificationTemplateType(DEFAULT_NOTIFICATION_TEMPLATE_TYPE)
            .subjectTemplate(DEFAULT_SUBJECT_TEMPLATE)
            .bodyTemplate(DEFAULT_BODY_TEMPLATE)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationTemplate createUpdatedEntity() {
        return new NotificationTemplate()
            .templateCode(UPDATED_TEMPLATE_CODE)
            .notificationChannel(UPDATED_NOTIFICATION_CHANNEL)
            .notificationLanguage(UPDATED_NOTIFICATION_LANGUAGE)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationTemplateType(UPDATED_NOTIFICATION_TEMPLATE_TYPE)
            .subjectTemplate(UPDATED_SUBJECT_TEMPLATE)
            .bodyTemplate(UPDATED_BODY_TEMPLATE)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        notificationTemplate = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotificationTemplate != null) {
            notificationTemplateRepository.delete(insertedNotificationTemplate);
            notificationTemplateSearchRepository.delete(insertedNotificationTemplate);
            insertedNotificationTemplate = null;
        }
    }

    @Test
    void createNotificationTemplate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);
        var returnedNotificationTemplateDTO = om.readValue(
            restNotificationTemplateMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(notificationTemplateDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificationTemplateDTO.class
        );

        // Validate the NotificationTemplate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotificationTemplate = notificationTemplateMapper.toEntity(returnedNotificationTemplateDTO);
        assertNotificationTemplateUpdatableFieldsEquals(
            returnedNotificationTemplate,
            getPersistedNotificationTemplate(returnedNotificationTemplate)
        );

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedNotificationTemplate = returnedNotificationTemplate;
    }

    @Test
    void createNotificationTemplateWithExistingId() throws Exception {
        // Create the NotificationTemplate with an existing ID
        notificationTemplate.setId("existing_id");
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTemplateCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        // set the field null
        notificationTemplate.setTemplateCode(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNotificationChannelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        // set the field null
        notificationTemplate.setNotificationChannel(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNotificationLanguageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        // set the field null
        notificationTemplate.setNotificationLanguage(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNotificationTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        // set the field null
        notificationTemplate.setNotificationType(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNotificationTemplateTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        // set the field null
        notificationTemplate.setNotificationTemplateType(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        // set the field null
        notificationTemplate.setActive(null);

        // Create the NotificationTemplate, which fails.
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllNotificationTemplates() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);

        // Get all the notificationTemplateList
        restNotificationTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationTemplate.getId())))
            .andExpect(jsonPath("$.[*].templateCode").value(hasItem(DEFAULT_TEMPLATE_CODE)))
            .andExpect(jsonPath("$.[*].notificationChannel").value(hasItem(DEFAULT_NOTIFICATION_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].notificationLanguage").value(hasItem(DEFAULT_NOTIFICATION_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].notificationType").value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].notificationTemplateType").value(hasItem(DEFAULT_NOTIFICATION_TEMPLATE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].subjectTemplate").value(hasItem(DEFAULT_SUBJECT_TEMPLATE)))
            .andExpect(jsonPath("$.[*].bodyTemplate").value(hasItem(DEFAULT_BODY_TEMPLATE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    void getNotificationTemplate() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);

        // Get the notificationTemplate
        restNotificationTemplateMockMvc
            .perform(get(ENTITY_API_URL_ID, notificationTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationTemplate.getId()))
            .andExpect(jsonPath("$.templateCode").value(DEFAULT_TEMPLATE_CODE))
            .andExpect(jsonPath("$.notificationChannel").value(DEFAULT_NOTIFICATION_CHANNEL.toString()))
            .andExpect(jsonPath("$.notificationLanguage").value(DEFAULT_NOTIFICATION_LANGUAGE.toString()))
            .andExpect(jsonPath("$.notificationType").value(DEFAULT_NOTIFICATION_TYPE.toString()))
            .andExpect(jsonPath("$.notificationTemplateType").value(DEFAULT_NOTIFICATION_TEMPLATE_TYPE.toString()))
            .andExpect(jsonPath("$.subjectTemplate").value(DEFAULT_SUBJECT_TEMPLATE))
            .andExpect(jsonPath("$.bodyTemplate").value(DEFAULT_BODY_TEMPLATE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    void getNonExistingNotificationTemplate() throws Exception {
        // Get the notificationTemplate
        restNotificationTemplateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingNotificationTemplate() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationTemplateSearchRepository.save(notificationTemplate);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());

        // Update the notificationTemplate
        NotificationTemplate updatedNotificationTemplate = notificationTemplateRepository
            .findById(notificationTemplate.getId())
            .orElseThrow();
        updatedNotificationTemplate
            .templateCode(UPDATED_TEMPLATE_CODE)
            .notificationChannel(UPDATED_NOTIFICATION_CHANNEL)
            .notificationLanguage(UPDATED_NOTIFICATION_LANGUAGE)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationTemplateType(UPDATED_NOTIFICATION_TEMPLATE_TYPE)
            .subjectTemplate(UPDATED_SUBJECT_TEMPLATE)
            .bodyTemplate(UPDATED_BODY_TEMPLATE)
            .active(UPDATED_ACTIVE);
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(updatedNotificationTemplate);

        restNotificationTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationTemplateDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isOk());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationTemplateToMatchAllProperties(updatedNotificationTemplate);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<NotificationTemplate> notificationTemplateSearchList = Streamable.of(
                    notificationTemplateSearchRepository.findAll()
                ).toList();
                NotificationTemplate testNotificationTemplateSearch = notificationTemplateSearchList.get(searchDatabaseSizeAfter - 1);

                assertNotificationTemplateAllPropertiesEquals(testNotificationTemplateSearch, updatedNotificationTemplate);
            });
    }

    @Test
    void putNonExistingNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        notificationTemplate.setId(UUID.randomUUID().toString());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationTemplateDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        notificationTemplate.setId(UUID.randomUUID().toString());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        notificationTemplate.setId(UUID.randomUUID().toString());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateNotificationTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationTemplate using partial update
        NotificationTemplate partialUpdatedNotificationTemplate = new NotificationTemplate();
        partialUpdatedNotificationTemplate.setId(notificationTemplate.getId());

        partialUpdatedNotificationTemplate
            .notificationLanguage(UPDATED_NOTIFICATION_LANGUAGE)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .subjectTemplate(UPDATED_SUBJECT_TEMPLATE);

        restNotificationTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationTemplate.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationTemplate))
            )
            .andExpect(status().isOk());

        // Validate the NotificationTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationTemplateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotificationTemplate, notificationTemplate),
            getPersistedNotificationTemplate(notificationTemplate)
        );
    }

    @Test
    void fullUpdateNotificationTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationTemplate using partial update
        NotificationTemplate partialUpdatedNotificationTemplate = new NotificationTemplate();
        partialUpdatedNotificationTemplate.setId(notificationTemplate.getId());

        partialUpdatedNotificationTemplate
            .templateCode(UPDATED_TEMPLATE_CODE)
            .notificationChannel(UPDATED_NOTIFICATION_CHANNEL)
            .notificationLanguage(UPDATED_NOTIFICATION_LANGUAGE)
            .notificationType(UPDATED_NOTIFICATION_TYPE)
            .notificationTemplateType(UPDATED_NOTIFICATION_TEMPLATE_TYPE)
            .subjectTemplate(UPDATED_SUBJECT_TEMPLATE)
            .bodyTemplate(UPDATED_BODY_TEMPLATE)
            .active(UPDATED_ACTIVE);

        restNotificationTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationTemplate.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationTemplate))
            )
            .andExpect(status().isOk());

        // Validate the NotificationTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationTemplateUpdatableFieldsEquals(
            partialUpdatedNotificationTemplate,
            getPersistedNotificationTemplate(partialUpdatedNotificationTemplate)
        );
    }

    @Test
    void patchNonExistingNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        notificationTemplate.setId(UUID.randomUUID().toString());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationTemplateDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        notificationTemplate.setId(UUID.randomUUID().toString());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamNotificationTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        notificationTemplate.setId(UUID.randomUUID().toString());

        // Create the NotificationTemplate
        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toDto(notificationTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationTemplateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteNotificationTemplate() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);
        notificationTemplateRepository.save(notificationTemplate);
        notificationTemplateSearchRepository.save(notificationTemplate);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the notificationTemplate
        restNotificationTemplateMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationTemplate.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchNotificationTemplate() throws Exception {
        // Initialize the database
        insertedNotificationTemplate = notificationTemplateRepository.save(notificationTemplate);
        notificationTemplateSearchRepository.save(notificationTemplate);

        // Search the notificationTemplate
        restNotificationTemplateMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + notificationTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationTemplate.getId())))
            .andExpect(jsonPath("$.[*].templateCode").value(hasItem(DEFAULT_TEMPLATE_CODE)))
            .andExpect(jsonPath("$.[*].notificationChannel").value(hasItem(DEFAULT_NOTIFICATION_CHANNEL.toString())))
            .andExpect(jsonPath("$.[*].notificationLanguage").value(hasItem(DEFAULT_NOTIFICATION_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].notificationType").value(hasItem(DEFAULT_NOTIFICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].notificationTemplateType").value(hasItem(DEFAULT_NOTIFICATION_TEMPLATE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].subjectTemplate").value(hasItem(DEFAULT_SUBJECT_TEMPLATE)))
            .andExpect(jsonPath("$.[*].bodyTemplate").value(hasItem(DEFAULT_BODY_TEMPLATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    protected long getRepositoryCount() {
        return notificationTemplateRepository.count();
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

    protected NotificationTemplate getPersistedNotificationTemplate(NotificationTemplate notificationTemplate) {
        return notificationTemplateRepository.findById(notificationTemplate.getId()).orElseThrow();
    }

    protected void assertPersistedNotificationTemplateToMatchAllProperties(NotificationTemplate expectedNotificationTemplate) {
        assertNotificationTemplateAllPropertiesEquals(
            expectedNotificationTemplate,
            getPersistedNotificationTemplate(expectedNotificationTemplate)
        );
    }

    protected void assertPersistedNotificationTemplateToMatchUpdatableProperties(NotificationTemplate expectedNotificationTemplate) {
        assertNotificationTemplateAllUpdatablePropertiesEquals(
            expectedNotificationTemplate,
            getPersistedNotificationTemplate(expectedNotificationTemplate)
        );
    }
}
