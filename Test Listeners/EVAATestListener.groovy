

import internal.GlobalVariable
import stories.LogStories
import stories.UtilHelper

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite

import org.openqa.selenium.remote.DesiredCapabilities

import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.configuration.RunConfiguration

class EVAATestListener {
	/*
	 * Executes before every test case starts.
	 * @param testCaseContext related information of the executed test case.
	 */
	@BeforeTestCase
	def beforeTestCase(TestCaseContext testCaseContext) {

		UtilHelper.setGlobalVariables(testCaseContext)

		// Define capabilities before browser launch
		Keywords_DesiredCapabilities.addCapabilities()

		CustomKeywords.'steps.CommonSteps.openEVAAScribeBrowser'()
	}

	/*
	 * Executes after every test case ends.
	 * @param testCaseContext related information of the executed test case.
	 */
	@AfterTestCase
	def afterTestCase(TestCaseContext testCaseContext) {
		// Full test case ID (includes folder path)
		String fullId = testCaseContext.getTestCaseId()

		// Just the test case name (strip path)
		String testCaseName = fullId.substring(fullId.lastIndexOf("/") + 1)

		LogStories.logInfo("➡️➡️➡️➡️➡️➡️➡️➡️➡️ Completed Test Case: " + testCaseName)

		if (testCaseContext.getTestCaseStatus() != 'PASS') {

			CustomKeywords.'steps.CommonSteps.takeScreenshots'()
		}

		//		LogStories.sendNotification("${testCaseId} is completed.")

		WebUI.closeBrowser()
	}

	/*
	 * Executes before every test suite starts.
	 * @param testSuiteContext: related information of the executed test suite.
	 */
	@BeforeTestSuite
	def beforeTestSuite(TestSuiteContext testSuiteContext) {
		println testSuiteContext.getTestSuiteId()
		Locale.setDefault(new Locale("en", "US"));
	}
	/*
	 * Executes after every test suite ends.
	 * @param testSuiteContext: related information of the executed test suite.
	 */
	@AfterTestSuite
	def afterTestSuite(TestSuiteContext testSuiteContext) {
		println testSuiteContext.getTestSuiteId()
	}
}