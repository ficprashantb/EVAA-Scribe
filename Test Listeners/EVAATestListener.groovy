

import internal.GlobalVariable
import stories.LogStories
import stories.UtilHelper
import stories.VideoRecorderHelper

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI


import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.helper.screenrecorder.VideoRecorder
 

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

		//		WebUI.openBrowser('')

		'Maximize the window'
		WebUI.maximizeWindow()
		
		GlobalVariable.IS_ENCOUNTER_ID = false
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
			String ssName = UtilHelper.randomString()

			LogStories.logInfo("TestCase Name: $testCaseId")
			LogStories.logInfo("Screenshot: $ssName")

			CustomKeywords.'steps.CommonSteps.takeScreenshots'(ssName)
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