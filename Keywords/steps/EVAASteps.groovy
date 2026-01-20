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
import org.openqa.selenium.WebElement as WebElement
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import CustomKeywords
import FakeMicStream
import java.lang.String
import internal.GlobalVariable
import stories.VariableStories
import stories.AssertStory
import stories.NavigateStory
import stories.TestObjectStory
import stories.CommonStory
import stories.LogStories

import org.openqa.selenium.Keys
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI2

public class EVAASteps {
	NavigateStory navigateStory = new NavigateStory()
	TestObjectStory testObjectStory = new TestObjectStory()
	AssertStory assertStory = new AssertStory();

	@Keyword
	def verifyPatientConsentReceived(String isReceived) {
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
	def commonStepsForEVAA(String FirstName, LastName, String FinalizedStatus = 'Pending', String MicStatus='Recording Not Started' ) {
		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'), 30, FailureHandling.STOP_ON_FAILURE)

		String PtName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'))

		String expectedPtName = "$FirstName $LastName"

		assertStory.verifyMatch('PatientName', PtName, expectedPtName)

		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('false')

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/button_Patient Consent Received'))

		LogStories.logInfo('Patient Consent Received checked.')

		WebUI.delay(5)

		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, 'Invalid Date (NaN)', '', FinalizedStatus, MicStatus)
	}

