

import internal.GlobalVariable
import stories.LogStories
import stories.UtilHelper

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
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
		println testCaseContext.getTestCaseId()
		println testCaseContext.getTestCaseVariables()

		PermissionManagerListener.enableBrowserPermissions()

		//	 WebUI.openBrowser('')
		
		String siteURL = GlobalVariable.EVAA_SiteURL
		WebUI.navigateToUrl(siteURL)
		LogStories.logInfo("Site URL: $siteURL")

		'Maximize the window'
		WebUI.maximizeWindow()

		GlobalVariable.IS_ENCOUNTER_ID = false

		boolean isCloud = UtilHelper.isCloud()

		if(!isCloud) {
			String screenshotPath = RunConfiguration.getProjectDir() +"/Screenshots/FAILED"

			CustomKeywords.'steps.CommonKeywords.deleteAllFiles'(screenshotPath)
		}
	}

	/*
	 * Executes after every test case ends.
	 * @param testCaseContext related information of the executed test case.
	 */
	@AfterTestCase
	def afterTestCase(TestCaseContext testCaseContext) {
		println testCaseContext.getTestCaseId()
		println testCaseContext.getTestCaseStatus()

		String testCaseId = testCaseContext.getTestCaseId()

		if (testCaseContext.getTestCaseStatus() != 'PASS') {
			LogStories.logInfo("TestCase Name: $testCaseId")

			CustomKeywords.'steps.CommonSteps.takeScreenshots'()
		}

		LogStories.sendNotification("${testCaseId} is completed.")

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