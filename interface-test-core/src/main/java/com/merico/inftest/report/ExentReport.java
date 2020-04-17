package com.merico.inftest.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.util.*;

public class ExentReport implements IReporter {

    private static final String OUT_PATH = "test-out/";

    private static final String REPORT_NAME = "extent.html";

    private static ExtentReports extentReports;

    static {
        init();
    }

    private static void init() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("extent.html");
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setDocumentTitle("Wa oh!");
        htmlReporter.config().setReportName("测试报告");
        extentReports = new ExtentReports();
        extentReports.attachReporter(htmlReporter);

    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {

        for (ISuite iSuite : suites) {
            Map<String, ISuiteResult> results = iSuite.getResults();

            for (ISuiteResult iSuiteResult : results.values()) {
                ITestContext testContext = iSuiteResult.getTestContext();
                ExtentTest parent = extentReports.createTest(testContext.getName());
                buildrrReport(testContext.getFailedTests(), Status.FAIL,parent);
                buildrrReport(testContext.getSkippedTests(), Status.SKIP,parent);
                buildrrReport(testContext.getPassedTests(), Status.PASS,parent);
                parent.getModel().setStartTime(testContext.getStartDate());
                parent.getModel().setEndTime(testContext.getEndDate());
            }
        }

        extentReports.setTestRunnerOutput(Reporter.getOutput());

        extentReports.flush();
    }

    private void buildrrReport(IResultMap iResultMap, Status status, ExtentTest parent) {
        Set<ITestResult> allResults = iResultMap.getAllResults();
        ExtentTest extentTest;
        for (ITestResult result : allResults) {
            extentTest = parent.createNode(result.getName());

            if (result.getThrowable() != null) {
                extentTest.log(status,result.getThrowable());
            } else {
                extentTest.log(status, result.getName() + " " + status.name());
            }

            extentTest.getModel().setStartTime(getTime(result.getStartMillis()));
            extentTest.getModel().setEndTime(getTime(result.getEndMillis()));
        }

    }

    private Date getTime(long startMillis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(startMillis);

        return instance.getTime();
    }
}
