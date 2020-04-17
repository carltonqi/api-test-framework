package com.merico.inftest.intercept;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.merico.inftest.cases.RequestInfo;
import com.merico.inftest.cases.RequestInfoAndAssert;
import com.merico.inftest.cases.StepCaseInfo;
import com.merico.inftest.cases.TestCaseInfoNew;
import com.merico.inftest.commonutils.PropertyUtils;
import com.merico.inftest.commonutils.UrlUtils;
import com.merico.inftest.response.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HostnameInterceptor implements Interceptor {
	private static Logger logger = LoggerFactory.getLogger(HostnameInterceptor.class);

	public static String hostvariableRegex = "^\\$\\{.*\\}.*";

	@Override
	public Object beforeStep(StepCaseInfo caseInfo, Response response) {
		if (null == caseInfo) {
			return caseInfo;
		}

		List<RequestInfoAndAssert> requests = caseInfo.getRequests();
		if (null == requests || requests.size() == 0) {
			return caseInfo;
		}

		for (RequestInfoAndAssert requestInfoAndAssert : requests) {
			RequestInfo request = requestInfoAndAssert.getRequestInfo();
			if (null != requestInfoAndAssert.getLoopCheckInfo()) {
				request = requestInfoAndAssert.getLoopCheckInfo().getRequestInfo();
			}

			if (null == request) {
				continue;
			}
			String urlOrigin = request.getUrl();
			// 若未定义host变量直接返回
			if (StringUtils.isBlank(urlOrigin) || !urlOrigin.matches(hostvariableRegex)) {
				continue;
			}
			List<String> list = Splitter.on("/").splitToList(urlOrigin);
			list = Lists.newArrayList(list);
			if (list.isEmpty() || StringUtils.isBlank(list.get(0))) {
				continue;
			}
			String hostVariable = list.get(0);
			String varName = UrlUtils.getVarName(hostVariable);
			String varValue = PropertyUtils.getProperty(varName);
			if (StringUtils.isBlank(varValue)) {
				logger.error("{} not set in config.properties", varName);
				continue;
				// 对于整个URL的变量替换非host替换 直接抛出异常可能存在问题
				// throw new RuntimeException(varName + "未配置请检查 该host配置");
			}
			list.set(0, varValue);
			String url = Joiner.on("/").join(list);
			request.setUrl(url);
			logger.debug("replace host success! url: {} hostName {} host {} ", urlOrigin, varName, varValue);
		}

		return caseInfo;
	}

	@Override
	public Object afterStep(StepCaseInfo caseInfo, Response response) {
		return response;
	}

	private String replaceHost(String originUrl) {
		// 若未定义host变量直接返回
		if (StringUtils.isBlank(originUrl) || !originUrl.matches(hostvariableRegex)) {
			return originUrl;
		}
		String varName = getHostVarName(originUrl);

		String varValue = PropertyUtils.getProperty(varName);

		return replaceVar(originUrl, varValue);
	}

	private String getHostVarName(String originUrl) {
		List<String> list = Splitter.on("/").omitEmptyStrings().trimResults().splitToList(originUrl);
		list = Lists.newArrayList(list);
		if (list.isEmpty() || StringUtils.isBlank(list.get(0))) {
			return originUrl;
		}
		String hostVariable = list.get(0);
		String varName = UrlUtils.getVarName(hostVariable);
		return varName;
	}

	private String replaceVar(String originUrl, String varValue) {
		List<String> list = Splitter.on("/").omitEmptyStrings().trimResults().splitToList(originUrl);
		list = Lists.newArrayList(list);

		if (StringUtils.isBlank(varValue)) {
			logger.error("{} not set in config.properties", getHostVarName(originUrl));
			return originUrl;
		}
		list.set(0, varValue);
		String url = Joiner.on("/").join(list);
		return url;

	}
}
