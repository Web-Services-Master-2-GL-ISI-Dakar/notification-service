package sn.ondmoney.notificationservice.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.ondmoney.notificationservice.repository.TransactionEventRepository;
import sn.ondmoney.notificationservice.service.TransactionEventService;
import sn.ondmoney.notificationservice.service.dto.TransactionEventDTO;
import sn.ondmoney.notificationservice.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.ondmoney.notificationservice.domain.TransactionEvent}.
 */
@RestController
@RequestMapping("/api/transaction-events")
public class TransactionEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionEventResource.class);

    private static final String ENTITY_NAME = "ondmoneyNotificationServiceTransactionEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionEventService transactionEventService;

    private final TransactionEventRepository transactionEventRepository;

    public TransactionEventResource(
        TransactionEventService transactionEventService,
        TransactionEventRepository transactionEventRepository
    ) {
        this.transactionEventService = transactionEventService;
        this.transactionEventRepository = transactionEventRepository;
    }

    /**
     * {@code POST  /transaction-events} : Create a new transactionEvent.
     *
     * @param transactionEventDTO the transactionEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionEventDTO, or with status {@code 400 (Bad Request)} if the transactionEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TransactionEventDTO> createTransactionEvent(@Valid @RequestBody TransactionEventDTO transactionEventDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TransactionEvent : {}", transactionEventDTO);
        if (transactionEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new transactionEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        transactionEventDTO = transactionEventService.save(transactionEventDTO);
        return ResponseEntity.created(new URI("/api/transaction-events/" + transactionEventDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, transactionEventDTO.getId().toString()))
            .body(transactionEventDTO);
    }

    /**
     * {@code PUT  /transaction-events/:id} : Updates an existing transactionEvent.
     *
     * @param id the id of the transactionEventDTO to save.
     * @param transactionEventDTO the transactionEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionEventDTO,
     * or with status {@code 400 (Bad Request)} if the transactionEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionEventDTO> updateTransactionEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransactionEventDTO transactionEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TransactionEvent : {}, {}", id, transactionEventDTO);
        if (transactionEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        transactionEventDTO = transactionEventService.update(transactionEventDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionEventDTO.getId().toString()))
            .body(transactionEventDTO);
    }

    /**
     * {@code PATCH  /transaction-events/:id} : Partial updates given fields of an existing transactionEvent, field will ignore if it is null
     *
     * @param id the id of the transactionEventDTO to save.
     * @param transactionEventDTO the transactionEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionEventDTO,
     * or with status {@code 400 (Bad Request)} if the transactionEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transactionEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionEventDTO> partialUpdateTransactionEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransactionEventDTO transactionEventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TransactionEvent partially : {}, {}", id, transactionEventDTO);
        if (transactionEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionEventDTO> result = transactionEventService.partialUpdate(transactionEventDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionEventDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transaction-events} : get all the transactionEvents.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionEvents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TransactionEventDTO>> getAllTransactionEvents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of TransactionEvents");
        Page<TransactionEventDTO> page = transactionEventService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-events/:id} : get the "id" transactionEvent.
     *
     * @param id the id of the transactionEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionEventDTO> getTransactionEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TransactionEvent : {}", id);
        Optional<TransactionEventDTO> transactionEventDTO = transactionEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionEventDTO);
    }

    /**
     * {@code DELETE  /transaction-events/:id} : delete the "id" transactionEvent.
     *
     * @param id the id of the transactionEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TransactionEvent : {}", id);
        transactionEventService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
