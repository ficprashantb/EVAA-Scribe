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

import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.datatransfer.*
import java.util.concurrent.CompletableFuture
import internal.GlobalVariable
import steps.CommonSteps

import com.kms.katalon.core.exception.StepFailedException
import org.monte.media.*
import org.monte.media.math.Rational
import static org.monte.media.FormatKeys.*
import static org.monte.media.VideoFormatKeys.*

import ScreenRecorder
import org.apache.commons.lang.RandomStringUtils
import java.awt.datatransfer.DataFlavor

public class UtilHelper {

	// Generic retry helper
	static <T> T retryOperation(Closure<T> operation, int maxRetries = 2, long delayMillis = 200) {
		int attempt = 0
		while (attempt < maxRetries) {
			try {
				return operation.call()
			} catch (Exception e) {
				println("***********************************Attempt ${attempt+1} failed: ${e.message}")
				Thread.sleep(delayMillis)
			}
			attempt++
		}
		println("***********************************Operation failed after ${maxRetries} attempts")
		return null
	}

	static String randomString(int maxLen = 5) {
		return RandomStringUtils.randomAlphanumeric(maxLen)
	}

	/**
	 * Removes all special characters from a string.
	 * Keeps only letters, digits, and spaces.
	 */
	static String removeSpecialChars(String input) {
		// Remove all non-alphanumeric characters except spaces
		String noSpecials = input.replaceAll("[^a-zA-Z0-9 ]", "")
		// Collapse multiple spaces into one and trim edges
		return noSpecials.replaceAll("\\s+", " ").trim()
	}

	/**
	 * Gets the text currently copied to the clipboard.
	 */
	static String getClipboardText() {
		for (int i = 0; i < 5; i++) {
			WebUI.delay(1)
			try {
				def clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
				def contents = clipboard.getContents(null)
				if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					return contents.getTransferData(DataFlavor.stringFlavor).toString()
				}
			} catch (Exception e) {
				// ignore and retry
			}
		}
		return null
	}

	static void sendWindowsNotification(String title, String message) {
		String psCommand = """
    powershell -command "
    [Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType = WindowsRuntime] > \$null;
    \$template = [Windows.UI.Notifications.ToastTemplateType]::ToastText02;
    \$xml = [Windows.UI.Notifications.ToastNotificationManager]::GetTemplateContent(\$template);
    \$textNodes = \$xml.GetElementsByTagName('text');
    \$textNodes.Item(0).AppendChild(\$xml.CreateTextNode('${title}')) > \$null;
    \$textNodes.Item(1).AppendChild(\$xml.CreateTextNode('${message}')) > \$null;
    \$toast = [Windows.UI.Notifications.ToastNotification]::new(\$xml);
    [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('Katalon').Show(\$toast);
    "
    """

		psCommand.execute()
	}

	static boolean isCloud() {
		return System.getenv("KATALON_CLOUD_URL") ||
				System.getenv("TESTOPS_URL")
	}

	/**
	 * Extracts only selected section labels from text,
	 * skipping "Assessment" if it appears before "Differential Diagnosis".
	 */
	static List<String> getSelectedLabels(String input, List<String> desiredOrder) {
		def lines = input.readLines()
		List<String> foundLabels = []
		boolean seenDifferential = false
		boolean planAdded = false

		lines.each { line ->
			// Match any word characters (including camelCase) and spaces before colon
			def matcher = (line =~ /^([\w\s]+):/)
			if (matcher.find()) {
				String label = matcher.group(1).trim() + ":"

				if (label == "Differential Diagnosis:") {
					seenDifferential = true
					foundLabels << label
				} else if (label == "Assessment:" && !seenDifferential) {
					// skip Assessment before Differential Diagnosis
				} else if (label == "Plan:") {
					if (!planAdded) {
						foundLabels << label
						planAdded = true
					}
				} else {
					foundLabels << label
				}
			}
		}

		// Return labels in the desired sequence, only if they were found

		def resultData = desiredOrder.findAll { foundLabels.contains(it) }

		return resultData
	}
}

public class ExceptionHelper {

	/**
	 * Handle exception and decide execution behavior
	 */
	static void handle(Exception e,String stepName,
			String message = 'Unexpected error occurred',
			boolean stopExecution = true) {

		String finalMessage = """
        ❌ ${message}
        🔍 Exception: ${e.getClass().getSimpleName()}
        📄 Details: ${e.message}
        """.stripIndent()

		if (stopExecution) {
			LogStories.markFailedAndStop(finalMessage)
		} else {
			LogStories.markFailed(finalMessage)
		}

		LogStories.logInfo("Screenshot: $stepName")
		CommonSteps commonSteps = new CommonSteps()
		commonSteps.takeScreenshots(stepName)
	}

	/**
	 * Safe execution wrapper
	 */
	static def execute(String stepName,
			boolean stopExecution = true,
			Closure action) {
		try {
			return action.call()
		} catch (Exception e) {
			handle(e, stepName,"Step failed: ${stepName}", stopExecution)
		}
	}
}
