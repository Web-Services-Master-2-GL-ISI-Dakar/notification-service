package sn.ondmoney.notificationservice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.ondmoney.notificationservice.domain.TransactionEventAsserts.*;
import static sn.ondmoney.notificationservice.web.rest.TestUtil.createUpdateProxyForBean;
import static sn.ondmoney.notificationservice.web.rest.TestUtil.sameNumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
import sn.ondmoney.notificationservice.domain.TransactionEvent;
import sn.ondmoney.notificationservice.domain.enumeration.TransactionType;
import sn.ondmoney.notificationservice.repository.TransactionEventRepository;
import sn.ondmoney.notificationservice.service.dto.TransactionEventDTO;
import sn.ondmoney.notificationservice.service.mapper.TransactionEventMapper;

/**
 * Integration tests for the {@link TransactionEventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionEventResourceIT {

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER_ACCOUNT = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_ACCOUNT = "BBBBBBBBBB";

    private static final String DEFAULT_RECEIVER_ACCOUNT = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER_ACCOUNT = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final TransactionType DEFAULT_TYPE = TransactionType.SEND;
    private static final TransactionType UPDATED_TYPE = TransactionType.RECEIVE;

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ADDITIONAL_DATA = "AAAAAAAAAA";
    private static final String UPDATED_ADDITIONAL_DATA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transaction-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TransactionEventRepository transactionEventRepository;

    @Autowired
    private TransactionEventMapper transactionEventMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionEventMockMvc;

    private TransactionEvent transactionEvent;

    private TransactionEvent insertedTransactionEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionEvent createEntity() {
        return new TransactionEvent()
            .transactionId(DEFAULT_TRANSACTION_ID)
            .senderAccount(DEFAULT_SENDER_ACCOUNT)
            .receiverAccount(DEFAULT_RECEIVER_ACCOUNT)
            .amount(DEFAULT_AMOUNT)
            .type(DEFAULT_TYPE)
            .timestamp(DEFAULT_TIMESTAMP)
            .additionalData(DEFAULT_ADDITIONAL_DATA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionEvent createUpdatedEntity() {
        return new TransactionEvent()
            .transactionId(UPDATED_TRANSACTION_ID)
            .senderAccount(UPDATED_SENDER_ACCOUNT)
            .receiverAccount(UPDATED_RECEIVER_ACCOUNT)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .timestamp(UPDATED_TIMESTAMP)
            .additionalData(UPDATED_ADDITIONAL_DATA);
    }

    @BeforeEach
    void initTest() {
        transactionEvent = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTransactionEvent != null) {
            transactionEventRepository.delete(insertedTransactionEvent);
            insertedTransactionEvent = null;
        }
    }

    @Test
    @Transactional
    void createTransactionEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TransactionEvent
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);
        var returnedTransactionEventDTO = om.readValue(
            restTransactionEventMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(transactionEventDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TransactionEventDTO.class
        );

        // Validate the TransactionEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTransactionEvent = transactionEventMapper.toEntity(returnedTransactionEventDTO);
        assertTransactionEventUpdatableFieldsEquals(returnedTransactionEvent, getPersistedTransactionEvent(returnedTransactionEvent));

        insertedTransactionEvent = returnedTransactionEvent;
    }

    @Test
    @Transactional
    void createTransactionEventWithExistingId() throws Exception {
        // Create the TransactionEvent with an existing ID
        transactionEvent.setId(1L);
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionEventMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTransactionIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transactionEvent.setTransactionId(null);

        // Create the TransactionEvent, which fails.
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        restTransactionEventMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSenderAccountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transactionEvent.setSenderAccount(null);

        // Create the TransactionEvent, which fails.
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        restTransactionEventMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReceiverAccountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transactionEvent.setReceiverAccount(null);

        // Create the TransactionEvent, which fails.
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        restTransactionEventMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transactionEvent.setAmount(null);

        // Create the TransactionEvent, which fails.
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        restTransactionEventMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transactionEvent.setType(null);

        // Create the TransactionEvent, which fails.
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        restTransactionEventMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transactionEvent.setTimestamp(null);

        // Create the TransactionEvent, which fails.
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        restTransactionEventMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactionEvents() throws Exception {
        // Initialize the database
        insertedTransactionEvent = transactionEventRepository.saveAndFlush(transactionEvent);

        // Get all the transactionEventList
        restTransactionEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].senderAccount").value(hasItem(DEFAULT_SENDER_ACCOUNT)))
            .andExpect(jsonPath("$.[*].receiverAccount").value(hasItem(DEFAULT_RECEIVER_ACCOUNT)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].additionalData").value(hasItem(DEFAULT_ADDITIONAL_DATA)));
    }

    @Test
    @Transactional
    void getTransactionEvent() throws Exception {
        // Initialize the database
        insertedTransactionEvent = transactionEventRepository.saveAndFlush(transactionEvent);

        // Get the transactionEvent
        restTransactionEventMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionEvent.getId().intValue()))
            .andExpect(jsonPath("$.transactionId").value(DEFAULT_TRANSACTION_ID))
            .andExpect(jsonPath("$.senderAccount").value(DEFAULT_SENDER_ACCOUNT))
            .andExpect(jsonPath("$.receiverAccount").value(DEFAULT_RECEIVER_ACCOUNT))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.additionalData").value(DEFAULT_ADDITIONAL_DATA));
    }

    @Test
    @Transactional
    void getNonExistingTransactionEvent() throws Exception {
        // Get the transactionEvent
        restTransactionEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionEvent() throws Exception {
        // Initialize the database
        insertedTransactionEvent = transactionEventRepository.saveAndFlush(transactionEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transactionEvent
        TransactionEvent updatedTransactionEvent = transactionEventRepository.findById(transactionEvent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransactionEvent are not directly saved in db
        em.detach(updatedTransactionEvent);
        updatedTransactionEvent
            .transactionId(UPDATED_TRANSACTION_ID)
            .senderAccount(UPDATED_SENDER_ACCOUNT)
            .receiverAccount(UPDATED_RECEIVER_ACCOUNT)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .timestamp(UPDATED_TIMESTAMP)
            .additionalData(UPDATED_ADDITIONAL_DATA);
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(updatedTransactionEvent);

        restTransactionEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionEventDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTransactionEventToMatchAllProperties(updatedTransactionEvent);
    }

    @Test
    @Transactional
    void putNonExistingTransactionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionEvent.setId(longCount.incrementAndGet());

        // Create the TransactionEvent
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionEventDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionEvent.setId(longCount.incrementAndGet());

        // Create the TransactionEvent
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionEvent.setId(longCount.incrementAndGet());

        // Create the TransactionEvent
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionEventMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionEventWithPatch() throws Exception {
        // Initialize the database
        insertedTransactionEvent = transactionEventRepository.saveAndFlush(transactionEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transactionEvent using partial update
        TransactionEvent partialUpdatedTransactionEvent = new TransactionEvent();
        partialUpdatedTransactionEvent.setId(transactionEvent.getId());

        partialUpdatedTransactionEvent.senderAccount(UPDATED_SENDER_ACCOUNT).type(UPDATED_TYPE).additionalData(UPDATED_ADDITIONAL_DATA);

        restTransactionEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionEvent.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransactionEvent))
            )
            .andExpect(status().isOk());

        // Validate the TransactionEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTransactionEvent, transactionEvent),
            getPersistedTransactionEvent(transactionEvent)
        );
    }

    @Test
    @Transactional
    void fullUpdateTransactionEventWithPatch() throws Exception {
        // Initialize the database
        insertedTransactionEvent = transactionEventRepository.saveAndFlush(transactionEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transactionEvent using partial update
        TransactionEvent partialUpdatedTransactionEvent = new TransactionEvent();
        partialUpdatedTransactionEvent.setId(transactionEvent.getId());

        partialUpdatedTransactionEvent
            .transactionId(UPDATED_TRANSACTION_ID)
            .senderAccount(UPDATED_SENDER_ACCOUNT)
            .receiverAccount(UPDATED_RECEIVER_ACCOUNT)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .timestamp(UPDATED_TIMESTAMP)
            .additionalData(UPDATED_ADDITIONAL_DATA);

        restTransactionEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionEvent.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransactionEvent))
            )
            .andExpect(status().isOk());

        // Validate the TransactionEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionEventUpdatableFieldsEquals(
            partialUpdatedTransactionEvent,
            getPersistedTransactionEvent(partialUpdatedTransactionEvent)
        );
    }

    @Test
    @Transactional
    void patchNonExistingTransactionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionEvent.setId(longCount.incrementAndGet());

        // Create the TransactionEvent
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionEventDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionEvent.setId(longCount.incrementAndGet());

        // Create the TransactionEvent
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionEvent.setId(longCount.incrementAndGet());

        // Create the TransactionEvent
        TransactionEventDTO transactionEventDTO = transactionEventMapper.toDto(transactionEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionEventMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionEventDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransactionEvent() throws Exception {
        // Initialize the database
        insertedTransactionEvent = transactionEventRepository.saveAndFlush(transactionEvent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the transactionEvent
        restTransactionEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionEvent.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return transactionEventRepository.count();
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

    protected TransactionEvent getPersistedTransactionEvent(TransactionEvent transactionEvent) {
        return transactionEventRepository.findById(transactionEvent.getId()).orElseThrow();
    }

    protected void assertPersistedTransactionEventToMatchAllProperties(TransactionEvent expectedTransactionEvent) {
        assertTransactionEventAllPropertiesEquals(expectedTransactionEvent, getPersistedTransactionEvent(expectedTransactionEvent));
    }

    protected void assertPersistedTransactionEventToMatchUpdatableProperties(TransactionEvent expectedTransactionEvent) {
        assertTransactionEventAllUpdatablePropertiesEquals(
            expectedTransactionEvent,
            getPersistedTransactionEvent(expectedTransactionEvent)
        );
    }
}
