package sn.ondmoney.notification.web.rest;

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
import sn.ondmoney.notification.repository.NotificationLogRepository;
import sn.ondmoney.notification.service.NotificationLogService;
import sn.ondmoney.notification.service.dto.NotificationLogDTO;
import sn.ondmoney.notification.web.rest.errors.BadRequestAlertException;
import sn.ondmoney.notification.web.rest.errors.ElasticsearchExceptionMapper;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.ondmoney.notification.domain.NotificationLog}.
 */
@RestController
@RequestMapping("/api/notification-logs")
public class NotificationLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationLogResource.class);

    private static final String ENTITY_NAME = "ondmoneyNotificationServiceNotificationLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationLogService notificationLogService;

    private final NotificationLogRepository notificationLogRepository;

    public NotificationLogResource(NotificationLogService notificationLogService, NotificationLogRepository notificationLogRepository) {
        this.notificationLogService = notificationLogService;
        this.notificationLogRepository = notificationLogRepository;
    }

    /**
     * {@code POST  /notification-logs} : Create a new notificationLog.
     *
     * @param notificationLogDTO the notificationLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationLogDTO, or with status {@code 400 (Bad Request)} if the notificationLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NotificationLogDTO> createNotificationLog(@Valid @RequestBody NotificationLogDTO notificationLogDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save NotificationLog : {}", notificationLogDTO);
        if (notificationLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new notificationLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notificationLogDTO = notificationLogService.save(notificationLogDTO);
        return ResponseEntity.created(new URI("/api/notification-logs/" + notificationLogDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, notificationLogDTO.getId()))
            .body(notificationLogDTO);
    }

    /**
     * {@code PUT  /notification-logs/:id} : Updates an existing notificationLog.
     *
     * @param id the id of the notificationLogDTO to save.
     * @param notificationLogDTO the notificationLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationLogDTO,
     * or with status {@code 400 (Bad Request)} if the notificationLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotificationLogDTO> updateNotificationLog(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody NotificationLogDTO notificationLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update NotificationLog : {}, {}", id, notificationLogDTO);
        if (notificationLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        notificationLogDTO = notificationLogService.update(notificationLogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationLogDTO.getId()))
            .body(notificationLogDTO);
    }

    /**
     * {@code PATCH  /notification-logs/:id} : Partial updates given fields of an existing notificationLog, field will ignore if it is null
     *
     * @param id the id of the notificationLogDTO to save.
     * @param notificationLogDTO the notificationLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationLogDTO,
     * or with status {@code 400 (Bad Request)} if the notificationLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotificationLogDTO> partialUpdateNotificationLog(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody NotificationLogDTO notificationLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update NotificationLog partially : {}, {}", id, notificationLogDTO);
        if (notificationLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotificationLogDTO> result = notificationLogService.partialUpdate(notificationLogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationLogDTO.getId())
        );
    }

    /**
     * {@code GET  /notification-logs} : get all the notificationLogs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notificationLogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<NotificationLogDTO>> getAllNotificationLogs(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of NotificationLogs");
        Page<NotificationLogDTO> page = notificationLogService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notification-logs/:id} : get the "id" notificationLog.
     *
     * @param id the id of the notificationLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationLogDTO> getNotificationLog(@PathVariable("id") String id) {
        LOG.debug("REST request to get NotificationLog : {}", id);
        Optional<NotificationLogDTO> notificationLogDTO = notificationLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationLogDTO);
    }

    /**
     * {@code DELETE  /notification-logs/:id} : delete the "id" notificationLog.
     *
     * @param id the id of the notificationLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificationLog(@PathVariable("id") String id) {
        LOG.debug("REST request to delete NotificationLog : {}", id);
        notificationLogService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /notification-logs/_search?query=:query} : search for the notificationLog corresponding
     * to the query.
     *
     * @param query the query of the notificationLog search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<NotificationLogDTO>> searchNotificationLogs(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of NotificationLogs for query {}", query);
        try {
            Page<NotificationLogDTO> page = notificationLogService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
