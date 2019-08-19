package com.itranswarp.crypto.manage.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.itranswarp.crypto.common.exception.ManageException;
import com.itranswarp.crypto.encrypt.EncryptService;
import com.itranswarp.crypto.store.model.CurrencyEntity;
import com.itranswarp.crypto.store.model.SymbolEntity;
import com.itranswarp.crypto.symbol.Currency;
import com.itranswarp.crypto.symbol.Symbol;
import com.itranswarp.crypto.util.JsonUtil;
import com.itranswarp.crypto.util.SequenceUtil;

/**
 * Special global service to load all currencies and symbols.
 *
 * @author liaoxuefeng
 */
public abstract class ValidateSymbolLoader {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	EncryptService encryptService;


	public static Map<String, CurrencyEntity> validateCurrencies(List<CurrencyEntity> ceList) {
		// load all currencies as map:
		Map<String, CurrencyEntity> ceMap = new HashMap<>();
		ceList.forEach(c -> {
			if (ceMap.put(c.name, c) != null) {
				throw new ManageException("币种重复："+c.name);
				//throw new IllegalArgumentException("Duplicate currency name: " + c.name);
			}
		});
		validateCurrency(ceMap);
		validateToken(ceMap);
		return ceMap;
	}

	public static Map<String, SymbolEntity> validateSymbols(List<CurrencyEntity> ceList, List<SymbolEntity> seList) {
		Map<String, CurrencyEntity> ceMap = validateCurrencies(ceList);
		// load all symbols as map:
		Map<String, SymbolEntity> seMap = new HashMap<>();
		seList.forEach(s -> {
			String name = s.baseName + "_" + s.quoteName;
			if (seMap.put(name, s) != null) {
				//throw new IllegalArgumentException("Duplicate symbol name: " + name);
				throw new IllegalArgumentException("该币对已经存在： " + name);
			}
		});
		validateSymbol(seMap, ceMap);
		return seMap;
	}


	// validate symbol ////////////////////////////////////////////////////////

	static void validateSymbol(Map<String, SymbolEntity> map, Map<String, CurrencyEntity> cMap) {
		map.values().forEach(se -> validateSymbol(se, cMap));
	}

	static void validateSymbol(SymbolEntity se, Map<String, CurrencyEntity> cMap) {
		if (se.baseName.equals(se.quoteName)) {
			//throw new IllegalArgumentException("Invalid baseName: " + se.baseName + " and quoteName: " + se.quoteName);
			throw new IllegalArgumentException("交易币种和计价币种不能相同!");
		}
		if (cMap.get(se.baseName) == null) {
			//throw new IllegalArgumentException("Invalid baseName: " + se.baseName);
			throw new IllegalArgumentException("交易币种不存在");
		}
		if (cMap.get(se.quoteName) == null) {
			//throw new IllegalArgumentException("Invalid quoteName: " + se.quoteName);
			throw new IllegalArgumentException("计价币种不存在");
		}
		if (se.baseScale < 0 || se.baseScale > 10) {
			//throw new IllegalArgumentException("Invalid baseScale: " + se.baseScale);
			throw new IllegalArgumentException("交易币种精度应该在0~10中的整数");
		}
		if (se.quoteScale < 0 || se.quoteScale > 10) {
			//throw new IllegalArgumentException("Invalid quoteScale: " + se.quoteScale);
			throw new IllegalArgumentException("计价币种精度应该在0~10中的整数");
		}
		if (se.baseScale + se.quoteScale > 12) {
//			throw new IllegalArgumentException(
//					"Invalid scale of baseScale+quoteScale: " + (se.baseScale + se.quoteScale));
			throw new IllegalArgumentException("交易币种精度+计价币种精度之和不能大于12");
		}
		if (se.sequenceIndex < 0 || se.sequenceIndex >= SequenceUtil.SEQUENCE_SLOTS) {
//			throw new IllegalArgumentException("Invalid sequenceIndex: " + se.sequenceIndex + ", must between 0 ~ "
//					+ (SequenceUtil.SEQUENCE_SLOTS - 1));
			throw new IllegalArgumentException("定序数据库表序号   应该在 0 ~ "+ (SequenceUtil.SEQUENCE_SLOTS - 1));
		}
		
		if (se.quoteMinimum ==null) {
			throw new IllegalArgumentException("最小成交额（计价币种）不能为空");
		}
		checkMinimum(se.baseMinimum, se.baseScale);
		checkMinimum(se.quoteMinimum, se.quoteScale);
		// check time:
		if (se.startTime < 0) {
			throw new IllegalArgumentException("Invalid startTime: " + se.startTime);
		}
		if (se.endTime < 0) {
			throw new IllegalArgumentException("Invalid endTime: " + se.endTime);
		}
		if (se.endTime > 0 && se.endTime <= se.startTime) {
			throw new IllegalArgumentException(
					"Invalid endTime: " + se.endTime + " less than startTime: " + se.startTime);
		}
		checkMeta(se.meta);
	}