	@Keyword
	def searchStringAndVerify(String searchText) {
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
	def verifyEVAAScribeDetails(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName ,String SearchText= 'b', String FinalizedStatus = 'Pending', String MicStatus='Completed'  ) {
		String expectedPtName = "$FirstName $LastName"

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 60, FailureHandling.STOP_ON_FAILURE)

		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeHeaderDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeSOAPNotesAndSpeakerNotes'(expectedPtName)

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, '', DOB,FinalizedStatus , MicStatus)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Send_to_MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)

		CustomKeywords.'steps.EVAASteps.getAndStoreEVAAScribeSOAPNote'()

		CustomKeywords.'steps.EVAASteps.searchStringAndVerify'(SearchText)
	}

	@Keyword
	def unfinalizedDictationAfterFinalized(String FirstName, String LastName, String DOB, String Provider_FirstName, String Provider_LastName, Boolean isExpandClose = false) {

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

			WebUI.delay(5)
		}

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		CustomKeywords.'steps.EVAASteps.finalizedAndSendToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'(Provider_FirstName, Provider_LastName)
	}

	@Keyword
	def finalizedAndSendToMaximEyes(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName, Boolean isExpandClose = true  ) {
		String expectedPtName = "$FirstName $LastName"

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalize - Blue'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 60, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Finalize'), FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo("Clicked on Finalize")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Status updated to Finalized'), 60, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Status updated to Unfinalized!")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized'), 60, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Send_to_MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(2)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes'))
		LogStories.logInfo("Clicked on Send to MaximEyes")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Sending SOAP notes and PDF to MaximEyes'), 60,
				FailureHandling.CONTINUE_ON_FAILURE)
		LogStories.markPassed("Sending SOAP notes and PDF to MaximEyes...")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Sent SOAP notes and PDF to MaximEyes successfully'), 60,
				FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Sent SOAP notes and PDF to MaximEyes successfully.")

		WebUI.delay(10)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized - Green'), 30, FailureHandling.STOP_ON_FAILURE)

		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, '', DOB, 'Finalized', 'Completed')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeHeaderDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		if(isExpandClose == true) {
			WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording'))

			LogStories.logInfo('Clicked on Expand Recording')

			WebUI.delay(5)
		}
	}

	@Keyword
	def finalizedAndSendIndividualElementsToMaximEyes(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName, Boolean isExpandClose = true  ) {
		String expectedPtName = "$FirstName $LastName"

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalize - Blue'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 60, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Send_to_MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Finalize'), FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo("Clicked on Finalize")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Status updated to Finalized'), 60, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("Status updated to Finalized!")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized'), 60, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Send_to_MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(2)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized - Green'), 30, FailureHandling.STOP_ON_FAILURE)

		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, '', DOB, 'Finalized', 'Completed')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeHeaderDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}
		else {
			def elementStorageList = VariableStories.elementStorage

			try {

				for (String name : elementStorageList) {
					LogStories.logInfo("Element from Storage → ${name}")

					String moduleName = CommonStory.moduleMapForDirectDictation.get(name)

					TestObject sectionTO = testObjectStory.img_SendToMaximeyesWithParams(moduleName)

					if (!sectionTO) {
						LogStories.markWarning("No TestObject mapped for → ${name}")
						return
					}

					WebUI.waitForElementClickable(sectionTO, 10, FailureHandling.STOP_ON_FAILURE)

					WebUI.click(sectionTO, FailureHandling.STOP_ON_FAILURE)
					LogStories.logInfo("Clicked on Send to MaximEyes for element- ${name}");

					WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/SOAP note sent to MaximEyes successfully'), 60,
							FailureHandling.CONTINUE_ON_FAILURE)
					LogStories.markPassed("SOAP note sent to MaximEyes successfully.")

					CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)
										
					CustomKeywords.'steps.EVAASteps.verifyIndividualSOAPNoteSentToMaximeyes'(name)

					CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)
				}
			} catch (e) {
				e.printStackTrace()
			}finally		{
			}
		} 
	}

	@Keyword
	def verifyIndividualSOAPNoteSentToMaximeyes(String key) {
		def variableKey = CommonStory.sectionMapForStorageKey.get(key)
		String expectedData = VariableStories.getItem(variableKey)

		boolean isPresent= WebUI.verifyElementPresent(findTestObject('EncounterPage/Menu/a_Electronic_Files'), 5, FailureHandling.OPTIONAL)
		if(isPresent) {
			WebUI.click(findTestObject('EncounterPage/Menu/a_Electronic_Files'),FailureHandling.OPTIONAL)
			LogStories.logInfo("Clicked on Encounter Files.")
			WebUI.waitForElementPresent(findTestObject('EncounterPage/Menu/a_dvInboundFile'), 5, FailureHandling.CONTINUE_ON_FAILURE)
		}
		else {
			isPresent= WebUI.verifyElementPresent(findTestObject('EncounterPage/Menu/a_Encounter_Summary'), 5, FailureHandling.OPTIONAL)
			if(isPresent) {
				WebUI.click(findTestObject('EncounterPage/Menu/a_Encounter_Summary'),FailureHandling.OPTIONAL)
				LogStories.logInfo("Clicked on Encounter Summary.")
				WebUI.waitForElementPresent(findTestObject('EncounterPage/Menu/a_Full Summary'), 5, FailureHandling.CONTINUE_ON_FAILURE)
			}
		}

		if(key == "ChiefComplaint") {

			// ===== Chief Complaint =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Chief Complaint'])

				String actualChiefComplaint = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea Patient Chief Complaint'), 'value', FailureHandling.STOP_ON_FAILURE)

				actualChiefComplaint = actualChiefComplaint?.replaceAll("[:]", "")
				expectedData = expectedData?.replaceAll("[:]", "")

				assertStory.verifyMatch("Chief Complaint", actualChiefComplaint, expectedData)
			}
		}
		else if(key == "HPI") {
			// ===== HPI =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Chief Complaint'])
				String actualHPI = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea HPI Notes'), 'value', FailureHandling.STOP_ON_FAILURE)

				actualHPI = actualHPI?.replaceAll("[:]", "")
				expectedData = expectedData?.replaceAll("[:]", "")

				assertStory.verifyMatch("HPI", actualHPI, expectedData)
			}
		}
		else if(key == "CurrentEyeSymptoms") {
			// ===== Current Eye Symptoms =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				List currentEyeSymptomsList = CommonStory.getListObject(expectedData)
				if (currentEyeSymptomsList.size() > 0) {
					navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Current Eye Symptoms'])
					for (int i = 0; i < currentEyeSymptomsList.size(); i++) {
						def currentEyeSymptoms = currentEyeSymptomsList.get(i)
						LogStories.logInfo("Current Eye Symptoms→ $currentEyeSymptoms")
						def result = CommonStory.getKeyValueDetails(currentEyeSymptoms, 'CES')

						if (result) {
							String text = result._key
							String expected = result._expected
							String name = result._name

							LogStories.logInfo("Result Text: $text")
							LogStories.logInfo("Result Expected: $expected")
							LogStories.logInfo("Result Name: $name")

							// ✅ FIXED: Changed from 'return' to 'continue' to skip only this iteration
							if (expected == 'True') {
								continue
							}

							TestObject input_CurrentEyeSymptoms = testObjectStory.input_CurrentEyeSymptoms(name)
							if (text?.toLowerCase()?.contains('additional')) {
								input_CurrentEyeSymptoms = findTestObject('EncounterPage/Encounter Details/Current Eye Symptoms/textarea_Additional_Notes_CES')
							}

							String actual = WebUI.getAttribute(input_CurrentEyeSymptoms, 'value', FailureHandling.STOP_ON_FAILURE)

							actual = actual?.replaceAll("[:]", "")
							expected = expected?.replaceAll("[:]", "")

							assertStory.verifyMatch("Current Eye Symptoms- $name", actual, expected)
							verifyRadioButtonIsChecked(currentEyeSymptoms, name, 'CES')
						}
					}
				} else {
					LogStories.markWarning('No Current Eye Symptoms found')
				}
			} else {
				LogStories.markWarning('No Current Eye Symptoms found')
			}
		}
		else if(key == "Allergies") {
			// ===== Allergies =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				List allergiesList = CommonStory.getListObject(expectedData)
				if (allergiesList.size() > 0) {
					navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Allergies'])
					int index = 1
					for (int i = 0; i < allergiesList.size(); i++) {
						String allergies = allergiesList.get(i)

						LogStories.logInfo("Allergies→ $allergies")
						TestObject tableAllergies = testObjectStory.tableAllergies(index)
						String actual = WebUI.getText(tableAllergies, FailureHandling.STOP_ON_FAILURE)

						actual = actual?.replaceAll("[:]", "")
						String expected = allergies?.replaceAll("[:]", "")

						assertStory.verifyMatch("Allergies", actual, expected)
						index++
					}
				} else {
					LogStories.markWarning('No Allergies found')
				}
			} else {
				LogStories.markWarning('No Allergies found')
			}
		}
		else if(key == "Medications") {
			// ===== Medications =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				List medicationsList = CommonStory.getListObject(expectedData)
				if (medicationsList.size() > 0) {
					navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Medications'])
					int count = medicationsList.size()
					int index = 1
					for (int i = 0; i < count; i++) {
						String medications = medicationsList.get(i)
						String expected2 = ''

						// 🔹 Safely read NEXT item
						if (i + 1 < count) {
							String medications2 = medicationsList[i + 1]
							String result2 = CommonStory.getKeyValueDetails(medications2, 'MED')
							if (result2?._key == 'Generic Name') {
								expected2 = result2._expected
							}
						}

						LogStories.logInfo("Medications→ $medications")
						def result = CommonStory.getKeyValueDetails(medications, 'MED')
						if (!result) continue

							String keyTxt = result._key
						String expected = result._expected

						// 🔹 Skip standalone Generic Name rows
						if (keyTxt == 'Generic Name') continue

							// 🔹 Merge Generic Name if present
							if (!CommonStory.isNullOrEmpty(expected2)) {
								expected = "${expected} ${expected2}"
							}

						LogStories.logInfo("Expected: $expected")
						TestObject tableMedications = testObjectStory.tableMedications(index)
						String actual = WebUI.getText(tableMedications, FailureHandling.STOP_ON_FAILURE)

						actual = actual?.replaceAll("[:]", "")
						expected = expected?.replaceAll("[:]", "")

						assertStory.verifyMatch("Medications", actual, expected)
						index++
					}
				} else {
					LogStories.markWarning('No Medications found')
				}
			} else {
				LogStories.markWarning('No Medications found')
			}
		}
		else if(key == "ReviewOfSystems") {
			// ===== Review Of Systems =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				List reviewList = CommonStory.getListObject(expectedData)
				if (reviewList.size() > 0) {
					navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Review of Systems - Brief'])
					for (int i = 0; i < reviewList.size(); i++) {
						def review = reviewList.get(i)
						LogStories.logInfo("Review Of Systems→ $review")
						def result = CommonStory.getKeyValueDetails(review, 'ROS')

						if (result) {
							String text = result._key
							String expected = result._expected
							String name = result._name

							LogStories.logInfo("Result Text: $text")
							LogStories.logInfo("Result Expected: $expected")
							LogStories.logInfo("Result Name: $name")

							// ✅ FIXED: Changed from 'return' to 'continue'
							if (expected == 'True') {
								continue
							}

							TestObject input_Review_of_Systems = testObjectStory.input_Review_of_Systems(name)
							if (text?.toLowerCase()?.contains('additional')) {
								input_Review_of_Systems = findTestObject('EncounterPage/Encounter Details/Review Of Systems/textarea_Additional_Notes_ROS')
							}

							String actual = WebUI.getAttribute(input_Review_of_Systems, 'value', FailureHandling.STOP_ON_FAILURE)

							actual = actual?.replaceAll("[:]", "")
							expected = expected?.replaceAll("[:]", "")

							assertStory.verifyMatch("Review Of Systems- $name", actual, expected)
							verifyRadioButtonIsChecked(review, name, 'ROS')
						}
					}
				} else {
					LogStories.markWarning('No Review Of Systems found')
				}
			} else {
				LogStories.markWarning('No Review Of Systems found')
			}
		}
		else if(key == "Problems") {
			// ===== Problems =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				List problemsList = CommonStory.getListObject(expectedData)
				if (problemsList.size() > 0) {
					navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Problems'])
					int index = 1
					for (int i = 0; i < problemsList.size(); i++) {
						String expected = problemsList.get(i)
						LogStories.logInfo("Problems→ $expected")
						LogStories.logInfo("Result Expected: $expected")
						TestObject tableProblems = testObjectStory.tableProblems(index)
						String actual = WebUI.getText(tableProblems, FailureHandling.STOP_ON_FAILURE)

						actual = actual?.replaceAll("[:]", "")
						expected = expected?.replaceAll("[:]", "")

						assertStory.verifyMatch("Problems", actual, expected)
						index++
					}
				} else {
					LogStories.markWarning('No Problems found')
				}
			} else {
				LogStories.markWarning('No Problems found')
			}
		}
		else if(key == "DifferentialDiagnosis") {
			// ===== Differential Diagnosis =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				List diffDiagnosisList = CommonStory.getListObject(expectedData)
				if (diffDiagnosisList.size() > 0) {
					navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])
					for (int i = 0; i < diffDiagnosisList.size(); i++) {
						def diffDiagnosis = diffDiagnosisList.get(i)
						LogStories.logInfo("Differential Diagnosis→ $diffDiagnosis")
						def result = CommonStory.getKeyValueDetails(diffDiagnosis, 'DD')

						if (result) {
							String code = result._key
							String desc = result._expected
							LogStories.logInfo("Result Expected: $code, $desc")
							def expected = "${code}, ${desc}"
							TestObject tableFDDifferentialDiagnosis = testObjectStory.tableFDDifferentialDiagnosis(code, desc)
							boolean _isPresent = WebUI.verifyElementPresent(tableFDDifferentialDiagnosis, 1, FailureHandling.STOP_ON_FAILURE)

							assertStory.verifyMatch("Differential Diagnosis- $expected", _isPresent, true)
						}
					}
				} else {
					LogStories.markWarning('No Differential Diagnosis found')
				}
			} else {
				LogStories.markWarning('No Differential Diagnosis found')
			}
		}
		else if(key == "Assessment") {
			// ===== Assessment =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				List assessmentList = CommonStory.getListObject(expectedData)
				if (assessmentList.size() > 0) {
					navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])
					String _expectedAssessment = assessmentList.join('\n')
					LogStories.logInfo("Assessment→ $_expectedAssessment")
					String actualAssessment = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea Assessments'), 'value', FailureHandling.STOP_ON_FAILURE)

					actualAssessment = actualAssessment?.replaceAll("[:]", "")
					_expectedAssessment = _expectedAssessment?.replaceAll("[:]", "")

					assertStory.verifyMatch("Assessment", actualAssessment, _expectedAssessment)
				} else {
					LogStories.markWarning('No Assessment found')
				}
			} else {
				LogStories.markWarning('No Assessment found')
			}
		}
		else if(key == "Plan") {
			// ===== Plans =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				List plansList = CommonStory.getListObject(expectedData)
				if (plansList.size() > 0) {
					navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])
					String _expectedPlans = plansList.join('\n')
					LogStories.logInfo("Plans→ $_expectedPlans")
					String actualPlans = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/div Plans'), 'value', FailureHandling.OPTIONAL)

					if (CommonStory.isNullOrEmpty(actualPlans)) {
						actualPlans = WebUI.getText(findTestObject('EncounterPage/Encounter Details/div Plans'), FailureHandling.OPTIONAL)
					}

					actualPlans = actualPlans?.replaceAll("[:]", "")
					_expectedPlans = _expectedPlans?.replaceAll("[:]", "")


					assertStory.verifyMatch("Plans", actualPlans, _expectedPlans)
				} else {
					LogStories.markWarning('No Plans found')
				}
			} else {
				LogStories.markWarning('No Plans found')
			}
		}
		else if(key == "EyeDiseases") {
			// ===== Eye Diseases =====
			if (CommonStory.isNullOrEmpty(expectedData) == false) {
				List eyeDiseaseList = CommonStory.getListObject(expectedData)
				if (eyeDiseaseList.size() > 0) {
					navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Eye Diseases'])
					for (int i = 0; i < eyeDiseaseList.size(); i++) {
						def eyeDisease = eyeDiseaseList.get(i)
						LogStories.logInfo("Eye Diseases→ $eyeDisease")
						def result = CommonStory.getKeyValueDetails(eyeDisease, 'ED')

						if (result) {
							String text = result._key
							String expected = result._expected
							String name = result._name

							LogStories.logInfo("Result Text: $text")
							LogStories.logInfo("Result Expected: $expected")
							LogStories.logInfo("Result Name: $name")

							// ✅ FIXED: Changed from empty block to 'continue'
							if (expected == 'True') {
								continue
							}

							TestObject inputEyeDiseases = testObjectStory.inputEyeDiseases(name)
							if (text?.toLowerCase()?.contains('additional')) {
								inputEyeDiseases = findTestObject('EncounterPage/Encounter Details/Eye Diseases/textarea_Additional_Notes_EyeDiseases')
							}

							String actual = WebUI.getAttribute(inputEyeDiseases, 'value', FailureHandling.STOP_ON_FAILURE)

							actual = actual?.replaceAll("[:]", "")
							expected = expected?.replaceAll("[:]", "")

							assertStory.verifyMatch("Eye Diseases- $name", actual, expected)
							verifyRadioButtonIsChecked(eyeDisease, name, 'ED')
						}
					}
				} else {
					LogStories.markWarning('No Eye Diseases found')
				}
			} else {
				LogStories.markWarning('No Eye Diseases found')
			}
		}
	}

	@Keyword
	def verifyEVAAScribeHeaderDetails(String FirstName, String LastName, String DOB , String Provider_FirstName, String Provider_LastName) {
		WebUI.delay(5)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'), 30, FailureHandling.STOP_ON_FAILURE)

		String _ptKey = "${FirstName}_${LastName}".toUpperCase()

		String PtName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'))

		String expectedPtName = "${FirstName} ${LastName}"

		assertStory.verifyMatch("Header→→ Patient Name", PtName, expectedPtName)


		String actualPTDOB = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/PatientDOB'))

		String expectedPTDOB = DOB

		if (!CommonStory.isNullOrEmpty(DOB) &&
				!DOB.trim().equalsIgnoreCase("Invalid Date")) {
			expectedPTDOB = CustomKeywords.'DateHelper.GetFormattedDate'(DOB, 'd/M/yyyy')
		}

		//		String actualPTDOB = PTDOB

		//		if (!CommonStory.isNullOrEmpty(PTDOB) &&
		//				!PTDOB.trim().equalsIgnoreCase("Invalid Date")) {
		//			actualPTDOB = CustomKeywords.'DateHelper.GetFormattedDate'(PTDOB, 'd/M/yyyy')
		//		}

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
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name'), 30, FailureHandling.STOP_ON_FAILURE)

		String ptName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name'))
		expectedPtName = "Pt: $expectedPtName"

		assertStory.verifyMatch("Patient Name", ptName, expectedPtName)

		String ptDictationDt = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Pt Dictation Date'))

		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetUTCDate'()

		if (CommonStory.isNullOrEmpty(expectedPtDictationDt) == false) {
			expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(expectedPtDictationDt, 'd/M/yyyy')
		}

		if (CommonStory.isNullOrEmpty(ptDictationDt) == false) {
			ptDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(ptDictationDt, 'd/M/yyyy')
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
		int wordMaxCount = 1
		int maxCount = 1

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name'), 30, FailureHandling.STOP_ON_FAILURE)

		String ptName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Pt Name'))
		expectedPtName = "Pt: $expectedPtName"

		assertStory.verifyMatch("Patient Name", ptName, expectedPtName)

		String ptDictationDt = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Pt Dictation Date'))

		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetUTCDate'()

		if (CommonStory.isNullOrEmpty(expectedPtDictationDt) == false) {
			expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(expectedPtDictationDt, 'd/M/yyyy')
		}

		if (CommonStory.isNullOrEmpty(ptDictationDt) == false) {
			ptDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(ptDictationDt, 'd/M/yyyy')
		}

		assertStory.verifyMatch("Patient Dictation Date",ptDictationDt, expectedPtDictationDt)

		LogStories.markPassed("Speaker Patient Dictation Date→ $ptDictationDt")

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		LogStories.logInfo("SOAP Notes→ $soapNotes")

		int wordCountSOAPNotes = soapNotes.trim().split('\\s+').length

		assertStory.verifyGreaterThanOrEqual("SOAP Notes Total Words", wordCountSOAPNotes, wordMaxCount)

		def speakerNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes'))

		LogStories.logInfo("Speaker Notes→ $speakerNotes")

		//		//Speaker A
		//		int countSpeakerA = speakerNotes.findAll('Speaker A').size()
		//
		//		assertStory.verifyGreaterThanOrEqual("'Speaker A' Word found", countSpeakerA, maxCount)
		//
		//		//Speaker B
		//		int countSpeakerB = speakerNotes.findAll('Speaker B').size()
		//
		//		assertStory.verifyGreaterThanOrEqual("'Speaker B' Word found", countSpeakerB, maxCount)

		//Speaker Dictation Word Count
		int wordCount = speakerNotes.trim().split('\\s+').length

		assertStory.verifyGreaterThanOrEqual("Speaker Dictation Total Words", wordCount, wordMaxCount)

		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')
	}

	@Keyword
	def verifyEVAAScribeLeftSidePanel(String PatientName, String txtDOB,  String DOB, String FinalizedStatus, String MicStatus ) {
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/div_PatientName'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(5)

		def div_PatientName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/div_PatientName'))

		assertStory.verifyMatch("Left Side Panel→→ Patient Name", div_PatientName, PatientName)

		def actual_PatientDOB_DictationDate = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/div_PatientDOB_DictationDate'))

		def expectedPTDOBText = txtDOB
		if (CommonStory.isNullOrEmpty(txtDOB) == true) {
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

		String expectedPtDictationDt = CustomKeywords.'DateHelper.GetUTCDate'()
		expectedPtDictationDt = CustomKeywords.'DateHelper.GetFormattedDate'(expectedPtDictationDt, 'M/d/yyyy')

		def expected_PatientDOB_DictationDate = "${expectedPTDOBText} | ${expectedPtDictationDt}"

		assertStory.verifyMatch('Left Side Panel→→ Patient Name, DOB, Dictation Date',actual_PatientDOB_DictationDate, expected_PatientDOB_DictationDate)

		def isPending = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/svg_Pending'),1,FailureHandling.OPTIONAL)

		def _finalizedStatus = isPending ? 'Pending' : ''

		def isFinalized = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/svg_Finalized'),1,FailureHandling.OPTIONAL)

		_finalizedStatus = isFinalized ? 'Finalized' : _finalizedStatus

		assertStory.verifyMatch('Left Side Panel→→ Finalized Status',_finalizedStatus, FinalizedStatus)

		def notStarted = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/img_grey_mic-status'),1,FailureHandling.OPTIONAL)
		def _micStatus = notStarted ? 'Recording Not Started' : ''

		def isInProgress = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/img_green_mic-status'),1,FailureHandling.OPTIONAL)
		_micStatus = isInProgress ? 'In Progress' : _micStatus

		def isCompleted = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/img_blue_mic-status'),1,FailureHandling.OPTIONAL)
		_micStatus = isCompleted ? 'Completed' : _micStatus

		assertStory.verifyMatch('"Left Side Panel→→ Mic Status',_micStatus, MicStatus)
	}

	private void captureSectionDirectDictation(
			String sectionName,
			String testObjectPath,
			boolean isList = true
	) {
		try {
			def isPresent = WebUI.verifyElementPresent(
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

		WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

		VariableStories.elementStorage.clear()

		captureSectionDirectDictation('ChiefComplaint',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/ChiefComplaint', false)

		captureSectionDirectDictation('HPI',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/HPI', false)

		captureSectionDirectDictation('CurrentEyeSymptoms',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Current Eye Symptoms')

		captureSectionDirectDictation('Allergies',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Allergies')

		captureSectionDirectDictation('Medications',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Medications')

		captureSectionDirectDictation('ReviewOfSystems',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Review Of Systems')

		captureSectionDirectDictation('Problems',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Problems')

		captureSectionDirectDictation('Refractions',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Refractions')

		captureSectionDirectDictation('AuxiliaryLabTests',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Auxiliary Lab Tests')

		captureSectionDirectDictation('DifferentialDiagnosis',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Differential Diagnosis')

		captureSectionDirectDictation('Assessment',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Assessment')

		captureSectionDirectDictation('Plan',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Plans')

		captureSectionDirectDictation('EyeDiseases',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Eye Diseases')

		captureSectionDirectDictation('MentalAndFunctionalStatus',
				'EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Mental and Functional Status')

		WebUI.switchToDefaultContent()
	}

	@Keyword
	def getAndStoreEVAAScribeSOAPNote() {

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

		captureSectionDirectDictation('Refractions',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Refractions')

		captureSectionDirectDictation('AuxiliaryLabTests',
				'EVAAPage/EVAA Scribe/SOAP Notes/Note/Auxiliary Lab Tests')

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
	}

	@Keyword
	def verifySOAPNoteSentToMaximeyesOLD(String Provider_FirstName, String Provider_LastName) {
		def variableKeyCC = CommonStory.sectionMapForStorageKey.get('ChiefComplaint')
		String expectedChiefComplaint = VariableStories.getItem(variableKeyCC)

		def variableKeyHPI = CommonStory.sectionMapForStorageKey.get('HPI')
		def expectedHPI = VariableStories.getItem(variableKeyHPI)

		def variableKeyCES = CommonStory.sectionMapForStorageKey.get('CurrentEyeSymptoms')
		def expectedCurrentEyeSymptoms = VariableStories.getItem(variableKeyCES)

		def variableKeyALG = CommonStory.sectionMapForStorageKey.get('Allergies')
		def expectedAllergies = VariableStories.getItem(variableKeyALG)

		def variableKeyMED = CommonStory.sectionMapForStorageKey.get('Medications')
		def expectedMedications = VariableStories.getItem(variableKeyMED)

		def variableKeyROS = CommonStory.sectionMapForStorageKey.get('ReviewOfSystems')
		def expectedReviewOfSystems = VariableStories.getItem(variableKeyROS)

		def variableKeyPRL = CommonStory.sectionMapForStorageKey.get('Problems')
		def expectedProblems = VariableStories.getItem(variableKeyPRL)

		def variableKeyREF = CommonStory.sectionMapForStorageKey.get('Refractions')
		def expectedRefractions = VariableStories.getItem(variableKeyREF)

		def variableKeyALT = CommonStory.sectionMapForStorageKey.get('AuxiliaryLabTests')
		def expectedAuxiliaryLabTests = VariableStories.getItem(variableKeyALT)

		def variableKeyDD = CommonStory.sectionMapForStorageKey.get('DifferentialDiagnosis')
		def expectedDifferentialDiagnosis = VariableStories.getItem(variableKeyDD)

		def variableKeyASMT = CommonStory.sectionMapForStorageKey.get('Assessment')
		def expectedAssessment = VariableStories.getItem(variableKeyASMT)

		def variableKeyPLN = CommonStory.sectionMapForStorageKey.get('Plan')
		def expectedPlans = VariableStories.getItem(variableKeyPLN)

		def variableKeyED = CommonStory.sectionMapForStorageKey.get('EyeDiseases')
		def expectedEyeDiseases = VariableStories.getItem(variableKeyED)

		def variableKeyMFS = CommonStory.sectionMapForStorageKey.get('MentalAndFunctionalStatus')
		def expectedMentalAndFunctionalStatus = VariableStories.getItem(variableKeyMFS)

		if (CommonStory.isNullOrEmpty(expectedChiefComplaint) == false) {
			navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Chief Complaint'])

			String actualChiefComplaint = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea Patient Chief Complaint'),'value',FailureHandling.OPTIONAL)

			assertStory.verifyMatch("Chief Complaint", actualChiefComplaint, expectedChiefComplaint)
		}

		if (CommonStory.isNullOrEmpty(expectedHPI) == false) {
			navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Chief Complaint'])

			def actualHPI = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea HPI Notes'), 'value',FailureHandling.OPTIONAL)

			assertStory.verifyMatch("HPI", actualHPI, expectedHPI)
		}

		if (CommonStory.isNullOrEmpty(expectedAllergies) == false) {
			// region Allergies
			List allergiesList = CommonStory.getListObject(expectedAllergies)

			if (allergiesList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Allergies'])

				int index = 1
				for (int i = 0; i < allergiesList.size(); i++) {
					def allergies = allergiesList.get(i)

					LogStories.logInfo("Allergies→ $allergies")

					TestObject tableAllergies = testObjectStory.tableAllergies(index)

					def actual  = WebUI.getText(tableAllergies,FailureHandling.OPTIONAL)

					assertStory.verifyMatch("Allergies", actual, allergies)
					index ++
				}
			} else {
				LogStories.markWarning('No Allergies found')
			}
			// endregion Allergies
		} else {
			LogStories.markWarning('No Allergies found')
		}

		if (CommonStory.isNullOrEmpty(expectedMedications) == false) {
			// region Medications
			List medicationsList = CommonStory.getListObject(expectedMedications)

			if (medicationsList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Medications'])

				int count = medicationsList.size()
				int index  = 1
				for (int i = 0; i < count; i++) {
					def medications = medicationsList.get(i)

					def expected2 = '';

					// 🔹 Safely read NEXT item
					if (i + 1 < count) {
						def medications2 = medicationsList[i + 1]
						def result2 = CommonStory.getKeyValueDetails(medications2, 'MED')

						if (result2?._key == 'Generic Name') {
							expected2 = result2._expected
						}
					}

					LogStories.logInfo("Medications→ $medications")

					def result = CommonStory.getKeyValueDetails(medications, 'MED')
					if (!result) continue

						def key = result._key
					def expected = result._expected

					// 🔹 Skip standalone Generic Name rows
					if (key == 'Generic Name') continue

						// 🔹 Merge Generic Name if present
						if (!CommonStory.isNullOrEmpty(expected2)) {
							expected = "${expected} ${expected2}"
						}

					LogStories.logInfo("Expected: $expected")

					TestObject tableMedications = testObjectStory.tableMedications(index)
					def actual = WebUI.getText(tableMedications, FailureHandling.OPTIONAL)

					assertStory.verifyMatch("Medications", actual, expected)
					index++
				}
			} else {
				LogStories.markWarning('No Medications found')
			}
			// endregion Medications
		} else {
			LogStories.markWarning('No Medications found')
		}

		if (CommonStory.isNullOrEmpty(expectedProblems) == false) {
			// region Problems
			List problemsList = CommonStory.getListObject(expectedProblems)

			if (problemsList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Problems'])

				int index = 1
				for (int i = 0; i < problemsList.size(); i++) {
					def expected = problemsList.get(i)

					LogStories.logInfo("Problems→ $expected")

					LogStories.logInfo("Result Expected: $expected")

					TestObject tableProblems = testObjectStory.tableProblems(index)

					def actual =  WebUI.getText(tableProblems, FailureHandling.OPTIONAL)

					assertStory.verifyMatch("Problems", actual, expected)
					index ++
				}
			} else {
				LogStories.markWarning('No Problems found')
			}
			// endregion Problems
		} else {
			LogStories.markWarning('No Problems found')
		}

		if (CommonStory.isNullOrEmpty(expectedCurrentEyeSymptoms) == false) {
			// region Current Eye Symptoms
			List currentEyeSymptomsList = CommonStory.getListObject(expectedCurrentEyeSymptoms)

			if (currentEyeSymptomsList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Current Eye Symptoms'])

				for (int i = 0; i < currentEyeSymptomsList.size(); i++) {
					def currentEyeSymptoms = currentEyeSymptomsList.get(i)

					LogStories.logInfo("Current Eye Symptoms→ $currentEyeSymptoms")

					def result = CommonStory.getKeyValueDetails(currentEyeSymptoms, 'CES')

					def actual = ''

					def expected = ''

					def name = ''

					String text = ''
					if (result) {
						text = result._key

						LogStories.logInfo("Result Text: $text")

						expected = result._expected

						LogStories.logInfo("Result Expected: $expected")

						// Skip if expected is True
						if (expected == 'True') {
							return
						}

						name = result._name

						LogStories.logInfo("Result Name: $name")

						TestObject input_CurrentEyeSymptoms = testObjectStory.input_CurrentEyeSymptoms(name)

						if (text?.toLowerCase()?.contains('additional')) {
							input_CurrentEyeSymptoms = findTestObject('EncounterPage/Encounter Details/Current Eye Symptoms/textarea_Additional_Notes_CES')
						}

						actual = WebUI.getAttribute(input_CurrentEyeSymptoms, 'value',FailureHandling.OPTIONAL)
					}

					assertStory.verifyMatch("Current Eye Symptoms- $name", actual, expected)

					verifyRadioButtonIsChecked(currentEyeSymptoms, name, 'CES')
				}
			} else {
				LogStories.markWarning('No Current Eye Symptoms found')
			}
			// endregion Current Eye Symptoms
		}
		else {
			LogStories.markWarning('No Current Eye Symptoms found')
		}

		// endregion Current Eye Symptoms

		if (CommonStory.isNullOrEmpty(expectedReviewOfSystems) == false) {
			// region Review Of Systems
			List reviewList = CommonStory.getListObject(expectedReviewOfSystems)

			if (reviewList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Review of Systems - Brief'])

				for (int i = 0; i < reviewList.size(); i++) {
					def review = reviewList.get(i)

					LogStories.logInfo("Review Of Systems→ $review")

					def result = CommonStory.getKeyValueDetails(review, 'ROS')

					def actual = ''

					def expected = ''

					def name = ''

					String text = ''

					if (result) {
						text = result._key

						LogStories.logInfo("Result Text: $text")

						expected = result._expected

						LogStories.logInfo("Result Expected: $expected")

						// Skip if expected is True
						if (expected == 'True') {
							return
						}

						name = result._name

						LogStories.logInfo("Result Name: $name")

						TestObject input_Review_of_Systems = testObjectStory.input_Review_of_Systems(name)

						if (text?.toLowerCase()?.contains('additional')) {
							input_Review_of_Systems = findTestObject('EncounterPage/Encounter Details/Review Of Systems/textarea_Additional_Notes_ROS')
						}

						actual = WebUI.getAttribute(input_Review_of_Systems, 'value',FailureHandling.OPTIONAL)
					}

					assertStory.verifyMatch("Review Of Systems- $name", actual, expected)
					verifyRadioButtonIsChecked(review, name, 'ROS')
				}
			} else {
				LogStories.markWarning('No Review Of Systems found')
			}
			// endregion Review Of Systems
		} else {
			LogStories.markWarning('No Review Of Systems found')
		}


		if (CommonStory.isNullOrEmpty(expectedEyeDiseases) == false) {
			// region Eye Diseases
			List eyeDiseaseList = CommonStory.getListObject(expectedEyeDiseases)

			if (eyeDiseaseList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Eye Diseases'])

				for (int i = 0; i < eyeDiseaseList.size(); i++) {
					def eyeDisease = eyeDiseaseList.get(i)

					LogStories.logInfo("Eye Diseasess→ $eyeDisease")

					def result = CommonStory.getKeyValueDetails(eyeDisease, 'ED')

					def actual = ''

					def expected = ''

					def name = ''

					String  text = ''

					if (result) {
						text = result._key

						LogStories.logInfo("Result Text: $text")

						expected = result._expected

						LogStories.logInfo("Result Expected: $expected")

						// Skip if expected is True
						if (expected == 'True') {
						}

						name = result._name

						LogStories.logInfo("Result Name: $name")

						TestObject inputEyeDiseases = testObjectStory.inputEyeDiseases(name)

						if (text?.toLowerCase()?.contains('additional')) {
							inputEyeDiseases = findTestObject('EncounterPage/Encounter Details/Eye Diseases/textarea_Additional_Notes_EyeDiseases')
						}

						actual = WebUI.getAttribute(inputEyeDiseases, 'value',FailureHandling.OPTIONAL)
					}

					assertStory.verifyMatch("Eye Diseases- $name", actual, expected)

					verifyRadioButtonIsChecked(eyeDisease, name, 'ED')
				}
			} else {
				LogStories.markWarning('No Eye Diseases found')
			}
			// endregion Eye Diseases
		} else {
			LogStories.markWarning('No Eye Diseases found')
		}

		//		if (CommonStory.isNullOrEmpty(expectedMentalAndFunctionalStatus) == false) {
		//			// region Mental and Functional Status
		//			List mentalAndFunctionalStatusList = CommonStory.getListObject(expectedMentalAndFunctionalStatus)
		//
		//			if (mentalAndFunctionalStatusList.size() > 0) {
		//				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Mental and Functional Status'])
		//
		//				for (int i = 0; i < mentalAndFunctionalStatusList.size(); i++) {
		//					def mentalAndFunctionalStatus = mentalAndFunctionalStatusList.get(i)
		//
		//					LogStories.logInfo("Mental and Functional Status→ $mentalAndFunctionalStatus")
		//
		//					def result = CommonStory.getKeyValueDetails(mentalAndFunctionalStatus, 'MFS')
		//
		//					def actual = ''
		//
		//					def expected = ''
		//
		//					def name = ''
		//
		//					if (result) {
		//						def key = result._key
		//
		//						LogStories.logInfo("Result Key: $key")
		//
		//						expected = result._expected
		//
		//						LogStories.logInfo("Result Expected: $expected")
		//
		//						name = result._name
		//
		//						LogStories.logInfo("Result Name: $name")
		//
		//						TestObject inputMentalAndFunctionalStatus = testObjectStory.inputMentalAndFunctionalStatus(name)
		//
		//						actual = WebUI.getAttribute(inputMentalAndFunctionalStatus, 'value',FailureHandling.OPTIONAL)
		//					}
		//
		//					assertStory.verifyMatch("Mental and Functional Status $name", actual, expected)
		//				}
		//			} else {
		//				LogStories.markWarning('No Mental and Functional Status found')
		//			}
		//			// endregion Mental and Functional Status
		//		} else {
		//			LogStories.markWarning('No Mental and Functional Status found')
		//		}

		//		// region Refractions
		//		if (CommonStory.isNullOrEmpty(expectedRefractions) == false) {
		//			navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Refraction and Preliminaries', ('pElement') : 'Refractions'])
		//
		//			expectedRefractions = expectedRefractions.toString().replace('Notes:', '').trim()
		//
		//			def actualRefractions = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea_Refractions'), 'value',FailureHandling.OPTIONAL)
		//
		//			assertStory.verifyMatch("Chief Complaint", actualRefractions, expectedRefractions)
		//		}
		//		else {
		//			LogStories.logInfo('No Refractions found')
		//		}
		//
		//		// endregion Refractions

		//		if (CommonStory.isNullOrEmpty(expectedAuxiliaryLabTests) == false) {
		//			// region Auxiliary Lab Tests
		//			List auxiliaryLabTestsList = CommonStory.getListObject(expectedAuxiliaryLabTests)
		//
		//			if (auxiliaryLabTestsList.size() > 0) {
		//				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Examination', ('pElement') : 'Auxiliary & Lab Tests'])
		//
		//				// push data into list
		//				def orderDate = CustomKeywords.'DateHelper.GetUTCDate'()
		//
		//				def provider = "${Provider_FirstName} ${Provider_LastName}"
		//
		//				auxiliaryLabTestsList += [
		//					"Provider: $provider",
		//					"User: $GlobalVariable.EVAA_UserName",
		//					"Ordered: $orderDate"
		//				]
		//
		//				for (int i = 0; i < auxiliaryLabTestsList.size(); i++) {
		//					def auxiliaryLabTests = auxiliaryLabTestsList.get(i)
		//
		//					LogStories.logInfo("Auxiliary Lab Tests→ $auxiliaryLabTests")
		//
		//					def result = CommonStory.getKeyValueDetails(auxiliaryLabTests, 'ALT')
		//
		//					def actual = ''
		//
		//					def expected = ''
		//
		//					def name = ''
		//
		//					if (result) {
		//						def key = result._key
		//
		//						LogStories.logInfo("Result Key: $key")
		//
		//						expected = result._expected
		//
		//						LogStories.logInfo("Result Expected: $expected")
		//
		//						// Skip if expected is True
		//						if (expected == 'True') {
		//							return
		//						}
		//
		//						def value = "${key}, ${expected}"
		//
		//						LogStories.logInfo("Result Expected: $value")
		//
		//						TestObject toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span User')
		//
		//						switch (key) {
		//							case 'Category':
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span AuxCategory')
		//
		//								break
		//							case 'Type':
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span Type')
		//
		//								break
		//							case 'Note':
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span Test Notes')
		//
		//								break
		//							case ~('.*Findings.*') :
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span FINDINGS')
		//
		//								break
		//							case 'Provider':
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span Provider')
		//
		//								break
		//							case 'Ordered':
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span ORDERED DATE')
		//
		//								break
		//						}
		//
		//						actual = WebUI.getText(toAuxLab,FailureHandling.OPTIONAL)
		//					}
		//
		//					assertStory.verifyMatch("Auxiliary Lab Tests- $name", actual, expected)
		//				}
		//			} else {
		//				LogStories.markWarning('No Auxiliary Lab Tests found')
		//			}
		//			// endregion Auxiliary Lab Tests
		//		} else {
		//			LogStories.markWarning('No Auxiliary Lab Tests found')
		//		}

		if (CommonStory.isNullOrEmpty(expectedDifferentialDiagnosis) == false) {
			// region Differential Diagnosis
			List diffDiagnosisList = CommonStory.getListObject(expectedDifferentialDiagnosis)

			if (diffDiagnosisList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])

				for (int i = 0; i < diffDiagnosisList.size(); i++) {
					def diffDiagnosis = diffDiagnosisList.get(i)

					LogStories.logInfo("Differential Diagnosis→ $diffDiagnosis")

					def result = CommonStory.getKeyValueDetails(diffDiagnosis, 'DD')

					if (result) {
						def code = result._key

						def desc = result._expected

						LogStories.logInfo("Result Expected: $code, $desc")

						def expected = "${code}, ${desc}"

						TestObject tableFDDifferentialDiagnosis = testObjectStory.tableFDDifferentialDiagnosis(code, desc)

						def isPresent =	WebUI.verifyElementPresent(tableFDDifferentialDiagnosis,1,FailureHandling.OPTIONAL)

						assertStory.verifyMatch("Differential Diagnosis- $expected", isPresent, true)
					}
				}
			} else {
				LogStories.markWarning('No Differential Diagnosis found')
			}
			// endregion Differential Diagnosis
		} else {
			LogStories.markWarning('No Differential Diagnosis found')
		}

		if (CommonStory.isNullOrEmpty(expectedAssessment) == false) {
			// region Assessment
			List assessmentList = CommonStory.getListObject(expectedAssessment)

			if (assessmentList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])

				def _expectedAssessment = assessmentList.join('\n')

				LogStories.logInfo("Assessment→ $_expectedAssessment")

				def actualAssessment = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea Assessments'), 'value',FailureHandling.OPTIONAL)

				assertStory.verifyMatch("Assessment", actualAssessment, _expectedAssessment)
			} else {
				LogStories.markWarning('No Assessment found')
			}
			// endregion Assessment
		} else {
			LogStories.markWarning('No Assessment found')
		}

		if (CommonStory.isNullOrEmpty(expectedPlans) == false) {
			// region Plans
			List plansList = CommonStory.getListObject(expectedPlans)

			if (plansList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])

				def _expectedPlans = plansList.join('\n')

				LogStories.logInfo("Plans→ $_expectedPlans")

				def actualPlans = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/div Plans'), 'value',FailureHandling.OPTIONAL)

				if (CommonStory.isNullOrEmpty(actualPlans)) {
					actualPlans = WebUI.getText(findTestObject('EncounterPage/Encounter Details/div Plans'), FailureHandling.OPTIONAL)
				}

				assertStory.verifyMatch("Plans", actualPlans, _expectedPlans)
			} else {
				LogStories.markWarning('No Plans found')
			}
			// endregion Plans
		} else {
			LogStories.markWarning('No Plans found')
		}
	}

	@Keyword
	def verifySOAPNoteSentToMaximeyes(String Provider_FirstName, String Provider_LastName) {
		def variableKeyCC = CommonStory.sectionMapForStorageKey.get('ChiefComplaint')
		String expectedChiefComplaint = VariableStories.getItem(variableKeyCC)

		def variableKeyHPI = CommonStory.sectionMapForStorageKey.get('HPI')
		String expectedHPI = VariableStories.getItem(variableKeyHPI)

		def variableKeyCES = CommonStory.sectionMapForStorageKey.get('CurrentEyeSymptoms')
		def expectedCurrentEyeSymptoms = VariableStories.getItem(variableKeyCES)

		def variableKeyALG = CommonStory.sectionMapForStorageKey.get('Allergies')
		def expectedAllergies = VariableStories.getItem(variableKeyALG)

		def variableKeyMED = CommonStory.sectionMapForStorageKey.get('Medications')
		def expectedMedications = VariableStories.getItem(variableKeyMED)

		def variableKeyROS = CommonStory.sectionMapForStorageKey.get('ReviewOfSystems')
		def expectedReviewOfSystems = VariableStories.getItem(variableKeyROS)

		def variableKeyPRL = CommonStory.sectionMapForStorageKey.get('Problems')
		def expectedProblems = VariableStories.getItem(variableKeyPRL)

		def variableKeyREF = CommonStory.sectionMapForStorageKey.get('Refractions')
		def expectedRefractions = VariableStories.getItem(variableKeyREF)

		def variableKeyALT = CommonStory.sectionMapForStorageKey.get('AuxiliaryLabTests')
		def expectedAuxiliaryLabTests = VariableStories.getItem(variableKeyALT)

		def variableKeyDD = CommonStory.sectionMapForStorageKey.get('DifferentialDiagnosis')
		def expectedDifferentialDiagnosis = VariableStories.getItem(variableKeyDD)

		def variableKeyASMT = CommonStory.sectionMapForStorageKey.get('Assessment')
		def expectedAssessment = VariableStories.getItem(variableKeyASMT)

		def variableKeyPLN = CommonStory.sectionMapForStorageKey.get('Plan')
		def expectedPlans = VariableStories.getItem(variableKeyPLN)

		def variableKeyED = CommonStory.sectionMapForStorageKey.get('EyeDiseases')
		def expectedEyeDiseases = VariableStories.getItem(variableKeyED)

		def variableKeyMFS = CommonStory.sectionMapForStorageKey.get('MentalAndFunctionalStatus')
		def expectedMentalAndFunctionalStatus = VariableStories.getItem(variableKeyMFS)

		// ===== Chief Complaint =====
		if (CommonStory.isNullOrEmpty(expectedChiefComplaint) == false) {
			navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Chief Complaint'])
			String actualChiefComplaint = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea Patient Chief Complaint'), 'value', FailureHandling.OPTIONAL)

			actualChiefComplaint = actualChiefComplaint?.replaceAll("[:]", "")
			expectedChiefComplaint = expectedChiefComplaint?.replaceAll("[:]", "")

			assertStory.verifyMatch("Chief Complaint", actualChiefComplaint, expectedChiefComplaint)
		}

		// ===== HPI =====
		if (CommonStory.isNullOrEmpty(expectedHPI) == false) {
			navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Chief Complaint'])
			String actualHPI = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea HPI Notes'), 'value', FailureHandling.OPTIONAL)

			actualHPI = actualHPI?.replaceAll("[:]", "")
			expectedHPI = expectedHPI?.replaceAll("[:]", "")

			assertStory.verifyMatch("HPI", actualHPI, expectedHPI)
		}

		// ===== Allergies =====
		if (CommonStory.isNullOrEmpty(expectedAllergies) == false) {
			List allergiesList = CommonStory.getListObject(expectedAllergies)
			if (allergiesList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Allergies'])
				int index = 1
				for (int i = 0; i < allergiesList.size(); i++) {
					String allergies = allergiesList.get(i)

					LogStories.logInfo("Allergies→ $allergies")
					TestObject tableAllergies = testObjectStory.tableAllergies(index)
					String actual = WebUI.getText(tableAllergies, FailureHandling.OPTIONAL)

					actual = actual?.replaceAll("[:]", "")
					String expected = allergies?.replaceAll("[:]", "")

					assertStory.verifyMatch("Allergies", actual, expected)
					index++
				}
			} else {
				LogStories.markWarning('No Allergies found')
			}
		} else {
			LogStories.markWarning('No Allergies found')
		}

		// ===== Medications =====
		if (CommonStory.isNullOrEmpty(expectedMedications) == false) {
			List medicationsList = CommonStory.getListObject(expectedMedications)
			if (medicationsList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Medications'])
				int count = medicationsList.size()
				int index = 1
				for (int i = 0; i < count; i++) {
					String medications = medicationsList.get(i)
					String expected2 = ''

					// 🔹 Safely read NEXT item
					if (i + 1 < count) {
						String medications2 = medicationsList[i + 1]
						String result2 = CommonStory.getKeyValueDetails(medications2, 'MED')
						if (result2?._key == 'Generic Name') {
							expected2 = result2._expected
						}
					}

					LogStories.logInfo("Medications→ $medications")
					def result = CommonStory.getKeyValueDetails(medications, 'MED')
					if (!result) continue

						String key = result._key
					String expected = result._expected

					// 🔹 Skip standalone Generic Name rows
					if (key == 'Generic Name') continue

						// 🔹 Merge Generic Name if present
						if (!CommonStory.isNullOrEmpty(expected2)) {
							expected = "${expected} ${expected2}"
						}

					LogStories.logInfo("Expected: $expected")
					TestObject tableMedications = testObjectStory.tableMedications(index)
					String actual = WebUI.getText(tableMedications, FailureHandling.OPTIONAL)

					actual = actual?.replaceAll("[:]", "")
					expected = expected?.replaceAll("[:]", "")

					assertStory.verifyMatch("Medications", actual, expected)
					index++
				}
			} else {
				LogStories.markWarning('No Medications found')
			}
		} else {
			LogStories.markWarning('No Medications found')
		}

		// ===== Problems =====
		if (CommonStory.isNullOrEmpty(expectedProblems) == false) {
			List problemsList = CommonStory.getListObject(expectedProblems)
			if (problemsList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Problems'])
				int index = 1
				for (int i = 0; i < problemsList.size(); i++) {
					String expected = problemsList.get(i)
					LogStories.logInfo("Problems→ $expected")
					LogStories.logInfo("Result Expected: $expected")
					TestObject tableProblems = testObjectStory.tableProblems(index)
					String actual = WebUI.getText(tableProblems, FailureHandling.OPTIONAL)

					actual = actual?.replaceAll("[:]", "")
					expected = expected?.replaceAll("[:]", "")

					assertStory.verifyMatch("Problems", actual, expected)
					index++
				}
			} else {
				LogStories.markWarning('No Problems found')
			}
		} else {
			LogStories.markWarning('No Problems found')
		}

		// ===== Current Eye Symptoms =====
		if (CommonStory.isNullOrEmpty(expectedCurrentEyeSymptoms) == false) {
			List currentEyeSymptomsList = CommonStory.getListObject(expectedCurrentEyeSymptoms)
			if (currentEyeSymptomsList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Current Eye Symptoms'])
				for (int i = 0; i < currentEyeSymptomsList.size(); i++) {
					def currentEyeSymptoms = currentEyeSymptomsList.get(i)
					LogStories.logInfo("Current Eye Symptoms→ $currentEyeSymptoms")
					def result = CommonStory.getKeyValueDetails(currentEyeSymptoms, 'CES')

					if (result) {
						String text = result._key
						String expected = result._expected
						String name = result._name

						LogStories.logInfo("Result Text: $text")
						LogStories.logInfo("Result Expected: $expected")
						LogStories.logInfo("Result Name: $name")

						// ✅ FIXED: Changed from 'return' to 'continue' to skip only this iteration
						if (expected == 'True') {
							continue
						}

						TestObject input_CurrentEyeSymptoms = testObjectStory.input_CurrentEyeSymptoms(name)
						if (text?.toLowerCase()?.contains('additional')) {
							input_CurrentEyeSymptoms = findTestObject('EncounterPage/Encounter Details/Current Eye Symptoms/textarea_Additional_Notes_CES')
						}

						String actual = WebUI.getAttribute(input_CurrentEyeSymptoms, 'value', FailureHandling.OPTIONAL)

						actual = actual?.replaceAll("[:]", "")
						expected = expected?.replaceAll("[:]", "")

						assertStory.verifyMatch("Current Eye Symptoms- $name", actual, expected)
						verifyRadioButtonIsChecked(currentEyeSymptoms, name, 'CES')
					}
				}
			} else {
				LogStories.markWarning('No Current Eye Symptoms found')
			}
		} else {
			LogStories.markWarning('No Current Eye Symptoms found')
		}

		// ===== Review Of Systems =====
		if (CommonStory.isNullOrEmpty(expectedReviewOfSystems) == false) {
			List reviewList = CommonStory.getListObject(expectedReviewOfSystems)
			if (reviewList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Review of Systems - Brief'])
				for (int i = 0; i < reviewList.size(); i++) {
					def review = reviewList.get(i)
					LogStories.logInfo("Review Of Systems→ $review")
					def result = CommonStory.getKeyValueDetails(review, 'ROS')

					if (result) {
						String text = result._key
						String expected = result._expected
						String name = result._name

						LogStories.logInfo("Result Text: $text")
						LogStories.logInfo("Result Expected: $expected")
						LogStories.logInfo("Result Name: $name")

						// ✅ FIXED: Changed from 'return' to 'continue'
						if (expected == 'True') {
							continue
						}

						TestObject input_Review_of_Systems = testObjectStory.input_Review_of_Systems(name)
						if (text?.toLowerCase()?.contains('additional')) {
							input_Review_of_Systems = findTestObject('EncounterPage/Encounter Details/Review Of Systems/textarea_Additional_Notes_ROS')
						}

						String actual = WebUI.getAttribute(input_Review_of_Systems, 'value', FailureHandling.OPTIONAL)

						actual = actual?.replaceAll("[:]", "")
						expected = expected?.replaceAll("[:]", "")

						assertStory.verifyMatch("Review Of Systems- $name", actual, expected)
						verifyRadioButtonIsChecked(review, name, 'ROS')
					}
				}
			} else {
				LogStories.markWarning('No Review Of Systems found')
			}
		} else {
			LogStories.markWarning('No Review Of Systems found')
		}

		// ===== Eye Diseases =====
		if (CommonStory.isNullOrEmpty(expectedEyeDiseases) == false) {
			List eyeDiseaseList = CommonStory.getListObject(expectedEyeDiseases)
			if (eyeDiseaseList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Eye Diseases'])
				for (int i = 0; i < eyeDiseaseList.size(); i++) {
					def eyeDisease = eyeDiseaseList.get(i)
					LogStories.logInfo("Eye Diseases→ $eyeDisease")
					def result = CommonStory.getKeyValueDetails(eyeDisease, 'ED')

					if (result) {
						String text = result._key
						String expected = result._expected
						String name = result._name

						LogStories.logInfo("Result Text: $text")
						LogStories.logInfo("Result Expected: $expected")
						LogStories.logInfo("Result Name: $name")

						// ✅ FIXED: Changed from empty block to 'continue'
						if (expected == 'True') {
							continue
						}

						TestObject inputEyeDiseases = testObjectStory.inputEyeDiseases(name)
						if (text?.toLowerCase()?.contains('additional')) {
							inputEyeDiseases = findTestObject('EncounterPage/Encounter Details/Eye Diseases/textarea_Additional_Notes_EyeDiseases')
						}

						String actual = WebUI.getAttribute(inputEyeDiseases, 'value', FailureHandling.OPTIONAL)

						actual = actual?.replaceAll("[:]", "")
						expected = expected?.replaceAll("[:]", "")

						assertStory.verifyMatch("Eye Diseases- $name", actual, expected)
						verifyRadioButtonIsChecked(eyeDisease, name, 'ED')
					}
				}
			} else {
				LogStories.markWarning('No Eye Diseases found')
			}
		} else {
			LogStories.markWarning('No Eye Diseases found')
		}

		//		// ===== Mental and Functional Status =====
		//		if (CommonStory.isNullOrEmpty(expectedMentalAndFunctionalStatus) == false) {
		//			List mentalAndFunctionalStatusList = CommonStory.getListObject(expectedMentalAndFunctionalStatus)
		//			if (mentalAndFunctionalStatusList.size() > 0) {
		//				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Mental and Functional Status'])
		//				for (int i = 0; i < mentalAndFunctionalStatusList.size(); i++) {
		//					def mentalAndFunctionalStatus = mentalAndFunctionalStatusList.get(i)
		//					LogStories.logInfo("Mental and Functional Status→ $mentalAndFunctionalStatus")
		//					def result = CommonStory.getKeyValueDetails(mentalAndFunctionalStatus, 'MFS')
		//
		//					if (result) {
		//						def key = result._key
		//						def expected = result._expected
		//						def name = result._name
		//
		//						LogStories.logInfo("Result Key: $key")
		//						LogStories.logInfo("Result Expected: $expected")
		//						LogStories.logInfo("Result Name: $name")
		//
		//						TestObject inputMentalAndFunctionalStatus = testObjectStory.inputMentalAndFunctionalStatus(name)
		//						def actual = WebUI.getAttribute(inputMentalAndFunctionalStatus, 'value', FailureHandling.OPTIONAL)
		//						assertStory.verifyMatch("Mental and Functional Status $name", actual, expected)
		//					}
		//				}
		//			} else {
		//				LogStories.markWarning('No Mental and Functional Status found')
		//			}
		//		} else {
		//			LogStories.markWarning('No Mental and Functional Status found')
		//		}
		//
		//		// ===== Refractions =====
		//		if (CommonStory.isNullOrEmpty(expectedRefractions) == false) {
		//			navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Refraction and Preliminaries', ('pElement') : 'Refractions'])
		//			expectedRefractions = expectedRefractions.toString().replace('Notes:', '').trim()
		//			def actualRefractions = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea_Refractions'), 'value', FailureHandling.OPTIONAL)
		//			assertStory.verifyMatch("Refractions", actualRefractions, expectedRefractions)
		//		} else {
		//			LogStories.logInfo('No Refractions found')
		//		}
		//
		//		// ===== Auxiliary Lab Tests =====
		//		if (CommonStory.isNullOrEmpty(expectedAuxiliaryLabTests) == false) {
		//			List auxiliaryLabTestsList = CommonStory.getListObject(expectedAuxiliaryLabTests)
		//			if (auxiliaryLabTestsList.size() > 0) {
		//				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Examination', ('pElement') : 'Auxiliary & Lab Tests'])
		//
		//				// Push data into list
		//				def orderDate = CustomKeywords.'DateHelper.GetUTCDate'()
		//				def provider = "${Provider_FirstName} ${Provider_LastName}"
		//
		//				auxiliaryLabTestsList += [
		//					"Provider: $provider",
		//					"User: $GlobalVariable.EVAA_UserName",
		//					"Ordered: $orderDate"
		//				]
		//
		//				for (int i = 0; i < auxiliaryLabTestsList.size(); i++) {
		//					def auxiliaryLabTests = auxiliaryLabTestsList.get(i)
		//					LogStories.logInfo("Auxiliary Lab Tests→ $auxiliaryLabTests")
		//					def result = CommonStory.getKeyValueDetails(auxiliaryLabTests, 'ALT')
		//
		//					if (result) {
		//						def key = result._key
		//						def expected = result._expected
		//						def name = result._name
		//
		//						LogStories.logInfo("Result Key: $key")
		//						LogStories.logInfo("Result Expected: $expected")
		//
		//						// ✅ FIXED: Changed from 'return' to 'continue'
		//						if (expected == 'True') {
		//							continue
		//						}
		//
		//						def value = "${key}, ${expected}"
		//						LogStories.logInfo("Result Value: $value")
		//
		//						TestObject toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span User')
		//
		//						switch (key) {
		//							case 'Category':
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span AuxCategory')
		//								break
		//							case 'Type':
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span Type')
		//								break
		//							case 'Note':
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span Test Notes')
		//								break
		//							case ~('.*Findings.*'):
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span FINDINGS')
		//								break
		//							case 'Provider':
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span Provider')
		//								break
		//							case 'Ordered':
		//								toAuxLab = findTestObject('EncounterPage/Encounter Details/Auxiliary Lab Tests/span ORDERED DATE')
		//								break
		//						}
		//
		//						def actual = WebUI.getText(toAuxLab, FailureHandling.OPTIONAL)
		//						assertStory.verifyMatch("Auxiliary Lab Tests- $name", actual, expected)
		//					}
		//				}
		//			} else {
		//				LogStories.markWarning('No Auxiliary Lab Tests found')
		//			}
		//		} else {
		//			LogStories.markWarning('No Auxiliary Lab Tests found')
		//		}

		// ===== Differential Diagnosis =====
		if (CommonStory.isNullOrEmpty(expectedDifferentialDiagnosis) == false) {
			List diffDiagnosisList = CommonStory.getListObject(expectedDifferentialDiagnosis)
			if (diffDiagnosisList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])
				for (int i = 0; i < diffDiagnosisList.size(); i++) {
					def diffDiagnosis = diffDiagnosisList.get(i)
					LogStories.logInfo("Differential Diagnosis→ $diffDiagnosis")
					def result = CommonStory.getKeyValueDetails(diffDiagnosis, 'DD')

					if (result) {
						String code = result._key
						String desc = result._expected
						LogStories.logInfo("Result Expected: $code, $desc")
						def expected = "${code}, ${desc}"
						TestObject tableFDDifferentialDiagnosis = testObjectStory.tableFDDifferentialDiagnosis(code, desc)
						def isPresent = WebUI.verifyElementPresent(tableFDDifferentialDiagnosis, 1, FailureHandling.OPTIONAL)

						assertStory.verifyMatch("Differential Diagnosis- $expected", isPresent, true)
					}
				}
			} else {
				LogStories.markWarning('No Differential Diagnosis found')
			}
		} else {
			LogStories.markWarning('No Differential Diagnosis found')
		}

		// ===== Assessment =====
		if (CommonStory.isNullOrEmpty(expectedAssessment) == false) {
			List assessmentList = CommonStory.getListObject(expectedAssessment)
			if (assessmentList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])
				String _expectedAssessment = assessmentList.join('\n')
				LogStories.logInfo("Assessment→ $_expectedAssessment")
				String actualAssessment = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea Assessments'), 'value', FailureHandling.OPTIONAL)

				actualAssessment = actualAssessment?.replaceAll("[:]", "")
				_expectedAssessment = _expectedAssessment?.replaceAll("[:]", "")

				assertStory.verifyMatch("Assessment", actualAssessment, _expectedAssessment)
			} else {
				LogStories.markWarning('No Assessment found')
			}
		} else {
			LogStories.markWarning('No Assessment found')
		}

		// ===== Plans =====
		if (CommonStory.isNullOrEmpty(expectedPlans) == false) {
			List plansList = CommonStory.getListObject(expectedPlans)
			if (plansList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])
				String _expectedPlans = plansList.join('\n')
				LogStories.logInfo("Plans→ $_expectedPlans")
				String actualPlans = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/div Plans'), 'value', FailureHandling.OPTIONAL)

				if (CommonStory.isNullOrEmpty(actualPlans)) {
					actualPlans = WebUI.getText(findTestObject('EncounterPage/Encounter Details/div Plans'), FailureHandling.OPTIONAL)
				}

				actualPlans = actualPlans?.replaceAll("[:]", "")
				_expectedPlans = _expectedPlans?.replaceAll("[:]", "")


				assertStory.verifyMatch("Plans", actualPlans, _expectedPlans)
			} else {
				LogStories.markWarning('No Plans found')
			}
		} else {
			LogStories.markWarning('No Plans found')
		}
	}

	@Keyword
	def verifyRadioButtonIsChecked(String text, String name, String key) {

		def type = ''
		def resultODOSOU = CommonStory.getODOSOU(text)
		if (resultODOSOU) {
			type = resultODOSOU.type
			text = resultODOSOU.value
		}
		
		String txt;
		
		/** prefix handling **/
		switch (key) {
			case "ED":
				name = "Eye_Diseases.${name}"
				txt="Eye Diseases"
				break

			case "CES":
				name = "Current_Eye_Symptoms.${name}"
				txt = "Current Eye Symptoms"
				break

			case "ROS":
				name = "Review_of_Systems_Brief.${name}"
				txt = "Review of Systems Brief"
				break

			default:
				LogStories.markWarning("Unknown key → $key")
				return
		}

		if (CommonStory.isNullOrEmpty(type)) {
			LogStories.logInfo("No type present for element → '$txt'.")
			return
		}
 
		/** name formatting **/
		if (type in ["OD", "OS", "OU"]) {
			name = name.replace("_NOTES", "_LOCATION")
		} else if (type in ["Yes", "No", "Unk"]) {
			name = name.replace("_NOTES", "")
		}

		/** build TestObject **/
		TestObject rbTestObject = testObjectStory.input_RadioButton(name, type)

		if (!rbTestObject) {
			LogStories.markFailed("Radio Button not found → $text ($name:$type)")
			return
		}

		/** find web element safely **/
		WebElement el = WebUI.findWebElement(rbTestObject, 5, FailureHandling.OPTIONAL)

		if (!el) {
			LogStories.markFailed("Radio button missing in DOM → $text ($name:$type)")
			return
		}

		boolean isChecked = el.isSelected()

		LogStories.logInfo("Checked? $isChecked → ($text, $name, $type)")

		assertStory.verifyMatch("${text}-${type}", isChecked.toString(), "true")
	}

	@Keyword
	def generateSOAPNoteByAppendPauseResumeStop(String FileTime, String RecordFilePath) {
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
		int fileTimeinSeconds = Integer.valueOf(FileTime)

		LogStories.logInfo("File Path $UploadFilePath")

		def fakeMic = new FakeMicStream(UploadFilePath)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		LogStories.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()
		LogStories.logInfo('Clicked on fakeMic Start Record Button')

		WebUI.delay(fileTimeinSeconds)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		LogStories.logInfo('Clicked on Stop Record Button')

		fakeMic.stop()
		LogStories.logInfo('Clicked on fakeMic Stop Record Button')

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
		LogStories.logInfo("File Path $UploadFilePath")

		WebUI.uploadFile(findTestObject('EVAAPage/EVAA Scribe/Menu/defile input'), UploadFilePath)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/File processed successfully'), 180, FailureHandling.STOP_ON_FAILURE)
		LogStories.markPassed("File processed successfully")

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def UploadingSOAPNoteFile(String UploadFilePath) {
		LogStories.logInfo("File Path $UploadFilePath")

		WebUI.uploadFile(findTestObject('EVAAPage/EVAA Scribe/Menu/defile input'), UploadFilePath)
	}

	@Keyword
	def generateSOAPNoteByUploadingFileAndSwitchPatient(String UploadFilePath) {
		UploadingSOAPNoteFile(UploadFilePath)
	}

	@Keyword
	def verifySOAPNoteGenerateSucessfully() {
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/File processed successfully'), 150, FailureHandling.OPTIONAL)
		LogStories.markPassed("File processed successfully")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def StartRecording_CreateNewEncounter_StopRecording(String recordFilePath, String FirstName, String LastName, String EncounterType, String ExamLocation, String Provider, String Technician ) {
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

					TestObject sectionTO = CommonStory.sectionMapForDirectDictation.get(name)

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
	def verifyStoredDirectDictationOnEVAAScribe(int index = 1) {

		if (VariableStories.elementStorage.isEmpty()) {
			LogStories.markWarning("No stored elements for verification")
			return
		}

		TestData dictationData = TestDataFactory.findTestData('Data Files/DirectDictationData')

		int row = 1   // first row

		int rowCount = dictationData.getRowNumbers()
		if (index < 1 || index > rowCount) {
			LogStories.markFailed("Invalid row index ${index}")
			return
		}

		CommonStory commonStory = new CommonStory(dictationData,index);

		if (!VariableStories.elementStorage.isEmpty()) {
			WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

			try {
				def elementStorageList = VariableStories.elementStorage
				for (String name : elementStorageList) {
					LogStories.logInfo("Element from Storage → ${name}")

					TestObject sectionTO = CommonStory.sectionMapForDirectDictation.get(name)

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
						String appendText = dictationData.getValue(name, row)
						String expectedText = "${expected} ${appendText}"

						appendText = appendText?.replaceAll("[:]", "")
						expectedText = expectedText?.replaceAll("[:]", "")

						assertStory.verifyMatch("Direct Dictation→→ ${name}",actualTexts[i],expectedText)
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
	def directDictationByRecordStartStopOnElements(String UploadFilePath) {
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

					TestObject sectionTO = CommonStory.sectionMapForDirectDictation.get(name)

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

						actualText = actualText?.replaceAll("[:]", "")
						expectedText = expectedText?.replaceAll("[:]", "")

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
		WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/input_btnDataTransferEncBill'), 5, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EncounterPage/Encounter Details/Data Transferred/input_btnDataTransferEncBill'), FailureHandling.STOP_ON_FAILURE)
		LogStories.logInfo("Clicked on Transfer button.")

		try {
			WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/toast_Data transferred'), 10, FailureHandling.CONTINUE_ON_FAILURE)
			LogStories.markPassed("Data transferred.")

			WebUI.focus(findTestObject('EncounterPage/Encounter Details/Data Transferred/input_btnDataTransferEncBill'), FailureHandling.STOP_ON_FAILURE)

			WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/powerTip_Data transferred'), 5, FailureHandling.STOP_ON_FAILURE)
			LogStories.markPassed("Data transferred to Superbill.")
		} catch (e) {
			LogStories.markFailedAndStop("Data not transferred to Superbill.")
		}
	}
}