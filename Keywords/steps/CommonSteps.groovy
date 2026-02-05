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

import CustomKeywords
import internal.GlobalVariable
import stories.VariableStories
import stories.AssertStory
import stories.CommonStory
import stories.LogStories
import stories.NavigateStory
import stories.TestObjectStory
import stories.UtilHelper

import com.kms.katalon.core.configuration.RunConfiguration
import org.openqa.selenium.chrome.ChromeOptions
import com.kms.katalon.core.webui.driver.DriverFactory

public class CommonSteps {
	NavigateStory navigateStory = new NavigateStory()
	TestObjectStory testObjectStory = new TestObjectStory()
	AssertStory assertStory = new AssertStory()

	@Keyword
	def takeScreenshots(String ssName = "") {

		if(CommonStory.isNullOrEmpty(ssName)) {
			ssName = UtilHelper.randomString()
		}

		LogStories.logInfo(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Screenshot: $ssName")

		String timeStamp = new Date().format("yyyyMMdd_HHmmss")

		String screenshotPath = RunConfiguration.getProjectDir() +
				"/Screenshots/${ssName}_${timeStamp}.png"

		WebUI.takeScreenshot(screenshotPath)

		println "Screenshot captured: " + screenshotPath
	}

	@Keyword
	def takeTestCaseScreenshot() {
		boolean isCloud = UtilHelper.isCloud()

		if(!isCloud) {

			String ssName = UtilHelper.randomString()

			LogStories.logInfo(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Screenshot: $ssName")

			String timeStamp = new Date().format("yyyyMMdd_HHmmss")

			String screenshotPath = RunConfiguration.getProjectDir() +
					"/Screenshots/${ssName}_${timeStamp}.png"

			WebUI.takeScreenshot(screenshotPath)

			println "Screenshot captured: " + screenshotPath
		}
	}

	@Keyword
	def maximeyesLogin(String userName, String password) {
		WebUI.setText(findTestObject('LoginPage/UserName'), userName)

		LogStories.logInfo("User Name: $userName")

		WebUI.setText(findTestObject('LoginPage/Password'), password)

		LogStories.logInfo("User Name: $password")

		WebUI.click(findTestObject('LoginPage/LoginBtn'))

		LogStories.logInfo("Clicked on SignIn Button.")

		LogStories.logInfo("Awaiting the Home Screen.")

		WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

		WebUI.waitForElementVisible(findTestObject('CommonPage/WorkQueue'), 30)

		LogStories.logInfo("Home Screen is visible.")
	}

	@Keyword
	def findPatient(String lastName, String firstName) {
		WebUI.click(findTestObject('PatientPage/Find Patients/FindPatient'))
		LogStories.logInfo("Clicked on Find Patient") 

		WebUI.setText(findTestObject('PatientPage/Find Patients/input_Find Patient_LastName'), lastName)

		LogStories.logInfo("Last Name: $lastName")

		WebUI.setText(findTestObject('PatientPage/Find Patients/input_Find Patient_FirstName'), firstName)

		LogStories.logInfo("First Name: $firstName")

		WebUI.click(findTestObject('PatientPage/Find Patients/input_Active_btnSearchPatient'))

		LogStories.logInfo('Clicked on Find Button.')

		WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

		WebUI.waitForElementVisible(findTestObject('PatientPage/Find Patients/Header Patient Name'),30)

		String PatientName = WebUI.getText(findTestObject('PatientPage/Find Patients/Header Patient Name'))

		String expectedPtName = "${firstName} ${lastName}"

		WebUI.verifyMatch(PatientName, expectedPtName, true)

		LogStories.markPassed("Patient Name: $expectedPtName")

		VariableStories.setItem('FP_PATIENT_NAME', expectedPtName)

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
		LogStories.logInfo("Clicked on Add New Encounter.")

		WebUI.selectOptionByLabel(findTestObject('EncounterPage/Add New Encounter/select_Encounter Type_EncounterTypeID'), encounterType, true)
		LogStories.logInfo("Encounter Type: $encounterType")

		WebUI.selectOptionByLabel(findTestObject('EncounterPage/Add New Encounter/select_PracticeLocationID'), examLocation, true)
		LogStories.logInfo("Exam Location: $examLocation")

		WebUI.delay(3)

		WebUI.selectOptionByLabel(findTestObject('EncounterPage/Add New Encounter/select_ProviderId'), provider, true)
		LogStories.logInfo("Provider: $provider")

		WebUI.selectOptionByLabel(findTestObject('EncounterPage/Add New Encounter/select_Technician_NewPE_TechnicianID'), technician, true)
		LogStories.logInfo("Technician: $technician")

		WebUI.click(findTestObject('EncounterPage/Add New Encounter/btnSaveNewPEPopup'))
		LogStories.logInfo("Clicked on Create Button.")

		try {
			Boolean isCreateNew =  WebUI.waitForElementVisible(findTestObject('EncounterPage/Add New Encounter/input_Confirmation_btnCreateANewEncounter'), 5, FailureHandling.OPTIONAL)
			if(isCreateNew) {
				WebUI.click(findTestObject('EncounterPage/Add New Encounter/input_Confirmation_btnCreateANewEncounter'), FailureHandling.OPTIONAL)
				LogStories.logInfo("Clicked on Create New Button.")
			}
		}
		catch (def e) {
			e.printStackTrace()
		}

		WebUI.waitForElementVisible(findTestObject('EncounterPage/Add New Encounter/EncounterPatientHeader'), 30)

		String PatientName = WebUI.getText(findTestObject('EncounterPage/Add New Encounter/EncounterPatientHeader'))

		String expected = "${firstName} ${lastName}"

		WebUI.verifyMatch(PatientName, expected, true)

		LogStories.markPassed('Encounter Saved Suceesfully.')

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
		// Wait for encounter grid to be ready
		WebUI.waitForElementVisible(findTestObject('EncounterPage/EncounterHx/Grid/first_Tr_EncounterGrid'), 30, FailureHandling.STOP_ON_FAILURE)

		// Enter encounter ID and click Go
		WebUI.setText(findTestObject('EncounterPage/EncounterHx/input_ExamNumber'), encounterId)
		WebUI.click(findTestObject('EncounterPage/EncounterHx/button_GO'))
		LogStories.logInfo("Clicked on Go button with EncounterId: $encounterId")

		// Wait for busy indicator to disappear
		WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30, FailureHandling.STOP_ON_FAILURE)

		// Locate and click the encounter row
		TestObject td_EncounterId = testObjectStory.td_EncounterId(encounterId)
		WebUI.waitForElementVisible(td_EncounterId, 30, FailureHandling.STOP_ON_FAILURE)
		WebUI.click(td_EncounterId)
		LogStories.logInfo("Clicked on Encounter Id row: $encounterId")

		// Wait for busy indicator again
		WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30, FailureHandling.STOP_ON_FAILURE)

		// Verify patient header is visible
		WebUI.waitForElementVisible(findTestObject('EncounterPage/Add New Encounter/EncounterPatientHeader'), 30, FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo("Encounter patient header loaded successfully for EncounterId: $encounterId")
	}

	@Keyword
	def getFirstEncounterId(String firstName, String lastName) {
		// Build key
		String _key = "ENC_${firstName}_${lastName}".toUpperCase()
		String encIdKey = "${_key}_ENCOUNTER_ID"

		// Get encounter ID from grid
		String encId = WebUI.getText(findTestObject('EncounterPage/EncounterHx/Grid/td_FirstEncounterId'))

		// Store encounter ID in variables
		VariableStories.setItem(encIdKey, encId)

		VariableStories.setItem('ENCOUNTER_ID', encId)

		VariableStories.setItem(encIdKey, encId)

		VariableStories.setItem('ENCOUNTER_ID', encId)

		LogStories.logInfo("Encounter Id retrieved and stored: $encId (Key: $encIdKey)")
	}

	@Keyword
	def clickOnExpandRecording(Boolean isExpand = true) {
		LogStories.logInfo("------------------------clickOnExpandRecording------------------------")

		// Wait for iframe and expand recording button
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/iframeContainer'), 120, FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo('iframeContainer found')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording'), 120, FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo('Expand Recording found')

		CustomKeywords.'steps.CommonSteps.takeTestCaseScreenshot'()

		// Check if search box is present
		boolean isSearchPresent = WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/input_Search_iFrame'), 5, FailureHandling.OPTIONAL)

		String mode = isSearchPresent ? "Collpase"  : "Expand"

		def timeout = isSearchPresent ? 5  : 10

		LogStories.logInfo(".........................timeout ${timeout}.........................")

		// Decide whether to click based on current state vs desired state
		if ((isSearchPresent && isExpand) || (!isSearchPresent && !isExpand)) {
			LogStories.logInfo("Expand Recording already in ${mode} state")
			return
		}

		// Perform click
		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording'))
		LogStories.logInfo("Clicked on ${mode} Recording")

		Boolean isptVisible =  WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'), 5, FailureHandling.OPTIONAL)
		if((isptVisible && mode == "Expand") || (!isptVisible && mode == "Collpase")) {
			LogStories.markPassed("${mode}ed Recording Suceesfully.")
		}

		// If expanding, validate patient and encounter headers
		if (isExpand) {
			String ptName = VariableStories.getItem('FP_PATIENT_NAME')
			TestObject header_PatientName = testObjectStory.header_PatientName(ptName)
			WebUI.waitForElementVisible(header_PatientName, 20, FailureHandling.STOP_ON_FAILURE)

			CustomKeywords.'steps.CommonSteps.takeTestCaseScreenshot'()

			Boolean IS_ENCOUNTER_ID = GlobalVariable.IS_ENCOUNTER_ID
			if (IS_ENCOUNTER_ID) {
				String encounterId = VariableStories.getItem('ENCOUNTER_ID')
				TestObject header_EncounterId = testObjectStory.header_EncounterId(encounterId)
				WebUI.waitForElementVisible(header_EncounterId, 10, FailureHandling.STOP_ON_FAILURE)
			} else {
				WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/EncounterId'), 10, FailureHandling.STOP_ON_FAILURE)
			}
		}

		WebUI.delay(timeout)
	}
}