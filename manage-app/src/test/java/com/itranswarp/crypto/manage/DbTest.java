package com.itranswarp.crypto.manage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.itranswarp.crypto.manage.common.utils.JsonUtil;
import com.itranswarp.crypto.manage.model.ShareBenefitDetails;
import com.itranswarp.crypto.store.model.Order;
import com.itranswarp.crypto.store.model.extension.ExtOrderFeeRefund;
import com.itranswarp.crypto.ui.model.InviteRelation;
import com.itranswarp.warpdb.WarpDb;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

class CommonTestConfiguration {

	@Value("${spring.exdatasource.hikari.poolName:HikariCP}")
	String poolName;

	@Value("${spring.datasource.hikari.connectionTimeout:5000}")
	String connectionTimeout;

	// ui主库
	@Value("${spring.datasource.url}")
	String url;

	@Value("${spring.datasource.username}")
	String username;

	@Value("${spring.datasource.password}")
	String password;

	static final String DATASOURCE = "DataSource";
	static final String JDBC_TEMPLATE = "JdbcTemplate";
	public static final String WARP_DB = "WarpDB";
	String basePackage = "com.itranswarp.crypto.ui.model";

	@Bean(DATASOURCE)
	public DataSource createDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(this.url);
		config.setUsername(this.username);
		config.setPassword(this.password);
		config.addDataSourceProperty("poolName", this.poolName);
		config.addDataSourceProperty("connectionTimeout", this.connectionTimeout);
		config.addDataSourceProperty("autoCommit", "false");
		return new HikariDataSource(config);
	}

	@Bean(JDBC_TEMPLATE)
	JdbcTemplate createJdbcTemplate(@Autowired @Qualifier(DATASOURCE) DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean(WARP_DB)
	@Primary
	WarpDb createWarpDb(@Autowired @Qualifier(JDBC_TEMPLATE) JdbcTemplate jdbcTemplate) {
		WarpDb db = new WarpDb();
		db.setJdbcTemplate(jdbcTemplate);
		db.setBasePackages(Arrays.asList(basePackage));
		return db;
	}

	// ex从库
	@Value("${spring.exdatasource.url}")
	String exurl;

	@Value("${spring.exdatasource.username}")
	String exusername;

	@Value("${spring.exdatasource.password}")
	String expassword;

	static final String EXCHANGE_DATASOURCE = "exchangeDataSource";
	static final String EXCHANGE_JDBC_TEMPLATE = "exchangeJdbcTemplate";
	public static final String EXCHANGE_WARP_DB = "exchangeWarpDB";
	String exchangeBasePackage = "com.itranswarp.crypto.store.model";

	@Bean(EXCHANGE_DATASOURCE)
	public DataSource createExDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(this.exurl);
		config.setUsername(this.exusername);
		config.setPassword(this.expassword);
		config.addDataSourceProperty("poolName", this.poolName);
		config.addDataSourceProperty("connectionTimeout", this.connectionTimeout);
		config.addDataSourceProperty("autoCommit", "false");
		return new HikariDataSource(config);
	}

	@Bean(EXCHANGE_JDBC_TEMPLATE)
	JdbcTemplate createReadOnlyExchangeJdbcTemplate(@Autowired @Qualifier(EXCHANGE_DATASOURCE) DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean(EXCHANGE_WARP_DB)
	WarpDb createReadOnlyExchangeWarpDb(@Autowired @Qualifier(EXCHANGE_JDBC_TEMPLATE) JdbcTemplate jdbcTemplate) {
		WarpDb db = new WarpDb();
		db.setJdbcTemplate(jdbcTemplate);
		db.setBasePackages(Arrays.asList(exchangeBasePackage));
		return db;
	}
}

@Commit
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:/ui-jdbc.properties")
@ContextConfiguration(classes = { CommonTestConfiguration.class })
public class DbTest {

	@Autowired
	@Qualifier(CommonTestConfiguration.EXCHANGE_WARP_DB)
	protected WarpDb exdb;

	@Autowired
	protected WarpDb uidb;

	@Test
	public void exdbTest() throws Exception {
		long beInvitedUserId = 100001;
		long invitedUserId = 10000;
		String date = "2018-02-05";
		List<ShareBenefitDetails> fun = fun(beInvitedUserId, date);
		System.out.println("测试结果集：" + JsonUtil.toJson(fun));
	}

