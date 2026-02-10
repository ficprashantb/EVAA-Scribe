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

public class EVAASteps {
	NavigateStory navigateStory = new NavigateStory()
	TestObjectStory testObjectStory = new TestObjectStory()
	AssertStory assertStory = new AssertStory();
	WaitStory waitStory = new WaitStory()

	@Keyword
	def verifyPatientConsentReceived(String isReceived) {
		LogStories.logInfo('----------------------Step AAF----------------------')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/button_Patient Consent Received'), 30, FailureHandling.STOP_ON_FAILURE)

		def chk_PatientConsentReceived = WebUI.getAttribute(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/button_Patient Consent Received'),
				'aria-checked')

		if(chk_PatientConsentReceived == isReceived) {
			LogStories.markPassed("Patient Consent Received?→ $chk_PatientConsentReceived")
		}
		else {
			LogStories.markFailed("Patient Consent Received?→ $chk_PatientConsentReceived")
		}

		assertStory.verifyMatch('Patient Consent Received?', chk_PatientConsentReceived, isReceived)
	}

	@Keyword
	def commonStepsForEVAA(String FirstName, LastName, String DOB, String FinalizedStatus = 'Pending', String MicStatus='Recording Not Started' ) {
		LogStories.logInfo('----------------------Step AAE----------------------')

		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'()

		GlobalVariable.IS_ENCOUNTER_ID = true
 
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'), 30, FailureHandling.STOP_ON_FAILURE)

		String PtName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'))

		String expectedPtName = "$FirstName $LastName"

		assertStory.verifyMatch('PatientName', PtName, expectedPtName)

		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('false')

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/button_Patient Consent Received'))

		LogStories.logInfo('Patient Consent Received checked.')

		WebUI.delay(5)

		LogStories.logInfo('----------------------Step F----------------------')
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		LogStories.logInfo('----------------------Step G----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName,  DOB, FinalizedStatus, MicStatus)
	}

	@Keyword
	def searchStringAndVerify(String searchText) {
		LogStories.logInfo('----------------------Step AAG----------------------')

		WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 30, FailureHandling.STOP_ON_FAILURE)

		TestObject searchInput = findTestObject('EVAAPage/EVAA Scribe/Header/input_Search')

		// Wait & type
		WebUI.waitForElementVisible(searchInput, 10)
		WebUI.clearText(searchInput)
		WebUI.sendKeys(searchInput, searchText)

		// Press ENTER using Selenium element
		WebUI.findWebElement(searchInput, 10).sendKeys(Keys.ENTER)

		LogStories.logInfo("Search: $searchText")

		// Wait for results to appear
		TestObject span_Search = testObjectStory.span_Search(searchText)
		WebUI.waitForElementVisible(span_Search, 10)

		// Verify results
		def elements = WebUI.findWebElements(span_Search, 5)
		assertStory.verifyGreaterThanOrEqual("Search", elements.size(), 1)

		// Re-locate input before clearing (VERY IMPORTANT)
		WebUI.waitForElementVisible(searchInput, 10)
		WebElement freshInput = WebUI.findWebElement(searchInput, 10)

		// Clear input safely
		freshInput.click()
		freshInput.sendKeys(Keys.chord(Keys.CONTROL, "a"))
		freshInput.sendKeys(Keys.BACK_SPACE)

		// Switch back
		WebUI.switchToDefaultContent()
	}

	@Keyword
	def verifyEVAAScribeAllDetails(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName ,String SearchText= 'b', String FinalizedStatus = 'Pending', String MicStatus='Completed' ) {
		LogStories.logInfo('----------------------Step AAD----------------------')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		LogStories.logInfo('----------------------Step M----------------------')
		CustomKeywords.'steps.EVAASteps.getAndStoreEVAAScribeSOAPNote'()

		LogStories.logInfo('----------------------Step N----------------------')
		CustomKeywords.'steps.EVAASteps.searchStringAndVerify'(SearchText)
	}

	@Keyword
	def verifyEVAAScribeDetails(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName ,String SearchText= 'b', String FinalizedStatus = 'Pending', String MicStatus='Completed') {
		LogStories.logInfo('----------------------Step AAK----------------------')

		String expectedPtName = "$FirstName $LastName"

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 60, FailureHandling.STOP_ON_FAILURE)

