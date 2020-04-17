package com.merico.inftest.intercept;

import com.merico.inftest.cases.StepCaseInfo;
import com.merico.inftest.response.Response;

public interface Interceptor {

	Object beforeStep(StepCaseInfo caseInfo, Response response);

	Object afterStep(StepCaseInfo caseInfo, Response response);
}
