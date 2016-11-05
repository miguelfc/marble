package org.marble.commons.config;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's mongo
 * support.
 * <p>
 * Registers a {@link MongoTemplate} and {@link GridFsTemplate} beans if no
 * other beans of the same type are configured.
 * <P>
 * Honors the {@literal spring.data.mongodb.database} property if set, otherwise
 * connects to the {@literal test} database.
 *
 * @author Dave Syer
 * @author Oliver Gierke
 * @author Josh Long
 * @author Phillip Webb
 * @author Eddú Meléndez
 * @since 1.1.0
 */
@Configuration
@ConditionalOnClass({ Mongo.class, MongoTemplate.class })
@EnableConfigurationProperties(MongoProperties.class)
@AutoConfigureAfter(MongoCustomConfiguration.class)
public class MongoDataCustomConfiguration implements BeanClassLoaderAware {

    @Autowired
    private MongoProperties properties;

    @Autowired
    private Environment environment;

    @Autowired
    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Bean
    @ConditionalOnMissingBean(MongoDbFactory.class)
    public SimpleMongoDbFactory mongoDbFactory(MongoClient mongo) throws Exception {
        String database = this.properties.getMongoClientDatabase();
        return new SimpleMongoDbFactory(mongo, database);
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory,
            MongoConverter converter) throws UnknownHostException {
        return new MongoTemplate(mongoDbFactory, converter);
    }

    @Bean
    // @ConditionalOnMissingBean(MongoConverter.class)
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory,
            MongoMappingContext context, BeanFactory beanFactory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver,
                context);
        try {
            mappingConverter.setMapKeyDotReplacement("~_~");
            mappingConverter.setCustomConversions(beanFactory
                    .getBean(CustomConversions.class));

        } catch (NoSuchBeanDefinitionException ex) {
            // Ignore
        }
        return mappingConverter;
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoMappingContext mongoMappingContext(BeanFactory beanFactory)
            throws ClassNotFoundException {
        MongoMappingContext context = new MongoMappingContext();
        context.setInitialEntitySet(getInitialEntitySet(beanFactory));
        Class<? extends FieldNamingStrategy> strategyClass = (Class<? extends FieldNamingStrategy>) this.properties
                .getFieldNamingStrategy();
        if (strategyClass != null) {
            context.setFieldNamingStrategy(BeanUtils.instantiate(strategyClass));
        }
        return context;
    }

    private Set<Class<?>> getInitialEntitySet(BeanFactory beanFactory)
            throws ClassNotFoundException {
        Set<Class<?>> entitySet = new HashSet<Class<?>>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
                false);
        scanner.setEnvironment(this.environment);
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Document.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Persistent.class));
        for (String basePackage : getMappingBasePackages(beanFactory)) {
            if (StringUtils.hasText(basePackage)) {
                for (BeanDefinition candidate : scanner
                        .findCandidateComponents(basePackage)) {
                    entitySet.add(ClassUtils.forName(candidate.getBeanClassName(),
                            this.classLoader));
                }
            }
        }
        return entitySet;
    }

    private static Collection<String> getMappingBasePackages(BeanFactory beanFactory) {
        try {
            return AutoConfigurationPackages.get(beanFactory);
        } catch (IllegalStateException ex) {
            // no auto-configuration package registered yet
            return Collections.emptyList();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public GridFsTemplate gridFsTemplate(MongoDbFactory mongoDbFactory,
            MongoTemplate mongoTemplate) {
        return new GridFsTemplate(new GridFsMongoDbFactory(mongoDbFactory,
                this.properties), mongoTemplate.getConverter());
    }

    /**
     * {@link MongoDbFactory} decorator to respect
     * {@link MongoProperties#getGridFsDatabase()} if set.
     */
    private static class GridFsMongoDbFactory implements MongoDbFactory {

        private final MongoDbFactory mongoDbFactory;

        private final MongoProperties properties;

        public GridFsMongoDbFactory(MongoDbFactory mongoDbFactory,
                MongoProperties properties) {
            Assert.notNull(mongoDbFactory, "MongoDbFactory must not be null");
            Assert.notNull(properties, "Properties must not be null");
            this.mongoDbFactory = mongoDbFactory;
            this.properties = properties;
        }

        @Override
        public DB getDb() throws DataAccessException {
            String gridFsDatabase = this.properties.getGridFsDatabase();
            if (StringUtils.hasText(gridFsDatabase)) {
                return this.mongoDbFactory.getDb(gridFsDatabase);
            }
            return this.mongoDbFactory.getDb();
        }

        @Override
        public DB getDb(String dbName) throws DataAccessException {
            return this.mongoDbFactory.getDb(dbName);
        }

        @Override
        public PersistenceExceptionTranslator getExceptionTranslator() {
            return this.mongoDbFactory.getExceptionTranslator();
        }

    }

}
/*
 * {
 * 
 * @Bean public MongoTypeMapper mongoTypeMapper() { return new
 * DefaultMongoTypeMapper(null); }
 * 
 * public @Bean MappingMongoConverter mongoConverter() throws Exception {
 * MappingMongoConverter mappingMongoConverter = new
 * MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());
 * mappingMongoConverter.setTypeMapper(mongoTypeMapper());
 * mappingMongoConverter.setMapKeyDotReplacement("~_~"); return
 * mappingMongoConverter; } }
 */