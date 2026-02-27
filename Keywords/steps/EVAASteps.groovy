package steps

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import org.openqa.selenium.WebElement as WebElement
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import CustomKeywords
import FakeMicStream
import java.lang.String
import internal.GlobalVariable
import stories.VariableStories
import stories.WaitStory
import stories.AssertStory
import stories.NavigateStory
import stories.TestObjectStory
import stories.UtilHelper
import stories.CommonStory
import stories.LogStories
import groovy.json.JsonOutput

import org.openqa.selenium.Keys
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI2

import org.testng.Assert
import java.awt.Robot
import java.awt.event.KeyEvent

public class EVAASteps {
	NavigateStory navigateStory = new NavigateStory()
	TestObjectStory testObjectStory = new TestObjectStory()
	AssertStory assertStory = new AssertStory();
	WaitStory waitStory = new WaitStory()

	@Keyword
	def verifyPatientConsentReceived(String isReceived) {
		LogStories.log('----------------------Step AAF----------------------')

		//		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/button_Patient Consent Received'), 30, FailureHandling.STOP_ON_FAILURE)
		//
		//		def chk_PatientConsentReceived = WebUI.getAttribute(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/button_Patient Consent Received'),
		//				'aria-checked')
		//
		//		if(chk_PatientConsentReceived == isReceived) {
		//			LogStories.markPassed("Patient Consent Received?→ $chk_PatientConsentReceived")
		//		}
		//		else {
		//			LogStories.markFailed("Patient Consent Received?→ $chk_PatientConsentReceived")
		//		}
		//
		//		assertStory.verifyMatch('Patient Consent Received?', chk_PatientConsentReceived, isReceived)
	}

	@Keyword
	def commonStepsForEVAA(String firstName, String lastName, String DOB,
			String finalizedStatus = 'Pending',
			String micStatus = 'Recording Not Started') {

		LogStories.log('----------------------Step AAE----------------------')

		// Expand recording panel
		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'()
		GlobalVariable.IS_ENCOUNTER_ID = true

		// Wait for patient header and verify name
		TestObject patientHeader = findTestObject('EVAAPage/EVAA Scribe/Header/PatientName')
		WebUI.waitForElementVisible(patientHeader, 30, FailureHandling.STOP_ON_FAILURE)
		String ptName = WebUI.getText(patientHeader)
		String expectedPtName = "$firstName $lastName"
		assertStory.verifyMatch('PatientName', ptName, expectedPtName)

		// Verify consent not received, then click to mark received
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('false')
		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/button_Patient Consent Received'))
		LogStories.logInfo('Patient Consent Received checked.')

		WebUI.delay(3)

		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		LogStories.log('----------------------Step G----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, DOB, finalizedStatus, micStatus)
	}

	@Keyword
	def searchStringAndVerify(String searchText) {
		LogStories.log('----------------------Step AAG----------------------')

		// Switch once into the frame
		WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 15, FailureHandling.STOP_ON_FAILURE)

		TestObject searchInput = findTestObject('EVAAPage/EVAA Scribe/Header/input_Search')

		// Wait & type directly
		WebUI.waitForElementVisible(searchInput, 10)
		WebUI.setText(searchInput, searchText)   // replaces clearText + sendKeys

		// Press ENTER
		WebUI.sendKeys(searchInput, Keys.chord(Keys.ENTER))

		LogStories.logInfo("Search: $searchText")

		// Wait for results
		TestObject span_Search = testObjectStory.span_Search(searchText)
		WebUI.waitForElementVisible(span_Search, 10)

		// Verify results
		def elements = WebUI.findWebElements(span_Search, 5)
		assertStory.verifyGreaterThanOrEqual("Search", elements.size(), 1)

		// Clear input safely (no need to re-find)
		WebElement freshInput = WebUI.findWebElement(searchInput, 10)
		freshInput.click()
		freshInput.sendKeys(Keys.chord(Keys.CONTROL, "a"))
		freshInput.sendKeys(Keys.BACK_SPACE)

		// Switch back
		WebUI.switchToDefaultContent()
	}

	@Keyword
	def verifyEVAAScribeAllDetails(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName ,String SearchText= 'b', String FinalizedStatus = 'Pending', String MicStatus='Completed' ) {
		LogStories.log('----------------------Step AAD----------------------')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName,SearchText,FinalizedStatus,MicStatus)

		LogStories.log('----------------------Step M----------------------')
		CustomKeywords.'steps.EVAASteps.getAndStoreEVAAScribeSOAPNote'()

