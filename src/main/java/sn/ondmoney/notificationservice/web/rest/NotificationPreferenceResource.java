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
import sn.ondmoney.notificationservice.repository.NotificationPreferenceRepository;
import sn.ondmoney.notificationservice.service.NotificationPreferenceQueryService;
import sn.ondmoney.notificationservice.service.NotificationPreferenceService;
import sn.ondmoney.notificationservice.service.criteria.NotificationPreferenceCriteria;
import sn.ondmoney.notificationservice.service.dto.NotificationPreferenceDTO;
import sn.ondmoney.notificationservice.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.ondmoney.notificationservice.domain.NotificationPreference}.
 */
@RestController
@RequestMapping("/api/notification-preferences")
public class NotificationPreferenceResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationPreferenceResource.class);

    private static final String ENTITY_NAME = "ondmoneyNotificationServiceNotificationPreference";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationPreferenceService notificationPreferenceService;

    private final NotificationPreferenceRepository notificationPreferenceRepository;

    private final NotificationPreferenceQueryService notificationPreferenceQueryService;

    public NotificationPreferenceResource(
        NotificationPreferenceService notificationPreferenceService,
        NotificationPreferenceRepository notificationPreferenceRepository,
        NotificationPreferenceQueryService notificationPreferenceQueryService
    ) {
        this.notificationPreferenceService = notificationPreferenceService;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.notificationPreferenceQueryService = notificationPreferenceQueryService;
    }

    /**
     * {@code POST  /notification-preferences} : Create a new notificationPreference.
     *
     * @param notificationPreferenceDTO the notificationPreferenceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationPreferenceDTO, or with status {@code 400 (Bad Request)} if the notificationPreference has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NotificationPreferenceDTO> createNotificationPreference(
        @Valid @RequestBody NotificationPreferenceDTO notificationPreferenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save NotificationPreference : {}", notificationPreferenceDTO);
        if (notificationPreferenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new notificationPreference cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notificationPreferenceDTO = notificationPreferenceService.save(notificationPreferenceDTO);
        return ResponseEntity.created(new URI("/api/notification-preferences/" + notificationPreferenceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, notificationPreferenceDTO.getId().toString()))
            .body(notificationPreferenceDTO);
    }

    /**
     * {@code PUT  /notification-preferences/:id} : Updates an existing notificationPreference.
     *
     * @param id the id of the notificationPreferenceDTO to save.
     * @param notificationPreferenceDTO the notificationPreferenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationPreferenceDTO,
     * or with status {@code 400 (Bad Request)} if the notificationPreferenceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationPreferenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotificationPreferenceDTO> updateNotificationPreference(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotificationPreferenceDTO notificationPreferenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update NotificationPreference : {}, {}", id, notificationPreferenceDTO);
        if (notificationPreferenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationPreferenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationPreferenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        notificationPreferenceDTO = notificationPreferenceService.update(notificationPreferenceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationPreferenceDTO.getId().toString()))
            .body(notificationPreferenceDTO);
    }

    /**
     * {@code PATCH  /notification-preferences/:id} : Partial updates given fields of an existing notificationPreference, field will ignore if it is null
     *
     * @param id the id of the notificationPreferenceDTO to save.
     * @param notificationPreferenceDTO the notificationPreferenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationPreferenceDTO,
     * or with status {@code 400 (Bad Request)} if the notificationPreferenceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationPreferenceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationPreferenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotificationPreferenceDTO> partialUpdateNotificationPreference(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotificationPreferenceDTO notificationPreferenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update NotificationPreference partially : {}, {}", id, notificationPreferenceDTO);
        if (notificationPreferenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationPreferenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationPreferenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotificationPreferenceDTO> result = notificationPreferenceService.partialUpdate(notificationPreferenceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationPreferenceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notification-preferences} : get all the notificationPreferences.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notificationPreferences in body.
     */
    @GetMapping("")
    public ResponseEntity<List<NotificationPreferenceDTO>> getAllNotificationPreferences(
        NotificationPreferenceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get NotificationPreferences by criteria: {}", criteria);

        Page<NotificationPreferenceDTO> page = notificationPreferenceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notification-preferences/count} : count all the notificationPreferences.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countNotificationPreferences(NotificationPreferenceCriteria criteria) {
        LOG.debug("REST request to count NotificationPreferences by criteria: {}", criteria);
        return ResponseEntity.ok().body(notificationPreferenceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /notification-preferences/:id} : get the "id" notificationPreference.
     *
     * @param id the id of the notificationPreferenceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationPreferenceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationPreferenceDTO> getNotificationPreference(@PathVariable("id") Long id) {
        LOG.debug("REST request to get NotificationPreference : {}", id);
        Optional<NotificationPreferenceDTO> notificationPreferenceDTO = notificationPreferenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationPreferenceDTO);
    }

    /**
     * {@code DELETE  /notification-preferences/:id} : delete the "id" notificationPreference.
     *
     * @param id the id of the notificationPreferenceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificationPreference(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete NotificationPreference : {}", id);
        notificationPreferenceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
