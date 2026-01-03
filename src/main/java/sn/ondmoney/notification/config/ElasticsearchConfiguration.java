package sn.ondmoney.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Configuration Elasticsearch.
 * Activée uniquement en production (profil "prod").
 * En développement, Elasticsearch n'est pas requis.
 */
@Configuration
@Profile("prod")
@EnableElasticsearchRepositories("sn.ondmoney.notification.repository.search")
public class ElasticsearchConfiguration {
    // Configuration Elasticsearch activée uniquement en production
}
