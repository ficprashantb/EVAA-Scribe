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

import internal.GlobalVariable

public class TestObjectStory {

	TestObject td_EncounterId(String encounterId) {
		return findTestObject('EncounterPage/EncounterHx/Grid/td_EncounterId',
				[('encounterId') : encounterId]
				)
	}

	TestObject input_RadioButton(String id, String value) {
		return findTestObject(
				'EncounterPage/Encounter Details/Radio Button/input_RadioButton',
				[('id') : id, ('value') : value]
				)
	}

	TestObject input_CurrentEyeSymptoms(String inputId) {
		return findTestObject('EncounterPage/Encounter Details/Current Eye Symptoms/input_CurrentEyeSymptoms',
				[('inputId') : inputId]
				)
	}

	TestObject input_Review_of_Systems(String inputId) {
		return findTestObject('EncounterPage/Encounter Details/Review Of Systems/input_Review_of_Systems',
				[('inputId') : inputId]
				)
	}

		TestObject tableAllergies(int index) {
			return findTestObject('EncounterPage/Encounter Details/table Allergies',
					[('index') : index]
					)
		}

		TestObject tableMedications(int index) {
			return findTestObject('EncounterPage/Encounter Details/table Medications',
					[('index') : index]
					)
		}
	
		TestObject tableProblems(int index) {
			return findTestObject('EncounterPage/Encounter Details/table Problems',
					[('index') : index]
					)
		}

	TestObject tableFDDifferentialDiagnosis(String code, String description) {
		return findTestObject('EncounterPage/Encounter Details/table FD Differential Diagnosis',
				[ ('code')        : code,
					('description') : description]
				)
	}

	TestObject inputEyeDiseases(String eye) {
		return findTestObject('EncounterPage/Encounter Details/Eye Diseases/input_Eye_Diseases',
				[('eye') : eye]
				)
	}

	TestObject inputMentalAndFunctionalStatus(String status) {
		return findTestObject('EncounterPage/Encounter Details/Mental and Functional Status/input_Mental_and_Functional_Status',
				[('status') : status]
				)
	}

	TestObject divPatientDOBDictationDate(String patientName) {
		return findTestObject('EVAAPage/EVAA Scribe/Left Side Filter/div_PatientDOB_DictationDate',
				[('patientName') : patientName]
				)
	}

	TestObject span_Search(String search) {
		return findTestObject('EVAAPage/EVAA Scribe/Header/span_Search',
				[('search') : search]
				)
	}
	
	TestObject img_Start_Dictation(String type) {
		return findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Start_Dictation',
				[('type') : type]
				)
	}
	
	TestObject img_Stop_Dictation(String type) {
		return findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Stop_Dictation',
				[('type') : type]
				)
	}
	
}
