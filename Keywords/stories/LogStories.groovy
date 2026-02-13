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
import com.networknt.schema.OutputFormat.Boolean

import internal.GlobalVariable
import com.kms.katalon.core.util.KeywordUtil

public class LogStories {


	static void sendNotification(def text) {
		text = "${text} "
		boolean isCloud = UtilHelper.isCloud()

		if(!isCloud) {
			UtilHelper.sendWindowsNotification("", text)
		}
	}

	static void logInfo(def text) {
		KeywordUtil.logInfo("⏩ ${text}")
	}

	static void markPassed(def text) {
		KeywordUtil.markPassed("✅ ${text}")
	}

	static void markError(def text) {
		KeywordUtil.markError("❌ ${text}")
	}

	static void markErrorAndStop(def text) {
		KeywordUtil.markErrorAndStop("❌ ${text}")
	}

	static void markFailed(def text) {
		KeywordUtil.markFailed("❌ ${text}")
	}

	static void markFailedAndStop(def text) {
		KeywordUtil.markFailedAndStop("❌ ${text}")
	}

	static void markWarning(def text) {
		KeywordUtil.markWarning("⚠️ ${text}")
	}
}
