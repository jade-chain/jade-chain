package com.itranswarp.crypto;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itranswarp.crypto.client.RestClient;
import com.itranswarp.crypto.manage.redis.RedisClientBuilderCheack;
import com.itranswarp.crypto.manage.redis.RedisConfigurationManage;
import com.itranswarp.crypto.manage.redis.RedisConfigurationManageAPI;
import com.itranswarp.crypto.util.DateTimeUtil;
import com.itranswarp.crypto.util.JsonUtil;
import com.itranswarp.warpdb.WarpDb;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.spring4.PebbleViewResolver;

/**
 * A crypto exchange management application.
 * 
 * @author liaoxuefeng
 */
@EnableScheduling
@SpringBootApplication
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@DubboComponentScan(basePackages = "com.itranswarp.crypto.manage")
public class CryptoManageApplication {

	final Logger logger = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) {
		SpringApplication.run(CryptoManageApplication.class, args);
	}

	@Value("${spring.dubbo-newManage.application.name}") 
	private String applicationName;
	@Value("${spring.common-c2c.registry.address}") 
	private String registryAddr;
	@Value("${spring.dubbo-newManage.protocol.name}") 
	private String protocolName;
	@Value("${spring.dubbo-newManage.protocol.port}") 
	private Integer protocolPort;
	@Value("${spring.dubbo-newManage.scan}") 
	private String registryId;
	@Value("${server.port}") 
	private int port;
	
	String manageBasePackage = "com.itranswarp.crypto.manage.model";

	String exchangeBasePackage = "com.itranswarp.crypto.store.model";

	String uiBasePackage = "com.itranswarp.crypto.ui";
	String uiC2cBasePackage = "com.itranswarp.crypto";

	@Value("${crypto.setting.timezone:}")
	String timezone;

	@Value("${crypto.manage.view.cache:false}")
	boolean viewCache;

	@Value("${crypto.manage.api.endpoint}")
	String apiEndpoint;

	@Value("${crypto.manage.api.api-key}")
	String apiKey;

	@Value("${crypto.manage.api.api-secret}")
	String apiSecret;

	@Value("${crypto.manage.ui.endpoint}")
	String uiEndpoint;

	@Value("${crypto.manage.ui.api-key}")
	String uiKey;

	@Value("${crypto.manage.ui.api-secret}")
	String uiSecret;
	@Value("${web.upload-path}")
	String uploadPath;

	/**
	 * Create a rest client.
	 * 
	 * @return RestClient object.
	 */
	@Primary
	@Bean
	public RestClient createRestClientForApi() {
		return new RestClient.Builder(apiEndpoint).authenticate(apiKey, apiSecret).build();
	}

	/**
	 * Create a rest client.
	 * 
	 * @return RestClient object.
	 */
	@Bean("uiRestClient")
	public RestClient createRestClientForUI() {
		return new RestClient.Builder(uiEndpoint).authenticate(uiKey, uiSecret).build();
	}

	/**
	 * Create JSON object mapper.
	 * 
	 * @return Shared static object mapper from JsonUtil.
	 */
	@Bean
	public ObjectMapper objectMapper() {
		return JsonUtil.OBJECT_MAPPER;
	}

	/**
	 * Create ZoneId as exchange zone. Default to system zone id.
	 * 
	 * @return ZoneId object.
	 */
	@Bean
	public ZoneId createTimezone() {
		ZoneId zoneId = ZoneId.systemDefault();
		if (timezone != null && !timezone.trim().isEmpty()) {
			zoneId = ZoneId.of(timezone);
		}
		logger.info("set exchange timezone = {}", zoneId.getId());
		return zoneId;
	}

	@Bean
	public WebMvcConfigurer webMvcConfigurer(@Autowired ObjectMapper objectMapper) {
		return new WebMvcConfigurerAdapter() {
			/**
			 * Keep /static/ prefix
			 */
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				super.addResourceHandlers(registry);
				registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/statics/");
				registry.addResourceHandler("/**").addResourceLocations("file:" + uploadPath);
			}

			@Override
			public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
				// MappingJackson2HttpMessageConverter
				// jackson2HttpMessageConverter = new
				// MappingJackson2HttpMessageConverter();
				// ObjectMapper objectMapper =
				// jackson2HttpMessageConverter.getObjectMapper();
				//
				// // 生成json时，将所有Long转换成String
				// SimpleModule simpleModule = new SimpleModule();
				// simpleModule.addSerializer(Long.class,
				// ToStringSerializer.instance);
				// simpleModule.addSerializer(Long.TYPE,
				// ToStringSerializer.instance);
				// objectMapper.registerModule(simpleModule);
				// objectMapper.configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS,
				// false);
				// jackson2HttpMessageConverter.setObjectMapper(objectMapper);
				// converters.add(0, jackson2HttpMessageConverter);

				// 定义一个转换消息的对象
				FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

				// 添加fastjson的配置信息 比如 ：是否要格式化返回的json数据
				FastJsonConfig fastJsonConfig = new FastJsonConfig();
				/**
				 * 
				 * ValueFilter filter = new ValueFilter() {
				 * 
				 * @Override public Object process(Object object, String name,
				 *           Object value) { if (value instanceof BigDecimal) {
				 *           return ((BigDecimal)
				 *           value).stripTrailingZeros().toPlainString(); }
				 *           return value; } };
				 */
				ValueFilter filter = new ValueFilter() {
					@Override
					public Object process(Object object, String name, Object value) {
						if (value instanceof BigDecimal) {
							return ((BigDecimal) value).stripTrailingZeros().toPlainString();
						}
						return value;
					}
				};
				fastJsonConfig.setSerializeFilters(filter);
				fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);

				// 在转换器中添加配置信息
				fastConverter.setFastJsonConfig(fastJsonConfig);

				// 将转换器添加到converters中
				converters.add(0, fastConverter);
			}
		};
	}

	/**
	 * Init view resolver.
	 * 
	 * @return PebbleViewResolver
	 */
	@Bean
	public ViewResolver pebbleViewResolver() {
		PebbleViewResolver viewResolver = new PebbleViewResolver();
		viewResolver.setPrefix("templates/");
		viewResolver.setSuffix("");
		viewResolver.setPebbleEngine(new PebbleEngine.Builder().extension(pebbleExtension())
				.loader(new ClasspathLoader()).autoEscaping(true).cacheActive(viewCache).build());
		return viewResolver;
	}

	Extension pebbleExtension() {
		return new AbstractExtension() {
			@Override
			public Map<String, Filter> getFilters() {
				Map<String, Filter> map = new HashMap<>();
				map.put("json", new Filter() {
					@Override
					public List<String> getArgumentNames() {
						return null;
					}

					@Override
					public Object apply(Object input, Map<String, Object> args) {
						return JsonUtil.writeJson(input);
					}
				});
				map.put("datetime", new Filter() {
					@Override
					public List<String> getArgumentNames() {
						return null;
					}

					@Override
					public Object apply(Object input, Map<String, Object> args) {
						if (input == null) {
							return null;
						}
						Long n = (Long) input;
						return DateTimeUtil.timestampToString(n);
					}
				});
				return map;
			}
		};
	}

	// database ///////////////////////////////////////////////////////////////

	@Bean(MANAGE_WARP_DB)
	WarpDb createPrimaryWarpDb(@Autowired @Qualifier(MANAGE_JDBC_TEMPLATE) JdbcTemplate jdbcTemplate) {
		logger.info("create warpdb by scanning package: {}", manageBasePackage);
		WarpDb db = new WarpDb();
		db.setJdbcTemplate(jdbcTemplate);
		db.setBasePackages(Arrays.asList(manageBasePackage));
		return db;
	}

	@Bean(EXCHANGE_WARP_DB)
	@Primary
	WarpDb createReadOnlyExchangeWarpDb(@Autowired @Qualifier(EXCHANGE_JDBC_TEMPLATE) JdbcTemplate jdbcTemplate) {
		logger.info("create warpdb by scanning package: {}", exchangeBasePackage);
		WarpDb db = new WarpDb();
		db.setJdbcTemplate(jdbcTemplate);
		db.setBasePackages(Arrays.asList(exchangeBasePackage));
		return db;
	}
	
	@Bean(EXCHANGE_RW_WARP_DB)
	WarpDb createReadOnlyExchangeRwWarpDb(@Autowired @Qualifier(EXCHANGE_RW_JDBC_TEMPLATE) JdbcTemplate jdbcTemplate) {
		logger.info("create warpdb by scanning package: {}", exchangeBasePackage);
		WarpDb db = new WarpDb();
		db.setJdbcTemplate(jdbcTemplate);
		db.setBasePackages(Arrays.asList(exchangeBasePackage));
		return db;
	}

	@Bean(UI_WARP_DB)
	WarpDb createReadOnlyUIWarpDb(@Autowired @Qualifier(UI_JDBC_TEMPLATE) JdbcTemplate jdbcTemplate) {
		logger.info("create warpdb by scanning package: {}", uiBasePackage);
		WarpDb db = new WarpDb();
		db.setJdbcTemplate(jdbcTemplate);
		db.setBasePackages(Arrays.asList(uiBasePackage));
		return db;
	}
	@Bean(UI_C2C_DB)
	WarpDb createReadOnlyUiC2cDb(@Autowired @Qualifier(UI_C2C_JDBC_TEMPLATE) JdbcTemplate jdbcTemplate) {
		logger.info("create warpdb by scanning package: {}", uiC2cBasePackage);
		WarpDb db = new WarpDb();
		db.setJdbcTemplate(jdbcTemplate);
		db.setBasePackages(Arrays.asList(uiC2cBasePackage));
		return db;
	}

	@Bean(MANAGE_JDBC_TEMPLATE)
	JdbcTemplate createPrimaryJdbcTemplate(@Autowired @Qualifier(MANAGE_DATASOURCE) DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean(EXCHANGE_RW_JDBC_TEMPLATE)
	JdbcTemplate createReadOnlyExchangeRwJdbcTemplate(@Autowired @Qualifier(EXCHANGE_RW_DATASOURCE) DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	@Bean(EXCHANGE_JDBC_TEMPLATE)
	@Primary
	JdbcTemplate createReadOnlyExchangeJdbcTemplate(@Autowired @Qualifier(EXCHANGE_DATASOURCE) DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}


	@Bean(UI_JDBC_TEMPLATE)
	JdbcTemplate createReadOnlyUIJdbcTemplate(@Autowired @Qualifier(UI_DATASOURCE) DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	@Bean(UI_C2C_JDBC_TEMPLATE)
	JdbcTemplate createReadOnlyUiC2cJdbcTemplate(@Autowired @Qualifier(UI_C2C_DATASOURCE) DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	/**
	 * RW data source.
	 */
	@Bean(MANAGE_DATASOURCE)
	@ConfigurationProperties("spring.datasource")
	DataSource createPrimaryDataSource() {
		logger.info("create manage datasource...");
		return DataSourceBuilder.create().build();
	}

	/**
	 * Read only data source for access exchange.
	 */
	@Bean(EXCHANGE_DATASOURCE)
	@Primary
	@ConfigurationProperties("spring.exchange-datasource")
	DataSource createReadOnlyExchangeDataSource() {
		logger.info("create readonly exchange datasource...");
		return DataSourceBuilder.create().build();
	}

	/**
	 * Read only data source for access exchange.
	 */
	@Bean(EXCHANGE_RW_DATASOURCE)
	@ConfigurationProperties("spring.exchange-rw-datasource")
	DataSource createReadOnlyExchangeRwDataSource() {
		logger.info("create readonly exchange datasource...");
		return DataSourceBuilder.create().build();
	}

	/**
	 * Read only data source for access user.
	 */
	@Bean(UI_DATASOURCE)
	@ConfigurationProperties("spring.ui-datasource")
	DataSource createReadOnlyUIDataSource() {
		logger.info("create readonly ui datasource...");
		return DataSourceBuilder.create().build();
	}
	/**
	 * Read only data source for access user.
	 */
	@Bean(UI_C2C_DATASOURCE)
	@ConfigurationProperties("spring.ui-c2c-datasource")
	DataSource createReadOnlyUiC2cDataSource() {
		logger.info("create readonly uiC2c datasource...");
		return DataSourceBuilder.create().build();
	}

	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
		// 1.需要定义一个convert转换消息的对象;
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		// 2:添加fastJson的配置信息;
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
		// 3处理中文乱码问题
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		// 4.在convert中添加配置信息.
		fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
		fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
		HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;
		return new HttpMessageConverters(converter);

	}

	// redis
	@Bean
	@Primary
	public RedissonClient createRedisClient(@Autowired RedisConfigurationManage rc) {
		return RedisClientBuilderCheack.buildRedissionClient(rc);
	}
	
	
	// redis
	public static final String API_REDIS="apiRedis";
	@Bean(API_REDIS)
	public RedissonClient createAPIRedisClient(@Autowired RedisConfigurationManageAPI rc) {
		return RedisClientBuilderCheack.buildRedissionClient(rc);
	}

	public static final String EXCHANGE_RW_WARP_DB = "exchangeRwWarpDB";
	public static final String EXCHANGE_WARP_DB = "exchangeWarpDB";
	public static final String UI_WARP_DB = "uiDb";
	public static final String MANAGE_WARP_DB = "manageWarpDB";
	public static final String UI_C2C_DB = "uiC2cDb";
	
	
	static final String EXCHANGE_RW_DATASOURCE = "exchangeRwDataSource";
	static final String EXCHANGE_DATASOURCE = "exchangeDataSource";
	static final String UI_DATASOURCE = "uiDataSource";
	static final String MANAGE_DATASOURCE = "manageDataSource";
	static final String UI_C2C_DATASOURCE = "uiC2cDataSource";
	
	static final String EXCHANGE_RW_JDBC_TEMPLATE = "exchangeRwJdbcTemplate";
	static final String EXCHANGE_JDBC_TEMPLATE = "exchangeJdbcTemplate";
	static final String UI_JDBC_TEMPLATE = "uiJdbcTemplate";
	static final String MANAGE_JDBC_TEMPLATE = "manageJdbcTemplate";
	static final String UI_C2C_JDBC_TEMPLATE = "uiC2cJdbcTemplate";

	@Bean 
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(applicationName);
		return applicationConfig;
	}
	

	@Bean
	public RegistryConfig registryConfig() {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(registryAddr);
		registryConfig.setClient("zkclient");
		return registryConfig;
	}
	
	@Bean
	 public ProtocolConfig protocolConfig() {
	    ProtocolConfig protocolConfig = new ProtocolConfig();
	    protocolConfig.setName(protocolName);
	    protocolConfig.setPort(protocolPort);
	    return protocolConfig;
	 }
	
}
