package stories

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import internal.GlobalVariable

public class WaitStory {

	 
	def waitForElementText(TestObject to, int timeoutInSeconds) {
		int elapsed = 0
		int interval = 1  // check every 1 second

		while (elapsed < timeoutInSeconds) {
			if (WebUI.waitForElementVisible(to, 1, FailureHandling.OPTIONAL)) {
				String text = WebUI.getText(to).trim()
				if (text && text.length() > 0) {
					return text
				}
			}
			WebUI.delay(interval)
			elapsed += interval
		}
		WebUI.comment("Timeout waiting for text in element: ${to.getObjectId()}")
		return null
	}

	
}