	/**
	 * 某一被邀请人在某一天的全部订单相关手续费
	 * 
	 * @param beInvitedUserId
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public List<ShareBenefitDetails> fun(long beInvitedUserId, String date) throws Exception {
		long startTime = DateUtils.parseDate(date + " 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
		long endTime = DateUtils.parseDate(date + " 23:59:59", "yyyy-MM-dd HH:mm:ss").getTime();

		String ordersql = "SELECT * FROM orders WHERE userId = ? AND status IN ( 'PARTIAL_CANCELLED','FULLY_FILLED' ) AND updatedAt > ? AND updatedAt < ?";
		List<Order> lists = exdb.list(Order.class, ordersql, beInvitedUserId, startTime, endTime);

		// 未抵扣开BDB抵扣的订单手续费
		List<ShareBenefitDetails> nofeaturesOrder = lists.parallelStream().filter(order -> order.features == 0)
				.map(order -> {
					String[] a = order.type.name().split("_");
					String[] b = order.symbol.split("_");
					String actPayCurrency = a[0].equals("BUY") ? b[0] : b[1];
					ShareBenefitDetails shareBenefitDetails = new ShareBenefitDetails();
					shareBenefitDetails.actPayCurrency = actPayCurrency;
					shareBenefitDetails.feeAmt = order.fee;
					shareBenefitDetails.orderCreatedAt = order.createdAt;
					shareBenefitDetails.orderUpdatedAt = order.updatedAt;
					return shareBenefitDetails;
				}).collect(Collectors.toList());

		// 开了BDB燃烧的部分订单

		// 开了抵扣的订单手续费
		String refundssql = "SELECT * FROM ext_order_fee_refunds WHERE userId = ? AND createdAt > ? AND createdAt < ?";
		List<ExtOrderFeeRefund> refunds = exdb.list(ExtOrderFeeRefund.class, refundssql, beInvitedUserId,
				startTime - 1000, endTime + 1000);
		Map<Long, ExtOrderFeeRefund> refundMaps = refunds.parallelStream()
				.collect(Collectors.toMap(refund -> refund.orderId, refund -> refund));
		// 开了BDB燃烧的部分订单

		List<ShareBenefitDetails> feeInfos = Lists.newArrayList();
		lists.stream().filter(order -> order.features == 65536).forEach(order -> {
			ExtOrderFeeRefund feeRefund = refundMaps.get(order.id);
			// 未能成功抵扣BDB的部分

			if (feeRefund.originalFee.compareTo(feeRefund.refundedFee) != 0) {
				Map<String, ? extends Object> feeInfoMap = ImmutableMap.of("feeAmt",
						feeRefund.originalFee.subtract(feeRefund.refundedFee), "orderId", order.id, "actPayCurrency",
						feeRefund.originalFeeCurrency);
				ShareBenefitDetails shareBenefitDetails = new ShareBenefitDetails();
				shareBenefitDetails.feeAmt = feeRefund.originalFee.subtract(feeRefund.refundedFee);
				shareBenefitDetails.orderId = order.id;
				shareBenefitDetails.actPayCurrency = feeRefund.originalFeeCurrency;
				shareBenefitDetails.orderCreatedAt = order.createdAt;
				shareBenefitDetails.orderUpdatedAt = order.updatedAt;
				// JSONObject orderMap =
				// JSON.parseObject(JSON.toJSONString(order));
				// orderMap.put("orderCreatedAt", order.createdAt);
				// orderMap.put("orderUpdatedAt", order.updatedAt);
				// orderMap.putAll(feeInfoMap);
				feeInfos.add(shareBenefitDetails);
			}
			// 抵扣成功BDB的部分
			ShareBenefitDetails shareBenefitDetails = new ShareBenefitDetails();
			shareBenefitDetails.feeAmt = feeRefund.replacedFee;
			shareBenefitDetails.orderId = order.id;
			shareBenefitDetails.actPayCurrency = feeRefund.replacedFeeCurrency;
			shareBenefitDetails.orderCreatedAt = order.createdAt;
			shareBenefitDetails.orderUpdatedAt = order.updatedAt;
			// Map<String, ? extends Object> feeInfoMap =
			// ImmutableMap.of("feeAmt", feeRefund.replacedFee, "orderId",
			// order.id, "actPayCurrency", feeRefund.replacedFeeCurrency);
			// JSONObject orderMap = JSON.parseObject(JSON.toJSONString(order));
			// orderMap.put("orderCreatedAt", order.createdAt);
			// orderMap.put("orderUpdatedAt", order.updatedAt);
			// orderMap.putAll(feeInfoMap);
			feeInfos.add(shareBenefitDetails);

		});

		// 抵扣 + 未抵扣
		feeInfos.addAll(nofeaturesOrder);
		System.out.println("nofeaturesOrder  :" + nofeaturesOrder.size());
		System.out.println("feeinfos  :" + feeInfos.size());
		System.out.println("feeinfos  :" + feeInfos);
		return feeInfos;
	}

	@Test
	public void uidbTest() throws Exception {
		String date = "2018-02-05";
		long time = DateUtils.parseDate(date + " 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();

		List<InviteRelation> userlist = uidb.list(InviteRelation.class,
				"SELECT inviteduserId,GROUP_CONCAT(beInvitedUserId) AS beInvitedEmail FROM invite_relation WHERE createdAt < ? GROUP BY inviteduserId;",
				time);

		List<Map<String, Object>> collect = userlist.parallelStream().map(user -> {
			List<String> beInvitedUserIds = Arrays.asList(user.beInvitedEmail.split(","));
			List<List<ShareBenefitDetails>> feeInfos = beInvitedUserIds.parallelStream().map(beInvitedUserId -> {
				try {
					return fun(Long.parseLong(beInvitedUserId), date);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}).collect(Collectors.toList());
			return ImmutableMap.of("userId", user.inviteduserId, "feeInfos", feeInfos);
		}).collect(Collectors.toList());
		System.out.println("解析结果:" + JsonUtil.toJson(collect));
	}

}
