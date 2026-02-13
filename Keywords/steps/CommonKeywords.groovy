package steps

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
import stories.LogStories

import java.nio.file.*
import java.awt.Robot
import java.awt.event.KeyEvent
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection


public class CommonKeywords {

	@Keyword
	def deleteAllFiles(String folderPath) {
		Path dir = Paths.get(folderPath)
		if (Files.exists(dir) && Files.isDirectory(dir)) {
			Files.list(dir).each { Path file ->
				if (Files.isRegularFile(file)) {
					Files.delete(file)
					println "Deleted: ${file.fileName}"
				}
			}
		} else {
			println "Invalid folder path: $folderPath"
		}
	}

	def pressTabs(int count) {
		Robot robot = new Robot()
		robot.delay(500)

		(1..count).each {
			robot.keyPress(KeyEvent.VK_TAB)
			robot.keyRelease(KeyEvent.VK_TAB)
			robot.delay(300) // small pause between tabs
		}
	}

	@Keyword
	def enterFilePathAndName2(String folderPath, String fileName) {
		Robot robot = new Robot()

		// Step 1: Copy folder path to clipboard
		StringSelection folderSelection = new StringSelection(folderPath)
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(folderSelection, null)
		robot.delay(1000)

		// Step 2: Focus top address bar (ALT+D), paste folder path, press ENTER
		robot.keyPress(KeyEvent.VK_ALT)
		robot.keyPress(KeyEvent.VK_D)
		robot.keyRelease(KeyEvent.VK_D)
		robot.keyRelease(KeyEvent.VK_ALT)
		robot.delay(500)

		robot.keyPress(KeyEvent.VK_CONTROL)
		robot.keyPress(KeyEvent.VK_V)
		robot.keyRelease(KeyEvent.VK_V)
		robot.keyRelease(KeyEvent.VK_CONTROL)
		robot.delay(500)

		robot.keyPress(KeyEvent.VK_ENTER)
		robot.keyRelease(KeyEvent.VK_ENTER)
		robot.delay(1000)

		// Step 3: Copy file name to clipboard
		StringSelection fileSelection = new StringSelection(fileName)
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(fileSelection, null)
		robot.delay(500)

		pressTabs(7)

		// Step 5: Paste file name into bottom input field
		robot.keyPress(KeyEvent.VK_CONTROL)
		robot.keyPress(KeyEvent.VK_V)
		robot.keyRelease(KeyEvent.VK_V)
		robot.keyRelease(KeyEvent.VK_CONTROL)
		robot.delay(500)

		robot.keyPress(KeyEvent.VK_ENTER)
		robot.keyRelease(KeyEvent.VK_ENTER)
		robot.delay(1000)
	}

	@Keyword
	def enterFilePathAndName(String folderPath, String fileName) {
		Robot robot = new Robot()

		robot.delay(1000)

		folderPath.each { char c ->
			typeChar(robot, c)
		}

		robot.delay(500)

		robot.keyPress(KeyEvent.VK_ENTER)
		robot.keyRelease(KeyEvent.VK_ENTER)
		robot.delay(1000)

		fileName.each { char c ->
			typeChar(robot, c)
		}

		robot.keyPress(KeyEvent.VK_ENTER)
		robot.keyRelease(KeyEvent.VK_ENTER)
		robot.delay(1000)
	}

	@Keyword
	def enterFilePathInFileNameInput(String fullFilePath) {
		Robot robot = new Robot()

		// Step 1: Copy folder path to clipboard
		StringSelection folderSelection = new StringSelection(fullFilePath)
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(folderSelection, null)
		robot.delay(1000)

		// Step 2: Focus top address bar (ALT+D), paste folder path, press ENTER
		robot.keyPress(KeyEvent.VK_ALT)
		robot.keyPress(KeyEvent.VK_D)
		robot.keyRelease(KeyEvent.VK_D)
		robot.keyRelease(KeyEvent.VK_ALT)
		robot.delay(500)

		pressTabs(7)

		// Step 3: Paste file name into bottom input field
		robot.keyPress(KeyEvent.VK_CONTROL)
		robot.keyPress(KeyEvent.VK_V)
		robot.keyRelease(KeyEvent.VK_V)
		robot.keyRelease(KeyEvent.VK_CONTROL)
		robot.delay(500)

		robot.keyPress(KeyEvent.VK_ENTER)
		robot.keyRelease(KeyEvent.VK_ENTER)
		robot.delay(1000)
	}

