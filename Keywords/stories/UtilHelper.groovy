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
import java.awt.Toolkit
import java.awt.datatransfer.*
import java.util.concurrent.CompletableFuture
import internal.GlobalVariable
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.exception.StepFailedException

public class UtilHelper {

	//	UtilHelper.getClipboardTextAsync()
	//	.thenAccept { text ->
	//		println "Clipboard: $text"
	//	}

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
}

public class ExceptionHelper {
	
		/**
		 * Handle exception and decide execution behavior
		 */
		static void handle(Exception e,
						   String message = 'Unexpected error occurred',
						   boolean stopExecution = true) {
	
			String finalMessage = """
        ❌ ${message}
        🔍 Exception: ${e.getClass().getSimpleName()}
        📄 Details: ${e.message}
        """.stripIndent()
	
			if (stopExecution) {
				KeywordUtil.markFailedAndStop(finalMessage)
			} else {
				KeywordUtil.markFailed(finalMessage)
			}
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
				handle(e, "Step failed: ${stepName}", stopExecution)
			}
		}
	}
