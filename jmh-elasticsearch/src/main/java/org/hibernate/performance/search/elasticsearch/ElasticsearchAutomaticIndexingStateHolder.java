package org.hibernate.performance.search.elasticsearch;

import java.util.Properties;

import org.hibernate.performance.search.model.application.BackendType;
import org.hibernate.performance.search.model.application.IndexingType;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.tck.AutomaticIndexingStateHolder;

public class ElasticsearchAutomaticIndexingStateHolder extends AutomaticIndexingStateHolder {

	@Override
	protected Properties autoProperties(ModelService modelService) {
		return modelService.properties( BackendType.ELASTICSEARCH, IndexingType.AUTOMATIC );
	}
}
