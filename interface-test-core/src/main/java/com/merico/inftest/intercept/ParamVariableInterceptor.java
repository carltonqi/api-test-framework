package com.merico.inftest.intercept;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.merico.inftest.cases.RequestInfo;
import com.merico.inftest.cases.RequestInfoAndAssert;
import com.merico.inftest.cases.StepCaseInfo;
import com.merico.inftest.cases.TestCaseInfoNew;
import com.merico.inftest.response.Response;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamVariableInterceptor implements Interceptor {

	// http://www.baidu.com/blog/{bloger}/{year}/{month}/{day}/{id}.json
	private final static Pattern pattern = Pattern.compile("\\{([A-Za-z]+)\\}");

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
			RequestInfo requestInfo = requestInfoAndAssert.getRequestInfo();
			if (null == requestInfo) {
				continue;
			}
			String url = requestInfo.getUrl();
			Map<String, Object> params = requestInfo.getParams();
			if (null == params || params.isEmpty()) {
				continue;
			}

			List<String> paramWithUrl = getParamWithUrl(url);
			for (String varName : paramWithUrl) {
				String value = params.get(varName) == null ? null : String.valueOf(params.get(varName));
				if (StringUtils.isBlank(value)) {
					continue;
				}
				url = replaceRestfulParam(url, varName, value);
				// 替换后将对应的数据从param中移除 防止对于get请求 产生影响
				params.remove(varName);
			}

			requestInfo.setUrl(url);
		}

		return caseInfo;
	}

	private String replaceRestfulParam(String url, String varName, String value) {
		return url.replace(String.format("{%s}", varName), value);
	}

	private List<String> getParamWithUrl(String url) {
		List<String> paramList = Lists.newArrayList();
		Matcher matcher = pattern.matcher(url);
		while (matcher.find()) {
			paramList.add(matcher.group(1));
		}
		return paramList;
	}

	@Override
	public Object afterStep(StepCaseInfo caseInfo, Response response) {
		return response;
	}

}