		LogStories.logInfo('----------------------Step H----------------------')
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		LogStories.logInfo('----------------------Step I----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeHeaderDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		LogStories.logInfo('----------------------Step J----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeSOAPNotesAndSpeakerNotes'(expectedPtName)

		LogStories.logInfo('----------------------Step K----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, DOB,FinalizedStatus , MicStatus)

		LogStories.logInfo('----------------------Step L----------------------')
		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Send_to_MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo('Send to MaximEyes button visible.')
	}

	@Keyword
	def unfinalizedDictationAfterFinalized(Boolean isExpandClose = false) {
		LogStories.logInfo('----------------------Step O----------------------')
		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized - Green'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Finalized'), FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo("Clicked on UnFinalized")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Status updated to Unfinalized'), 60, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Status updated to Unfinalized!")

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Finalized - Green'), 30, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalize - Blue'), 30, FailureHandling.STOP_ON_FAILURE)

		if(isExpandClose == true) {
			WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording'))

			LogStories.logInfo('Clicked on Expand Recording')
		}

		WebUI.delay(5)
	}

	@Keyword
	def finalizedAndSendToMaximEyes(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName, Boolean isExpandClose = true , Boolean isSendToEHR = true ) {
		LogStories.logInfo('----------------------Step AAC----------------------')

		String expectedPtName = "$FirstName $LastName"

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalize - Blue'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 60, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Finalize'), FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo("Clicked on Finalize")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Status updated to Finalized'), 60, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Status updated to Finalized")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized'), 60, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Send_to_MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(2)

		if(isSendToEHR) {
			LogStories.logInfo('----------------------Step P----------------------')
			CustomKeywords.'steps.EVAASteps.sendToAllSOAPNotesToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName,isExpandClose)
		}
	}

	@Keyword
	def sendToAllSOAPNotesToMaximEyes(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName, Boolean isExpandClose = true  ) {
		LogStories.logInfo('----------------------Step AAB----------------------')

		String expectedPtName = "$FirstName $LastName"

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes'))
		LogStories.logInfo("Clicked on Send to MaximEyes")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Sending SOAP notes and PDF to MaximEyes'), 60,
				FailureHandling.CONTINUE_ON_FAILURE)
		LogStories.markPassed("Sending SOAP notes and PDF to MaximEyes...")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Sent SOAP notes and PDF to MaximEyes successfully'), 60,
				FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Sent SOAP notes and PDF to MaximEyes successfully.")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized - Green'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(10)

		LogStories.logInfo('----------------------div_PatientName----------------------')
		navigateStory.retryAction {
			WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/div_PatientName'), 60, FailureHandling.STOP_ON_FAILURE)
		}

		LogStories.logInfo('----------------------Step Q----------------------')
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		LogStories.logInfo('----------------------Step R----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, DOB, 'Finalized', 'Completed')

		LogStories.logInfo('----------------------Step S----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeHeaderDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		if(isExpandClose == true) {
			WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording'))

			LogStories.logInfo('Clicked on Expand Recording')

			WebUI.delay(5)
		}
	}

	@Keyword
	def finalizedAndSendIndividualElementsToMaximEyes(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName, Boolean isFinalize = true) {
		LogStories.logInfo('----------------------Step AAA----------------------')

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

		LogStories.logInfo('----------------------Step T----------------------')
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		LogStories.logInfo('----------------------Step U----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName,  DOB, 'Finalized', 'Completed')

		LogStories.logInfo('----------------------Step V----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeHeaderDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)


		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}

		def elementStorageList = VariableStories.elementStorage

		for (String name : elementStorageList) {
			LogStories.logInfo("Element from Storage → ${name}")

			LogStories.logInfo("============================Element Name - ${name}============================")

			String moduleName = CommonStory.moduleMapForDirectDictation.get(name)

			TestObject sectionTO = testObjectStory.img_SendToMaximeyesWithParams(moduleName)

			if (!sectionTO) {
				LogStories.markWarning("No TestObject mapped for → ${name}")
				continue
			}

			CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(name)

			WebUI.waitForElementVisible(sectionTO, 20)

			WebUI.click(sectionTO)
			LogStories.logInfo("Clicked on Send to MaximEyes for element- ${name}");

			// Toast checks
			TestObject failedSOAPNote = findTestObject('EVAAPage/EVAA Scribe/Toast/Failed to send SOAP note to MaximEyes')
			boolean	isVisible = WebUI.waitForElementVisible(failedSOAPNote, 2,FailureHandling.OPTIONAL)
			if(isVisible) {
				LogStories.markFailed("${name} - Failed to send SOAP note to MaximEyes.")
			}

			TestObject passSOAPNote = findTestObject('EVAAPage/EVAA Scribe/Toast/SOAP note sent to MaximEyes successfully')
			isVisible = WebUI.waitForElementVisible(passSOAPNote, 10, FailureHandling.CONTINUE_ON_FAILURE)
			if(isVisible) {
				LogStories.markPassed("${name} - SOAP note sent to MaximEyes successfully.")
			}

			boolean	isRefreshPresent = WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_refresh'), 5, FailureHandling.OPTIONAL)
			if(isRefreshPresent) {
				WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/button_refresh'))
				LogStories.logInfo("Clicked on Refresh button")

				WebUI.delay(2)
			}

			LogStories.logInfo('----------------------Step W----------------------')
			CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

			LogStories.logInfo('----------------------Step X----------------------')
			CustomKeywords.'steps.EVAASteps.verifyIndividualSOAPNoteSentToMaximeyes'(name, true, isRefreshPresent)

			LogStories.logInfo('----------------------Step Y----------------------')
			CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)
		}
	}

	@Keyword
	def verifyIndividualSOAPNoteSentToMaximeyes(String key, Boolean isElementText = false , Boolean isRefreshPresent = false) {
		LogStories.logInfo('----------------------Step AAH----------------------')

		def variableKey = CommonStory.sectionMapForStorageKey.get(key)
		String expectedData = VariableStories.getItem(variableKey)

		LogStories.logInfo("********************SOAP Note - ${key}*********************")

		WebUI.delay(4)

		// ===== Chief Complaint =====
		if (key == "ChiefComplaint" && !CommonStory.isNullOrEmpty(expectedData)) {
			CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

			String actualChiefComplaint = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea Patient Chief Complaint'), 'value', FailureHandling.STOP_ON_FAILURE)

			actualChiefComplaint=	actualChiefComplaint?.replaceAll(":(?=.*:)", "")
			expectedData=	expectedData?.replaceAll(":(?=.*:)", "")

			assertStory.verifyMatch("Chief Complaint", actualChiefComplaint, expectedData)
		}

		// ===== HPI =====
		else if (key == "HPI" && !CommonStory.isNullOrEmpty(expectedData)) {
			CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

			String actualHPI = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea HPI Notes'), 'value', FailureHandling.STOP_ON_FAILURE)

			actualHPI=	actualHPI?.replaceAll(":(?=.*:)", "")
			expectedData=	expectedData?.replaceAll(":(?=.*:)", "")
			assertStory.verifyMatch("HPI", actualHPI, expectedData)
		}

		// ===== Current Eye Symptoms =====
		else if (key == "CurrentEyeSymptoms" && !CommonStory.isNullOrEmpty(expectedData)) {
			List currentEyeSymptomsList = CommonStory.getListObject(expectedData)
			if (currentEyeSymptomsList.size() > 0) {
				CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

				for (def currentEyeSymptoms : currentEyeSymptomsList) {
					def result = CommonStory.getKeyValueDetails(currentEyeSymptoms, 'CES')
					if (result) {
						String expected = result._expected
						String name = result._name
						if (expected == 'True') continue
							TestObject input_CurrentEyeSymptoms = testObjectStory.input_CurrentEyeSymptoms(name)
						if (result._key?.toLowerCase()?.contains('additional')) {
							input_CurrentEyeSymptoms = findTestObject('EncounterPage/Encounter Details/Current Eye Symptoms/textarea_Additional_Notes_CES')
						}

						WebUI.waitForElementVisible(input_CurrentEyeSymptoms, 10, FailureHandling.OPTIONAL)

						String actual = WebUI.getAttribute(input_CurrentEyeSymptoms, 'value', FailureHandling.OPTIONAL)

						actual=	actual?.replaceAll(":(?=.*:)", "")
						expected=	expected?.replaceAll(":(?=.*:)", "")

						assertStory.verifyMatch("Current Eye Symptoms- $name", actual, expected)
						verifyRadioButtonIsChecked(currentEyeSymptoms, name, 'CES')
					}
				}
			} else {
				LogStories.markWarning('No Current Eye Symptoms found')
			}
		}

		// ===== Allergies =====
		else if (key == "Allergies" && !CommonStory.isNullOrEmpty(expectedData)) {
			List allergiesList = CommonStory.getListObject(expectedData)
			if (allergiesList.size() > 0) {
				CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

				int index = 1
				for (String allergies : allergiesList) {
					TestObject tableAllergies = testObjectStory.tableAllergies(index)

					WebUI.waitForElementVisible(tableAllergies, 10, FailureHandling.OPTIONAL)

					String actual = WebUI.getText(tableAllergies, FailureHandling.OPTIONAL)

					actual=	actual?.replaceAll(":(?=.*:)", "")
					String expected = allergies?.replaceAll(":(?=.*:)", "")

					assertStory.verifyMatch("Allergies", actual, expected)
					index++
				}
			} else {
				LogStories.markWarning('No Allergies found')
			}
		}

		// ===== Medications =====
		else if (key == "Medications" && !CommonStory.isNullOrEmpty(expectedData)) {
			List medicationsList = CommonStory.getListObject(expectedData)
			if (medicationsList.size() > 0) {
				CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

				int index = 1
				for (int i = 0; i < medicationsList.size(); i++) {
					def result = CommonStory.getKeyValueDetails(medicationsList[i], 'MED')
					if (!result) continue
						String expected = result._expected
					if (result._key == 'Generic Name') continue
						if (i + 1 < medicationsList.size()) {
							def nextResult = CommonStory.getKeyValueDetails(medicationsList[i + 1], 'MED')
							if (nextResult?._key == 'Generic Name') {
								expected = "${expected} ${nextResult._expected}"
							}
						}
					TestObject tableMedications = testObjectStory.tableMedications(index)

					WebUI.waitForElementVisible(tableMedications, 10, FailureHandling.OPTIONAL)

					String actual = WebUI.getText(tableMedications, FailureHandling.OPTIONAL)

					actual=	actual?.replaceAll(":(?=.*:)", "")
					expected=	expected?.replaceAll(":(?=.*:)", "")

					assertStory.verifyContainsRegex("Medications", actual, expected)
					index++
				}
			} else {
				LogStories.markWarning('No Medications found')
			}
		}

		// ===== Review Of Systems =====
		else if (key == "ReviewOfSystems" && !CommonStory.isNullOrEmpty(expectedData)) {
			List reviewList = CommonStory.getListObject(expectedData)
			if (reviewList.size() > 0) {
				CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

				for (def review : reviewList) {
					def result = CommonStory.getKeyValueDetails(review, 'ROS')
					if (result) {
						String expected = result._expected
						String name = result._name
						if (expected == 'True') continue
							TestObject input_Review_of_Systems = testObjectStory.input_Review_of_Systems(name)
						if (result._key?.toLowerCase()?.contains('additional')) {
							input_Review_of_Systems = findTestObject('EncounterPage/Encounter Details/Review Of Systems/textarea_Additional_Notes_ROS')
						}

						WebUI.waitForElementVisible(input_Review_of_Systems, 10, FailureHandling.OPTIONAL)

						String actual = WebUI.getAttribute(input_Review_of_Systems, 'value', FailureHandling.OPTIONAL)

						actual=	actual?.replaceAll(":(?=.*:)", "")
						expected =	expected?.replaceAll(":(?=.*:)", "")

						assertStory.verifyMatch("Review Of Systems- $name", actual, expected)
						verifyRadioButtonIsChecked(review, name, 'ROS')
					}
				}
			} else {
				LogStories.markWarning('No Review Of Systems found')
			}
		}

		// ===== Problems =====
		else if (key == "Problems" && !CommonStory.isNullOrEmpty(expectedData)) {
			List problemsList = CommonStory.getListObject(expectedData)
			if (problemsList.size() > 0) {
				CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

				int index = 1
				for (String expected : problemsList) {
					TestObject tableProblems = testObjectStory.tableProblems(index)

					WebUI.waitForElementVisible(tableProblems, 10, FailureHandling.OPTIONAL)

					String actual = WebUI.getText(tableProblems, FailureHandling.OPTIONAL)

					actual=	actual?.replaceAll(":(?=.*:)", "")
					expected =	expected?.replaceAll(":(?=.*:)", "")

					assertStory.verifyMatch("Problems", actual, expected)
					index++
				}
			} else {
				LogStories.markWarning('No Problems found')
			}
		}

		//		// ===== Differential Diagnosis =====
		//		else if (key == "DifferentialDiagnosis" && !CommonStory.isNullOrEmpty(expectedData)) {
		//			List diffDiagnosisList = CommonStory.getListObject(expectedData)
		//			if (diffDiagnosisList.size() > 0) {
		//				CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)
		//
		//				for (def diffDiagnosis : diffDiagnosisList) {
		//					def result = CommonStory.getKeyValueDetails(diffDiagnosis, 'DD')
		//					if (result) {
		//						String expected = "${result._key}, ${result._expected}"
		//						TestObject tableFDDifferentialDiagnosis = testObjectStory.tableFDDifferentialDiagnosis(result._key, result._expected)
		//
		//						WebUI.waitForElementVisible(tableFDDifferentialDiagnosis, 10, FailureHandling.OPTIONAL)
		//
		//						boolean isPresentDD = WebUI.waitForElementPresent(tableFDDifferentialDiagnosis, 1, FailureHandling.OPTIONAL)
		//						assertStory.verifyMatch("Differential Diagnosis- $expected", isPresentDD, true)
		//					}
		//				}
		//			} else {
		//				LogStories.markWarning('No Differential Diagnosis found')
		//			}
		//		}

		// ===== Assessment =====
		else if (key == "Assessment" && !CommonStory.isNullOrEmpty(expectedData)) {
			List assessmentList = CommonStory.getListObject(expectedData)
			if (assessmentList.size() > 0) {
				CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

				String expectedAssessment = assessmentList.join('\n')
				String actualAssessment = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea Assessments'), 'value', FailureHandling.OPTIONAL)

				String actual=	actualAssessment?.replaceAll(":(?=.*:)", "")
				// Remove leading number + dot + spaces at the start of each line
				actual = actual.replaceAll(/(?m)^\d+\.\s*/, '')

				String expected = expectedAssessment?.replaceAll(":(?=.*:)", "")

				assertStory.verifyMatch("Assessment", actual, expected)
			} else {
				LogStories.markWarning('No Assessment found')
			}
		}

		// ===== Plan =====
		else if (key == "Plan" && !CommonStory.isNullOrEmpty(expectedData)) {
			List plansList = CommonStory.getListObject(expectedData)
			if (plansList.size() > 0) {
				CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

				String expectedPlans = plansList.join('\n')
				String actualPlans = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/div Plans'), 'value', FailureHandling.OPTIONAL)
				if (CommonStory.isNullOrEmpty(actualPlans)) {
					actualPlans = WebUI.getText(findTestObject('EncounterPage/Encounter Details/div Plans'), FailureHandling.OPTIONAL)
				}

				String actual=	actualPlans?.replaceAll(":(?=.*:)", "")
				// Remove leading number + dot + spaces at the start of each line
				actual = actual.replaceAll(/(?m)^\d+\.\s*/, '')

				String expected = expectedPlans?.replaceAll(":(?=.*:)", "")

				assertStory.verifyMatch("Plans", actual, expected)
			} else {
				LogStories.markWarning('No Plans found')
			}
		}

		// ===== Eye Diseases =====
		else if (key == "EyeDiseases" && !CommonStory.isNullOrEmpty(expectedData)) {
			List eyeDiseaseList = CommonStory.getListObject(expectedData)
			if (eyeDiseaseList.size() > 0) {

				CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

				for (def eyeDisease : eyeDiseaseList) {
					def result = CommonStory.getKeyValueDetails(eyeDisease, 'ED')
					if (result) {
						String expected = result._expected
						String name = result._name
						if (expected == 'True') continue
							TestObject inputEyeDiseases = testObjectStory.inputEyeDiseases(name)
						if (result._key?.toLowerCase()?.contains('additional')) {
							inputEyeDiseases = findTestObject('EncounterPage/Encounter Details/Eye Diseases/textarea_Additional_Notes_EyeDiseases')
						}

						WebUI.waitForElementVisible(inputEyeDiseases, 10, FailureHandling.OPTIONAL)

						String actual = WebUI.getAttribute(inputEyeDiseases, 'value', FailureHandling.OPTIONAL)

						actual=	actual?.replaceAll(":(?=.*:)", "")
						expected=	expected?.replaceAll(":(?=.*:)", "")

						assertStory.verifyMatch("Eye Diseases- $name", actual, expected)
						verifyRadioButtonIsChecked(eyeDisease, name, 'ED')
					}
				}
			} else {
				LogStories.markWarning('No Eye Diseases found')
			}
		}
		// ===== Mental and Functional Status =====
		else if (key == "MentalAndFunctionalStatus" && !CommonStory.isNullOrEmpty(expectedData)) {
			List mentalAndFunctionalStatusList = CommonStory.getListObject(expectedData)
			if (mentalAndFunctionalStatusList.size() > 0) {

				CustomKeywords.'steps.EVAASteps.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

				for (def mentalAndFunctionalStatus : mentalAndFunctionalStatusList) {
					def result = CommonStory.getKeyValueDetails(mentalAndFunctionalStatus, 'MFS')

					if (result) {
						String expected = result._expected
						String name = result._name

						TestObject inputMentalAndFunctionalStatus = testObjectStory.inputMentalAndFunctionalStatus(name)

						WebUI.waitForElementVisible(inputMentalAndFunctionalStatus, 10, FailureHandling.OPTIONAL)

						String actual = WebUI.getAttribute(inputMentalAndFunctionalStatus, 'value', FailureHandling.OPTIONAL)

						actual=	actual?.replaceAll(":(?=.*:)", "")
						expected=	expected?.replaceAll(":(?=.*:)", "")

						assertStory.verifyMatch("Mental and Functional Status- $name", actual, expected)
					}
				}
			} else {
				LogStories.markWarning('No Mental and Functional Status found')
			}
		}
	}

	@Keyword
	def navigateToEncounterElement(String key, Boolean isElementText = false , Boolean isRefreshPresent = false) {
		LogStories.logInfo('----------------------Step AAI----------------------')

		def page, element

		TestObject testObj

		switch (key) {
			case "ChiefComplaint":
				page    = "CC & History Review"
				element = "Chief Complaint"
				testObj = findTestObject('EncounterPage/Encounter Details/textarea Patient Chief Complaint')
				break

			case "HPI":
				page    = "CC & History Review"
				element = "Chief Complaint"
				testObj = findTestObject('EncounterPage/Encounter Details/textarea HPI Notes')
				break

			case "CurrentEyeSymptoms":
				page    = "Medical History"
				element = "Current Eye Symptoms"
				testObj = findTestObject('EncounterPage/Encounter Details/Current Eye Symptoms/divCurrentEyeSymptoms')
				break

			case "Allergies":
				page    = "CC & History Review"
				element = "Allergies"
				testObj = findTestObject('EncounterPage/Encounter Details/trAllergies')
				break

			case "Medications":
				page    = "CC & History Review"
				element = "Medications"
				testObj = findTestObject('EncounterPage/Encounter Details/trMedications')
				break

			case "ReviewOfSystems":
				page    = "Medical History"
				element = "Review of Systems - Brief"
				testObj = findTestObject('EncounterPage/Encounter Details/Review Of Systems/divReviewOfSystems')
				break

			case "Problems":
				page    = "CC & History Review"
				element = "Problems"
				testObj = findTestObject('EncounterPage/Encounter Details/trProblems')
				break

			case "DifferentialDiagnosis":
				page    = "Final Findings"
				element = "Final Diagnoses"
				testObj = findTestObject('EncounterPage/Encounter Details/trFDDifferentialDiagnosis')
				break

			case "Assessment":
				page    = "Final Findings"
				element = "Final Diagnoses"
				testObj = findTestObject('EncounterPage/Encounter Details/textarea Assessments')
				break

			case "Plan":
				page    = "Final Findings"
				element = "Final Diagnoses"
				testObj = findTestObject('EncounterPage/Encounter Details/div Plans')
				break

			case "EyeDiseases":
				page    = "Medical History"
				element = "Eye Diseases"
				testObj = findTestObject('EncounterPage/Encounter Details/Eye Diseases/textarea_Additional_Notes_EyeDiseases')
				break

			case "MentalAndFunctionalStatus":
				page    = "Medical History"
				element = "Mental and Functional Status"
				testObj = findTestObject('EncounterPage/Encounter Details/Mental and Functional Status/input_MOOD_AFFECT')
				break

			default:
				LogStories.markWarning("Unknown encounter key: ${key}")
				return
		}

		// Navigate only if element not already present
		if (!isRefreshPresent) {
			LogStories.logInfo('----------------------Step Z----------------------')
			navigateStory.SelectEncounterElementFromLeftNavOnEncounter([
				('pElementPage'): page,
				('pElement')    : element
			])
		}

		// Wait for the target element
		WebUI.waitForElementVisible(testObj, 10, FailureHandling.OPTIONAL)

		if (isElementText) {
			waitStory.waitForElementText(testObj, 30)
		}

		LogStories.logInfo("Navigated to Encounter Element: ${key}")
	}

	@Keyword
	def verifyEVAAScribeHeaderDetails(String FirstName, String LastName, String DOB , String Provider_FirstName, String Provider_LastName) {
		WebUI.delay(5)

		LogStories.logInfo('----------------------Step AA----------------------')

		TestObject toPatientName = findTestObject('EVAAPage/EVAA Scribe/Header/PatientName')
		waitStory.waitForElementText(toPatientName, 30)

		WebUI.waitForElementVisible(toPatientName, 60, FailureHandling.STOP_ON_FAILURE)

		String _ptKey = "${FirstName}_${LastName}".toUpperCase()

		String PtName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'))

		String expectedPtName = "${FirstName} ${LastName}"

		assertStory.verifyMatch("Header→→ Patient Name", PtName, expectedPtName)


		String actualPTDOB = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/PatientDOB'))

		String expectedPTDOB = DOB

		if (!CommonStory.isNullOrEmpty(DOB) &&
				!DOB.trim().equalsIgnoreCase("Invalid Date")) {
			expectedPTDOB = CustomKeywords.'DateHelper.GetFormattedDate'(DOB, 'M/d/yyyy')
		}

		assertStory.verifyMatch("Header→→ Patient DOB", actualPTDOB, expectedPTDOB)


		String PtId = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/PatientId'))

		PtId = PtId?.replaceAll('\\D+', '')

		String patientIdKey = "FP_${_ptKey}_PATIENT_ID"

		String expectedPTId = VariableStories.getItem(patientIdKey)

		assertStory.verifyMatch("Header→→ Patient ID", PtId, expectedPTId)


		String EncId = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/EncounterId'))

		EncId = EncId?.replaceAll('\\D+', '')

		String encIdKey = "ENC_${_ptKey}_ENCOUNTER_ID"

		String expectedEncId = VariableStories.getItem(encIdKey)

		assertStory.verifyMatch("Header→→ Encounter ID", EncId, expectedEncId)


		String Provider = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/Provider'))

		Provider = Provider?.replace('Prov:', '').replace('|', '').replaceAll('\\s+', '')

		String providerKey = "ENC_${_ptKey}_PROVIDER_ID"

		String expectedProvider = "${Provider_FirstName}${Provider_LastName}"

		assertStory.verifyMatch("Header→→ Provider", Provider, expectedProvider)
	}

	@Keyword
	def getSOAPNotesAndSpeakerNotesWordCount(String expectedPtName ) {
		LogStories.logInfo('----------------------Step AB----------------------')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 30, FailureHandling.STOP_ON_FAILURE)

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		LogStories.logInfo("SOAP Notes→ $soapNotes")

		int wordCountSOAPNotes = soapNotes.trim().split('\\s+').length

		VariableStories.setItem('SOAP_NOTE_WORDS_COUNT', wordCountSOAPNotes)

		def speakerNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes'))

		LogStories.logInfo("Speaker Notes→ $speakerNotes")

		//Speaker Dictation Word Count
		int wordCount = speakerNotes.trim().split('\\s+').length

		VariableStories.setItem('SPEAKER_NOTE_WORDS_COUNT', wordCount)
	}

	@Keyword
	def verifyAppendedSOAPNotesAndSpeakerNotes(String expectedPtName ) {
		LogStories.logInfo('----------------------Step AC----------------------')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name'), 30, FailureHandling.STOP_ON_FAILURE)

		String ptName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name'))
		expectedPtName = "Pt: $expectedPtName"

		assertStory.verifyMatch("Patient Name", ptName, expectedPtName)

		String ptDictationDt = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Pt Dictation Date'))

		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'()

		if (CommonStory.isNullOrEmpty(expectedPtDictationDt) == false) {
			expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(expectedPtDictationDt, 'M/d/yyyy')
		}

		if (CommonStory.isNullOrEmpty(ptDictationDt) == false) {
			ptDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(ptDictationDt,'M/d/yyyy')
		}

		assertStory.verifyMatch("Patient Dictation Date",ptDictationDt, expectedPtDictationDt)

		LogStories.markPassed("Speaker Patient Dictation Date→ $ptDictationDt")

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		LogStories.logInfo("SOAP Notes→ $soapNotes")

		int wordCountSOAPNotes = soapNotes.trim().split('\\s+').length

		def expectedWordCountSOAPNotes = VariableStories.getItem('SOAP_NOTE_WORDS_COUNT')

		assertStory.verifyGreaterThanOrEqual("SOAP Notes Total Words", wordCountSOAPNotes, expectedWordCountSOAPNotes )

		def speakerNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes'))

		LogStories.logInfo("Speaker Notes→ $speakerNotes")

		//Speaker A
		int countSpeakerA = speakerNotes.findAll('Speaker A').size()

		assertStory.verifyGreaterThanOrEqual("'Speaker A' Word found", countSpeakerA, 1)

		//Speaker B
		int countSpeakerB = speakerNotes.findAll('Speaker B').size()

		assertStory.verifyGreaterThanOrEqual("'Speaker B' Word found", countSpeakerB, 1)

		//Speaker Dictation Word Count
		int wordCount = speakerNotes.trim().split('\\s+').length

		def expectedWordCount = VariableStories.getItem('SPEAKER_NOTE_WORDS_COUNT')

		assertStory.verifyGreaterThanOrEqual("Speaker Dictation Total Words", wordCount, expectedWordCount)

		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')
	}

	@Keyword
	def verifyEVAAScribeSOAPNotesAndSpeakerNotes(String expectedPtName ) {
		LogStories.logInfo('----------------------Step AD----------------------')

		int wordMaxCount = 1
		int maxCount = 1

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name'), 30, FailureHandling.STOP_ON_FAILURE)

		String ptName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name'))
		expectedPtName = "Pt: $expectedPtName"

		assertStory.verifyMatch("Patient Name", ptName, expectedPtName)

		String ptDictationDt = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Pt Dictation Date'))

		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(null, 'M/d/yyyy')

		if (CommonStory.isNullOrEmpty(ptDictationDt) == false) {
			ptDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(ptDictationDt, 'M/d/yyyy')
		}

		assertStory.verifyMatch("Patient Dictation Date",ptDictationDt, expectedPtDictationDt)

		LogStories.markPassed("Speaker Patient Dictation Date→ $ptDictationDt")

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		LogStories.logInfo("SOAP Notes→ $soapNotes")

		int wordCountSOAPNotes = soapNotes.trim().split('\\s+').length

		//		VariableStories.setItem("SOAP_NOTE_LENGTH", wordCountSOAPNotes)
		//		VariableStories.setItem("SOAP_NOTES", soapNotes)

		assertStory.verifyGreaterThanOrEqual("SOAP Notes Total Words", wordCountSOAPNotes, wordMaxCount)

		def speakerNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes'))

		LogStories.logInfo("Speaker Notes→ $speakerNotes")

		//Speaker A
		int countSpeakerA = speakerNotes.findAll('Speaker A').size()

		assertStory.verifyGreaterThanOrEqual("'Speaker A' Word found", countSpeakerA, maxCount)

		//Speaker B
		int countSpeakerB = speakerNotes.findAll('Speaker B').size()

		assertStory.verifyGreaterThanOrEqual("'Speaker B' Word found", countSpeakerB, maxCount)

		//Speaker Dictation Word Count
		int wordCount = speakerNotes.trim().split('\\s+').length

		assertStory.verifyGreaterThanOrEqual("Speaker Dictation Total Words", wordCount, wordMaxCount)

		LogStories.logInfo('----------------------Step AE----------------------')
		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')
	}

	@Keyword
	def verifySOAPNotesAndSpeakerNotesNotGenerated(String expectedPtName) {
		LogStories.logInfo('----------------------Step AAJ----------------------')

		WebUI.delay(5)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name'), 30, FailureHandling.STOP_ON_FAILURE)

		String ptName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name'))
		expectedPtName = "Pt: $expectedPtName"

		assertStory.verifyMatch("Patient Name", ptName, expectedPtName)

		String ptDictationDt = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Pt Dictation Date'))

		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(null, 'M/d/yyyy')

		if (CommonStory.isNullOrEmpty(ptDictationDt) == false) {
			ptDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(ptDictationDt, 'M/d/yyyy')
		}

		assertStory.verifyMatch("Patient Dictation Date",ptDictationDt, expectedPtDictationDt)

		LogStories.markPassed("Speaker Patient Dictation Date→ $ptDictationDt")

		String soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), FailureHandling.OPTIONAL)

		LogStories.logInfo("SOAP Notes→ $soapNotes")

		assertStory.verifyMatch("SOAP Notes Total Words", soapNotes, "")

		def speakerNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes'), FailureHandling.OPTIONAL)
		LogStories.logInfo("Speaker Notes→ $speakerNotes")

		assertStory.verifyMatch("Speaker Dictation Total Words", speakerNotes, "No transcript available. Upload an audio file to get started.")
	}

	@Keyword
	def verifyEVAAScribeLeftSidePanel(String PatientName, String DOB, String FinalizedStatus, String MicStatus ) {
		LogStories.logInfo('----------------------Step AF----------------------')

		TestObject toPatientName = findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/div_PatientName')

		WebUI.waitForElementVisible(toPatientName, 60, FailureHandling.STOP_ON_FAILURE)

		waitStory.waitForElementText(toPatientName, 30)

		WebUI.delay(5)

		def div_PatientName = WebUI.getText(toPatientName)

		assertStory.verifyMatch("Left Side Panel→→ Patient Name", div_PatientName, PatientName)

		def actual_PatientDOB_DictationDate = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/div_PatientDOB_DictationDate'))

		def expectedPTDOBText = DOB
		if (!CommonStory.isNullOrEmpty(DOB) == true) {
			String ageKey = "FP_${PatientName}".toUpperCase()

			ageKey = "${ageKey}_PATIENT_AGE_AT_EXAM"
			ageKey = ageKey.replaceAll(/\s+/, '_')

			def age =	VariableStories.getItem(ageKey)
			if (CommonStory.isNullOrEmpty(age) == false) {
				age = age.toString()
						.replaceAll('YRS', "")
						.trim()
			}

			String expectedPTDOB = CustomKeywords.'DateHelper.GetFormattedDate'(DOB, 'M/d/yyyy')

			expectedPTDOBText =  "$expectedPTDOB ($age)"
		}

		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(null,'M/d/yyyy')

		def expected_PatientDOB_DictationDate = "${expectedPTDOBText} | ${expectedPtDictationDt}"

		assertStory.verifyMatch('Left Side Panel→→ Patient Name, DOB, Dictation Date',actual_PatientDOB_DictationDate, expected_PatientDOB_DictationDate)

		// Finalized Status
		boolean isPending = WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/svg_Pending'), 2, FailureHandling.OPTIONAL)
		String _finalizedStatus = isPending ? 'Pending' : ''

		boolean isFinalized = WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/svg_Finalized'), 1, FailureHandling.OPTIONAL)
		_finalizedStatus = isFinalized ? 'Finalized' : _finalizedStatus

		assertStory.verifyMatch('Left Side Panel→→ Finalized Status', _finalizedStatus, FinalizedStatus)

		// Mic Status
		boolean notStarted = WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/img_grey_mic-status'), 2, FailureHandling.OPTIONAL)
		String _micStatus = notStarted ? 'Recording Not Started' : ''

		boolean isInProgress = WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/img_green_mic-status'), 1, FailureHandling.OPTIONAL)
		_micStatus = isInProgress ? 'In Progress' : _micStatus

		boolean isCompleted = WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/img_blue_mic-status'), 1, FailureHandling.OPTIONAL)
		_micStatus = isCompleted ? 'Completed' : _micStatus

		assertStory.verifyMatch('Left Side Panel→→ Mic Status', _micStatus, MicStatus)
	}

	private void captureSectionDirectDictation(
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
	def getAndStoreEVAAScribeDirectDictationNote() {
		LogStories.logInfo('----------------------Step AG----------------------')

		CustomKeywords.'steps.EVAASteps.getAndStoreEVAAScribeSOAPNote'()
	}

	@Keyword
	def getAndStoreEVAAScribeSOAPNote() {
		LogStories.logInfo('----------------------Step AH----------------------')

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
		LogStories.logInfo('----------------------Step AI----------------------')

		for (key in  VariableStories.elementStorage) {
			println "Key: ${key}"

			CustomKeywords.'steps.EVAASteps.verifyIndividualSOAPNoteSentToMaximeyes'(key)
		}
	}

	@Keyword
	def verifyRadioButtonIsChecked(String text, String name, String key) {
		LogStories.logInfo('----------------------Step AJ----------------------')

		//		def type = ''
		//		def resultODOSOU = CommonStory.getODOSOU(text)
		//		if (resultODOSOU) {
		//			type = resultODOSOU.type
		//			text = resultODOSOU.value
		//		}
		//
		//		String txt;
		//
		//		/** prefix handling **/
		//		switch (key) {
		//			case "ED":
		//				name = "Eye_Diseases.${name}"
		//				txt="Eye Diseases"
		//				break
		//
		//			case "CES":
		//				name = "Current_Eye_Symptoms.${name}"
		//				txt = "Current Eye Symptoms"
		//				break
		//
		//			case "ROS":
		//				name = "Review_of_Systems_Brief.${name}"
		//				txt = "Review of Systems Brief"
		//				break
		//
		//			default:
		//				LogStories.markWarning("Unknown key → $key")
		//				return
		//		}
		//
		//		if (CommonStory.isNullOrEmpty(type)) {
		//			LogStories.logInfo("No type present for element → '$txt'.")
		//			return
		//		}
		//
		//		/** name formatting **/
		//		if (type in ["OD", "OS", "OU"]) {
		//			name = name.replace("_NOTES", "_LOCATION")
		//		} else if (type in ["Yes", "No", "Unk"]) {
		//			name = name.replace("_NOTES", "")
		//		}
		//
		//		/** build TestObject **/
		//		TestObject rbTestObject = testObjectStory.input_RadioButton(name, type)
		//
		//		if (!rbTestObject) {
		//			LogStories.markFailed("Radio Button not found → $text ($name:$type)")
		//			return
		//		}
		//
		//		/** find web element safely **/
		//		WebElement el = WebUI.findWebElement(rbTestObject, 5, FailureHandling.OPTIONAL)
		//
		//		if (!el) {
		//			LogStories.markFailed("Radio button missing in DOM → $text ($name:$type)")
		//			return
		//		}
		//
		//		boolean isChecked = el.isSelected()
		//
		//		LogStories.logInfo("Checked? $isChecked → ($text, $name, $type)")
		//
		//		assertStory.verifyMatch("${text}-${type}", isChecked.toString(), "true")
	}

	@Keyword
	def generateSOAPNoteByAppendPauseResumeStop(String FileTime, String RecordFilePath) {
		LogStories.logInfo('----------------------Step AK----------------------')

		int fileTimeinSeconds = Integer.valueOf(FileTime)

		int pauseTimeinSeconds = 10

		fileTimeinSeconds = (fileTimeinSeconds - pauseTimeinSeconds)

		LogStories.logInfo("File Path $RecordFilePath")

		def fakeMic = new FakeMicStream(RecordFilePath)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 5, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'))

		LogStories.logInfo('Clicked on Append Audio Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/div_Append-mode recording started'), 10, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Append-mode recording started")

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(pauseTimeinSeconds)

		fakeMic.pause()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'))
		LogStories.logInfo('Clicked on Pause Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'), 20, FailureHandling.OPTIONAL)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'))
		LogStories.logInfo('Clicked on Resume Button')

		fakeMic.resume()

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'), 10, FailureHandling.OPTIONAL)

		WebUI.delay(fileTimeinSeconds)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.STOP_ON_FAILURE)

		fakeMic.stop()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Append Audio Button')

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 30, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 20, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'), 20, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.OPTIONAL)
	}

	@Keyword
	def generateSOAPNoteByRecordPauseResumeStop(String FileTime, String RecordFilePath) {
		LogStories.logInfo('----------------------Step AL----------------------')

		int fileTimeinSeconds = Integer.valueOf(FileTime)

		int pauseTimeinSeconds = 10

		int resumeTimeinSeconds = 5

		fileTimeinSeconds = (fileTimeinSeconds - (pauseTimeinSeconds+resumeTimeinSeconds))

		LogStories.logInfo("File Path $RecordFilePath")

		def fakeMic = new FakeMicStream(RecordFilePath)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		LogStories.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		WebUI.delay(pauseTimeinSeconds)

		fakeMic.pause()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'))
		LogStories.logInfo('Clicked on Pause Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'),10, FailureHandling.OPTIONAL)

		WebUI.delay(resumeTimeinSeconds)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'),10, FailureHandling.STOP_ON_FAILURE)

		fakeMic.resume()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'))
		LogStories.logInfo('Clicked on Resume Button')

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'), 10, FailureHandling.OPTIONAL)

		WebUI.delay(fileTimeinSeconds)

		fakeMic.stop()

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
	def generateSOAPNoteByAppendStartStop(String FileTime, String RecordFilePath) {
		LogStories.logInfo('----------------------Step AM----------------------')

		def fileTimeinSeconds = Integer.valueOf(FileTime)

		LogStories.logInfo("File Path $RecordFilePath")

		def fakeMic = new FakeMicStream(RecordFilePath)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'))

		LogStories.logInfo('Clicked on Append Audio Button')

		fakeMic.start()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/div_Append-mode recording started'), 10, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Append-mode recording started")

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(fileTimeinSeconds)

		fakeMic.stop()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Append Audio Button')

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 30, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 30, FailureHandling.OPTIONAL)
	}

	@Keyword
	def generateSOAPNoteByRecordStartStop(String FileTime, String UploadFilePath) {
		LogStories.logInfo('----------------------Step AN----------------------')

		int fileTimeinSeconds = Integer.valueOf(FileTime)

		LogStories.logInfo("File Path $UploadFilePath")

		def fakeMic = new FakeMicStream(UploadFilePath)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		LogStories.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		//		fakeMic.start()
		//		LogStories.logInfo('Clicked on fakeMic Start Record Button')

		WebUI.delay(fileTimeinSeconds)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Record Button')

		//		fakeMic.stop()
		//		LogStories.logInfo('Clicked on fakeMic Stop Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)
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
		LogStories.logInfo('----------------------Step AO----------------------')

		// Log the file path
		LogStories.logInfo("File Path: " + UploadFilePath)

		// Upload the file
		//		WebUI.uploadFile(findTestObject('EVAAPage/EVAA Scribe/Menu/defile input'), UploadFilePath)

		TestObject upload = findTestObject('EVAAPage/EVAA Scribe/Menu/defile input')

		//		WebUI.executeJavaScript(
		//		  "arguments[0].value = ''",
		//		  Arrays.asList(WebUI.findWebElement(upload, 5))
		//		)

//		WebUI.uploadFileToWeb(upload, UploadFilePath)
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
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.STOP_ON_FAILURE)

		// Wait for SOAP Notes element
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		// Ensure Finalize button is clickable
		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		// Wait for SOAP Notes section
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def UploadingSOAPNoteFile(String UploadFilePath) {
		LogStories.logInfo('----------------------Step AP----------------------')

		LogStories.logInfo("File Path $UploadFilePath")

//		WebUI.uploadFileToWeb(findTestObject('EVAAPage/EVAA Scribe/Menu/defile input'), UploadFilePath)
		CustomKeywords.'com.katalon.testcloud.FileExecutor.uploadFileToWeb'(findTestObject('EVAAPage/EVAA Scribe/Menu/defile input'), UploadFilePath)

		LogStories.logInfo("File uploaded: " + UploadFilePath)

		LogStories.logInfo("Awaiting file upload...")
	}

	@Keyword
	def generateSOAPNoteByUploadingFileAndSwitchPatient(String UploadFilePath) {
		LogStories.logInfo('----------------------Step AQ----------------------')

		UploadingSOAPNoteFile(UploadFilePath)
	}

	@Keyword
	def verifySOAPNoteGenerateSucessfully() {
		LogStories.logInfo('----------------------Step AR----------------------')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/File processed successfully'), 150, FailureHandling.OPTIONAL)
		LogStories.markPassed("File processed successfully")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def StartRecording_CreateNewEncounter_StopRecording(String recordFilePath, String FirstName, String LastName, String EncounterType, String ExamLocation, String Provider, String Technician ) {
		LogStories.logInfo('----------------------Step AS----------------------')

		def fakeMic = new FakeMicStream(recordFilePath)

		// Start Recording
		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		LogStories.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

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

		WebUI.delay(10)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Record Button')

		fakeMic.stop()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Generating SOAP Notes")

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.OPTIONAL)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'), 5, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def StartRecording_CreateNewEncounterForOtherPatient_StopRecording(String recordFilePath, String FirstName, String LastName, String EncounterType, String ExamLocation, String Provider, String Technician ) {
		LogStories.logInfo('----------------------Step AT----------------------')

		def fakeMic = new FakeMicStream(recordFilePath)

		// Start Recording
		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		LogStories.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		// Collapse Expand Recording Screen
		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

		TestData patientData = TestDataFactory.findTestData('Data Files/PatientData')

		def LastName2 = patientData.getValue('LastName', 2)

		def FirstName2 = patientData.getValue('FirstName', 2)

		//Find Patient 2
		CustomKeywords.'steps.CommonSteps.findPatient'(LastName2, FirstName2)

		CustomKeywords.'steps.CommonSteps.createNewEncounter'(FirstName2, LastName2, EncounterType, ExamLocation, Provider, Technician,
				false)

		//Find patient 1
		CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

		navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])

		String encounterId = VariableStories.getItem('ENCOUNTER_ID')

		LogStories.logInfo("Encounter Id=> $encounterId")

		CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

		// Stop Recording
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(10)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Record Button')

		fakeMic.stop()

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
	def directDictationByTypingOnElements() {
		LogStories.logInfo('----------------------Step AU----------------------')

		TestData dictationData = TestDataFactory.findTestData('Data Files/DirectDictationData')

		int rowCount = dictationData.getRowNumbers()

		if (rowCount == 0) {
			LogStories.markFailed("❌ DirectDictationData has NO rows")
			return
		}

		int row = 1   // first row

		if (!VariableStories.elementStorage.isEmpty()) {
			def elementStorageList = VariableStories.elementStorage

			try {
				WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

				for (String name : elementStorageList) {
					LogStories.logInfo("Element from Storage → ${name}")

					TestObject sectionTO = CommonStory.sectionMapForDirectDictationTyping.get(name)

					if (!sectionTO) {
						LogStories.markWarning("No TestObject mapped for → ${name}")
						return
					}

					List<WebElement> elements = WebUI.findWebElements(sectionTO, 10)

					elements.each { WebElement el ->
						LogStories.logInfo("${name} value → ${el.text}")

						String textToAppend = dictationData.getValue(name, row)

						el.click()
						el.sendKeys(Keys.chord(Keys.CONTROL, Keys.END))
						el.sendKeys(" ${textToAppend}")

						WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/input_Search'))

						WebUI.delay(1)
					}
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
	def verifyStoredDirectDictationOnEVAAScribe() {
		LogStories.logInfo('----------------------Step AV----------------------')

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
				LogStories.logInfo("*************************Element from Storage → ${name}*************************")

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
				if (CommonStory.isNullOrEmpty(storedValue)) continue

					List expectedList = CommonStory.getListObject(storedValue)

				// Cache dictation value once per name
				String appendText = dictationData.getValue(name, row)?.replaceAll(":(?=.*:)", "")
				expectedList.eachWithIndex { expected, i ->
					String expectedText = "${expected} ${appendText}".replaceAll(":(?=.*:)", "")
					assertStory.verifyMatch("Direct Dictation→→ ${name}", actualTexts[i], expectedText)
				}
			}
		} catch (e) {
			e.printStackTrace()
		} finally {
			WebUI.switchToDefaultContent()
		}
	}

	@Keyword
	def directDictationByRecordStartStopOnElements(String UploadFilePath) {
		LogStories.logInfo('----------------------Step AW----------------------')

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

						WebUI.delay(10)

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
		LogStories.logInfo('----------------------Step AX----------------------')

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
						return
					}

					List<WebElement> elements = WebUI.findWebElements(sectionTO, 10)
					List<String> actualTexts = elements.collect { it.text.trim() }

					def variableKey = CommonStory.sectionMapForStorageKey.get(name)

					def storedValue = VariableStories.getItem(variableKey)
					if (CommonStory.isNullOrEmpty(storedValue)) return

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
		LogStories.logInfo('----------------------Step AY----------------------')

		WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/input_btnDataTransferEncBill'), 5, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EncounterPage/Encounter Details/Data Transferred/input_btnDataTransferEncBill'), FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo("Clicked on Transfer button.")

		try {
			WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/toast_PatientEncounterSaved'), 10, FailureHandling.CONTINUE_ON_FAILURE)
			LogStories.markPassed("Patient encounter saved.")

			WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/toast_DataTransferred'), 10, FailureHandling.CONTINUE_ON_FAILURE)
			LogStories.markPassed("Data transferred.")

			WebUI.focus(findTestObject('EncounterPage/Encounter Details/Data Transferred/input_btnDataTransferedEncBill'), FailureHandling.STOP_ON_FAILURE)

			WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/powerTip_Data transferred'), 5, FailureHandling.STOP_ON_FAILURE)
			LogStories.markPassed("Data transferred to Superbill.")

			WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/input_btnDataTransferedEncBill'), 5, FailureHandling.STOP_ON_FAILURE)
		} catch (e) {
			LogStories.markFailedAndStop("Data not transferred to Superbill.")
		}
	}


	@Keyword
	def GenerateSOAPNoteByUploadingFileForSinglePatient(String UploadFilePath,String FirstName,String LastName,  String DOB, String Provider_FirstName, String Provider_LastName ,String EncounterType, String ExamLocation,String Technician, Boolean isFinalize = true, Boolean isSendToEHR = true) {
		LogStories.logInfo('----------------------Step AZ----------------------')

		CustomKeywords.'steps.CommonSteps.maximeyesLogin'(GlobalVariable.EVAA_UserName, GlobalVariable.EVAA_Password)

		CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

		String ProviderName = "$Provider_FirstName $Provider_LastName"

		CustomKeywords.'steps.CommonSteps.createNewEncounter'(FirstName, LastName, EncounterType, ExamLocation, ProviderName, Technician)

		def uploadFilePath = RunConfiguration.getProjectDir() + "/Files/${UploadFilePath}"

		LogStories.logInfo("Upload File Path=> $uploadFilePath")

		LogStories.logInfo('----------------------Step A----------------------')
		CustomKeywords.'steps.EVAASteps.commonStepsForEVAA'(FirstName, LastName,DOB )

		LogStories.logInfo('----------------------Step B----------------------')
		CustomKeywords.'steps.EVAASteps.generateSOAPNoteByUploadingFile'(uploadFilePath)

		LogStories.logInfo('----------------------Step C----------------------')
		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeAllDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		if(isFinalize) {
			LogStories.logInfo('----------------------Step D----------------------')
			CustomKeywords.'steps.EVAASteps.finalizedAndSendToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName, true, isSendToEHR)

			if(isSendToEHR) {
				LogStories.logInfo('----------------------Step E----------------------')
				CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'()
			}
		}
	}

	@Keyword
	def VerifyReRecordPopup(String FirstName,String LastName ) {
		LogStories.logInfo('----------------------Step AAM----------------------')

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
	def UploadReRecordDictation(String RecordFilePath, String FirstName,String LastName,  String DOB, String Provider_FirstName, String Provider_LastName ) {
		LogStories.logInfo('----------------------Step AAL----------------------')

		def wordCountSOAPNotes = VariableStories.getItem('SOAP_NOTE_LENGTH')

		LogStories.logInfo('^^^^^^^^^^^^^^^^^^^^^Step A^^^^^^^^^^^^^^^^^^^^^')

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Upload'), FailureHandling.STOP_ON_FAILURE)

		LogStories.logInfo('Clicked on Upload Button.')

		CustomKeywords.'steps.EVAASteps.VerifyReRecordPopup'(FirstName, LastName)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Cancel'), FailureHandling.STOP_ON_FAILURE)

		LogStories.logInfo('Clicked on Cancel Button.')

		WebUI.delay(2)

		LogStories.logInfo('^^^^^^^^^^^^^^^^^^^^^Step B^^^^^^^^^^^^^^^^^^^^^')

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		int wordCountSOAPNotes2 = soapNotes.trim().split('\\s+').length

		assertStory.verifyMatch('SOAP Notes', wordCountSOAPNotes2, wordCountSOAPNotes)

		LogStories.logInfo('^^^^^^^^^^^^^^^^^^^^^Step C^^^^^^^^^^^^^^^^^^^^^')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		LogStories.logInfo('^^^^^^^^^^^^^^^^^^^^^Step D^^^^^^^^^^^^^^^^^^^^^')

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Upload'), FailureHandling.STOP_ON_FAILURE)

		LogStories.logInfo('Clicked on Upload Button.')

		CustomKeywords.'steps.EVAASteps.VerifyReRecordPopup'(FirstName, LastName)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Re-Record'), FailureHandling.STOP_ON_FAILURE)

		LogStories.logInfo('Clicked on Re-Record Button.')

		def folderPath = RunConfiguration.getProjectDir() + '/Files'

		LogStories.logInfo('^^^^^^^^^^^^^^^^^^^^^Step E^^^^^^^^^^^^^^^^^^^^^')

		LogStories.logInfo('File uploaded: ' + RecordFilePath)

		CustomKeywords.'steps.CommonKeywords.enterFilePathAndName'(folderPath, RecordFilePath)

		LogStories.logInfo('Awaiting file upload...')

		// Wait for toast message confirming file processed
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/File processed successfully'), 180, FailureHandling.STOP_ON_FAILURE)

		// Verify if toast is present quickly
		boolean isPresent = WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/File processed successfully'), 1, FailureHandling.OPTIONAL)

		if (isPresent) {
			LogStories.markPassed('File processed successfully.')
		} else {
			LogStories.markWarning('File not processed successfully.')
		}

		// Wait for Append Audio button
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.STOP_ON_FAILURE)

		// Wait for SOAP Notes element
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		// Ensure Finalize button is clickable
		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		// Wait for SOAP Notes section
		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)

		LogStories.logInfo('^^^^^^^^^^^^^^^^^^^^^Step F^^^^^^^^^^^^^^^^^^^^^')

		def soapNotes2 = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		int wordCountSOAPNotes3 = soapNotes2.trim().split('\\s+').length

		assertStory.verifyNotMatch('SOAP Notes', wordCountSOAPNotes3, wordCountSOAPNotes)

		LogStories.logInfo('^^^^^^^^^^^^^^^^^^^^^Step G^^^^^^^^^^^^^^^^^^^^^')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)
	}

	@Keyword
	def VerifyCopiedAllSOAPNotes() {
		LogStories.logInfo("---------------------Verify clicking Copy All copies data from all available elements---------------------")

		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}

		def elementStorageList = VariableStories.elementStorage

		String clipboardText = UtilHelper.getClipboardText()

		if (!CommonStory.isNullOrEmpty(clipboardText)) {
			LogStories.markPassed("Verified copied content can be pasted successfully into an external editor")
		}
		else {
			LogStories.markFailedAndStop("Verified copied content can not be pasted into an external editor")
		}

		clipboardText = clipboardText.replaceAll("Review Of Systems - Brief", "Review Of Systems Brief").replaceAll("\\s{2,}", " ").trim()

		for (String name : elementStorageList) {

			LogStories.logInfo("============================SOAP Note Element Name - ${name}============================")

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
		LogStories.logInfo("---------------------Verify copied content follows the correct order of elements---------------------")

		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}
		else {

			String clipboardText = UtilHelper.getClipboardText()

			// Get all values as a list
			List<String> moduleList = CommonStory.moduleMapForDirectDictation.values().toList()

			List<String> clipboardTextList = UtilHelper.getSelectedLabels(clipboardText,moduleList)

			LogStories.logInfo("<<<<<<<<<<<<<<<<<<<<<<<<<List Data<<<<<<<<<<<<<<<<<<<<<<<<<")
			String jsonWanted = JsonOutput.toJson(moduleList)
			String jsonClipboard = JsonOutput.toJson(clipboardTextList)

			// Ensure moduleList only shows data that exists in clipboardTextList
			moduleList = moduleList.findAll { clipboardTextList.contains(it) }
			String jsonWantedModified = JsonOutput.toJson(moduleList)

			LogStories.logInfo("Module List: ${jsonWanted}")
			LogStories.logInfo("Clipboard List: ${jsonClipboard}")

			LogStories.logInfo("Modified Module List: ${jsonWantedModified}")

			LogStories.logInfo("<<<<<<<<<<<<<<<<<<<<<<<<<List Data<<<<<<<<<<<<<<<<<<<<<<<<<")

			// Equivalent index-based loop
			for (int i = 0; i < moduleList.size(); i++) {
				String name = moduleList.get(i)

				LogStories.logInfo("============================SOAP Note Element Name - ${name}============================")

				def expectedData = name.replaceAll("Review Of Systems - Brief", "Review Of Systems Brief").replaceAll("\\s{2,}", " ").trim()

				def actualData = clipboardTextList.get(i)
				actualData = actualData.replaceAll("Review Of Systems - Brief", "Review Of Systems Brief").replaceAll("\\s{2,}", " ").trim()

				assertStory.verifyMatch("SOAP Note Order - ${name}", actualData, expectedData)
			}
		}
	}

	@Keyword
	def VerifyCopiedIndividualElementSOAPNotes() {
		LogStories.logInfo("---------------------Verify clicking Copy All copies data from all available elements---------------------")

		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}

		def elementStorageList = VariableStories.elementStorage

		for (String name : elementStorageList) {

			LogStories.logInfo("============================Copy SOAP Note Element Name - ${name}============================")

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

			LogStories.logInfo("**********************************Get Clipboard Text for element - ${name}**********************************")
			WebUI.delay(1)

			String clipboardText = UtilHelper.getClipboardText()

			if (!CommonStory.isNullOrEmpty(clipboardText)) {
				LogStories.markPassed("Verified copied content can be pasted successfully into an external editor")
			}
			else {
				LogStories.markFailed("Verified copied content can not be pasted into an external editor")
			}

			clipboardText = clipboardText.replaceAll("\\s{2,}", " ").trim()
			LogStories.logInfo("***********************************************************************************************************")

			LogStories.logInfo("**********************************Verify Clipboard Text for element - ${name}**********************************")
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

			LogStories.logInfo("***********************************************************************************************************")
		}
	}
}