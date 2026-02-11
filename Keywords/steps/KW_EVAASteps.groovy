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
import stories.AssertStory
import stories.CommonStory
import stories.LogStories
import stories.NavigateStory
import stories.TestObjectStory
import stories.VariableStories
import stories.WaitStory

public class KW_EVAASteps {

	NavigateStory navigateStory = new NavigateStory()
	TestObjectStory testObjectStory = new TestObjectStory()
	AssertStory assertStory = new AssertStory();
	WaitStory waitStory = new WaitStory()


	private String clean(String value) {
		return value?.replaceAll(":(?=.*:)", "")?.trim()
	}

	private String getValue(TestObject obj) {
		String value = WebUI.getAttribute(obj, 'value', FailureHandling.OPTIONAL)
		if (CommonStory.isNullOrEmpty(value)) {
			value = WebUI.getText(obj, FailureHandling.OPTIONAL)
		}
		return clean(value)
	}

	private void verifySimpleTable(String key, List dataList) {

		int index = 1

		dataList.each { expected ->
			TestObject obj = (key == "Allergies") ?
					testObjectStory.tableAllergies(index) :
					testObjectStory.tableProblems(index)

			String actual = getValue(obj)
			assertStory.verifyMatch(key, actual, clean(expected))
			index++
		}
	}

	private void verifyMedications(List dataList) {

		int index = 1

		for (int i = 0; i < dataList.size(); i++) {

			def result = CommonStory.getKeyValueDetails(dataList[i], 'MED')
			if (!result || result._key == 'Generic Name') continue

				String expected = result._expected

			if (i + 1 < dataList.size()) {
				def next = CommonStory.getKeyValueDetails(dataList[i + 1], 'MED')
				if (next?._key == 'Generic Name') {
					expected = "${expected} ${next._expected}"
				}
			}

			String actual = getValue(testObjectStory.tableMedications(index))
			assertStory.verifyContainsRegex("Medications", actual, clean(expected))
			index++
		}
	}

	private void verifyMultiLineText(String key, List dataList) {

		String expected = clean(dataList.join('\n'))

		TestObject obj = (key == "Assessment") ?
				findTestObject('EncounterPage/Encounter Details/textarea Assessments') :
				findTestObject('EncounterPage/Encounter Details/div Plans')

		String actual = getValue(obj)

		// Remove numbering
		actual = actual?.replaceAll(/(?m)^\d+\.\s*/, '')

		assertStory.verifyMatch(key, actual, expected)
	}

	private void verifyDynamicSection(List dataList,
			String prefix,
			String additionalPath,
			Closure<TestObject> objectResolver) {

		dataList.each { item ->

			def result = CommonStory.getKeyValueDetails(item, prefix)
			if (!result || result._expected == 'True') return

				String expected = clean(result._expected)
			String name = result._name

			TestObject obj = objectResolver(name)

			boolean isAdditional = result?._key
					?.toString()
					?.replaceAll(/[_:-]/,' ')
					?.toLowerCase()
					?.contains('additional')

			if (isAdditional) {
				obj = findTestObject(additionalPath)
			}

			String actual = getValue(obj)

			assertStory.verifyMatch("${prefix} - ${name}", actual, expected)

			if (prefix in ['CES', 'ROS', 'ED']) {
				verifyRadioButtonIsChecked(item, name, prefix)
			}
		}
	}

	@Keyword
	def verifyIndividualSOAPNoteSentToMaximeyes(String key,
			Boolean isElementText = false,
			Boolean isRefreshPresent = false) {

		LogStories.logInfo("---- SOAP Note Validation : ${key} ----")

		def variableKey = CommonStory.sectionMapForStorageKey.get(key)
		String expectedData = VariableStories.getItem(variableKey)

		if (CommonStory.isNullOrEmpty(expectedData)) {
			LogStories.markWarning("No data found for ${key}")
			return
		}

		List dataList = CommonStory.getListObject(expectedData)
		if (dataList.isEmpty()) {
			LogStories.markWarning("Empty list for ${key}")
			return
		}

		// Navigate only once
		CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key, isElementText, isRefreshPresent)
	
		switch (key) {

			case "Allergies":
			case "Problems":
				verifySimpleTable(key, dataList)
				break

			case "Medications":
				verifyMedications(dataList)
				break

			case "Assessment":
			case "Plan":
				verifyMultiLineText(key, dataList)
				break

			case "CurrentEyeSymptoms":
				verifyDynamicSection(dataList, 'CES',
				'EncounterPage/Encounter Details/Current Eye Symptoms/textarea_Additional_Notes_CES',
				{ name -> testObjectStory.input_CurrentEyeSymptoms(name) })
				break

			case "ReviewOfSystems":
				verifyDynamicSection(dataList, 'ROS',
				'EncounterPage/Encounter Details/Review Of Systems/textarea_Additional_Notes_ROS',
				{ name -> testObjectStory.input_Review_of_Systems(name) })
				break

			case "EyeDiseases":
				verifyDynamicSection(dataList, 'ED',
				'EncounterPage/Encounter Details/Eye Diseases/textarea_Additional_Notes_EyeDiseases',
				{ name -> testObjectStory.inputEyeDiseases(name) })
				break

			case "MentalAndFunctionalStatus":
				verifyDynamicSection(dataList, 'MFS', null,
				{ name -> testObjectStory.inputMentalAndFunctionalStatus(name) })
				break
		}
	}

	@Keyword
	def verifyIndividualSOAPNoteSentToMaximeyes2(String key, Boolean isElementText = false , Boolean isRefreshPresent = false) {
		LogStories.logInfo('----------------------Step AAH----------------------')

		def variableKey = CommonStory.sectionMapForStorageKey.get(key)
		String expectedData = VariableStories.getItem(variableKey)

		LogStories.logInfo("********************SOAP Note - ${key}*********************")

		WebUI.delay(4)

		// ===== Chief Complaint =====
		if (key == "ChiefComplaint" && !CommonStory.isNullOrEmpty(expectedData)) {
			CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

			String actualChiefComplaint = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea Patient Chief Complaint'), 'value', FailureHandling.STOP_ON_FAILURE)

			actualChiefComplaint=	actualChiefComplaint?.replaceAll(":(?=.*:)", "")
			expectedData=	expectedData?.replaceAll(":(?=.*:)", "")

			assertStory.verifyMatch("Chief Complaint", actualChiefComplaint, expectedData)
		}

		// ===== HPI =====
		else if (key == "HPI" && !CommonStory.isNullOrEmpty(expectedData)) {
			CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

			String actualHPI = WebUI.getAttribute(findTestObject('EncounterPage/Encounter Details/textarea HPI Notes'), 'value', FailureHandling.STOP_ON_FAILURE)

			actualHPI=	actualHPI?.replaceAll(":(?=.*:)", "")
			expectedData=	expectedData?.replaceAll(":(?=.*:)", "")
			assertStory.verifyMatch("HPI", actualHPI, expectedData)
		}

		// ===== Current Eye Symptoms =====
		else if (key == "CurrentEyeSymptoms" && !CommonStory.isNullOrEmpty(expectedData)) {
			List currentEyeSymptomsList = CommonStory.getListObject(expectedData)
			if (currentEyeSymptomsList.size() > 0) {
				CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

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
				CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

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
				CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

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
				CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

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
				CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

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
		//				CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)
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
				CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

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
				CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

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

				CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

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

				CustomKeywords.'stories.NavigateStory.navigateToEncounterElement'(key,isElementText,isRefreshPresent)

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
}
