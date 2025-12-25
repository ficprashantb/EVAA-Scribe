 

import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI


import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext


class NewTestListener {
 
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
  
	}

	/*
	 * Executes after every test case ends.
	 * @param testCaseContext related information of the executed test case.
	 */
	@AfterTestCase
	def afterTestCase(TestCaseContext testCaseContext) {
		println testCaseContext.getTestCaseId()
		println testCaseContext.getTestCaseStatus()

		if (testCaseContext.getTestCaseStatus() == 'FAILED' ||  testCaseContext.getTestCaseStatus() == 'ERROR') {

			String testCaseName = testCaseContext.getTestCaseId()
					.replaceAll('[^a-zA-Z0-9_]', '_')

			CustomKeywords.'steps.CommonSteps.takeScreenshots'(testCaseName)
		}
 
 
		WebUI.closeBrowser()
	}

	/*
	 * Executes before every test suite starts.
	 * @param testSuiteContext: related information of the executed test suite.
	 */
	@BeforeTestSuite
	def sampleBeforeTestSuite(TestSuiteContext testSuiteContext) {
		println testSuiteContext.getTestSuiteId()
	}
	/*
	 * Executes after every test suite ends.
	 * @param testSuiteContext: related information of the executed test suite.
	 */
	@AfterTestSuite
	def sampleAfterTestSuite(TestSuiteContext testSuiteContext) {
		println testSuiteContext.getTestSuiteId()
	}
}