	static void checkMinimum(BigDecimal value, int scale) {
		BigDecimal defaultValue = minimum(scale);
		if (value != null&&value.setScale(scale, RoundingMode.DOWN).compareTo(value) != 0) {
			//throw new IllegalArgumentException("Invalid minimum value: " + value + ", scale is greater than " + scale);
			throw new IllegalArgumentException("最小成交额（计价币种）或计价精度无效，精度应该大于"+scale);
		}
		if (value != null&&value.compareTo(defaultValue) < 0) {
			throw new IllegalArgumentException(
					"Invalid minimum value: " + value + ", must be equal or greater than " + defaultValue);
		}
	}

	static BigDecimal minimum(int scale) {
		if (scale == 0) {
			return BigDecimal.ONE;
		}
		BigDecimal value = BigDecimal.ONE;
		for (int i = 0; i < scale; i++) {
			value = value.divide(BigDecimal.TEN);
		}
		return value;
	}

	// validate currency //////////////////////////////////////////////////////

	static void validateCurrency(Map<String, CurrencyEntity> map) {
		map.values().forEach(ce -> validate(ce));
	}

	static void validate(CurrencyEntity ce) {
		// check name:
		Matcher m = CURRENCY_NAME.matcher(ce.name);
		if (!m.matches()) {
			throw new ManageException("请输入正确格式的币种名字："+ce.name);
			//throw new IllegalArgumentException("Invalid currency name: " + ce.name);
		}
		// check token settings if currency is a token:
		if (ce.token) {
			// token must be virtual:
			if (ce.legal) {
				throw new ManageException("token类型是虚拟代币应该是非法币，币种名字："+ce.name);
				//throw new IllegalArgumentException("Token " + ce.name + " should be virtual currency.");
			}
			// check token address-alias-to must be exist:
			if (ce.addressAliasTo==null||ce.addressAliasTo.isEmpty()) {
				throw new ManageException("token类型是虚拟代币时，属于地址不能为空，币种名字："+ce.name);
				//throw new IllegalArgumentException("addressAliasTo of token " + ce.name + " must not be empty.");
			}
			// check token-issues-on:
			if (ce.tokenIssuesOn==null||ce.tokenIssuesOn.isEmpty()) {
				throw new ManageException("token类型是虚拟代币时，属于公链不能为空，币种名字："+ce.name);
				//throw new IllegalArgumentException("tokenIssuesOn of token " + ce.name + " must not be empty.");
			}
			// check token-contract-address:
			if (ce.tokenContractAddress==null||ce.tokenContractAddress.isEmpty()) {
				throw new ManageException("token类型是虚拟代币时，合约地址不能为空，币种名字："+ce.name);
				//throw new IllegalArgumentException("tokenContractAddress of token " + ce.name + " must not be null.");
			}
			Pattern pattern = TOKEN_CONTRACT_ADDRESS_PATTERNS.get(ce.tokenIssuesOn);
			if (pattern == null) {
				throw new ManageException("属于公链不能校验通过，合约地址上没有找到公链，币种名字："+ce.name);
//				throw new IllegalArgumentException("Cannot validate tokenContractAddress of token " + ce.name
//						+ " because pattern for " + ce.tokenIssuesOn + " is not found.");
			}
			if (!pattern.matcher(ce.tokenContractAddress).matches()) {
				throw new ManageException("合约地址上不匹配，币种名字："+ce.name);
//				throw new IllegalArgumentException("tokenContractAddress of token " + ce.name
//						+ " is invalid because pattern not match: " + pattern.pattern());
			}
			// check token-decimals:
			if (ce.tokenDecimals < 0 || ce.tokenDecimals > 36) {
				throw new ManageException("合约精度应该在0 ~ 36中的整数，币种名字："+ce.name);
				//throw new IllegalArgumentException("tokenDecimals of token " + ce.name + " should between 0 ~ 36.");
			}
		}
		// check xpubKey:
		String xpubKey = ce.getXpubKey();
		if (!xpubKey.isEmpty()) {
			if (!(xpubKey.startsWith("xpub") || xpubKey.startsWith("tpub"))) {
				throw new ManageException("无效的公钥，公钥应该以'xpub'或者'tpub'开头");
				//throw new IllegalArgumentException("Invalid pubKey. MUST be start with 'xpub' or 'tpub'.");
			}
			if (!(ce.addressAliasTo==null||ce.addressAliasTo.isEmpty())) {
				throw new ManageException("当有公钥时，属于地址应该为空");
				//throw new IllegalArgumentException("Currency which has xpubKey must NOT have addressAliasTo.");
			}
			if (ce.token) {
				throw new ManageException("当有公钥时，token类型不能是链");
				//throw new IllegalArgumentException("Currency which has xpubKey must NOT be a token.");
			}
		}
		// check meta:
		checkMeta(ce.meta);
	}

