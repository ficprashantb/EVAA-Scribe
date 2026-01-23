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

public class UtilHelper {

	//	UtilHelper.getClipboardTextAsync()
	//	.thenAccept { text ->
	//		println "Clipboard: $text"
	//	}
	
	static String randomString(int maxLen = 5) {
		return RandomStringUtils.randomAlphanumeric(maxLen)
	}

	static CompletableFuture<String> getClipboardTextAsync() {
		return CompletableFuture.supplyAsync({
			String clipboardText = ""
			try {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
				if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
					clipboardText = clipboard.getData(DataFlavor.stringFlavor)
				}
			} catch (Exception e) {
				e.printStackTrace()
			}
			return clipboardText
		})
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
 