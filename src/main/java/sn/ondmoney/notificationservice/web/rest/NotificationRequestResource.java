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
import sn.ondmoney.notificationservice.repository.NotificationRequestRepository;
import sn.ondmoney.notificationservice.service.NotificationRequestService;
import sn.ondmoney.notificationservice.service.dto.NotificationRequestDTO;
import sn.ondmoney.notificationservice.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.ondmoney.notificationservice.domain.NotificationRequest}.
 */
@RestController
@RequestMapping("/api/notification-requests")
public class NotificationRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationRequestResource.class);

    private static final String ENTITY_NAME = "ondmoneyNotificationServiceNotificationRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationRequestService notificationRequestService;

    private final NotificationRequestRepository notificationRequestRepository;

    public NotificationRequestResource(
        NotificationRequestService notificationRequestService,
        NotificationRequestRepository notificationRequestRepository
    ) {
        this.notificationRequestService = notificationRequestService;
        this.notificationRequestRepository = notificationRequestRepository;
    }

    /**
     * {@code POST  /notification-requests} : Create a new notificationRequest.
     *
     * @param notificationRequestDTO the notificationRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationRequestDTO, or with status {@code 400 (Bad Request)} if the notificationRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NotificationRequestDTO> createNotificationRequest(
        @Valid @RequestBody NotificationRequestDTO notificationRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save NotificationRequest : {}", notificationRequestDTO);
        if (notificationRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new notificationRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notificationRequestDTO = notificationRequestService.save(notificationRequestDTO);
        return ResponseEntity.created(new URI("/api/notification-requests/" + notificationRequestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, notificationRequestDTO.getId().toString()))
            .body(notificationRequestDTO);
    }

    /**
     * {@code PUT  /notification-requests/:id} : Updates an existing notificationRequest.
     *
     * @param id the id of the notificationRequestDTO to save.
     * @param notificationRequestDTO the notificationRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationRequestDTO,
     * or with status {@code 400 (Bad Request)} if the notificationRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotificationRequestDTO> updateNotificationRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotificationRequestDTO notificationRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update NotificationRequest : {}, {}", id, notificationRequestDTO);
        if (notificationRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        notificationRequestDTO = notificationRequestService.update(notificationRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationRequestDTO.getId().toString()))
            .body(notificationRequestDTO);
    }

    /**
     * {@code PATCH  /notification-requests/:id} : Partial updates given fields of an existing notificationRequest, field will ignore if it is null
     *
     * @param id the id of the notificationRequestDTO to save.
     * @param notificationRequestDTO the notificationRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationRequestDTO,
     * or with status {@code 400 (Bad Request)} if the notificationRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotificationRequestDTO> partialUpdateNotificationRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotificationRequestDTO notificationRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update NotificationRequest partially : {}, {}", id, notificationRequestDTO);
        if (notificationRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotificationRequestDTO> result = notificationRequestService.partialUpdate(notificationRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notification-requests} : get all the notificationRequests.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notificationRequests in body.
     */
    @GetMapping("")
    public ResponseEntity<List<NotificationRequestDTO>> getAllNotificationRequests(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of NotificationRequests");
        Page<NotificationRequestDTO> page = notificationRequestService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notification-requests/:id} : get the "id" notificationRequest.
     *
     * @param id the id of the notificationRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationRequestDTO> getNotificationRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get NotificationRequest : {}", id);
        Optional<NotificationRequestDTO> notificationRequestDTO = notificationRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationRequestDTO);
    }

    /**
     * {@code DELETE  /notification-requests/:id} : delete the "id" notificationRequest.
     *
     * @param id the id of the notificationRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificationRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete NotificationRequest : {}", id);
        notificationRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
