package com.insframe.server.config;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.insframe.server.data.repository.RepositoryPackage;
import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories(basePackageClasses=RepositoryPackage.class)
class MongoConfig {
	
	@Value("${mongodb.host}")
	private String host;
	@Value("${mongodb.port}")
	private String port;
	@Value("${mongodb.database}")
	private String database;
	@Value("${mongodb.username}")
	private String username;
	@Value("${mongodb.password}")
	private String password;

    @Bean
    public MongoDbFactory mongoDbFactory() throws UnknownHostException {
        return new SimpleMongoDbFactory(new MongoClient(host, Integer.parseInt(port)), database, new UserCredentials(username, password));
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        MongoTemplate template = new MongoTemplate(mongoDbFactory(), mongoConverter());
        return template;
    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws UnknownHostException {
      return new GridFsTemplate(mongoDbFactory(), mongoConverter());
    }

    @Bean
    public MongoTypeMapper mongoTypeMapper() {
        return new DefaultMongoTypeMapper(null);
    }

    @Bean
    public MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }

    @Bean
    public MappingMongoConverter mongoConverter() throws UnknownHostException {
        MappingMongoConverter converter = new MappingMongoConverter(mongoDbFactory(), mongoMappingContext());
        converter.setTypeMapper(mongoTypeMapper());
        return converter;
    }
}