		LogStories.log('----------------------Step N----------------------')
		CustomKeywords.'steps.EVAASteps.searchStringAndVerify'(SearchText)
	}

	@Keyword
	def verifyEVAAScribeDetails(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName ,String SearchText= 'b', String FinalizedStatus = 'Pending', String MicStatus='Completed') {
		LogStories.log('----------------------Step AAK----------------------')

		String expectedPtName = "$FirstName $LastName"

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 20, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 20, FailureHandling.STOP_ON_FAILURE)

		LogStories.log('----------------------Step H----------------------')
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		LogStories.log('----------------------Step I----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeHeaderDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		LogStories.log('----------------------Step J----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeSOAPNotesAndSpeakerNotes'(expectedPtName)

		LogStories.log('----------------------Step K----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, DOB,FinalizedStatus , MicStatus)

		LogStories.log('----------------------Step L----------------------')
		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Send_to_MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo('Send to MaximEyes button visible.')
	}

	@Keyword
	def unfinalizedDictationAfterFinalized(Boolean isExpandClose = false) {
		LogStories.log('----------------------Step O----------------------')
		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized - Green'), 15, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Finalized'), FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo("Clicked on UnFinalized")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Status updated to Unfinalized'), 60, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Status updated to Unfinalized!")

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Finalized - Green'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalize - Blue'), 10, FailureHandling.STOP_ON_FAILURE)

		if(isExpandClose == true) {
			WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording'))

			LogStories.logInfo('Clicked on Expand Recording')
		}

		WebUI.delay(3)
	}

	@Keyword
	def finalizedAndSendToMaximEyes(
			String FirstName,
			String LastName,
			String DOB,
			String Provider_FirstName,
			String Provider_LastName,
			Boolean isExpandClose = true,
			Boolean isSendToEHR = true) {

		LogStories.log('----------------------Step AAC----------------------')

		String expectedPtName = "$FirstName $LastName"

		// Cache objects once
		TestObject finalizeBtn = findTestObject('EVAAPage/EVAA Scribe/Finalize')
		TestObject finalizeBlue = findTestObject('EVAAPage/EVAA Scribe/Finalize - Blue')
		TestObject soapNotesObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes')
		TestObject toastFinalized = findTestObject('EVAAPage/EVAA Scribe/Toast/Status updated to Finalized')
		TestObject finalizedIcon = findTestObject('EVAAPage/EVAA Scribe/Finalized')
		TestObject sendToMaximEyesBtn = findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes')
		TestObject sendToMaximEyesImg = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Send_to_MaximEyes')
		TestObject micObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike')

		// Wait for key elements (shorter timeouts)
		WebUI.waitForElementClickable(finalizeBtn, 20, FailureHandling.STOP_ON_FAILURE)
		WebUI.waitForElementVisible(finalizeBlue, 20, FailureHandling.STOP_ON_FAILURE)
		WebUI.waitForElementVisible(soapNotesObj, 30, FailureHandling.STOP_ON_FAILURE)

		// Click Finalize
		WebUI.click(finalizeBtn, FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo("Clicked on Finalize")

		// Wait for finalized confirmation
		WebUI.waitForElementVisible(toastFinalized, 30, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Status updated to Finalized")

		WebUI.waitForElementVisible(finalizedIcon, 20, FailureHandling.STOP_ON_FAILURE)

		// Wait for Send to MaximEyes availability
		WebUI.waitForElementClickable(sendToMaximEyesBtn, 20, FailureHandling.STOP_ON_FAILURE)
		WebUI.waitForElementVisible(sendToMaximEyesImg, 20, FailureHandling.STOP_ON_FAILURE)

		// Ensure mic is not present
		WebUI.waitForElementNotPresent(micObj, 5, FailureHandling.OPTIONAL)

		// Small buffer delay only if needed
		WebUI.delay(1)

		if (isSendToEHR) {
			LogStories.log('----------------------Step P----------------------')
			CustomKeywords.'steps.EVAASteps.sendToAllSOAPNotesToMaximEyes'(
					FirstName, LastName, DOB, Provider_FirstName, Provider_LastName, isExpandClose)
		}
	}

	@Keyword
	def sendToAllSOAPNotesToMaximEyes(
			String FirstName,
			String LastName,
			String DOB,
			String Provider_FirstName,
			String Provider_LastName,
			Boolean isExpandClose = true) {

		LogStories.log('----------------------Step AAB----------------------')

		String expectedPtName = "$FirstName $LastName"

		// Cache objects once
		TestObject sendBtn = findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes')
		TestObject toastSending = findTestObject('EVAAPage/EVAA Scribe/Toast/Sending SOAP notes and PDF to MaximEyes')
		TestObject toastSent = findTestObject('EVAAPage/EVAA Scribe/Toast/Sent SOAP notes and PDF to MaximEyes successfully')
		TestObject finalizedGreen = findTestObject('EVAAPage/EVAA Scribe/Finalized - Green')
		TestObject patientNameObj = findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/div_PatientName')
		TestObject expandRecordingBtn = findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording')

		// Click Send to MaximEyes
		WebUI.waitForElementPresent(sendBtn, 10, FailureHandling.STOP_ON_FAILURE)
		WebUI.click(sendBtn)
		LogStories.logInfo("Clicked on Send to MaximEyes")

		// Toast messages
		WebUI.waitForElementVisible(toastSending, 30, FailureHandling.OPTIONAL)
		LogStories.markPassed("Sending SOAP notes and PDF to MaximEyes...")

		WebUI.waitForElementVisible(toastSent, 30, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Sent SOAP notes and PDF to MaximEyes successfully.")

		// Finalized confirmation
		WebUI.waitForElementVisible(finalizedGreen, 20, FailureHandling.STOP_ON_FAILURE)

		// Avoid fixed 10s delay, retry patient name instead
		LogStories.log('----------------------div_PatientName----------------------')
		navigateStory.retryAction {
			WebUI.waitForElementVisible(patientNameObj, 30, FailureHandling.STOP_ON_FAILURE)
		}

		// Verify consent and details
		LogStories.log('----------------------Step Q----------------------')
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		LogStories.log('----------------------Step R----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, DOB, 'Finalized', 'Completed')

		LogStories.log('----------------------Step S----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeHeaderDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		// Expand recording if needed
		if (isExpandClose) {
			WebUI.click(expandRecordingBtn)
			LogStories.logInfo('Clicked on Expand Recording')
			WebUI.delay(5) // shorter buffer
		}
	}

	@Keyword
	def finalizedAndSendIndividualElementsToMaximEyes(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName, Boolean isFinalize = true, Boolean isVerifySOAPNote = true) {
		LogStories.log('----------------------Step AAA----------------------')

		String expectedPtName = "$FirstName $LastName"

		if(isFinalize) {
			WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 10, FailureHandling.STOP_ON_FAILURE)

			WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalize - Blue'), 5, FailureHandling.STOP_ON_FAILURE)

			WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)

			WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Send_to_MaximEyes'), 5, FailureHandling.STOP_ON_FAILURE)

			WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Finalize'), FailureHandling.STOP_ON_FAILURE)
			LogStories.logInfo("Clicked on Finalize")

			WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Status updated to Finalized'), 30, FailureHandling.STOP_ON_FAILURE)
			LogStories.markPassed("Status updated to Finalized!")

			WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized'), 15, FailureHandling.STOP_ON_FAILURE)

			WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes'), 5, FailureHandling.STOP_ON_FAILURE)

			WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Send_to_MaximEyes'), 5, FailureHandling.STOP_ON_FAILURE)
		}

		//		WebUI.delay(2)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized - Green'), 30, FailureHandling.STOP_ON_FAILURE)

		LogStories.log('----------------------Step T----------------------')
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		LogStories.log('----------------------Step U----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName,  DOB, 'Finalized', 'Completed')

		LogStories.log('----------------------Step V----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeHeaderDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)


		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}

		def elementStorageList = VariableStories.elementStorage

		for (String name : elementStorageList) {
			LogStories.logInfo("Element from Storage → ${name}")

			LogStories.log("============================Element Name - ${name}============================")

			String moduleName = CommonStory.moduleMapForDirectDictation.get(name)

			TestObject sectionTO = testObjectStory.img_SendToMaximeyesWithParams(moduleName)

			if (!sectionTO) {
				LogStories.markWarning("No TestObject mapped for → ${name}")
				continue
			}

			CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(name)

			WebUI.waitForElementVisible(sectionTO, 20)

			WebUI.click(sectionTO)
			LogStories.logInfo("Clicked on Send to MaximEyes for element- ${name}");

			// Toast checks
			TestObject failedSOAPNote = findTestObject('EVAAPage/EVAA Scribe/Toast/Failed to send SOAP note to MaximEyes')
			TestObject toSOAPNote = failedSOAPNote
			boolean	isVisible = WebUI.waitForElementVisible(failedSOAPNote, 2,FailureHandling.OPTIONAL)
			if(isVisible) {
				LogStories.markFailed("${name} - Failed to send SOAP note to MaximEyes.")
			}

			TestObject passSOAPNote = findTestObject('EVAAPage/EVAA Scribe/Toast/SOAP note sent to MaximEyes successfully')
			isVisible = WebUI.waitForElementVisible(passSOAPNote, 10, FailureHandling.CONTINUE_ON_FAILURE)
			if(isVisible) {
				LogStories.markPassed("${name} - SOAP note sent to MaximEyes successfully.")
				toSOAPNote = passSOAPNote
			}

			if(isVerifySOAPNote) {
				boolean	isRefreshPresent = WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_refresh'), 5, FailureHandling.OPTIONAL)
				if(isRefreshPresent) {
					WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/button_refresh'))
					LogStories.logInfo("Clicked on Refresh button")

					WebUI.delay(2)
				}

				LogStories.log('----------------------Step W----------------------')
				CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

				LogStories.log('----------------------Step X----------------------')
				CustomKeywords.'steps.KW_EVAASteps.verifyIndividualSOAPNoteSentToMaximeyes'(name, true, isRefreshPresent)

				LogStories.log('----------------------Step Y----------------------')
				CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)
			}
			else {
				WebUI.waitForElementNotVisible(toSOAPNote, 5, FailureHandling.OPTIONAL)
			}
		}
	}

	@Keyword
	def verifyEVAAScribeHeaderDetails(
			String FirstName,
			String LastName,
			String DOB,
			String Provider_FirstName,
			String Provider_LastName) {

		LogStories.log('----------------------Step AA----------------------')

		WebUI.delay(5)

		// Cache objects once
		TestObject toPatientName   = findTestObject('EVAAPage/EVAA Scribe/Header/PatientName')
		TestObject toPatientDOB    = findTestObject('EVAAPage/EVAA Scribe/Header/PatientDOB')
		TestObject toPatientId     = findTestObject('EVAAPage/EVAA Scribe/Header/PatientId')
		TestObject toEncounterId   = findTestObject('EVAAPage/EVAA Scribe/Header/EncounterId')
		TestObject toProvider      = findTestObject('EVAAPage/EVAA Scribe/Header/Provider')

		// Wait once for patient name text
		waitStory.waitForElementText(toPatientName, 20)
		WebUI.waitForElementVisible(toPatientName, 10, FailureHandling.STOP_ON_FAILURE)

		String _ptKey = "${FirstName}_${LastName}".toUpperCase()

		// Patient Name
		String PtName = WebUI.getText(toPatientName)
		String expectedPtName = "${FirstName} ${LastName}"
		assertStory.verifyMatch("Header→→ Patient Name", PtName, expectedPtName)

		// Patient DOB
		String actualPTDOB = WebUI.getText(toPatientDOB)
		String expectedPTDOB = DOB
		if (!CommonStory.isNullOrEmpty(DOB) && !DOB.trim().equalsIgnoreCase("Invalid Date")) {
			expectedPTDOB = CustomKeywords.'DateHelper.GetFormattedDate'(DOB, 'M/d/yyyy')
		}
		assertStory.verifyMatch("Header→→ Patient DOB", actualPTDOB, expectedPTDOB)

		// Patient ID
		String PtId = WebUI.getText(toPatientId)?.replaceAll("\\D+", "")
		String patientIdKey = "FP_${_ptKey}_PATIENT_ID"
		String expectedPTId = VariableStories.getItem(patientIdKey)
		assertStory.verifyMatch("Header→→ Patient ID", PtId, expectedPTId)

		// Encounter ID
		String EncId = WebUI.getText(toEncounterId)?.replaceAll("\\D+", "")
		String encIdKey = "ENC_${_ptKey}_ENCOUNTER_ID"
		String expectedEncId = VariableStories.getItem(encIdKey)
		assertStory.verifyMatch("Header→→ Encounter ID", EncId, expectedEncId)

		// Provider
		String Provider = WebUI.getText(toProvider)
				?.replace("Prov:", "")
				?.replace("|", "")
				?.replaceAll("\\s+", "")
		String expectedProvider = "${Provider_FirstName}${Provider_LastName}"
		assertStory.verifyMatch("Header→→ Provider", Provider, expectedProvider)
	}

	@Keyword
	def getSOAPNotesAndSpeakerNotesWordCount(String expectedPtName) {
		LogStories.log('----------------------Step AB----------------------')

		// Cache objects once
		TestObject soapNotesObj    = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes')
		TestObject speakerNotesObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes')

		// Wait for SOAP Notes only once
		WebUI.waitForElementVisible(soapNotesObj, 20, FailureHandling.STOP_ON_FAILURE)

		// SOAP Notes
		String soapNotes = WebUI.getText(soapNotesObj).trim()
		LogStories.logInfo("SOAP Notes→ $soapNotes")
		int wordCountSOAPNotes = soapNotes.tokenize().size()   // faster than regex split
		VariableStories.setItem('SOAP_NOTE_WORDS_COUNT', wordCountSOAPNotes)

		// Speaker Notes
		String speakerNotes = WebUI.getText(speakerNotesObj).trim()
		LogStories.logInfo("Speaker Notes→ $speakerNotes")
		int wordCountSpeakerNotes = speakerNotes.tokenize().size()
		VariableStories.setItem('SPEAKER_NOTE_WORDS_COUNT', wordCountSpeakerNotes)
	}

	@Keyword
	def verifyAppendedSOAPNotesAndSpeakerNotes(String expectedPtName) {
		LogStories.log('----------------------Step AC----------------------')

		// Cache objects once
		TestObject speakerPtNameObj   = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name')
		TestObject dictationDateObj   = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Pt Dictation Date')
		TestObject soapNotesObj       = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes')
		TestObject speakerNotesObj    = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes')

		// Patient Name
		WebUI.waitForElementVisible(speakerPtNameObj, 20, FailureHandling.STOP_ON_FAILURE)
		String ptName = WebUI.getText(speakerPtNameObj)
		assertStory.verifyMatch("Patient Name", ptName, "Pt: $expectedPtName")

		// Dictation Date
		String ptDictationDt = WebUI.getText(dictationDateObj)
		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'()
		if (!CommonStory.isNullOrEmpty(expectedPtDictationDt)) {
			expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(expectedPtDictationDt, 'M/d/yyyy')
		}
		if (!CommonStory.isNullOrEmpty(ptDictationDt)) {
			ptDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(ptDictationDt, 'M/d/yyyy')
		}
		assertStory.verifyMatch("Patient Dictation Date", ptDictationDt, expectedPtDictationDt)
		LogStories.markPassed("Speaker Patient Dictation Date→ $ptDictationDt")

		WebUI.waitForElementVisible(soapNotesObj, 10, FailureHandling.STOP_ON_FAILURE)

		// SOAP Notes
		String soapNotes = WebUI.getText(soapNotesObj).trim()
		LogStories.logInfo("SOAP Notes→ $soapNotes")
		int wordCountSOAPNotes = soapNotes.tokenize().size()
		int expectedWordCountSOAPNotes = VariableStories.getItem('SOAP_NOTE_WORDS_COUNT')
		assertStory.verifyGreaterThanOrEqual("SOAP Notes Total Words", wordCountSOAPNotes, expectedWordCountSOAPNotes)

		// Speaker Notes
		String speakerNotes = WebUI.getText(speakerNotesObj).trim()
		LogStories.logInfo("Speaker Notes→ $speakerNotes")

		// Speaker A & B checks
		int countSpeakerA = speakerNotes.count("Speaker A")
		assertStory.verifyGreaterThanOrEqual("'Speaker A' Word found", countSpeakerA, 1)

		int countSpeakerB = speakerNotes.count("Speaker B")
		assertStory.verifyGreaterThanOrEqual("'Speaker B' Word found", countSpeakerB, 1)

		// Speaker Dictation Word Count
		int wordCountSpeaker = speakerNotes.tokenize().size()
		int expectedWordCountSpeaker = VariableStories.getItem('SPEAKER_NOTE_WORDS_COUNT')
		assertStory.verifyGreaterThanOrEqual("Speaker Dictation Total Words", wordCountSpeaker, expectedWordCountSpeaker)

		// Consent check
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')
	}

	@Keyword
	def verifyEVAAScribeSOAPNotesAndSpeakerNotes(String expectedPtName) {
		LogStories.log('----------------------Step AD----------------------')

		int wordMaxCount = 1
		int maxCount = 1

		// Cache objects
		TestObject ptNameObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name')
		TestObject dictationDateObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Pt Dictation Date')
		TestObject soapNotesObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes')
		TestObject speakerNotesObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes')

		WebUI.waitForElementVisible(ptNameObj, 30, FailureHandling.STOP_ON_FAILURE)

		String ptName = WebUI.getText(ptNameObj)
		expectedPtName = "Pt: $expectedPtName"
		assertStory.verifyMatch("Patient Name", ptName, expectedPtName)

		String ptDictationDt = WebUI.getText(dictationDateObj)
		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(null, 'M/d/yyyy')
		if (!CommonStory.isNullOrEmpty(ptDictationDt)) {
			ptDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(ptDictationDt, 'M/d/yyyy')
		}
		assertStory.verifyMatch("Patient Dictation Date", ptDictationDt, expectedPtDictationDt)
		LogStories.markPassed("Speaker Patient Dictation Date→ $ptDictationDt")

		WebUI.waitForElementVisible(soapNotesObj, 10, FailureHandling.STOP_ON_FAILURE)
		// SOAP Notes
		def soapNotes = WebUI.getText(soapNotesObj)
		LogStories.logInfo("SOAP Notes→ $soapNotes")
		int wordCountSOAPNotes = soapNotes.tokenize().size()
		assertStory.verifyGreaterThanOrEqual("SOAP Notes Total Words", wordCountSOAPNotes, wordMaxCount)

		// Speaker Notes
		def speakerNotes = WebUI.getText(speakerNotesObj)
		LogStories.logInfo("Speaker Notes→ $speakerNotes")

		//		int countSpeakerA = speakerNotes.count("Speaker A")
		//		assertStory.verifyGreaterThanOrEqual("'Speaker A' Word found", countSpeakerA, maxCount)
		//
		//		int countSpeakerB = speakerNotes.count("Speaker B")
		//		assertStory.verifyGreaterThanOrEqual("'Speaker B' Word found", countSpeakerB, maxCount)

		int wordCountSpeakerNotes = speakerNotes.tokenize().size()
		assertStory.verifyGreaterThanOrEqual("Speaker Dictation Total Words", wordCountSpeakerNotes, wordMaxCount)

		LogStories.log('----------------------Step AE----------------------')
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')
	}

	@Keyword
	def verifySOAPNotesAndSpeakerNotesNotGenerated(String expectedPtName) {
		LogStories.log('----------------------Step AAJ----------------------')

		// Avoid fixed delay, rely on waitForElementVisible
		TestObject ptNameObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name')
		TestObject dictationDateObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Pt Dictation Date')
		TestObject soapNotesObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes')
		TestObject speakerNotesObj = findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes')

		WebUI.waitForElementVisible(ptNameObj, 30, FailureHandling.STOP_ON_FAILURE)

		// Patient Name
		String ptName = WebUI.getText(ptNameObj)
		expectedPtName = "Pt: $expectedPtName"
		assertStory.verifyMatch("Patient Name", ptName, expectedPtName)

		// Dictation Date
		String ptDictationDt = WebUI.getText(dictationDateObj)
		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(null, 'M/d/yyyy')
		if (!CommonStory.isNullOrEmpty(ptDictationDt)) {
			ptDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(ptDictationDt, 'M/d/yyyy')
		}
		assertStory.verifyMatch("Patient Dictation Date", ptDictationDt, expectedPtDictationDt)
		LogStories.markPassed("Speaker Patient Dictation Date→ $ptDictationDt")

		// SOAP Notes
		String soapNotes = WebUI.getText(soapNotesObj, FailureHandling.OPTIONAL)
		LogStories.logInfo("SOAP Notes→ $soapNotes")
		assertStory.verifyMatch("SOAP Notes Total Words", soapNotes, "")

		// Speaker Notes
		String speakerNotes = WebUI.getText(speakerNotesObj, FailureHandling.OPTIONAL)
		LogStories.logInfo("Speaker Notes→ $speakerNotes")
		assertStory.verifyMatch("Speaker Dictation Total Words", speakerNotes,
				"No transcript available. Upload an audio file to get started.")

		LogStories.log('----------------------Step AAJ Completed----------------------')
	}

	@Keyword
	def verifyEVAAScribeLeftSidePanel(String PatientName, String DOB, String FinalizedStatus, String MicStatus) {
		LogStories.log('----------------------Step AF----------------------')

		// Cache objects once
		TestObject toPatientName = findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/div_PatientName')
		TestObject toPatientDOBDictationDate = findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/div_PatientDOB_DictationDate')
		TestObject svgPending = findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/svg_Pending')
		TestObject svgFinalized = findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/svg_Finalized')
		TestObject micGrey = findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/img_grey_mic-status')
		TestObject micGreen = findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/img_green_mic-status')
		TestObject micBlue = findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/img_blue_mic-status')

		// Wait once for patient name, no need for delay
		WebUI.waitForElementVisible(toPatientName, 30, FailureHandling.STOP_ON_FAILURE)
		waitStory.waitForElementText(toPatientName, 30)

		// Patient Name
		String div_PatientName = WebUI.getText(toPatientName)
		assertStory.verifyMatch("Left Side Panel→→ Patient Name", div_PatientName, PatientName)

		// Patient DOB + Dictation Date
		String actual_PatientDOB_DictationDate = WebUI.getText(toPatientDOBDictationDate)
		String expectedPTDOBText = DOB

		if (!CommonStory.isNullOrEmpty(DOB)) {
			String ageKey = ("FP_" + PatientName.replaceAll("\\s+", "_")).toUpperCase() + "_PATIENT_AGE_AT_EXAM"
			def age = VariableStories.getItem(ageKey)
			if (!CommonStory.isNullOrEmpty(age)) {
				age = age.toString().replaceAll('YRS', "").trim()
			}
			String expectedPTDOB = CustomKeywords.'DateHelper.GetFormattedDate'(DOB, 'M/d/yyyy')
			expectedPTDOBText = "$expectedPTDOB ($age)"
		}

		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(null, 'M/d/yyyy')
		String expected_PatientDOB_DictationDate = "${expectedPTDOBText} | ${expectedPtDictationDt}"

		assertStory.verifyMatch('Left Side Panel→→ Patient Name, DOB, Dictation Date',
				actual_PatientDOB_DictationDate, expected_PatientDOB_DictationDate)

		// Finalized Status (check once, short waits)
		String _finalizedStatus = ''
		if (WebUI.waitForElementPresent(svgPending, 2, FailureHandling.OPTIONAL)) {
			_finalizedStatus = 'Pending'
		}
		if (WebUI.waitForElementPresent(svgFinalized, 1, FailureHandling.OPTIONAL)) {
			_finalizedStatus = 'Finalized'
		}
		assertStory.verifyMatch('Left Side Panel→→ Finalized Status', _finalizedStatus, FinalizedStatus)

		// Mic Status (check once, short waits)
		String _micStatus = ''
		if (WebUI.waitForElementPresent(micGrey, 2, FailureHandling.OPTIONAL)) {
			_micStatus = 'Recording Not Started'
		}
		if (WebUI.waitForElementPresent(micGreen, 1, FailureHandling.OPTIONAL)) {
			_micStatus = 'In Progress'
		}
		if (WebUI.waitForElementPresent(micBlue, 1, FailureHandling.OPTIONAL)) {
			_micStatus = 'Completed'
		}
		assertStory.verifyMatch('Left Side Panel→→ Mic Status', _micStatus, MicStatus)
	}

	private void captureSectionDirectDictation2(
			String sectionName,
			String testObjectPath,
			boolean isList = true
	) {
		try {
			def isPresent = WebUI.waitForElementPresent(
					findTestObject(testObjectPath),
					1,
					FailureHandling.OPTIONAL
					)

			if (!isPresent) {
				LogStories.markWarning("${sectionName} not found")
				return
			}

			def variableKey = CommonStory.sectionMapForStorageKey.get(sectionName)

			if (isList) {
				List<WebElement> elements =
						WebUI.findWebElements(findTestObject(testObjectPath), 10)

				List<String> values = elements.collect {
					it.getText().trim()
				}

				VariableStories.setItem(variableKey, values)
			} else {
				String value = WebUI.getText(findTestObject(testObjectPath)).trim()
				VariableStories.setItem(variableKey, value)
			}

			VariableStories.elementStorage << sectionName
			LogStories.logInfo("${sectionName} captured successfully")
		} catch (Exception e) {
			LogStories.markWarning("Error capturing ${sectionName}: ${e.message}")
		}
	}

	@Keyword
	def getAndStoreEVAAScribeSOAPNote() {
		LogStories.log('----------------------Step AH----------------------')

		// Switch once into the frame
		WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

		// Clear storage
		VariableStories.elementStorage.clear()

		// Define sections in a list for iteration
		def sections = [
			[name: 'ChiefComplaint', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/ChiefComplaint', isList: false],
			[name: 'HPI', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/HPI', isList: false],
			[name: 'CurrentEyeSymptoms', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/Current Eye Symptoms', isList: true],
			[name: 'Allergies', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/Allergies', isList: true],
			[name: 'Medications', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/Medications', isList: true],
			[name: 'EyeDiseases', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/Eye Diseases', isList: true],
			[name: 'ReviewOfSystems', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/Review Of Systems', isList: true],
			[name: 'Problems', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/Problems', isList: true],
			[name: 'MentalAndFunctionalStatus', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/Mental and Functional Status', isList: true],
			[name: 'DifferentialDiagnosis', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/Differential Diagnosis', isList: true],
			[name: 'Assessment', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/Assessment', isList: true],
			[name: 'Plan', path: 'EVAAPage/EVAA Scribe/SOAP Notes/Note/Plans', isList: true]
		]

		Boolean IS_LIMITED_ELEMENTS = GlobalVariable.G_IS_LIMITED_ELEMENTS

		if (IS_LIMITED_ELEMENTS) {
			def allowedNames = CommonStory.allowedNames
			sections = sections.findAll { elem -> elem.name in allowedNames }
		}

		// Iterate once instead of multiple calls
		sections.each { sec ->
			captureSectionDirectDictation(sec.name, sec.path, sec.isList)
		}

		WebUI.switchToDefaultContent()

		// SOAP Notes
		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))
		int wordCountSOAPNotes = soapNotes.tokenize().size()

		VariableStories.setItem("SOAP_NOTE_LENGTH", wordCountSOAPNotes)
		VariableStories.setItem("SOAP_NOTES", soapNotes)
	}

	private void captureSectionDirectDictation(String sectionName, String testObjectPath, boolean isList = true) {
		try {
			TestObject obj = findTestObject(testObjectPath)
			if (!WebUI.waitForElementPresent(obj, 1, FailureHandling.OPTIONAL)) {
				LogStories.markWarning("${sectionName} not found")
				return
			}

			def variableKey = CommonStory.sectionMapForStorageKey.get(sectionName)

			if (isList) {
				List<String> values = WebUI.findWebElements(obj, 5).collect { it.getText().trim() }
				VariableStories.setItem(variableKey, values)
			} else {
				String value = WebUI.getText(obj).trim()
				VariableStories.setItem(variableKey, value)
			}

			VariableStories.elementStorage << sectionName
			LogStories.logInfo("${sectionName} captured successfully")
		} catch (Exception e) {
			LogStories.markWarning("Error capturing ${sectionName}: ${e.message}")
		}
	}

	@Keyword
	def getAndStoreEVAAScribeDirectDictationNote() {
		LogStories.log('----------------------Step AG----------------------')

		CustomKeywords.'steps.EVAASteps.getAndStoreEVAAScribeSOAPNote'()
	}

	@Keyword
	def getAndStoreEVAAScribeSOAPNote2() {
		LogStories.log('----------------------Step AH----------------------')

		WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

		VariableStories.elementStorage.clear()

		captureSectionDirectDictation('ChiefComplaint',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/ChiefComplaint', false)

		captureSectionDirectDictation('HPI',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/HPI', false)

		captureSectionDirectDictation('CurrentEyeSymptoms',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Current Eye Symptoms')

		captureSectionDirectDictation('Allergies',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Allergies')

		captureSectionDirectDictation('Medications',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Medications')

		captureSectionDirectDictation('ReviewOfSystems',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Review Of Systems')

		captureSectionDirectDictation('Problems',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Problems')

		//		captureSectionDirectDictation('Refractions',
		//				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Refractions')

		//		captureSectionDirectDictation('AuxiliaryLabTests',
		//				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Auxiliary Lab Tests')

		captureSectionDirectDictation('DifferentialDiagnosis',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Differential Diagnosis')

		captureSectionDirectDictation('Assessment',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Assessment')

		captureSectionDirectDictation('Plan',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Plans')

		captureSectionDirectDictation('EyeDiseases',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Eye Diseases')

		captureSectionDirectDictation('MentalAndFunctionalStatus',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Mental and Functional Status')

		WebUI.switchToDefaultContent()

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))
		int wordCountSOAPNotes = soapNotes.trim().split('\\s+').length

		VariableStories.setItem("SOAP_NOTE_LENGTH", wordCountSOAPNotes)
		VariableStories.setItem("SOAP_NOTES", soapNotes)
	}

	@Keyword
	def verifySOAPNoteSentToMaximeyes() {
		LogStories.log('----------------------Step AI----------------------')

		for (key in  VariableStories.elementStorage) {
			println "Key: ${key}"

			CustomKeywords.'steps.KW_EVAASteps.verifyIndividualSOAPNoteSentToMaximeyes'(key)
		}
	}

	@Keyword
	def generateSOAPNoteByAppendPauseResumeStop(String fileTime, String recordFilePath) {
		LogStories.log('----------------------Step AK----------------------')

		int fileTimeInSeconds = Integer.valueOf(fileTime)
		int pauseTimeInSeconds = 20
		int resumeTimeInSeconds = 10
		int remainingTime = fileTimeInSeconds - pauseTimeInSeconds

		LogStories.logInfo("File Path $recordFilePath")

		def fakeMic = new FakeMicStream(recordFilePath)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 5, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'))

		LogStories.logInfo('Clicked on Append Audio Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/div_Append-mode recording started'), 10, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Append-mode recording started")

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.OPTIONAL)

		WebUI.delay(pauseTimeInSeconds)

		fakeMic.pause()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'))
		LogStories.logInfo('Clicked on Pause Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/toast_Recording Paused'),20, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'), 20, FailureHandling.OPTIONAL)

		WebUI.delay(resumeTimeInSeconds)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'))
		LogStories.logInfo('Clicked on Resume Button')

		fakeMic.resume()

		//		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'), 10, FailureHandling.OPTIONAL)

		WebUI.delay(remainingTime)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.STOP_ON_FAILURE)

		fakeMic.stop()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Append Audio Button')

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'), 20, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.OPTIONAL)
	}

	@Keyword
	def generateSOAPNoteByRecordPauseResumeStop(String fileTime, String recordFilePath, Boolean isResume = true) {
		LogStories.log('----------------------Step AL----------------------')

		int fileTimeInSeconds = Integer.valueOf(fileTime)
		int pauseTimeInSeconds = 20
		int resumeTimeInSeconds = 10

		if(!isResume) {
			pauseTimeInSeconds = fileTimeInSeconds - 5
		}

		int remainingTime = fileTimeInSeconds - pauseTimeInSeconds

		LogStories.logInfo("File Path $recordFilePath")

		def fakeMic = new FakeMicStream(recordFilePath)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		LogStories.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		WebUI.delay(pauseTimeInSeconds)

		fakeMic.pause()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'))
		LogStories.logInfo('Clicked on Pause Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'),10, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/toast_Recording Paused'),20, FailureHandling.OPTIONAL)

		WebUI.delay(resumeTimeInSeconds)

		if(isResume) {
			WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'),10, FailureHandling.STOP_ON_FAILURE)

			fakeMic.resume()

			WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'))
			LogStories.logInfo('Clicked on Resume Button')

			//		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'), 10, FailureHandling.OPTIONAL)

			WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'), 10, FailureHandling.OPTIONAL)

			WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'), 10, FailureHandling.OPTIONAL)

			WebUI.delay(remainingTime)
		}

		fakeMic.stop()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.OPTIONAL)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Generating SOAP Notes")

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.OPTIONAL)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120)
	}

	@Keyword
	def generateSOAPNoteByRecordPauseStop(String fileTime, String recordFilePath) {

		CustomKeywords.'steps.EVAASteps.generateSOAPNoteByRecordPauseResumeStop'(fileTime, recordFilePath,false)
	}

	@Keyword
	def generateSOAPNoteByAppendStartStop(String fileTime, String recordFilePath) {
		LogStories.log('----------------------Step AM----------------------')

		def fileTimeinSeconds = Integer.valueOf(fileTime)

		LogStories.logInfo("File Path $recordFilePath")

		def fakeMic = new FakeMicStream(recordFilePath)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'))

		LogStories.logInfo('Clicked on Append Audio Button')

		fakeMic.start()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/div_Append-mode recording started'), 10, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Append-mode recording started")

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 5, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(fileTimeinSeconds)

		fakeMic.stop()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Append Audio Button')

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.OPTIONAL)

		//		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 30, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 30, FailureHandling.OPTIONAL)
	}

	@Keyword
	def RecordStartStop(String fileTime, String uploadFilePath, boolean isReRecord = false) {
		LogStories.log('----------------------Step AN----------------------')

		int fileTimeInSeconds = Integer.valueOf(fileTime)
		LogStories.logInfo("File Path: $uploadFilePath")

		def fakeMic = new FakeMicStream(uploadFilePath)

		// Start recording
		TestObject recordBtn = findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record')
		WebUI.waitForElementClickable(recordBtn, 10, FailureHandling.STOP_ON_FAILURE)
		WebUI.click(recordBtn)
		LogStories.logInfo('Clicked on Start Record Button')

		// Handle re-record if applicable
		if (isReRecord) {
			TestObject reRecordBtn = findTestObject('EVAAPage/EVAA Scribe/Menu/button_Re-Record')
			WebUI.waitForElementClickable(reRecordBtn, 5, FailureHandling.STOP_ON_FAILURE)
			LogStories.markPassed("Re-Record button displayed.")
			WebUI.click(reRecordBtn)
			LogStories.logInfo("Clicked on Re-Record button.")
		}

		// Wait for stop button and record time
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		// Start fake mic stream
		fakeMic.start()
		LogStories.logInfo("FakeMic recording started.")

		// Controlled wait for file duration
		WebUI.delay(fileTimeInSeconds)

		// Stop recording
		TestObject stopBtn = findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop')
		WebUI.click(stopBtn)
		LogStories.logInfo("Clicked on Stop Record Button")

		fakeMic.stop()
		LogStories.logInfo("FakeMic recording stopped.")
	}

	@Keyword
	def generateSOAPNoteByRecordStartStop(String fileTime, String uploadFilePath, boolean isReRecord = false) {

		CustomKeywords.'steps.EVAASteps.RecordStartStop'(fileTime, uploadFilePath, isReRecord)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.OPTIONAL)
		LogStories.markPassed("Generating SOAP Notes")

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.OPTIONAL)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'), 5, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def generateSOAPNoteByUploadingFile(String UploadFilePath) {
		LogStories.log('----------------------Step AO----------------------')

		// Log the file path
		LogStories.logInfo("File Path: " + UploadFilePath)

		TestObject upload = findTestObject('EVAAPage/EVAA Scribe/Menu/defile input')

		//		WebUI.uploadFile(upload, UploadFilePath)
		CustomKeywords.'com.katalon.testcloud.FileExecutor.uploadFileToWeb'(upload, UploadFilePath)

		LogStories.logInfo("File uploaded: " + UploadFilePath)

		LogStories.logInfo("Awaiting file upload...")

		// Wait for toast message confirming file processed
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/File processed successfully'), 180, FailureHandling.STOP_ON_FAILURE)

		// Verify if toast is present quickly
		boolean isPresent = WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/File processed successfully'), 1, FailureHandling.OPTIONAL)

		if (isPresent) {
			LogStories.markPassed("File processed successfully.")
		} else {
			LogStories.markWarning("File not processed successfully.")
		}

		// Wait for Append Audio button
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 30, FailureHandling.STOP_ON_FAILURE)

		// Wait for SOAP Notes element
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 10, FailureHandling.STOP_ON_FAILURE)

		// Ensure Finalize button is clickable
		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 10, FailureHandling.STOP_ON_FAILURE)

		// Wait for SOAP Notes section
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def UploadingSOAPNoteFile(String UploadFilePath) {
		LogStories.log('----------------------Step AP----------------------')

		LogStories.logInfo("File Path $UploadFilePath")

		//		WebUI.uploadFile(findTestObject('EVAAPage/EVAA Scribe/Menu/defile input'), UploadFilePath)
		CustomKeywords.'com.katalon.testcloud.FileExecutor.uploadFileToWeb'(findTestObject('EVAAPage/EVAA Scribe/Menu/defile input'), UploadFilePath)

		LogStories.logInfo("File uploaded: " + UploadFilePath)

		LogStories.logInfo("Awaiting file upload...")
	}

	@Keyword
	def generateSOAPNoteByUploadingFileAndSwitchPatient(String UploadFilePath) {
		LogStories.log('----------------------Step AQ----------------------')

		UploadingSOAPNoteFile(UploadFilePath)
	}

	@Keyword
	def verifySOAPNoteGenerateSucessfully() {
		LogStories.log('----------------------Step AR----------------------')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/File processed successfully'), 150, FailureHandling.OPTIONAL)
		LogStories.markPassed("File processed successfully")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def StartRecording_CreateNewEncounter_StopRecording(String recordFilePath, String FirstName, String LastName, String EncounterType, String ExamLocation, String Provider, String Technician ) {
		LogStories.log('----------------------Step AS----------------------')

		def fakeMic = new FakeMicStream(recordFilePath)

		// Start Recording
		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		LogStories.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		// Collapse Expand Recording Screen
		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

		CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

		CustomKeywords.'steps.CommonSteps.createNewEncounter'(FirstName, LastName, EncounterType, ExamLocation, Provider, Technician,
				false)

		navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])

		String encounterId = VariableStories.getItem('ENCOUNTER_ID')

		LogStories.logInfo("Encounter Id=> $encounterId")

		CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

		// Stop Recording
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		def delaySec = 10
		LogStories.logInfo("Wait for ${delaySec} second.")
		WebUI.delay(delaySec)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Record Button')

		fakeMic.stop()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Generating SOAP Notes")

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'), 5, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def StartRecording_CreateNewEncounterForOtherPatient_StopRecording(String recordFilePath, String FirstName, String LastName, String EncounterType, String ExamLocation, String Provider, String Technician ) {
		LogStories.log('----------------------Step AT----------------------')

		def fakeMic = new FakeMicStream(recordFilePath)

		// Start Recording
		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		LogStories.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		// Collapse Expand Recording Screen
		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

		TestData patientData = TestDataFactory.findTestData('Data Files/PatientData')

		def LastName2 = patientData.getValue('LastName', 12)

		def FirstName2 = patientData.getValue('FirstName', 12)

		//Find Patient 2
		CustomKeywords.'steps.CommonSteps.findPatient'(LastName2, FirstName2)

		CustomKeywords.'steps.CommonSteps.createNewEncounter'(FirstName2, LastName2, EncounterType, ExamLocation, Provider, Technician,
				false)

		// Collapse Expand Recording Screen
		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

		//Find patient 1
		CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

		navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])

		String encounterId = VariableStories.getItem('ENCOUNTER_ID')

		LogStories.logInfo("Encounter Id=> $encounterId")

		CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

		// Stop Recording
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		def delaySec = 10
		LogStories.logInfo("Wait for ${delaySec} second.")
		WebUI.delay(delaySec)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Record Button')

		fakeMic.stop()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.OPTIONAL)
		LogStories.markPassed("Generating SOAP Notes")

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'), 5, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def directDictationByTypingOnElements() {
		LogStories.log('----------------------Step AU----------------------')

		TestData dictationData = TestDataFactory.findTestData('Data Files/DirectDictationData')
		int rowCount = dictationData.getRowNumbers()

		if (rowCount == 0) {
			LogStories.markFailed("❌ DirectDictationData has NO rows")
			return
		}

		int row = 1 // first row

		if (!VariableStories.elementStorage.isEmpty()) {
			def elementStorageList = VariableStories.elementStorage

			try {
				WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 8)

				elementStorageList.each { String name ->
					LogStories.logInfo("Element from Storage → ${name}")

					TestObject sectionTO = CommonStory.sectionMapForDirectDictationTyping.get(name)
					if (!sectionTO) {
						LogStories.markWarning("No TestObject mapped for → ${name}")
						return
					}

					List<WebElement> elements = WebUI.findWebElements(sectionTO, 5)
					String textToAppend = dictationData.getValue(name, row)

					elements.each { WebElement el ->
						LogStories.logInfo("${name} value → ${el.text}")
						el.click()
						el.sendKeys(Keys.chord(Keys.CONTROL, Keys.END))
						el.sendKeys(" ${textToAppend}")

						WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/input_Search'))
					}
				}
			} catch (Exception e) {
				LogStories.markWarning("Error in dictation typing: ${e.message}")
			} finally {
				WebUI.switchToDefaultContent()
			}
		}
	}

	@Keyword
	def verifyStoredDirectDictationOnEVAAScribe() {
		LogStories.log('----------------------Step AV----------------------')

		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}

		TestData dictationData = TestDataFactory.findTestData('Data Files/DirectDictationData')
		int row = 1   // always first row
		CommonStory commonStory = new CommonStory(dictationData, row)

		WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)
		try {
			def elementStorageList = VariableStories.elementStorage
			for (String name : elementStorageList) {
				LogStories.log("*************************Element from Storage → ${name}*************************")

				TestObject sectionTO = CommonStory.verifyMapForDirectDictation.get(name)
				if (!sectionTO) {
					LogStories.markWarning("No TestObject mapped for → ${name}")
					continue
				}

				List<WebElement> elements = WebUI.findWebElements(sectionTO, 5) // shorter timeout
				if (elements.isEmpty()) {
					LogStories.markWarning("No elements found for → ${name}")
					continue
				}

				List<String> actualTexts = elements.collect { it.text.trim() }

				def variableKey = CommonStory.sectionMapForStorageKey.get(name)
				def storedValue = VariableStories.getItem(variableKey)
				if (CommonStory.isNullOrEmpty(storedValue)) {
					continue
				}

				List expectedList = CommonStory.getListObject(storedValue)

				// Cache dictation value once per name
				String appendText = dictationData.getValue(name, row)?.replaceAll(":(?=.*:)", "")
				expectedList.eachWithIndex { expected, i ->
					String expectedText = "${expected} ${appendText}".replaceAll(":(?=.*:)", "")
					String actualText = "${actualTexts[i]}".replaceAll(":(?=.*:)", "")

					assertStory.verifyMatch("Direct Dictation→→ ${name}", actualText, expectedText)
				}
			}
		} catch (e) {
			e.printStackTrace()
		} finally {
			WebUI.switchToDefaultContent()
		}
	}

	@Keyword
	def directDictationByRecordStartStopOnElements(String uploadFilePath) {
		LogStories.log('----------------------Step AW----------------------')

		TestData dictationData = TestDataFactory.findTestData('Data Files/DirectDictationData')
		int rowCount = dictationData.getRowNumbers()

		if (rowCount == 0) {
			LogStories.markFailed("❌ DirectDictationData has NO rows")
			return
		}

		int row = 1 // first row

		// Clear previous storage
		VariableStories.elementStorageForDirectDictation.clear()

		if (!VariableStories.elementStorage.isEmpty()) {
			LogStories.logInfo("File Path $uploadFilePath")
			def elementStorageList = VariableStories.elementStorage

			try {
				WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

				int index = 1
				int breakIndex = 2

				for (String name : elementStorageList) {
					if (!(name in ['ChiefComplaint', 'HPI'])) break

						VariableStories.elementStorageForDirectDictation << name
					LogStories.logInfo("Element from Storage → ${name}")

					TestObject sectionTO = CommonStory.sectionMapForDirectDictationTyping.get(name)
					if (!sectionTO) {
						LogStories.markWarning("No TestObject mapped for → ${name}")
						continue
					}

					List<WebElement> elements = WebUI.findWebElements(sectionTO, 10)
					if (elements.isEmpty()) {
						LogStories.markWarning("No elements found for section → ${name}")
						continue
					}

					elements.each { WebElement el ->
						def fakeMic = new FakeMicStream(uploadFilePath)
						String textToAppend = dictationData.getValue(name, row)

						el.click()
						el.sendKeys(Keys.chord(Keys.CONTROL, Keys.END))

						String moduleName = CommonStory.moduleMapForDirectDictation.get(name)
						TestObject imgStartDictation = testObjectStory.img_Start_Dictation(moduleName)
						TestObject imgStopDictation = testObjectStory.img_Stop_Dictation(moduleName)

						WebUI.click(imgStartDictation)
						LogStories.logInfo("Clicked on ${name} to Start Dictation")

						WebUI.waitForElementVisible(imgStopDictation, 5, FailureHandling.STOP_ON_FAILURE)
						fakeMic.start()
						LogStories.logInfo("Dictation Started.")

						// Short controlled wait instead of blind delay
						def delaySec = 10
						LogStories.logInfo("Wait for ${delaySec} second.")
						WebUI.delay(delaySec)

						WebUI.waitForElementVisible(imgStopDictation, 5, FailureHandling.OPTIONAL)

						WebUI.click(imgStopDictation)
						LogStories.logInfo("Clicked on ${name} to Stop Dictation")
						fakeMic.stop()
						LogStories.logInfo("Dictation Stopped.")

						WebUI.waitForElementVisible(imgStartDictation, 5, FailureHandling.STOP_ON_FAILURE)

						// Quick focus reset
						WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/input_Search'))
					}

					if (index++ == breakIndex) break
				}
			} catch (Exception e) {
				LogStories.markFailed("Dictation process failed: ${e.message}")
			} finally {
				WebUI.switchToDefaultContent()
			}
		}
	}

	@Keyword
	def directDictationByRecordStartStopOnElements2(String UploadFilePath) {
		LogStories.log('----------------------Step AW----------------------')

		int index = 1;
		int breakIndex = 2;
		TestData dictationData = TestDataFactory.findTestData('Data Files/DirectDictationData')

		int rowCount = dictationData.getRowNumbers()

		if (rowCount == 0) {
			LogStories.markFailed("❌ DirectDictationData has NO rows")
			return
		}

		int row = 1   // first row

		if (!VariableStories.elementStorageForDirectDictation.isEmpty()) {
			VariableStories.elementStorageForDirectDictation.clear()
		}

		if (!VariableStories.elementStorage.isEmpty()) {

			LogStories.logInfo("File Path $UploadFilePath")

			def elementStorageList = VariableStories.elementStorage

			try {
				WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

				for (String name : elementStorageList) {

					if (!(name in ['ChiefComplaint', 'HPI'])) {
						break
					}

					VariableStories.elementStorageForDirectDictation << name

					LogStories.logInfo("Element from Storage → ${name}")

					TestObject sectionTO = CommonStory.sectionMapForDirectDictationTyping.get(name)

					if (!sectionTO) {
						LogStories.markWarning("No TestObject mapped for → ${name}")
						return
					}

					List<WebElement> elements = WebUI.findWebElements(sectionTO, 10)

					elements.each { WebElement el ->
						def fakeMic = new FakeMicStream(UploadFilePath)

						LogStories.logInfo("${name} value → ${el.text}")

						String textToAppend = dictationData.getValue(name, row)

						el.click()
						el.sendKeys(Keys.chord(Keys.CONTROL, Keys.END))
						//						el.sendKeys(Keys.SPACE)

						String moduleName = CommonStory.moduleMapForDirectDictation.get(name)

						TestObject img_Start_Dictation = testObjectStory.img_Start_Dictation(moduleName)

						TestObject img_Stop_Dictation = testObjectStory.img_Stop_Dictation(moduleName)

						WebUI.click(img_Start_Dictation)
						LogStories.logInfo("Clicked on ${name} to Start Dictation")

						WebUI.waitForElementVisible(img_Stop_Dictation, 5, FailureHandling.STOP_ON_FAILURE)

						fakeMic.start()
						LogStories.logInfo("Dictation Started.")

						def delaySec = 10
						LogStories.logInfo("Wait for ${delaySec} second.")
						WebUI.delay(delaySec)

						WebUI.click(img_Stop_Dictation)
						LogStories.logInfo("Clicked on ${name} to Stop Dictation")

						fakeMic.stop()
						LogStories.logInfo("Dictation Stopped.")

						WebUI.waitForElementVisible(img_Start_Dictation, 5, FailureHandling.STOP_ON_FAILURE)

						WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/input_Search'))

						WebUI.delay(1)
					}

					if(index == breakIndex) break;

					index++;
				}
			} catch (e) {
				e.printStackTrace()
			}finally		{
				//				WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/input_Search'))

				// Switch back
				WebUI.switchToDefaultContent()
			}
		}
	}

	@Keyword
	def verifyRecordedDirectDictationAddedOnEVAAScribe() {
		LogStories.log('----------------------Step AX----------------------')

		if (VariableStories.elementStorageForDirectDictation.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}

		if (!VariableStories.elementStorageForDirectDictation.isEmpty()) {
			WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

			try {
				def elementStorageList = VariableStories.elementStorageForDirectDictation
				for (String name : elementStorageList) {
					LogStories.logInfo("Element from Storage → ${name}")

					TestObject sectionTO = CommonStory.sectionMapForSOAPNote.get(name)
					if (!sectionTO) {
						LogStories.markWarning("No TestObject mapped for → ${name}")
						continue
					}

					List<WebElement> elements = WebUI.findWebElements(sectionTO, 10)
					if (elements.isEmpty()) {
						LogStories.markWarning("No elements found for section → ${name}")
						continue
					}

					List<String> actualTexts = elements.collect { it.text.trim() }
					def variableKey = CommonStory.sectionMapForStorageKey.get(name)
					def storedValue = VariableStories.getItem(variableKey)

					if (CommonStory.isNullOrEmpty(storedValue)) {
						LogStories.markWarning("No stored value for key → ${variableKey}")
						continue
					}
					List expectedList = CommonStory.getListObject(storedValue)

					expectedList.eachWithIndex { expected, i ->
						String expectedText = "${expected}"
						expectedText = expectedText.trim().split('\\s+')

						String actualText = actualTexts[i].trim().split('\\s+')

						actualText=	actualText?.replaceAll(":(?=.*:)", "")
						expectedText=	expectedText?.replaceAll(":(?=.*:)", "")

						def actualLen = actualText.length()

						def expectedLen = expectedText.length()

						assertStory.verifyGreaterThan("Direct Dictation→→ ${name}",actualLen,expectedLen)
					}
				}
			} catch (e) {
				e.printStackTrace()
			}finally		{
				// Switch back
				WebUI.switchToDefaultContent()
			}
		}
	}

	@Keyword
	def TransferEncounterDataToSuperbill() {
		LogStories.log('----------------------Step AY----------------------')

		TestObject transferBtn = findTestObject('EncounterPage/Encounter Details/Data Transferred/input_btnDataTransferEncBill')
		WebUI.waitForElementClickable(transferBtn, 5, FailureHandling.STOP_ON_FAILURE)
		WebUI.click(transferBtn, FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo("Clicked on Transfer button.")

		try {
			// Toast confirmations
			if (WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/toast_PatientEncounterSaved'), 10, FailureHandling.OPTIONAL)) {
				LogStories.markPassed("Patient encounter saved.")
			}
			if (WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/toast_DataTransferred'), 10, FailureHandling.OPTIONAL)) {
				LogStories.markPassed("Data transferred.")
			}

			// Verify transfer state
			TestObject transferredBtn = findTestObject('EncounterPage/Encounter Details/Data Transferred/input_btnDataTransferedEncBill')
			WebUI.focus(transferredBtn, FailureHandling.STOP_ON_FAILURE)
			WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/powerTip_Data transferred'), 5, FailureHandling.STOP_ON_FAILURE)
			LogStories.markPassed("Data transferred to Superbill.")

			WebUI.waitForElementVisible(transferredBtn, 5, FailureHandling.STOP_ON_FAILURE)
		} catch (Exception e) {
			LogStories.markFailedAndStop("Data not transferred to Superbill.")
		}
	}

	@Keyword
	def MaximeyesLoginAndFindPatient(String FirstName,String LastName,  String DOB, String Provider_FirstName, String Provider_LastName ,String EncounterType, String ExamLocation,String Technician, Boolean isLogin = true, Boolean isEncIdStore = true, Boolean isCommonSteps = true) {
		LogStories.log('----------------------Step AZ----------------------')

		if(isLogin) {
			CustomKeywords.'steps.CommonSteps.maximeyesLogin'(GlobalVariable.EVAA_UserName, GlobalVariable.EVAA_Password)
		}

		CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

		String ProviderName = "$Provider_FirstName $Provider_LastName"

		CustomKeywords.'steps.CommonSteps.createNewEncounter'(FirstName, LastName, EncounterType, ExamLocation, ProviderName, Technician,isEncIdStore )

		if(isCommonSteps) {
			LogStories.log('----------------------Step A----------------------')
			CustomKeywords.'steps.EVAASteps.commonStepsForEVAA'(FirstName, LastName,DOB )
		}
	}

	@Keyword
	def GenerateSOAPNoteByUploadingFileForSinglePatient(String UploadFilePath,String FirstName,String LastName,  String DOB, String Provider_FirstName, String Provider_LastName ,String EncounterType, String ExamLocation,String Technician, Boolean isFinalize = true, Boolean isSendToEHR = true) {
		LogStories.log('----------------------Step AZ----------------------')

		CustomKeywords.'steps.EVAASteps.MaximeyesLoginAndFindPatient'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName, EncounterType, ExamLocation, Technician)

		LogStories.log('----------------------Step B----------------------')

		def uploadFilePath = UtilHelper.getFilePath(UploadFilePath)

		LogStories.logInfo("Upload File Path=> $uploadFilePath")
		CustomKeywords.'steps.EVAASteps.generateSOAPNoteByUploadingFile'(uploadFilePath)

		LogStories.log('----------------------Step C----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeAllDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		if(isFinalize) {
			LogStories.log('----------------------Step D----------------------')
			CustomKeywords.'steps.EVAASteps.finalizedAndSendToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName, true, isSendToEHR)

			if(isSendToEHR) {
				LogStories.log('----------------------Step E----------------------')
				CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'()
			}
		}
	}

	@Keyword
	def VerifyReRecordPopup(String FirstName,String LastName ) {
		LogStories.log('----------------------Step AAM----------------------')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/h2_RecordingConflict'), 10, FailureHandling.STOP_ON_FAILURE)

		LogStories.markPassed('The \'Recording Conflict\' text is displayed on the popup.')

		String ptDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(null,'MM/dd/yyyy')

		String expectedPtName = "$FirstName $LastName"

		String expectedText = "There is another recording for '$expectedPtName' for '$ptDictationDt', do you want to replace this with a new recording?"

		String actualText = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Menu/p_There_is_another_recording'), FailureHandling.OPTIONAL)

		assertStory.verifyMatch('Recording Conflict Text', actualText, expectedText)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Re-Record'), 5, FailureHandling.STOP_ON_FAILURE)

		LogStories.markPassed('The \'Re-Record\' button is displayed on the popup.')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Cancel'), 5, FailureHandling.STOP_ON_FAILURE)

		LogStories.markPassed('The \'Cancel\' button is displayed on the popup.')
	}

	@Keyword
	def UploadReRecordDictation(String fileName, String FirstName,String LastName,  String DOB, String Provider_FirstName, String Provider_LastName ) {
		LogStories.log('----------------------Step AAL----------------------')

		def wordCountSOAPNotes = VariableStories.getItem('SOAP_NOTE_LENGTH')

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step A^^^^^^^^^^^^^^^^^^^^^')

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Upload'), FailureHandling.STOP_ON_FAILURE)

		LogStories.logInfo('Clicked on Upload Button.')

		CustomKeywords.'steps.EVAASteps.VerifyReRecordPopup'(FirstName, LastName)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Cancel'), FailureHandling.STOP_ON_FAILURE)

		LogStories.logInfo('Clicked on Cancel Button.')

		WebUI.delay(2)

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step B^^^^^^^^^^^^^^^^^^^^^')

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		int wordCountSOAPNotes2 = soapNotes.trim().split('\\s+').length

		assertStory.verifyMatch('SOAP Notes not chnaged after Cancel Re-RecordPopup', wordCountSOAPNotes2, wordCountSOAPNotes)

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step C^^^^^^^^^^^^^^^^^^^^^')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step D^^^^^^^^^^^^^^^^^^^^^')

		def filePath = UtilHelper.getFilePath(fileName)
		LogStories.logInfo("Re-Upload File Path=> $filePath")

		CustomKeywords.'steps.EVAASteps.generateSOAPNoteByUploadingFile'(filePath)

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step F^^^^^^^^^^^^^^^^^^^^^')

		def soapNotes2 = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		int wordCountSOAPNotes3 = soapNotes2.trim().split('\\s+').length

		assertStory.verifyNotMatch('SOAP Notes Re-Recorded', wordCountSOAPNotes3, wordCountSOAPNotes)

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step G^^^^^^^^^^^^^^^^^^^^^')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)
	}

	@Keyword
	def RecordReRecordDictation(String fileTime, String fileName, String FirstName,String LastName,  String DOB, String Provider_FirstName, String Provider_LastName ) {
		LogStories.log('----------------------Step AAL----------------------')

		def wordCountSOAPNotes = VariableStories.getItem('SOAP_NOTE_LENGTH')

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step A^^^^^^^^^^^^^^^^^^^^^')

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Upload'), FailureHandling.STOP_ON_FAILURE)

		LogStories.logInfo('Clicked on Upload Button.')

		CustomKeywords.'steps.EVAASteps.VerifyReRecordPopup'(FirstName, LastName)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Cancel'), FailureHandling.STOP_ON_FAILURE)

		LogStories.logInfo('Clicked on Cancel Button.')

		WebUI.delay(2)

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step B^^^^^^^^^^^^^^^^^^^^^')

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		int wordCountSOAPNotes2 = soapNotes.trim().split('\\s+').length

		assertStory.verifyMatch('SOAP Notes not chnaged after Cancel Re-RecordPopup', wordCountSOAPNotes2, wordCountSOAPNotes)

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step C^^^^^^^^^^^^^^^^^^^^^')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step D^^^^^^^^^^^^^^^^^^^^^')

		def filePath = UtilHelper.getFilePath(fileName)
		LogStories.logInfo("Re-Upload File Path=> $filePath")

		CustomKeywords.'steps.EVAASteps.generateSOAPNoteByRecordStartStop'(fileTime, filePath,true)

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step F^^^^^^^^^^^^^^^^^^^^^')

		def soapNotes2 = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		int wordCountSOAPNotes3 = soapNotes2.trim().split('\\s+').length

		assertStory.verifyNotMatch('SOAP Notes Re-Recorded', wordCountSOAPNotes3, wordCountSOAPNotes)

		LogStories.log('^^^^^^^^^^^^^^^^^^^^^Step G^^^^^^^^^^^^^^^^^^^^^')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)
	}

	@Keyword
	def ClickOnCopyAllButton() {
		LogStories.log("---------------------Click On Copy AllButton---------------------")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Copy All'), 10, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed('The \'Copy All\' button is displayed.')

		WebUI.verifyElementClickable(findTestObject('EVAAPage/EVAA Scribe/Header/button_Copy All'), FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed('The \'Copy All\' button is Clickable.')

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/button_Copy All'), FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed('Clicked on \'Copy All\' button.')

		String clipboardText = UtilHelper.getBrowserClipboardText()
		VariableStories.setItem("CLIPBOARD_TEXT",clipboardText)
	}

	@Keyword
	def VerifyCopiedAllSOAPNotes() {
		LogStories.log("---------------------Verify clicking Copy All copies data from all available elements---------------------")

		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}

		def elementStorageList = VariableStories.elementStorage

		elementStorageList = CustomKeywords.'steps.EVAASteps.GetLimitedElementList'(elementStorageList)

		String clipboardText = VariableStories.getItem("CLIPBOARD_TEXT")

		if (!CommonStory.isNullOrEmpty(clipboardText)) {
			LogStories.markPassed("Verified copied content can be pasted successfully into an external editor")
		}
		else {
			LogStories.markFailedAndStop("Verified copied content can not be pasted into an external editor")
		}

		clipboardText = clipboardText.replaceAll("Review Of Systems - Brief", "Review Of Systems Brief").replaceAll("\\s{2,}", " ").trim()

		for (String name : elementStorageList) {

			LogStories.log("============================SOAP Note Element Name - ${name}============================")

			def variableKey = CommonStory.sectionMapForStorageKey.get(name)
			String storedData = VariableStories.getItem(variableKey)

			if (!CommonStory.isNullOrEmpty(storedData)) {
				List dataList = CommonStory.getListObject(storedData)
				if (dataList.size() > 0) {

					for (String data : dataList) {
						String expectedData = data

						switch (name) {
							case "CurrentEyeSymptoms":
							case "EyeDiseases":
								expectedData = expectedData.replaceFirst(":", " Notes:").replaceAll("\\(OS\\)|\\(OD\\)|\\(OU\\)", "")
								break
							case "ReviewOfSystems":
								expectedData = expectedData.replaceAll("Review Of Systems - Brief", "Review Of Systems Brief")
								expectedData = expectedData.replaceFirst(":", " Notes:").replaceAll("\\(OS\\)|\\(OD\\)|\\(OU\\)", "")
								break
							case "DifferentialDiagnosis":
								def expectedDataList = expectedData
								.toString()
								.split(',', 2)   // split into two parts only
								.collect { it.trim() }
								.findAll { it }

								if (expectedDataList.size() >= 2) {
									// Build combined string with both parts
									expectedData = "Code: ${expectedDataList[0]} Diagnosis: ${expectedDataList[1]}"
								}
								break // skip the generic verification below for this case
						}

						expectedData = expectedData.replaceAll("\\s{2,}", " ").trim()

						assertStory.verifyContainsRegex("Copied SOAP Note for - ${name}", clipboardText, expectedData,false)
					}
				}
				else {
					LogStories.markWarning('No data found')
				}
			}
		}
	}

	@Keyword
	def VerifyCopiedSOAPNotesFollowsTheCorrectOrderOfElements() {
		LogStories.log("---------------------Verify copied content follows the correct order of elements---------------------")

		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}
		else {

			String clipboardText = VariableStories.getItem("CLIPBOARD_TEXT")

			// Get all values as a list
			List<String> moduleList = CommonStory.moduleMapForDirectDictation.values().toList()

			List<String> clipboardTextList = UtilHelper.getSelectedLabels(clipboardText,moduleList)

			LogStories.log("<<<<<<<<<<<<<<<<<<<<<<<<<List Data<<<<<<<<<<<<<<<<<<<<<<<<<")
			String jsonWanted = JsonOutput.toJson(moduleList)
			String jsonClipboard = JsonOutput.toJson(clipboardTextList)

			// Ensure moduleList only shows data that exists in clipboardTextList
			moduleList = moduleList.findAll { clipboardTextList.contains(it) }
			String jsonWantedModified = JsonOutput.toJson(moduleList)

			LogStories.logInfo("Module List: ${jsonWanted}")
			LogStories.logInfo("Clipboard List: ${jsonClipboard}")

			LogStories.logInfo("Modified Module List: ${jsonWantedModified}")

			LogStories.log("<<<<<<<<<<<<<<<<<<<<<<<<<List Data<<<<<<<<<<<<<<<<<<<<<<<<<")

			// Equivalent index-based loop
			for (int i = 0; i < moduleList.size(); i++) {
				String name = moduleList.get(i)

				LogStories.log("============================SOAP Note Element Name - ${name}============================")

				def expectedData = name.replaceAll("Review Of Systems - Brief", "Review Of Systems Brief").replaceAll("\\s{2,}", " ").trim()

				def actualData = clipboardTextList.get(i)
				actualData = actualData.replaceAll("Review Of Systems - Brief", "Review Of Systems Brief").replaceAll("\\s{2,}", " ").trim()

				assertStory.verifyMatch("SOAP Note Order - ${name}", actualData, expectedData)
			}
		}
	}

	@Keyword
	def VerifyCopiedIndividualElementSOAPNotes() {
		LogStories.log("---------------------Verify clicking Copy All copies data from all available elements---------------------")

		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}

		def elementStorageList = VariableStories.elementStorage

		elementStorageList = CustomKeywords.'steps.EVAASteps.GetLimitedElementList'(elementStorageList)

		for (String name : elementStorageList) {

			LogStories.log("============================Copy SOAP Note Element Name - ${name}============================")

			String moduleName = CommonStory.copyMapForDirectDictation.get(name)

			TestObject sectionTO = testObjectStory.img_Copy_Note(moduleName)

			if (!sectionTO) {
				LogStories.markWarning("No TestObject mapped for → ${name}")
				return
			}

			Boolean isVisible = WebUI.verifyElementVisible(sectionTO, FailureHandling.OPTIONAL)
			if(isVisible) {
				LogStories.markPassed("Copy button is visible for → ${name}")
			}
			else {
				LogStories.markFailed("Copy button is not visible for → ${name}")
			}

			WebUI.click(sectionTO)
			LogStories.logInfo("Clicked on copy button for element → ${name}")

			LogStories.log("**********************************Get Clipboard Text for element - ${name}**********************************")

			String clipboardText = UtilHelper.getBrowserClipboardText()

			if (!CommonStory.isNullOrEmpty(clipboardText)) {
				LogStories.markPassed("Verified copied content can be pasted successfully into an external editor")
			}
			else {
				LogStories.markFailed("Verified copied content can not be pasted into an external editor")
			}

			clipboardText = clipboardText.replaceAll("\\s{2,}", " ").trim()
			LogStories.log("***********************************************************************************************************")

			LogStories.log("**********************************Verify Clipboard Text for element - ${name}**********************************")
			def variableKey = CommonStory.sectionMapForStorageKey.get(name)
			String storedData = VariableStories.getItem(variableKey)

			if (!CommonStory.isNullOrEmpty(storedData)) {
				List dataList = CommonStory.getListObject(storedData)
				if (dataList.size() > 0) {

					for (String data : dataList) {
						String expectedData = data

						switch (name) {
							case "CurrentEyeSymptoms":
							case "EyeDiseases":
							case "ReviewOfSystems":
								expectedData = expectedData.replaceFirst(":", " Notes:").replaceAll("\\(OS\\)|\\(OD\\)|\\(OU\\)", "")
								break
							case "DifferentialDiagnosis":
								def expectedDataList = expectedData
								.toString()
								.split(',', 2)   // split into two parts only
								.collect { it.trim() }
								.findAll { it }

								if (expectedDataList.size() >= 2) {
									// Build combined string with both parts
									expectedData = "Code: ${expectedDataList[0]} Diagnosis: ${expectedDataList[1]}"
								}
								continue // skip the generic verification below for this case
						}

						expectedData = expectedData.replaceAll("\\s{2,}", " ").trim()

						assertStory.verifyContainsRegex("Copied SOAP Note for - ${name}", clipboardText, expectedData,false)
					}
				}
				else {
					LogStories.markWarning('No data found')
				}
			}

			LogStories.log("***********************************************************************************************************")
		}
	}

	@Keyword
	def GetLimitedElementList(def elementStorageList) {
		Boolean IS_LIMITED_ELEMENTS = GlobalVariable.G_IS_LIMITED_ELEMENTS

		if (IS_LIMITED_ELEMENTS) {
			def allowedNames = CommonStory.allowedNames
			elementStorageList = elementStorageList.findAll { elem -> elem in allowedNames }
		}

		return elementStorageList
	}
}