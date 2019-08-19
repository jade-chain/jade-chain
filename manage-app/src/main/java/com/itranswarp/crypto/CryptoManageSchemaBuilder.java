package com.itranswarp.crypto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.itranswarp.crypto.manage.model.MenuStatistic;
import com.itranswarp.crypto.manage.model.SystemUserRefRoleStatistic;
import com.itranswarp.crypto.store.model.User;
import com.itranswarp.crypto.store.model.support.SchemaBuilder;

/**
 * Generate database schema for manage server.
 * 
 * @author liaoxuefeng
 */
public class CryptoManageSchemaBuilder {

	public static void main(String[] args) throws IOException {
		String basePackage = "com.itranswarp.crypto.manage.model";
		String dbName = "mg";
		File ddlFile = new File("target/mg.sql");
		SchemaBuilder.export(basePackage, dbName, ddlFile, "utf8");

		File initFile = new File("target/init-mg.sql");
		StringBuffer sb = new StringBuffer();
		sb.append(buildCreateMenuSql());
		sb.append(buildCreateUserRoleSql());
		sb.append(buildCreateSysUserSql());
		sb.append(buildCreateRoleMenuSql());
		sb.append(buildCreateRoleSql());
		sb.append(buildCreateParamConfigSql());
		sb.append(buildCreateEnumValueSql());
		sb.append(buildCreateNoticeColumnSql());
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(initFile), "UTF-8"))) {
			writer.write(sb.toString());
			writer.flush();
			writer.close();
		}
	}

	static String buildCreateMenuSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('订单信息', null, '2', '/manage/userOrderInfo/', '0', '100002', '1516677716403');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('货币信息', null, '3', '/manage/currencies/', '0', '100003', '1516677716404');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('资产查询', null, '4', '/manage/userAssetQuery/', '0', '100004', '1516677716405');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('提现记录', null, '5', '/manage/userWithDrawRecord/', '0', '100005', '1516677716410');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('充值记录', null, '6', '/manage/userDepositRecord/', '0', '100006', '1516677716411');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('交易记录', null, '7', '/manage/userTradeRecord/', '0', '100007', '1516677716412');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('充值记录统计', null, '8', '/manage/userDepositRecordCount/', '0', '100008', '1516677716413');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('提现记录统计', null, '9', '/manage/userWithDrawRecordCount/', '0', '100009', '1516677716414');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('用户身份审核', null, '10', '/manage/userIdentityAudit/', '0', '100010', '1516677716415');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('用户提币审核', null, '11', '/manage/userWithDrawAudit/', '0', '100011', '1516677716416');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('用户文件上传', null, '12', '/manage/userFileUpload/', '0', '100012', '1516677716417');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('用户管理', null, '13', '/manage/systemuser/', '0', '100013', '1516677716418');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('菜单管理', null, '14', '/manage/menus/', '0', '100014', '1516677716419');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('角色管理', null, '15', '/manage/roles/', '0', '100015', '1516677716420');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('资产导出', null, '0', '/manage/userinvestexport/', '0', '100016', '1516677716421');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('发放BDB', null, '0', '/manage/giveOutBdb/', '0', '100017', '1516677716421');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('客户信息', null, '0', '/manage/users/', '0', '100018', '1516677716421');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('用户日志导出', null, '0', '/manage/userLogExport/', '0', '100019', '1516677716421');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('客户资产转账', null, '0', '/manage/usersTransfer/', '0', '100020', '1516677716421');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('内部转账', null, '0', '/manage/acctrans/', '0', '100021', '1516677716421');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('导入转账数据', null, '0', '/manage/acctrans/index', '0', '100022', '1516677716421');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('公告管理页面', null, '0', '/manage/notice/', '0', '100023', '1516677716421');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('用户详情', null, '0', '/manage/ShareBenefit/', '0', '100024', '1516677716421');\n");
		sb.append(
				"INSERT INTO menus_statisic(name,preId,sorder,url,enable,id,createdAt) VALUES ('用户身份审核', null, '0', '/manage/kyc/', '0', '100025', '1516677716421');\n");
		return sb.toString();
	}

	static String buildCreateUserRoleSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"INSERT INTO user_role_ref(userid,roleid,id,createdAt) VALUES ('100000', '100000', '8', '1516931054495');\n");
		sb.append(
				"INSERT INTO user_role_ref(userid,roleid,id,createdAt) VALUES ('100003', '100004', '9', '1516932861061');\n");
		return sb.toString();
	}

	static String buildCreateSysUserSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"INSERT INTO user_statisic(userName,passwd,id,createdAt,updatedAt,version) VALUES ('admin', '8d0e2e9d957327aaef72ec23de48441b', '100000', '1516932750715', '1516775241082', '2');\n");
		sb.append(
				"INSERT INTO user_statisic(userName,passwd,id,createdAt,updatedAt,version) VALUES ('业务员', '8d0e2e9d957327aaef72ec23de48441b', '100003', '1516932750715', '1516932750715', '0');\n");
		return sb.toString();
	}

	static String buildCreateRoleMenuSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100013', '100000', '100058', '1516930879119');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100014', '100000', '100059', '1516930879768');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100015', '100000', '100060', '1516930880339');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100002', '100004', '100064', '1516931089769');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100003', '100004', '100065', '1516931090627');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100004', '100004', '100066', '1516931091527');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100005', '100004', '100067', '1516931093392');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100006', '100004', '100068', '1516931095419');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100007', '100004', '100069', '1516931096427');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100008', '100004', '100070', '1516931097634');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100009', '100004', '100071', '1516931098825');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100010', '100004', '100072', '1516931100646');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100011', '100004', '100073', '1516931106878');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100012', '100004', '100074', '1516931108537');\n");
		sb.append(
				"INSERT INTO role_menu_ref(menuid,roleid,id,createdAt) VALUES ('100029', '100004', '100075', '1516931110745');\n");
		return sb.toString();
	}

	static String buildCreateRoleSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"INSERT INTO role_statisic(name,sorder,id,createdAt) VALUES ('管理员', null, '100000', '1516930986949');\n");
		sb.append(
				"INSERT INTO role_statisic(name,sorder,id,createdAt) VALUES ('业务员', null, '100004', '1516931077418');\n");
		return sb.toString();
	}

	static String buildCreateParamConfigSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('verifyUser', '17708510119,18515169896','','TRUE', '100000', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('verifyTimeOut', '60','','TRUE', '100001', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('verifyIntervalTime', '5','','TRUE', '100002', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('verifyCount', '5','','TRUE', '100003', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('feeCollUserId', '9be6cdef2169c345ca81dd486ebce552','手续费归集账户','TRUE', '100004', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('systemId', '103','系统转账账户Id','TRUE', '100005', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('systemEmail', 'asset@btcdo.com','系统转账账户邮箱','TRUE', '100006', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('verifyOpen', 'TRUE','验证码开关','TRUE', '100007', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('importFile', 'D:/temp/excel/','导入的excel存放地址','TRUE', '100008', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('PropertyToUserModel', 'D:/temp/PropertyToUser.xlsx','资产给用户的模板存放地址','TRUE', '100009', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('userToPropertyModel', 'D:/temp/userToProperty.xlsx','用户给资产的模板存放地址','TRUE', '100010', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('userToUserModel', 'D:/temp/userToUser.xlsx','用户给用户的模板存放地址','TRUE', '100011', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('registerActivityDate', '* * * * 2 ? 2018','注册送币有效时间区间','TRUE', '100012', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('invitationActivityDate', '* * * * 2 ? 2018','邀请好友送币有效时间区间','TRUE', '100013', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('firstDay', '2018-01-05','初始化日期','TRUE', '100014', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('bridgeCurrencyKey', 'ETH','桥接币','TRUE', '100015', '1516930986949', '1516930986949',0);\n");
		sb.append(
				"INSERT INTO param_config(configKey,configValue,configText,status,id,createdAt,updatedAt,version) VALUES ('sharePercentKey', '0.3','分润比例','TRUE', '100016', '1516930986949', '1516930986949',0);\n");
		return sb.toString();
	}

	static String buildCreateEnumValueSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('activityType', '用户-->资产', 'userToProperty', 'TRUE', '1', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('activityType', '资产-->用户', 'PropertyToUser', 'TRUE', '2', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('activityType', '用户-->用户', 'userToUser', 'TRUE', '3', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('userToProperty', '回冲订单（用户——>资产账户）', 'REVERSE_BALANCE', 'TRUE', '1', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('userToUser', '客户互转', 'BETWEEN_CUST', 'TRUE', '1', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('PropertyToUser', '认证奖励', 'CERT_AWARD', 'TRUE', '2', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('PropertyToUser', '社区邀请奖励', 'INVIT_AWARD', 'TRUE', '3', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('PropertyToUser', '早鸟私募', 'EARLY_AWARD', 'TRUE', '4', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('PropertyToUser', '内测竞猜奖励', 'INTEST_GUESS', 'TRUE', '5', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('PropertyToUser', '内测排行榜奖励', 'INTEST_TOP', 'TRUE', '6', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('PropertyToUser', '补发订单', 'RE_GRANT', 'TRUE', '7', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('userToProperty', '项目方资产充值', 'PROJECT_RECHANGE', 'TRUE', '8', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('PropertyToUser', '发放ICC', 'GRANT_ICC', 'TRUE', '9', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('PropertyToUser', '用户奖赏', 'ASSET_REWARD', 'TRUE', '10', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('PropertyToUser', 'ELF交易活动奖励', 'ELF_TRANSACATION_REWAR', 'TRUE', '11', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO enumvalue (enumName, enumKey, enumValue, status, sortsId,  createdAt, updatedAt, version) VALUES ('PropertyToUser', 'ELF充值活动奖励', 'ELF_DEPOSIT_REWARD', 'TRUE', '12', '1516930986949', '1516930986949', '0');\n");
		return sb.toString();
	}

	static String buildCreateNoticeColumnSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"INSERT INTO notice_language_page (languageName, languageValue, status, sortsId,createdAt,updatedAt,version) VALUES ('中文', 'chinese', 'TRUE', '1', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO notice_language_page (languageName, languageValue, status, sortsId,createdAt,updatedAt,version) VALUES ('英文', 'English', 'TRUE', '2', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO notice_language_page (languageName, languageValue, status, sortsId,createdAt,updatedAt,version) VALUES ('加拿大', 'canada', 'TRUE', '3', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO notice_column (columnName,columnValue,status,sortsId,createdAt,updatedAt,version) VALUES ('上币信息', 'InformationOnCurrency', 'TRUE', '1', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO notice_column (columnName,columnValue,status,sortsId,createdAt,updatedAt,version) VALUES ('活动通知', 'eventNotification', 'TRUE', '2', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO notice_column (columnName,columnValue,status,sortsId,createdAt,updatedAt,version) VALUES ('奖励发放', 'InformationOnCurrency', 'TRUE', '3', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO notice_column (columnName,columnValue,status,sortsId,createdAt,updatedAt,version) VALUES ('系统维护', 'systemMaintenance', 'TRUE', '4', '1516930986949', '1516930986949', '0');\n");
		sb.append(
				"INSERT INTO notice_column (columnName,columnValue,status,sortsId,createdAt,updatedAt,version) VALUES ('平台信息', 'platformInformatiom', 'TRUE', '5', '1516930986949', '1516930986949', '0');\n");
		return sb.toString();
	}
}
