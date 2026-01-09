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
import com.kms.katalon.core.util.KeywordUtil
import internal.GlobalVariable
import stories.VariableStories
import stories.AssertStory
import stories.NavigateStory
import stories.TestObjectStory
import stories.CommonStory as CommonStory
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
			KeywordUtil.markPassed("Patient Consent Received?→ $chk_PatientConsentReceived")
		}
		else {
			KeywordUtil.markFailed("Patient Consent Received?→ $chk_PatientConsentReceived")
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

		WebUI.delay(2)

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

		KeywordUtil.logInfo("Search: $searchText")

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

		CustomKeywords.'steps.EVAASteps.getAndStoreEVAAScribeSOAPNote'()

		CustomKeywords.'steps.EVAASteps.searchStringAndVerify'(SearchText)
	}

	@Keyword
	def finalizedAndSendToMaximEyes(String FirstName, LastName, String DOB,String Provider_FirstName, String Provider_LastName, Boolean isExpandClose = true  ) {
		String expectedPtName = "$FirstName $LastName"

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 60, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Finalize'), FailureHandling.STOP_ON_FAILURE)
		KeywordUtil.logInfo("Clicked on Finalize")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Status updated to Finalized'), 60, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Finalized'), 60, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(2)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Send to MaximEyes'))
		KeywordUtil.logInfo("Clicked on Send to MaximEyes")

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Sent SOAP notes and PDF to MaximEyes successfully'), 120,
				FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(10)

		CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, '', DOB, 'Finalized', 'Completed')

		CustomKeywords.'steps.EVAASteps.verifyEVAAScribeHeaderDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

		if(isExpandClose == true) {
			WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording'))

			KeywordUtil.logInfo('Clicked on Expand Recording')

			WebUI.delay(5)
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

		PtId = PtId.replaceAll('\\D+', '')

		String patientIdKey = "FP_${_ptKey}_PATIENT_ID"

		String expectedPTId = VariableStories.getItem(patientIdKey)

		assertStory.verifyMatch("Header→→ Patient ID", PtId, expectedPTId)


		String EncId = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/EncounterId'))

		EncId = EncId.replaceAll('\\D+', '')

		String encIdKey = "ENC_${_ptKey}_ENCOUNTER_ID"

		String expectedEncId = VariableStories.getItem(encIdKey)

		assertStory.verifyMatch("Header→→ Encounter ID", EncId, expectedEncId)


		String Provider = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/Provider'))

		Provider = Provider.replace('Prov:', '').replace('|', '').replaceAll('\\s+', '')

		String providerKey = "ENC_${_ptKey}_PROVIDER_ID"

		String expectedProvider = "${Provider_FirstName}${Provider_LastName}"

		assertStory.verifyMatch("Header→→ Provider", Provider, expectedProvider)
	}

	@Keyword
	def getSOAPNotesAndSpeakerNotesWordCount(String expectedPtName ) {
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 30, FailureHandling.STOP_ON_FAILURE)

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		KeywordUtil.logInfo("SOAP Notes→ $soapNotes")

		int wordCountSOAPNotes = soapNotes.trim().split('\\s+').length

		VariableStories.setItem('SOAP_NOTE_WORDS_COUNT', wordCountSOAPNotes)

		def speakerNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes'))

		KeywordUtil.logInfo("Speaker Notes→ $speakerNotes")

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

		KeywordUtil.markPassed("Speaker Patient Dictation Date→ $ptDictationDt")

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		KeywordUtil.logInfo("SOAP Notes→ $soapNotes")

		int wordCountSOAPNotes = soapNotes.trim().split('\\s+').length

		def expectedWordCountSOAPNotes = VariableStories.getItem('SOAP_NOTE_WORDS_COUNT')

		assertStory.verifyGreaterThanOrEqual("SOAP Notes Total Words", wordCountSOAPNotes, expectedWordCountSOAPNotes )

		def speakerNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes'))

		KeywordUtil.logInfo("Speaker Notes→ $speakerNotes")

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

		KeywordUtil.markPassed("Speaker Patient Dictation Date→ $ptDictationDt")

		def soapNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'))

		KeywordUtil.logInfo("SOAP Notes→ $soapNotes")

		int wordCountSOAPNotes = soapNotes.trim().split('\\s+').length

		assertStory.verifyGreaterThanOrEqual("SOAP Notes Total Words", wordCountSOAPNotes, wordMaxCount)

		def speakerNotes = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Speaker Notes'))

		KeywordUtil.logInfo("Speaker Notes→ $speakerNotes")

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
				KeywordUtil.markWarning("${sectionName} not found")
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
			KeywordUtil.logInfo("${sectionName} captured successfully")
		} catch (Exception e) {
			KeywordUtil.markWarning("Error capturing ${sectionName}: ${e.message}")
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
	def getAndStoreEVAAScribeSOAPNoteOLD() {
		WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

		if (!VariableStories.elementStorage.isEmpty()) {
			VariableStories.elementStorage.clear()
		}

		try {
			def isPresentChiefComplaint = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/ChiefComplaint'),1,FailureHandling.OPTIONAL)
			if(isPresentChiefComplaint) {
				// ChiefComplaint

				def _ChiefComplaint = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/ChiefComplaint'))

				KeywordUtil.logInfo("Chief Complaint→ $_ChiefComplaint")

				def variableKey = CommonStory.sectionMapForStorageKey.get('ChiefComplaint')
				VariableStories.setItem(variableKey, _ChiefComplaint)

				KeywordUtil.logInfo('Chief Complaint element found')

				VariableStories.elementStorage << 'ChiefComplaint'
			}
			else {
				KeywordUtil.markWarning('Chief Complaint element not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentHPI =	WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/HPI'),1,FailureHandling.OPTIONAL)
			if(isPresentHPI) {
				// HPI

				def _HPI = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/HPI'))

				KeywordUtil.logInfo("HPI→ $_HPI")

				def variableKey = CommonStory.sectionMapForStorageKey.get('HPI')
				VariableStories.setItem(variableKey, _HPI)

				KeywordUtil.logInfo('HPI element found')

				VariableStories.elementStorage << 'HPI'
			}
			else {
				KeywordUtil.markWarning('HPI element not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentCurrentEyeSymptoms=	WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Current Eye Symptoms'),1,FailureHandling.OPTIONAL)
			if(isPresentCurrentEyeSymptoms) {
				// Current Eye Symptoms
				List<WebElement> _CurrentEyeSymptoms = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Current Eye Symptoms'),
						10)

				List<String> allCurrentEyeSymptoms = _CurrentEyeSymptoms.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('CurrentEyeSymptoms')
				VariableStories.setItem(variableKey, allCurrentEyeSymptoms)

				KeywordUtil.logInfo('Current Eye Symptoms element found')

				VariableStories.elementStorage << 'CurrentEyeSymptoms'
			}
			else {
				KeywordUtil.markWarning('Current Eye Symptoms element not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentAllergies=	WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Allergies'),1,FailureHandling.OPTIONAL)
			if(isPresentAllergies) {
				// Allergies

				List<WebElement> _Allergies = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Allergies'),
						10)

				List<String> allAllergies = _Allergies.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('Allergies')
				VariableStories.setItem(variableKey, allAllergies)

				KeywordUtil.logInfo('Allergies found')

				VariableStories.elementStorage << 'Allergies'
			}
			else {
				KeywordUtil.markWarning('Allergies not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentMedications=	WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Medications'),1,FailureHandling.OPTIONAL)
			if(isPresentMedications) {
				// Medications

				List<WebElement> _Medications = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Medications'),
						10)

				List<String> allMedications = _Medications.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('Medications')
				VariableStories.setItem(variableKey, allMedications)

				KeywordUtil.logInfo('Medications found')

				VariableStories.elementStorage << 'Medications'
			}
			else {
				KeywordUtil.markWarning('Medications not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentReviewOfSystems =	WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Review Of Systems'),1,FailureHandling.OPTIONAL)
			if(isPresentReviewOfSystems) {
				// Review Of Systems - Brief

				List<WebElement> _ReviewOfSystems = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Review Of Systems'),
						10)

				List<String> allReviewOfSystems = _ReviewOfSystems.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('ReviewOfSystems')
				VariableStories.setItem(variableKey, allReviewOfSystems)

				KeywordUtil.logInfo('Review Of Systems found')

				VariableStories.elementStorage << 'ReviewOfSystems'
			}
			else {
				KeywordUtil.markWarning('Review Of Systems not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentProblems =	WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Problems'),1,FailureHandling.OPTIONAL)
			if(isPresentProblems) {
				// Problems

				List<WebElement> _Problems = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Problems'),
						10)

				List<String> allProblems = _Problems.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('Problems')
				VariableStories.setItem(variableKey, allProblems)

				KeywordUtil.logInfo('Problems found')

				VariableStories.elementStorage << 'Problems'
			}
			else {
				KeywordUtil.markWarning('Problems not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentRefractions = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Refractions'),1,FailureHandling.OPTIONAL)
			if(isPresentRefractions) {
				// Refractions

				List<WebElement> _Refractions = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Refractions'),
						10)

				List<String> allRefractions = _Refractions.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('Refractions')
				VariableStories.setItem(variableKey, allRefractions)

				KeywordUtil.logInfo('Refractions found')

				VariableStories.elementStorage << 'Refractions'
			}
			else {
				KeywordUtil.markWarning('Refractions not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentAuxiliaryLabTests = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Auxiliary Lab Tests'),1,FailureHandling.OPTIONAL)
			if(isPresentAuxiliaryLabTests) {
				// Auxiliary/Lab Tests

				List<WebElement> _AuxiliaryLabTests = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Auxiliary Lab Tests'),
						10)

				List<String> allAuxiliaryLabTests = _AuxiliaryLabTests.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('AuxiliaryLabTests')
				VariableStories.setItem(variableKey, allAuxiliaryLabTests)

				KeywordUtil.logInfo('Auxiliary Lab Tests found')

				VariableStories.elementStorage << 'AuxiliaryLabTests'
			}
			else {
				KeywordUtil.markWarning('Auxiliary Lab Tests not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentDifferentialDiagnosis = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Differential Diagnosis'),1,FailureHandling.OPTIONAL)
			if(isPresentDifferentialDiagnosis) {
				// Differential Diagnosis

				List<WebElement> _DifferentialDiagnosis = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Differential Diagnosis'),
						10)

				List<String> allDifferentialDiagnosis = _DifferentialDiagnosis.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('DifferentialDiagnosis')
				VariableStories.setItem(variableKey, allDifferentialDiagnosis)

				KeywordUtil.logInfo('Differential Diagnosis found')

				VariableStories.elementStorage << 'DifferentialDiagnosis'
			}
			else {
				KeywordUtil.markWarning('Differential Diagnosis not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentAssessment = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Assessment'),1,FailureHandling.OPTIONAL)
			if(isPresentAssessment) {
				// Assessment

				List<WebElement> _Assessment = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Assessment'),
						10)

				List<String> allAssessment = _Assessment.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('Assessment')
				VariableStories.setItem(variableKey, allAssessment)

				KeywordUtil.logInfo('Assessment found')

				VariableStories.elementStorage << 'Assessment'
			}
			else {
				KeywordUtil.markWarning('Assessment not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentPlans = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Plans'),1,FailureHandling.OPTIONAL)
			if(isPresentPlans) {
				// Plans

				List<WebElement> _Plans = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Plans'),
						10)

				List<String> allPlans = _Plans.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('Plan')
				VariableStories.setItem(variableKey, allPlans)

				KeywordUtil.logInfo('Plans found')

				VariableStories.elementStorage << 'Plan'
			}
			else {
				KeywordUtil.markWarning('Plans not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentEyeDiseases = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Eye Diseases'),1,FailureHandling.OPTIONAL)
			if(isPresentEyeDiseases) {
				// Eye Diseases

				List<WebElement> _EyeDiseases = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Eye Diseases'),
						10)

				List<String> allEyeDiseases = _EyeDiseases.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('EyeDiseases')
				VariableStories.setItem(variableKey, allEyeDiseases)

				KeywordUtil.logInfo('Eye Diseases found')

				VariableStories.elementStorage << 'EyeDiseases'
			}
			else {
				KeywordUtil.markWarning('Eye Diseases not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		try {
			def isPresentMentalAndFunctionalStatus = WebUI.verifyElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Mental and Functional Status'),1,FailureHandling.OPTIONAL)
			if(isPresentMentalAndFunctionalStatus) {
				// Mental and Functional Status

				List<WebElement> _MentalStatus = WebUI.findWebElements(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Mental and Functional Status'),
						10)

				List<String> allMentalStatus = _MentalStatus.collect({
					it.getText().trim()
				})

				def variableKey = CommonStory.sectionMapForStorageKey.get('MentalAndFunctionalStatus')
				VariableStories.setItem(variableKey, allMentalStatus)

				KeywordUtil.logInfo('Mental and Functional Status found')

				VariableStories.elementStorage << 'MentalAndFunctionalStatus'
			}
			else {
				KeywordUtil.markWarning('Mental and Functional Status not found')
			}
		} catch (e) {
			e.printStackTrace()
		}

		// Switch back
		WebUI.switchToDefaultContent()
	}

	@Keyword
	def verifySOAPNoteSentToMaximeyes(String Provider_FirstName, String Provider_LastName) {
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

					KeywordUtil.logInfo("Allergies→ $allergies")

					TestObject tableAllergies = testObjectStory.tableAllergies(index)

					def actual  = WebUI.getText(tableAllergies,FailureHandling.OPTIONAL)

					assertStory.verifyMatch("Allergies", actual, allergies)
					index ++
				}
			} else {
				KeywordUtil.markWarning('No Allergies found')
			}
			// endregion Allergies
		} else {
			KeywordUtil.markWarning('No Allergies found')
		}

		if (CommonStory.isNullOrEmpty(expectedMedications) == false) {
			// region Medications
			List medicationsList = CommonStory.getListObject(expectedMedications)

			if (medicationsList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Medications'])

				int index  = 1
				for (int i = 0; i < medicationsList.size(); i++) {
					def medications = medicationsList.get(i)

					KeywordUtil.logInfo("Medications→ $medications")

					def result = CommonStory.getKeyValueDetails(medications, 'MED')

					def actual = ''

					def expected = ''

					if (result) {
						def key = result._key

						KeywordUtil.logInfo("Result Key: $key")

						expected = result._expected

						KeywordUtil.logInfo("Result Expected: $expected")

						TestObject tableMedications = testObjectStory.tableMedications(index)

						actual = WebUI.getText(tableMedications,FailureHandling.OPTIONAL)
					}

					assertStory.verifyMatch("Medications", actual, expected)
					index ++
				}
			} else {
				KeywordUtil.markWarning('No Medications found')
			}
			// endregion Medications
		} else {
			KeywordUtil.markWarning('No Medications found')
		}

		if (CommonStory.isNullOrEmpty(expectedProblems) == false) {
			// region Problems
			List problemsList = CommonStory.getListObject(expectedProblems)

			if (problemsList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'CC & History Review', ('pElement') : 'Problems'])

				int index = 1
				for (int i = 0; i < problemsList.size(); i++) {
					def expected = problemsList.get(i)

					KeywordUtil.logInfo("Problems→ $expected")

					KeywordUtil.logInfo("Result Expected: $expected")

					TestObject tableProblems = testObjectStory.tableProblems(index)

					def actual =  WebUI.getText(tableProblems, ,FailureHandling.OPTIONAL)

					assertStory.verifyMatch("Problems", actual, expected)
					index ++
				}
			} else {
				KeywordUtil.markWarning('No Problems found')
			}
			// endregion Problems
		} else {
			KeywordUtil.markWarning('No Problems found')
		}

		if (CommonStory.isNullOrEmpty(expectedCurrentEyeSymptoms) == false) {
			// region Current Eye Symptoms
			List currentEyeSymptomsList = CommonStory.getListObject(expectedCurrentEyeSymptoms)

			if (currentEyeSymptomsList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Current Eye Symptoms'])

				for (int i = 0; i < currentEyeSymptomsList.size(); i++) {
					def currentEyeSymptoms = currentEyeSymptomsList.get(i)

					KeywordUtil.logInfo("Current Eye Symptoms→ $currentEyeSymptoms")

					def result = CommonStory.getKeyValueDetails(currentEyeSymptoms, 'CES')

					def actual = ''

					def expected = ''

					def name = ''

					String text = ''
					if (result) {
						text = result._key

						KeywordUtil.logInfo("Result Text: $text")

						expected = result._expected

						KeywordUtil.logInfo("Result Expected: $expected")

						// Skip if expected is True
						if (expected == 'True') {
						}

						name = result._name

						KeywordUtil.logInfo("Result Name: $name")

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
				KeywordUtil.markWarning('No Current Eye Symptoms found')
			}
			// endregion Current Eye Symptoms
		}
		else {
			KeywordUtil.markWarning('No Current Eye Symptoms found')
		}

		// endregion Current Eye Symptoms

		if (CommonStory.isNullOrEmpty(expectedReviewOfSystems) == false) {
			// region Review Of Systems
			List reviewList = CommonStory.getListObject(expectedReviewOfSystems)

			if (reviewList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Review of Systems - Brief'])

				for (int i = 0; i < reviewList.size(); i++) {
					def review = reviewList.get(i)

					KeywordUtil.logInfo("Review Of Systems→ $review")

					def result = CommonStory.getKeyValueDetails(review, 'ROS')

					def actual = ''

					def expected = ''

					def name = ''

					String text = ''

					if (result) {
						text = result._key

						KeywordUtil.logInfo("Result Text: $text")

						expected = result._expected

						KeywordUtil.logInfo("Result Expected: $expected")

						// Skip if expected is True
						if (expected == 'True') {
							return
						}

						name = result._name

						KeywordUtil.logInfo("Result Name: $name")

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
				KeywordUtil.markWarning('No Review Of Systems found')
			}
			// endregion Review Of Systems
		} else {
			KeywordUtil.markWarning('No Review Of Systems found')
		}


		if (CommonStory.isNullOrEmpty(expectedEyeDiseases) == false) {
			// region Eye Diseases
			List eyeDiseaseList = CommonStory.getListObject(expectedEyeDiseases)

			if (eyeDiseaseList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Medical History', ('pElement') : 'Eye Diseases'])

				for (int i = 0; i < eyeDiseaseList.size(); i++) {
					def eyeDisease = eyeDiseaseList.get(i)

					KeywordUtil.logInfo("Eye Diseasess→ $eyeDisease")

					def result = CommonStory.getKeyValueDetails(eyeDisease, 'ED')

					def actual = ''

					def expected = ''

					def name = ''

					String  text = ''

					if (result) {
						text = result._key

						KeywordUtil.logInfo("Result Text: $text")

						expected = result._expected

						KeywordUtil.logInfo("Result Expected: $expected")

						// Skip if expected is True
						if (expected == 'True') {
						}

						name = result._name

						KeywordUtil.logInfo("Result Name: $name")

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
				KeywordUtil.markWarning('No Eye Diseases found')
			}
			// endregion Eye Diseases
		} else {
			KeywordUtil.markWarning('No Eye Diseases found')
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
		//					KeywordUtil.logInfo("Mental and Functional Status→ $mentalAndFunctionalStatus")
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
		//						KeywordUtil.logInfo("Result Key: $key")
		//
		//						expected = result._expected
		//
		//						KeywordUtil.logInfo("Result Expected: $expected")
		//
		//						name = result._name
		//
		//						KeywordUtil.logInfo("Result Name: $name")
		//
		//						TestObject inputMentalAndFunctionalStatus = testObjectStory.inputMentalAndFunctionalStatus(name)
		//
		//						actual = WebUI.getAttribute(inputMentalAndFunctionalStatus, 'value',FailureHandling.OPTIONAL)
		//					}
		//
		//					assertStory.verifyMatch("Mental and Functional Status $name", actual, expected)
		//				}
		//			} else {
		//				KeywordUtil.markWarning('No Mental and Functional Status found')
		//			}
		//			// endregion Mental and Functional Status
		//		} else {
		//			KeywordUtil.markWarning('No Mental and Functional Status found')
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
		//			KeywordUtil.logInfo('No Refractions found')
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
		//					KeywordUtil.logInfo("Auxiliary Lab Tests→ $auxiliaryLabTests")
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
		//						KeywordUtil.logInfo("Result Key: $key")
		//
		//						expected = result._expected
		//
		//						KeywordUtil.logInfo("Result Expected: $expected")
		//
		//						// Skip if expected is True
		//						if (expected == 'True') {
		//							return
		//						}
		//
		//						def value = "${key}, ${expected}"
		//
		//						KeywordUtil.logInfo("Result Expected: $value")
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
		//				KeywordUtil.markWarning('No Auxiliary Lab Tests found')
		//			}
		//			// endregion Auxiliary Lab Tests
		//		} else {
		//			KeywordUtil.markWarning('No Auxiliary Lab Tests found')
		//		}

		if (CommonStory.isNullOrEmpty(expectedDifferentialDiagnosis) == false) {
			// region Differential Diagnosis
			List diffDiagnosisList = CommonStory.getListObject(expectedDifferentialDiagnosis)

			if (diffDiagnosisList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])

				for (int i = 0; i < diffDiagnosisList.size(); i++) {
					def diffDiagnosis = diffDiagnosisList.get(i)

					KeywordUtil.logInfo("Differential Diagnosis→ $diffDiagnosis")

					def result = CommonStory.getKeyValueDetails(diffDiagnosis, 'DD')

					if (result) {
						def code = result._key

						def desc = result._expected

						KeywordUtil.logInfo("Result Expected: $code, $desc")

						def expected = "${code}, ${desc}"

						TestObject tableFDDifferentialDiagnosis = testObjectStory.tableFDDifferentialDiagnosis(code, desc)

						def isPresent =	WebUI.verifyElementPresent(tableFDDifferentialDiagnosis,1,FailureHandling.OPTIONAL)

						assertStory.verifyMatch("Differential Diagnosis- $expected", isPresent, true)
					}
				}
			} else {
				KeywordUtil.markWarning('No Differential Diagnosis found')
			}
			// endregion Differential Diagnosis
		} else {
			KeywordUtil.markWarning('No Differential Diagnosis found')
		}

		if (CommonStory.isNullOrEmpty(expectedAssessment) == false) {
			// region Assessment
			List assessmentList = CommonStory.getListObject(expectedAssessment)

			if (assessmentList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])

				def _expectedAssessment = assessmentList.join('\n')

				KeywordUtil.logInfo("Assessment→ $_expectedAssessment")

				def actualAssessment = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea Assessments'), 'value',FailureHandling.OPTIONAL)

				assertStory.verifyMatch("Assessment", actualAssessment, _expectedAssessment)
			} else {
				KeywordUtil.markWarning('No Assessment found')
			}
			// endregion Assessment
		} else {
			KeywordUtil.markWarning('No Assessment found')
		}

		if (CommonStory.isNullOrEmpty(expectedPlans) == false) {
			// region Plans
			List plansList = CommonStory.getListObject(expectedPlans)

			if (plansList.size() > 0) {
				navigateStory.SelectEncounterElementFromLeftNavOnEncounter([('pElementPage') : 'Final Findings', ('pElement') : 'Final Diagnoses'])

				def _expectedPlans = plansList.join('\n')

				KeywordUtil.logInfo("Plans→ $_expectedPlans")

				def actualPlans = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/div Plans'), 'value',FailureHandling.OPTIONAL)

				if (CommonStory.isNullOrEmpty(actualPlans)) {
					actualPlans = WebUI.getText(findTestObject('EncounterPage/Encounter Details/div Plans'), FailureHandling.OPTIONAL)
				}

				assertStory.verifyMatch("Plans", actualPlans, _expectedPlans)
			} else {
				KeywordUtil.markWarning('No Plans found')
			}
			// endregion Plans
		} else {
			KeywordUtil.markWarning('No Plans found')
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

		if (CommonStory.isNullOrEmpty(type)) {
			KeywordUtil.logInfo("No type $type present → $text skipped.")
			return
		}

		/** prefix handling **/
		switch (key) {
			case "ED":
				name = "Eye_Diseases.${name}"
				break

			case "CES":
				name = "Current_Eye_Symptoms.${name}"
				break

			case "ROS":
				name = "Review_of_Systems_Brief.${name}"
				break

			default:
				KeywordUtil.markWarning("Unknown key → $key")
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
			KeywordUtil.markFailed("Radio Button not found → $text ($name:$type)")
			return
		}

		/** find web element safely **/
		WebElement el = WebUI.findWebElement(rbTestObject, 5, FailureHandling.OPTIONAL)

		if (!el) {
			KeywordUtil.markFailed("Radio button missing in DOM → $text ($name:$type)")
			return
		}

		boolean isChecked = el.isSelected()

		KeywordUtil.logInfo("Checked? $isChecked → ($text, $name, $type)")

		assertStory.verifyMatch("${text}-${type}", isChecked.toString(), "true")
	}

	@Keyword
	def generateSOAPNoteByAppendPauseResumeStop(String FileTime, String RecordFilePath) {
		int fileTimeinSeconds = Integer.valueOf(FileTime)

		int pauseTimeinSeconds = 10

		fileTimeinSeconds = (fileTimeinSeconds - pauseTimeinSeconds)

		KeywordUtil.logInfo("File Path $RecordFilePath")

		def fakeMic = new FakeMicStream(RecordFilePath)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 5, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'))

		KeywordUtil.logInfo('Clicked on Append Audio Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/div_Append-mode recording started'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(pauseTimeinSeconds)

		fakeMic.pause()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'))
		KeywordUtil.logInfo('Clicked on Pause Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'), 20, FailureHandling.OPTIONAL)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'))
		KeywordUtil.logInfo('Clicked on Resume Button')

		fakeMic.resume()

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'), 10, FailureHandling.OPTIONAL)

		WebUI.delay(fileTimeinSeconds)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.STOP_ON_FAILURE)

		fakeMic.stop()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		KeywordUtil.logInfo('Clicked on Stop Append Audio Button')

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

		KeywordUtil.logInfo("File Path $RecordFilePath")

		def fakeMic = new FakeMicStream(RecordFilePath)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		KeywordUtil.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		WebUI.delay(pauseTimeinSeconds)

		fakeMic.pause()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'))
		KeywordUtil.logInfo('Clicked on Pause Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'),10, FailureHandling.OPTIONAL)

		WebUI.delay(resumeTimeinSeconds)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'),10, FailureHandling.STOP_ON_FAILURE)

		fakeMic.resume()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'))
		KeywordUtil.logInfo('Clicked on Resume Button')

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_PAUSED_txt'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/button_Resume'), 10, FailureHandling.OPTIONAL)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Pause'), 10, FailureHandling.OPTIONAL)

		WebUI.delay(fileTimeinSeconds)

		fakeMic.stop()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		KeywordUtil.logInfo('Clicked on Stop Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.OPTIONAL)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120)
	}

	@Keyword
	def generateSOAPNoteByAppendStartStop(String FileTime, String RecordFilePath) {
		def fileTimeinSeconds = Integer.valueOf(FileTime)

		KeywordUtil.logInfo("File Path $RecordFilePath")

		def fakeMic = new FakeMicStream(RecordFilePath)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'))

		KeywordUtil.logInfo('Clicked on Append Audio Button')

		fakeMic.start()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/div_Append-mode recording started'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(fileTimeinSeconds)

		fakeMic.stop()

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		KeywordUtil.logInfo('Clicked on Stop Append Audio Button')

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

		KeywordUtil.logInfo("File Path $UploadFilePath")

		def fakeMic = new FakeMicStream(UploadFilePath)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		KeywordUtil.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		WebUI.delay(fileTimeinSeconds)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		KeywordUtil.logInfo('Clicked on Stop Record Button')

		fakeMic.stop()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.OPTIONAL)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'), 5, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def generateSOAPNoteByRecordStartStopOnCloud(String FileTime, String UploadFilePath) {
		int fileTimeinSeconds = Integer.valueOf(FileTime)

		KeywordUtil.logInfo("File Path $UploadFilePath")

		def fakeMic = new FakeMicStream(UploadFilePath)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'))

		KeywordUtil.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		// Open new tab with MP3 file using file:// protocol
		WebUI.newTab(UploadFilePath)

		// Step 4: Wait for audio to load (optional)
		WebUI.delay(2)

		// Step 5: Switch back to previous tab
		WebUI.switchToWindowIndex(0)

		WebUI.delay(fileTimeinSeconds)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		KeywordUtil.logInfo('Clicked on Stop Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.OPTIONAL)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'), 5, FailureHandling.STOP_ON_FAILURE)
	}


	@Keyword
	def StopRecordingForSOAPNote(String FileTime, String UploadFilePath) {

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		KeywordUtil.logInfo('Clicked on Stop Record Button')

		fakeMic.stop()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.OPTIONAL)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.OPTIONAL)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Record'), 5, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def generateSOAPNoteByUploadingFile(String UploadFilePath) {
		KeywordUtil.logInfo("File Path $UploadFilePath")

		WebUI.uploadFile(findTestObject('EVAAPage/EVAA Scribe/Menu/defile input'), UploadFilePath)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/File processed successfully'), 180, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Header/button_Append Audio'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Evaa Mike'), 120, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementClickable(findTestObject('EVAAPage/EVAA Scribe/Finalize'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/SOAP Notes'), 120, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	def UploadingSOAPNoteFile(String UploadFilePath) {
		KeywordUtil.logInfo("File Path $UploadFilePath")

		WebUI.uploadFile(findTestObject('EVAAPage/EVAA Scribe/Menu/defile input'), UploadFilePath)
	}

	@Keyword
	def generateSOAPNoteByUploadingFileAndSwitchPatient(String UploadFilePath) {
		UploadingSOAPNoteFile(UploadFilePath)
	}

	@Keyword
	def verifySOAPNoteGenerateSucessfully() {
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/File processed successfully'), 150, FailureHandling.OPTIONAL)

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

		KeywordUtil.logInfo('Clicked on Start Record Button')

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/div_RecordTime'), 5, FailureHandling.STOP_ON_FAILURE)

		fakeMic.start()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		// Collapse Expand Recording Screen
		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

		CustomKeywords.'steps.CommonSteps.createNewEncounter'(FirstName, LastName, EncounterType, ExamLocation, Provider, Technician,
				false)

		navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])

		String encounterId = VariableStories.getItem('ENCOUNTER_ID')

		KeywordUtil.logInfo("Encounter Id=> $encounterId")

		CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

		// Stop Recording
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(10)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		KeywordUtil.logInfo('Clicked on Stop Record Button')

		fakeMic.stop()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.STOP_ON_FAILURE)

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

		KeywordUtil.logInfo('Clicked on Start Record Button')

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

		KeywordUtil.logInfo("Encounter Id=> $encounterId")

		CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

		CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

		// Stop Recording
		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'), 30, FailureHandling.STOP_ON_FAILURE)

		WebUI.delay(10)

		WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Menu/img_Stop'))

		KeywordUtil.logInfo('Clicked on Stop Record Button')

		fakeMic.stop()

		WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Toast/Generating SOAP Notes'), 10, FailureHandling.OPTIONAL)

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
			KeywordUtil.markFailed("❌ DirectDictationData has NO rows")
			return
		}

		int row = 1   // first row

		if (!VariableStories.elementStorage.isEmpty()) {
			def elementStorageList = VariableStories.elementStorage

			try {
				WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

				for (String name : elementStorageList) {
					KeywordUtil.logInfo("Element from Storage → ${name}")

					TestObject sectionTO = CommonStory.sectionMapForDirectDictation.get(name)

					if (!sectionTO) {
						KeywordUtil.markWarning("No TestObject mapped for → ${name}")
						return
					}

					List<WebElement> elements = WebUI.findWebElements(sectionTO, 10)

					elements.each { WebElement el ->
						KeywordUtil.logInfo("${name} value → ${el.text}")

						String textToAppend = dictationData.getValue(name, row)

						el.click()
						el.sendKeys(Keys.chord(Keys.CONTROL, Keys.END))
						el.sendKeys(" ${textToAppend}")
					}
				}
			} catch (e) {
				e.printStackTrace()
			}finally		{
				WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/input_Search'))

				// Switch back
				WebUI.switchToDefaultContent()
			}
		}
	}

	@Keyword
	def verifyStoredDirectDictationOnEVAAScribe(int index = 1) {

		if (VariableStories.elementStorage.isEmpty()) {
			KeywordUtil.markWarning("No stored elements for verification")
			return
		}

		TestData dictationData = TestDataFactory.findTestData('Data Files/DirectDictationData')

		int row = 1   // first row

		int rowCount = dictationData.getRowNumbers()
		if (index < 1 || index > rowCount) {
			KeywordUtil.markFailed("Invalid row index ${index}")
			return
		}

		CommonStory commonStory = new CommonStory(dictationData,index);

		if (!VariableStories.elementStorage.isEmpty()) {
			WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

			try {
				def elementStorageList = VariableStories.elementStorage
				for (String name : elementStorageList) {
					KeywordUtil.logInfo("Element from Storage → ${name}")

					TestObject sectionTO = CommonStory.sectionMapForDirectDictation.get(name)

					if (!sectionTO) {
						KeywordUtil.markWarning("No TestObject mapped for → ${name}")
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
			KeywordUtil.markFailed("❌ DirectDictationData has NO rows")
			return
		}

		int row = 1   // first row

		if (!VariableStories.elementStorageForDirectDictation.isEmpty()) {
			VariableStories.elementStorageForDirectDictation.clear()
		}

		if (!VariableStories.elementStorage.isEmpty()) {

			KeywordUtil.logInfo("File Path $UploadFilePath")

			def elementStorageList = VariableStories.elementStorage

			try {
				WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

				for (String name : elementStorageList) {

					if (!(name in ['ChiefComplaint', 'HPI'])) {
						break
					}

					VariableStories.elementStorageForDirectDictation << name

					KeywordUtil.logInfo("Element from Storage → ${name}")

					TestObject sectionTO = CommonStory.sectionMapForDirectDictation.get(name)

					if (!sectionTO) {
						KeywordUtil.markWarning("No TestObject mapped for → ${name}")
						return
					}

					List<WebElement> elements = WebUI.findWebElements(sectionTO, 10)

					elements.each { WebElement el ->
						def fakeMic = new FakeMicStream(UploadFilePath)

						KeywordUtil.logInfo("${name} value → ${el.text}")

						String textToAppend = dictationData.getValue(name, row)

						el.click()
						el.sendKeys(Keys.chord(Keys.CONTROL, Keys.END))
						//						el.sendKeys(Keys.SPACE)

						String moduleName = CommonStory.moduleMapForDirectDictation.get(name)

						TestObject img_Start_Dictation = testObjectStory.img_Start_Dictation(moduleName)

						TestObject img_Stop_Dictation = testObjectStory.img_Stop_Dictation(moduleName)

						WebUI.click(img_Start_Dictation)
						KeywordUtil.logInfo("Clicked on ${name} to Start Dictation")

						WebUI.waitForElementVisible(img_Stop_Dictation, 5, FailureHandling.STOP_ON_FAILURE)

						fakeMic.start()
						KeywordUtil.logInfo("Dictation Started.")

						WebUI.delay(10)

						WebUI.click(img_Stop_Dictation)
						KeywordUtil.logInfo("Clicked on ${name} to Stop Dictation")

						fakeMic.stop()
						KeywordUtil.logInfo("Dictation Stopped.")

						WebUI.waitForElementVisible(img_Start_Dictation, 5, FailureHandling.STOP_ON_FAILURE)
					}

					if(index == breakIndex) break;

					index++;
				}
			} catch (e) {
				e.printStackTrace()
			}finally		{
				WebUI.click(findTestObject('EVAAPage/EVAA Scribe/Header/input_Search'))

				// Switch back
				WebUI.switchToDefaultContent()
			}
		}
	}

	@Keyword
	def verifyRecordedDirectDictationAddedOnEVAAScribe() {

		if (VariableStories.elementStorageForDirectDictation.isEmpty()) {
			KeywordUtil.markWarning("No stored elements for verification")
			return
		}

		if (!VariableStories.elementStorageForDirectDictation.isEmpty()) {
			WebUI.switchToFrame(findTestObject('EVAAPage/EVAA Scribe/iFrame'), 10)

			try {
				def elementStorageList = VariableStories.elementStorageForDirectDictation
				for (String name : elementStorageList) {
					KeywordUtil.logInfo("Element from Storage → ${name}")

					TestObject sectionTO = CommonStory.sectionMapForSOAPNote.get(name)

					if (!sectionTO) {
						KeywordUtil.markWarning("No TestObject mapped for → ${name}")
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

						def actualLen = actualTexts[i].trim().split('\\s+').length

						def expectedLen = expectedText.trim().split('\\s+').length

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
		KeywordUtil.logInfo("Clicked on Transfer button.")

		try {
			WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/toast_Data transferred'), 10, FailureHandling.CONTINUE_ON_FAILURE)

			WebUI.focus(findTestObject('EncounterPage/Encounter Details/Data Transferred/input_btnDataTransferEncBill'), FailureHandling.STOP_ON_FAILURE)

			WebUI.waitForElementVisible(findTestObject('EncounterPage/Encounter Details/Data Transferred/powerTip_Data transferred'), 5, FailureHandling.STOP_ON_FAILURE)
			KeywordUtil.markPassed("Data transferred to Superbill.")
		} catch (e) {
			KeywordUtil.markFailedAndStop("Data not transferred to Superbill.")
		}
	}
}