	static Map<String, String> checkMeta(String meta) {
		if (meta==null||meta.isEmpty()) {
			return new HashMap<>();
		}
		return JsonUtil.readJson(meta, MAP_STRING_STRING);
	}

	static void validateToken(Map<String, CurrencyEntity> map) {
		// check addressAliasTo and tokenIssuesOn:
		map.values().stream().forEach(ce -> {
			if (!(ce.addressAliasTo==null||ce.addressAliasTo.isEmpty())) {
				CurrencyEntity baseC = map.get(ce.addressAliasTo);
				if (baseC == null) {
					throw new ManageException("属于地址必须存在该币种");
//					throw new IllegalArgumentException(
//							"Cannot find alias by addressAliasTo: " + ce.addressAliasTo + " in currency: " + ce.name);
				}
				if (!(baseC.addressAliasTo==null||baseC.addressAliasTo.isEmpty())) {
					throw new ManageException("属于地址币种的属于地址必须为空");
//					throw new IllegalArgumentException("Invalid addressAliasTo: " + ce.addressAliasTo + " in currency: "
//							+ ce.name + ", for addressAliasTo of base currency " + baseC.name + " must be empty.");
				}
			}
			if (!(ce.tokenIssuesOn==null||ce.tokenIssuesOn.isEmpty())) {
				CurrencyEntity baseC = map.get(ce.tokenIssuesOn);
				if (baseC == null) {
					throw new ManageException("属于公链必须存在该币种");
					//throw new IllegalArgumentException("Cannot find currency by tokenIssuesOn: " + ce.tokenIssuesOn + " for currency " + ce.name);
				}
			}
		});
	}

	private static final Pattern CURRENCY_NAME = Pattern.compile("^[A-Z0-9][A-Z0-9]{1,9}$");

	private static final Map<String, Pattern> TOKEN_CONTRACT_ADDRESS_PATTERNS = initTokenContractAddressPatterns();

	private static final TypeReference<Map<String, String>> MAP_STRING_STRING = new TypeReference<Map<String, String>>() {
	};

	private static Map<String, Pattern> initTokenContractAddressPatterns() {
		Map<String, Pattern> patterns = new HashMap<>();
		Pattern ethContract = Pattern.compile("^0x[a-f0-9]{40}$");
		// token issues on ETH:
		patterns.put("ETH", ethContract);
		// token issues on ETC:
		patterns.put("ETC", ethContract);
		// token issues on QTUM:
		patterns.put("QTUM", Pattern.compile("^[a-f0-9]{40}$"));
		// token issues on ACT:
		patterns.put("ACT", Pattern.compile("^CON[A-Za-z0-9]{32,33}$"));
		// token issues on OMNI:
		patterns.put("OMNI", Pattern.compile("^[0-9]*$"));

		// token issues on NEO:
		patterns.put("NEO", Pattern.compile("^0x[0-9a-zA-Z]{40,64}$"));
		// token issues on EOSIO:
		patterns.put("EOSIO", Pattern.compile("^(?=^[1-5.a-z]{1,12}$)(([1-5a-z]{1,}[.]{1}[1-5a-z]{1,})|([1-5a-z]{1,12}))$"));
		// token issues on EOSFORCEIO:
		patterns.put("EOSFORCEIO", Pattern.compile("^(?=^[1-5.a-z]{1,12}$)(([1-5a-z]{1,}[.]{1}[1-5a-z]{1,})|([1-5a-z]{1,12}))$"));
		// token issues on IOST:
		patterns.put("IOSTIO", Pattern.compile("^[0-9.a-zA-Z]*$"));
		return Collections.unmodifiableMap(patterns);
	}
}
