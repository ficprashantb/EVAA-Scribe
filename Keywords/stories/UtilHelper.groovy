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

	//	UtilHelper.getClipboardTextAsync()
	//	.thenAccept { text ->
	//		println "Clipboard: $text"
	//	}

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
		def clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
		def data = clipboard.getData(DataFlavor.stringFlavor)
		return data?.toString()
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
	static List<String> getSelectedLabels(String input,List<String> wantedList) {

		// If wanted is null or empty, assign an empty list
		List<String> wanted = (wantedList == null || wantedList.isEmpty()) ? [] : wantedList
		
		def lines = input.readLines()
		List<String> labels = []
		boolean seenDifferential = false
		boolean planAdded = false

		lines.each { line ->
			def matcher = (line =~ /^([A-Za-z ]+):$/)
			if (matcher.matches()) {
				String _label = matcher[0][1].trim()
				String label = "${_label}:"
			 
				if (_label == "Differential Diagnosis") {
					seenDifferential = true
					if (wanted.contains(label)) labels << label
				} else if (_label == "Assessment" && !seenDifferential) {
					// skip Assessment before Differential Diagnosis
				} else if (_label == "Plan") {
					if (!planAdded) {
						labels << label
						planAdded = true
					}
				} else if (wanted.contains(label)) {
					labels << label
				}
			}
		}

		return labels
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
