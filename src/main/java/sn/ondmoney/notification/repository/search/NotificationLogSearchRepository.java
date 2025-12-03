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
import sn.ondmoney.notification.domain.NotificationLog;
import sn.ondmoney.notification.repository.NotificationLogRepository;

/**
 * Spring Data Elasticsearch repository for the {@link NotificationLog} entity.
 */
public interface NotificationLogSearchRepository
    extends ElasticsearchRepository<NotificationLog, String>, NotificationLogSearchRepositoryInternal {}

interface NotificationLogSearchRepositoryInternal {
    Page<NotificationLog> search(String query, Pageable pageable);

    Page<NotificationLog> search(Query query);

    @Async
    void index(NotificationLog entity);

    @Async
    void deleteFromIndexById(String id);
}

class NotificationLogSearchRepositoryInternalImpl implements NotificationLogSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final NotificationLogRepository repository;

    NotificationLogSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, NotificationLogRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<NotificationLog> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<NotificationLog> search(Query query) {
        SearchHits<NotificationLog> searchHits = elasticsearchTemplate.search(query, NotificationLog.class);
        List<NotificationLog> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(NotificationLog entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), NotificationLog.class);
    }
}
