package sn.ondmoney.notification.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import sn.ondmoney.notification.domain.NotificationTemplate;
import sn.ondmoney.notification.repository.NotificationTemplateRepository;

/**
 * Spring Data Elasticsearch repository for the {@link NotificationTemplate} entity.
 */
public interface NotificationTemplateSearchRepository
    extends ElasticsearchRepository<NotificationTemplate, String>, NotificationTemplateSearchRepositoryInternal {
    List<NotificationTemplate> findByTemplateCode(String templateCode);

    NotificationTemplate findNotificationTemplateByTemplateCode(String templateCode);
}

interface NotificationTemplateSearchRepositoryInternal {
    Page<NotificationTemplate> search(String query, Pageable pageable);

    Page<NotificationTemplate> search(Query query);

    @Async
    void index(NotificationTemplate entity);

    @Async
    void deleteFromIndexById(String id);
}

class NotificationTemplateSearchRepositoryInternalImpl implements NotificationTemplateSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final NotificationTemplateRepository repository;

    NotificationTemplateSearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        NotificationTemplateRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<NotificationTemplate> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<NotificationTemplate> search(Query query) {
        SearchHits<NotificationTemplate> searchHits = elasticsearchTemplate.search(query, NotificationTemplate.class);
        List<NotificationTemplate> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(NotificationTemplate entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), NotificationTemplate.class);
    }
}
