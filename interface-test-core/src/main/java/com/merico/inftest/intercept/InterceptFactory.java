package com.merico.inftest.intercept;

import com.google.common.collect.Lists;
import com.merico.inftest.cases.StepCaseInfo;
import com.merico.inftest.response.Response;

import java.util.List;

public class InterceptFactory {

	// 拦截器工厂类

	private static List<Interceptor> interceptorList = Lists.newArrayList();

	private static InterceptFactory interceptFactory = new InterceptFactory();

	static {
		// 内部实现的Intercept请在此注册
		interceptorList.add(new HostnameInterceptor());
		interceptorList.add(new ParamVariableInterceptor());

	}

	private InterceptFactory() {

	}

	public static void registerIntercept(Interceptor interceptor) {
		interceptorList.add(interceptor);
	}

	public static void beforStep(StepCaseInfo testCaseInfo, Response response) {
		interceptorList.forEach(interceptor -> interceptor.beforeStep(testCaseInfo, response));
	}

	public static void afterStep(StepCaseInfo testCaseInfo, Response response) {
		interceptorList.forEach(interceptor -> interceptor.afterStep(testCaseInfo, response));
	}

	public static InterceptFactory getInterceptFactory() {
		return interceptFactory;
	}
}
