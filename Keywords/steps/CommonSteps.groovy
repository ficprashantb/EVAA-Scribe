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
import com.kms.katalon.core.util.KeywordUtil
import internal.GlobalVariable
import stories.VariableStories
import stories.AssertStory
import stories.NavigateStory
import stories.TestObjectStory
import stories.UtilHelper

import com.kms.katalon.core.configuration.RunConfiguration
import org.openqa.selenium.chrome.ChromeOptions
import com.kms.katalon.core.webui.driver.DriverFactory



public class CommonSteps {
	NavigateStory navigateStory = new NavigateStory()
	TestObjectStory testObjectStory = new TestObjectStory()
	AssertStory assertStory = new AssertStory();

	@Keyword
	def addChromeArguments(String filePath) {

		ChromeOptions opt = new ChromeOptions()

		opt.addArguments("use-fake-ui-for-media-stream")
		opt.addArguments("use-fake-device-for-media-stream")
		opt.addArguments("use-file-for-fake-audio-capture=" + "$filePath")

		DriverFactory.changeWebDriver(new org.openqa.selenium.chrome.ChromeDriver(opt))
	}

	@Keyword
	def takeScreenshots(String name) {

		String timeStamp = new Date().format("yyyyMMdd_HHmmss")

		String screenshotPath = RunConfiguration.getProjectDir() +
				"/Screenshots/FAILED/${name}_${timeStamp}.png"

		WebUI.takeScreenshot(screenshotPath)

		println "Screenshot captured: " + screenshotPath
	}

	@Keyword
	def maximeyesLogin(String siteURL, String userName, String password) {
		WebUI.navigateToUrl(siteURL)
		KeywordUtil.logInfo("Site URL: $siteURL")

		WebUI.setText(findTestObject('LoginPage/UserName'), userName)

		KeywordUtil.logInfo("User Name: $userName")

		WebUI.setText(findTestObject('LoginPage/Password'), password)

		KeywordUtil.logInfo("User Name: $password")

		WebUI.click(findTestObject('LoginPage/LoginBtn'))

		KeywordUtil.logInfo("Clicked on SignIn Button.")

		KeywordUtil.logInfo("Awaiting the Home Screen.")

		WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

		WebUI.waitForElementVisible(findTestObject('CommonPage/WorkQueue'), 30)

		KeywordUtil.logInfo("Home Screen is visible.")
	}

	@Keyword
	def findPatient(String lastName, String firstName) {
		WebUI.click(findTestObject('PatientPage/Find Patients/FindPatient'))

		WebUI.setText(findTestObject('PatientPage/Find Patients/input_Find Patient_LastName'), lastName)

		KeywordUtil.logInfo("Last Name: $lastName")

		WebUI.setText(findTestObject('PatientPage/Find Patients/input_Find Patient_FirstName'), firstName)

		KeywordUtil.logInfo("First Name: $firstName")

		WebUI.click(findTestObject('PatientPage/Find Patients/input_Active_btnSearchPatient'))

		KeywordUtil.logInfo('Clicked on Find Button.')

//		UtilHelper.sendWindowsNotification("Katalon", "Execution finished")

		WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

		WebUI.waitForElementVisible(findTestObject('PatientPage/Find Patients/Header Patient Name'),30)

		String PatientName = WebUI.getText(findTestObject('PatientPage/Find Patients/Header Patient Name'))

		String expectedPtName = "${firstName} ${lastName}"

		WebUI.verifyMatch(PatientName, expectedPtName, true)

		KeywordUtil.markPassed("Patient Name: $expectedPtName")

		String _key = "FP_${firstName}_${lastName}".toUpperCase()

		String ageKey = "${_key}_PATIENT_AGE_AT_EXAM"

		String age = WebUI.getText(findTestObject('PatientPage/PatientAge'))

		VariableStories.setItem(ageKey, age)

		String patientIdKey = "${_key}_PATIENT_ID"

		String patientId = WebUI.getAttribute(findTestObject('PatientPage/PatientId'), 'value')

		VariableStories.setItem(patientIdKey, patientId)
	}