	// Helper method
	private void typeChar(Robot robot, char character) {
		switch (character) {
			case '\\': // backslash
				robot.keyPress(KeyEvent.VK_BACK_SLASH)
				robot.keyRelease(KeyEvent.VK_BACK_SLASH)
				break
			case '/': // forward slash
				robot.keyPress(KeyEvent.VK_SLASH)
				robot.keyRelease(KeyEvent.VK_SLASH)
				break
			case ':': // colon requires SHIFT + SEMICOLON
				robot.keyPress(KeyEvent.VK_SHIFT)
				robot.keyPress(KeyEvent.VK_SEMICOLON)
				robot.keyRelease(KeyEvent.VK_SEMICOLON)
				robot.keyRelease(KeyEvent.VK_SHIFT)
				break
			default:
				int keyCode = KeyEvent.getExtendedKeyCodeForChar((int) character)
				if (keyCode != KeyEvent.VK_UNDEFINED) {
					if (Character.isUpperCase(character)) {
						robot.keyPress(KeyEvent.VK_SHIFT)
						robot.keyPress(keyCode)
						robot.keyRelease(keyCode)
						robot.keyRelease(KeyEvent.VK_SHIFT)
					} else {
						robot.keyPress(keyCode)
						robot.keyRelease(keyCode)
					}
				} else {
					println "Unsupported char: ${character}"
				}
		}
	}

	@Keyword
	def getFileFromDownloads(String fileName) {
		// Resolve Downloads folder for current user
		def downloadsDir = Paths.get(System.getProperty("user.home"), "Downloads")

		// Build full path to the file
		def filePath = downloadsDir.resolve(fileName)

		if (Files.exists(filePath)) {
			println "File found: ${filePath.toAbsolutePath()}"

			def _filePath = filePath.toFile()

			return _filePath.getAbsolutePath()
		} else {
			println "File not found in Downloads: ${fileName}"
			return null
		}
	}

	@Keyword
	def getFilePathFromDownloads() {
		// Resolve Downloads folder for current user
		def downloadsDir = Paths.get(System.getProperty("user.home"), "Downloads")

		return downloadsDir.toString()
	}

	/**
	 * Returns the most recent .txt file from Downloads
	 * whose name contains 'test'.
	 */
	@Keyword
	File getLatestTestTxtFile(String fileName, String extenstion = ".txt") {
		Path downloadsDir = Paths.get(System.getProperty("user.home"), "Downloads")

		File latestFile = Files.list(downloadsDir)
				.map { it.toFile() }
				.filter { it.isFile() }
				.filter { it.name.toLowerCase().contains(fileName) && it.name.endsWith(extenstion) }
				.max { f1, f2 -> f1.lastModified() <=> f2.lastModified() }
				.orElse(null)

		return latestFile
	}


	@Keyword
	def copyFileToDownloads(String sourceFilePath) {
		// Resolve source file
		def source = Paths.get(sourceFilePath)

		// Resolve target path (Downloads folder under current user)
		def downloadsDir = Paths.get(System.getProperty("user.home"), "Downloads")
		if (!Files.exists(downloadsDir)) {
			Files.createDirectories(downloadsDir)
		}

		def target = downloadsDir.resolve(source.getFileName())

		// Copy file (replace if already exists)
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)

		LogStories.logInfo("File copied to: ${target.toAbsolutePath()}")

		// Verify file presence
		if (Files.exists(target)) {
			LogStories.markPassed("✅ File is present in Downloads: ${target.fileName}")
		} else {
			LogStories.markFailed("❌ File not found in Downloads after copy: ${target.fileName}")
		}
	}
}
