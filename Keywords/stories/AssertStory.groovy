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

public class AssertStory {

	def verifyElementVisible(TestObject testObject, int timeOut = 5) {
		def result = Math.round(timeOut / 2)

		def xpath = testObject.findPropertyValue('xpath')

		// 1️⃣ Wait for presence first
		boolean present = WebUI.waitForElementPresent(testObject, result, FailureHandling.OPTIONAL)

		if (!present) {
			LogStories.markFailed("$xpath → Element NOT PRESENT in DOM")
			return
		}

		// 2️⃣ Wait for visibility
		boolean visible = WebUI.waitForElementVisible(testObject, result, FailureHandling.OPTIONAL)

		if (visible) {
			LogStories.markPassed("$xpath → Element is VISIBLE ✔")
			return
		}

		// 3️⃣ Present but not visible
		LogStories.markFailed("$xpath → Element PRESENT but NOT visible ❌")
	}

	def verifyElementNotVisible(TestObject testObject, int timeOut = 5) {

		def result = Math.round(timeOut / 2)

		def xpath = testObject.findPropertyValue('xpath')

		try {

			boolean invisible = WebUI.waitForElementNotVisible(testObject, result, FailureHandling.OPTIONAL)
			boolean notPresent = WebUI.waitForElementNotPresent(testObject, result, FailureHandling.OPTIONAL)

			if (invisible || notPresent) {
				LogStories.markPassed("$xpath → Element is not visible / not present")
				return
			}

			LogStories.markFailed("$xpath → Still visible")
		}
		catch(Exception ignore){
			LogStories.markFailed("$xpath → Unexpected failure")
			throw ignore
		}
	}


	def verifyElementEnabled(TestObject testObj) {
		def isEnabled = WebUI.findWebElement(testObj, 5, FailureHandling.OPTIONAL).isEnabled()
		WebUI.verifyMatch(isEnabled, true, false,FailureHandling.CONTINUE_ON_FAILURE)
	}

	def verifyElementDisabled(TestObject testObj) {
		def isEnabled = WebUI.findWebElement(testObj, 5, FailureHandling.OPTIONAL).isEnabled()
		WebUI.verifyMatch(isEnabled, false, false,FailureHandling.CONTINUE_ON_FAILURE)
	}

	def verifyMatch (String text, def actual, def expected) {

		LogStories.logInfo("${text} → Actual: ${actual} | Expected: ${expected}")

		try {
			WebUI.verifyMatch(actual?.toString(), expected?.toString(), false,FailureHandling.CONTINUE_ON_FAILURE)
			LogStories.markPassed("${text} → PASSED => ${actual}")
		}
		catch(Exception err) {
			LogStories.markFailed("${text} FAILED → Actual: ${actual} | Expected: ${expected}")
			throw err
		}
	}
	
	def verifyNotMatch (String text, def actual, def expected) {
		
				LogStories.logInfo("${text} → Actual: ${actual} | Expected: ${expected}")
		
				try {
					WebUI.verifyNotMatch(actual?.toString(), expected?.toString(), false,FailureHandling.CONTINUE_ON_FAILURE)
					LogStories.markPassed("${text} → PASSED => ${actual}")
				}
				catch(Exception err) {
					LogStories.markFailed("${text} FAILED → Actual: ${actual} | Expected: ${expected}")
					throw err
				}
			}

	def verifyContainsRegex(String text, def actual, def expected) {
		LogStories.logInfo("${text} → Actual: ${actual} | Expected to contain (regex): ${expected}")

		try {
			// Regex pattern: .*expected.*
			String regexPattern = ".*" + java.util.regex.Pattern.quote(expected?.toString()) + ".*"

			WebUI.verifyMatch(actual?.toString(), regexPattern, true, FailureHandling.CONTINUE_ON_FAILURE)
			LogStories.markPassed("${text} → PASSED => '${actual}' contains '${expected}' (regex)")
		} catch (Exception err) {
			LogStories.markFailed("${text} FAILED → Actual: ${actual} | Expected to contain: ${expected}")
			throw err
		}
	}

	def verifyGreaterThanOrEqual(String text, def actual, def expected) {

		// normalise values to numbers
		def a = actual instanceof Number ? actual : actual.toString().toBigDecimal()
		def e = expected instanceof Number ? expected : expected.toString().toBigDecimal()

		LogStories.logInfo("${text} → Actual: ${a} | Expected >= ${e}")

		try {
			WebUI.verifyGreaterThanOrEqual(a, e,FailureHandling.CONTINUE_ON_FAILURE)
			LogStories.markPassed("${text} → Passed: ${a} >= ${e}")
		} catch (Exception err) {
			LogStories.markFailed("${text} → FAILED: ${a} < ${e}")
			throw err
		}
	}

	def verifyGreaterThan(String text, def actual, def expected) {

		// normalise values to numbers
		def a = actual instanceof Number ? actual : actual.toString().toBigDecimal()
		def e = expected instanceof Number ? expected : expected.toString().toBigDecimal()

		LogStories.logInfo("${text} → Actual: ${a} | Expected >= ${e}")

		try {
			WebUI.verifyGreaterThan(a, e,FailureHandling.CONTINUE_ON_FAILURE)
			LogStories.markPassed("${text} → Passed: ${a} > ${e}")
		} catch (Exception err) {
			LogStories.markFailed("${text} → FAILED: ${a} < ${e}")
			throw err
		}
	}
}