	@Keyword
	def createNewEncounter(String firstName,String lastName, String encounterType, String examLocation,String provider, String technician, Boolean isEncIdStore = true) {
		WebUI.click(findTestObject('EncounterPage/Add New Encounter/a_Encounters_dropdown-toggle menu-large rec_046ac3'))

		WebUI.click(findTestObject('EncounterPage/Add New Encounter/a_Actions_Encounters  Add New Encounter'))
		KeywordUtil.logInfo("Clicked on Add New Encounter.")

		WebUI.selectOptionByLabel(findTestObject('EncounterPage/Add New Encounter/select_Encounter Type_EncounterTypeID'), encounterType, true)
		KeywordUtil.logInfo("Encounter Type: $encounterType")

		WebUI.selectOptionByLabel(findTestObject('EncounterPage/Add New Encounter/select_PracticeLocationID'), examLocation, true)
		KeywordUtil.logInfo("Exam Location: $examLocation")

		WebUI.selectOptionByLabel(findTestObject('EncounterPage/Add New Encounter/select_ProviderId'), provider, true)
		KeywordUtil.logInfo("Provider: $provider")

		WebUI.selectOptionByLabel(findTestObject('EncounterPage/Add New Encounter/select_Technician_NewPE_TechnicianID'), technician, true)
		KeywordUtil.logInfo("Technician: $technician")

		WebUI.click(findTestObject('EncounterPage/Add New Encounter/btnSaveNewPEPopup'))
		KeywordUtil.logInfo("Clicked on Create Button.")

		try {
			Boolean isCreateNew =  WebUI.waitForElementVisible(findTestObject('EncounterPage/Add New Encounter/input_Confirmation_btnCreateANewEncounter'), 5, FailureHandling.OPTIONAL)
			if(isCreateNew) {
				WebUI.click(findTestObject('EncounterPage/Add New Encounter/input_Confirmation_btnCreateANewEncounter'), FailureHandling.OPTIONAL)
				KeywordUtil.logInfo("Clicked on Create New Button.")
			}
		}
		catch (def e) {
			e.printStackTrace()
		}

		WebUI.waitForElementVisible(findTestObject('EncounterPage/Add New Encounter/EncounterPatientHeader'), 30)

		String PatientName = WebUI.getText(findTestObject('EncounterPage/Add New Encounter/EncounterPatientHeader'))

		String expected = "${firstName} ${lastName}"

		WebUI.verifyMatch(PatientName, expected, true)

		KeywordUtil.markPassed('Encounter Saved Suceesfully.')

		if(isEncIdStore == true) {
			String _key = "ENC_${firstName}_${lastName}".toUpperCase()

			String encIdKey = "${_key}_ENCOUNTER_ID"

			String encId = WebUI.getText(findTestObject('EncounterPage/Header/EncounterId'))
			VariableStories.setItem(encIdKey, encId)

			VariableStories.setItem("ENCOUNTER_ID", encId)

			VariableStories.setItem(encIdKey, encId)

			VariableStories.setItem("ENCOUNTER_ID", encId)
		}
	}

	@Keyword
	def findEncounterByEncounterId(def encounterId) {
		WebUI.waitForElementVisible(findTestObject('EncounterPage/EncounterHx/Grid/first_Tr_EncounterGrid'), 30)

		WebUI.setText(findTestObject('EncounterPage/EncounterHx/input_ExamNumber'), encounterId)

		WebUI.click(findTestObject('EncounterPage/EncounterHx/button_GO'))
		KeywordUtil.logInfo("Clicked on Go button.")

		WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

		TestObject td_EncounterId = testObjectStory.td_EncounterId(encounterId);

		WebUI.waitForElementVisible(td_EncounterId, 30)

		WebUI.click(td_EncounterId)
		KeywordUtil.logInfo("Click on Encounter Id: $encounterId")

		WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

		WebUI.waitForElementVisible(findTestObject('EncounterPage/Add New Encounter/EncounterPatientHeader'), 30)
	}

	@Keyword
	def getFirstEncounterId(String firstName, String lastName) {
		String _key = "ENC_${firstName}_${lastName}".toUpperCase()

		String encIdKey = "${_key}_ENCOUNTER_ID"

		String encId = WebUI.getText(findTestObject('EncounterPage/EncounterHx/Grid/td_FirstEncounterId'))

		VariableStories.setItem(encIdKey, encId)

		VariableStories.setItem('ENCOUNTER_ID', encId)

		VariableStories.setItem(encIdKey, encId)

		VariableStories.setItem('ENCOUNTER_ID', encId)

		KeywordUtil.logInfo("Encounter Id=> $encId")
	}

	@Keyword
	def clickOnExpandRecording(Boolean isExpand = true) {
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/iframeContainer'), 120, FailureHandling.STOP_ON_FAILURE)

		KeywordUtil.logInfo('iframeContainer found')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording'), 120)

		KeywordUtil.logInfo('Expand Recording found')

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording'))

		KeywordUtil.logInfo('Clicked on Expand Recording')

		if(isExpand) {
			WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30)

			WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'), 30)
		}
	}
}

