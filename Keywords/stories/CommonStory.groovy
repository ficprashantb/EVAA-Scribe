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
import org.openqa.selenium.WebElement 

import groovy.json.JsonOutput as JsonOutput
import groovy.json.JsonSlurper as JsonSlurper
import stories.AssertStory

public class CommonStory {
	AssertStory assertStory = new AssertStory();

	TestData dictationData
	int row

	public CommonStory(TestData _dictationData, int _row) {
		dictationData = _dictationData
		row = _row
	}

	static boolean isNullOrEmpty(def val) {
		return val == null || val == '' || val == 'null'
	}

	private void verifySection(
			String label,
			TestObject to,
			String variableKey,
			String dataColumn
	) {
		if (!WebUI.verifyElementPresent(to, 1, FailureHandling.OPTIONAL)) {
			LogStories.markWarning("Direct Dictation ${label} not found")
			return
		}

		List<WebElement> elements = WebUI.findWebElements(to, 10)
		List<String> actualTexts = elements.collect { it.text.trim() }

		def storedValue = VariableStories.getItem(variableKey)
		if (CommonStory.isNullOrEmpty(storedValue)) return

			List expectedList = CommonStory.getListObject(storedValue)

		expectedList.eachWithIndex { expected, i ->
			String appendText = dictationData.getValue(dataColumn, row)
			String expectedText = "${expected} ${appendText}"

			assertStory.verifyMatch("Direct Dictation→→ ${label}",actualTexts[i],expectedText					)
		}
	}

	static def getListObject(def expectedObj) {
		List reviewList = []
		try {
			if (!(expectedObj)) {
				LogStories.logInfo('No Review Of Systems found')

				reviewList= []
			}

			if (expectedObj instanceof String) {
				def parsed = new JsonSlurper().parseText(expectedObj)

				if (parsed instanceof List) {
					reviewList = parsed
				} else {
					reviewList = [parsed]
				}
			} else if (expectedObj instanceof List) {
				reviewList = expectedObj
			} else {
				reviewList = [expectedObj]
			}

			if (reviewList.isEmpty()) {
				LogStories.logInfo('No Review Of Systems found')

				reviewList= []
			}
		} catch (e) {
			reviewList << expectedObj
		}

		return reviewList;
	}

	static def getKeyValueDetails(def listData, def keyName) {

		def delimeter = ':'
		if (keyName == 'DD') {
			delimeter = ','
		}

		def parts = listData.toString()
				.split(delimeter, 2)   // ✅ ONLY FIRST SPLIT
				.collect { it.trim() }
				.findAll { it }

		if (parts.size() < 2) { 
			def logData = JsonOutput.toJson(listData)
			LogStories.logInfo("Invalid Data format =>  $logData")
			return null
		}

		def key = parts[0]
		def expected = parts[1]
		def name = ""

		switch (keyName) {
			case "CES":
				name = CommonStory.getCurrentEyeSymptoms(key)
				break
			case "ROS":
				name = CommonStory.getReviewOfSystems(key)
				break
			case "ED":
				name = CommonStory.getEyeDiseases(key)
				break
			case "MFS":
				name = CommonStory.getMentalAndFunctionalStatus(key)
				break
		}

		return [_key: key, _expected: expected, _name: name]
	}

	static def getODOSOU(def data) {

		def match = data =~ /\((OD|OS|OU)\)/
		def type = match ? match[0][1] : ""

		def result = data.toString()
				.replaceAll(/\(\s*(OD|OS|OU)\s*\)/, "")
				.replaceAll(/\s+/, " ")
				.trim()

		return [
			type  : type,
			value : result
		]
	}

	static String getCurrentEyeSymptoms(String txt) {

		def map = [
			'Glare Sensitivity' : 'GLARE_NOTES',
			'Headaches'        	: 'HEADACHE_NOTES',
			'Light Sensitivity'	: 'LIGHT_SENSITIVITY_NOTES',
			'Tired Eyes'       	: 'TIRED_NOTES',
			'Burning'        	: 'BURNING_NOTES',
			'Dryness'        	: 'DRYNESS_NOTES',
			'Epiphora'        	: 'EPIPHORA_NOTES',
			'Eyelid Swell'      : 'EYELID_SWELLING_NOTES',
			'Eye Pain/Sore'     : 'EYE_PAIN_OR_SORENESS_NOTES',
			'Eye Pain'     		: 'EYE_PAIN_OR_SORENESS_NOTES',
			'Sore'     			: 'EYE_PAIN_OR_SORENESS_NOTES',
			'Foreign Body'      : 'FOREIGN_BODY_SENSATION_NOTES',
			'Eyelid Infect.'    : 'INFECTION_OF_EYE_LID_NOTES',
			'Itching'        	: 'ITCHING_NOTES',
			'Mucous'        	: 'MUCOUS_NOTES',
			'Eyelid Droop'      : 'PTOSIS_NOTES',
			'Redness'        	: 'REDNESS_NOTES',
			'Sand/Grit Feel'    : 'SANDY_OR_GRITTY_FEELING_NOTES',
			'Sand'        		: 'SANDY_OR_GRITTY_FEELING_NOTES',
			'Grit Feel'        	: 'SANDY_OR_GRITTY_FEELING_NOTES',
			'Dist. Blur'        : 'BLURRED_VISION_DISTANCE_NOTES',
			'Near Blur'        	: 'BLURRED_VISION_NEAR_NOTES',
			'Distortion'        : 'DISTORTED_VISION_NOTES',
			'Double'        	: 'DOUBLE_VISION_NOTES',
			'Flashes'        	: 'FLASHES_OF_LIGHTS_NOTES',
			'Floaters'        	: 'FLOATERS_OF_SPOTS_NOTES',
			'Fluctuation'       : 'FLUCTUATING_VISION_NOTES',
			'Central Loss'      : 'LOSS_OF_CENTRAL_VISION_NOTES',
			'Side Loss'      	: 'LOSS_OF_SIDE_VISION_NOTES',
			'Loss of Vision'    : 'LOSS_OF_VISION_NOTES',
			'Other'        		: 'OTHER_NOTES',
			'Additional'        : 'NOTES'
		]

		map.find { k, v -> txt.containsIgnoreCase(k) }?.value
	}

	static String getReviewOfSystems(String txt) {

		def map = [

			'Constitutional Sym'        : 'CONSTITUTIONAL_SYSTEMS_NOTES',
			'Ears, Nose, Throat, Mouth'	: 'EAR_NOSE_THROAT_MOUTH_NOTES',
			'Ears'						: 'EAR_NOSE_THROAT_MOUTH_NOTES',
			'Nose'						: 'EAR_NOSE_THROAT_MOUTH_NOTES',
			'Throat'					: 'EAR_NOSE_THROAT_MOUTH_NOTES',
			'Mouth'						: 'EAR_NOSE_THROAT_MOUTH_NOTES',
			'Cardiovascular'       		: 'EAR_NOSE_THROAT_MOUTH_NOTES',
			'Respiratory'        		: 'RESPIRATORY_NOTES',
			'Gastrointestinal'        	: 'GASTROINSTESTINAL_NOTES',
			'Genital, Kidney, Bladder'	: 'URINARY_NOTES',
			'Genital'        			: 'URINARY_NOTES',
			'Kidney'        			: 'URINARY_NOTES',
			'Bladder'        			: 'URINARY_NOTES',
			'Muscles, Bones, Joints'	: 'MUSCULOSKELETAL_NOTES',
			'Muscles'        			: 'MUSCULOSKELETAL_NOTES',
			'Bones'       				: 'MUSCULOSKELETAL_NOTES',
			'Joints'        			: 'MUSCULOSKELETAL_NOTES',
			'Skin'        				: 'SKIN_NOTES',
			'Neurological'        		: 'NEUROLOGICAL_NOTES',
			'Psychiatric'        		: 'PSYCHIATRIC_NOTES',
			'Endocrine'        			: 'ENDOCRINE_NOTES',
			'Blood/Lymph'        		: 'BLOOD_NOTES',
			'Blood'        				: 'BLOOD_NOTES',
			'Lymph'        				: 'BLOOD_NOTES',
			'Allergic/Immuno'        	: 'ALLERGIC_IMMUNOLOGICAL_NOTES',
			'Allergic'        			: 'ALLERGIC_IMMUNOLOGICAL_NOTES',
			'Immuno'        			: 'ALLERGIC_IMMUNOLOGICAL_NOTES',
			'Pregnant'        			: 'PREGNANT_NOTES',
			'Nursing'        			: 'NURSING_NOTES',
			'Additional'        		: 'ADDITIONAL_NOTES'

		]

		map.find { k, v -> txt.containsIgnoreCase(k) }?.value
	}

	static String getMentalAndFunctionalStatus(String txt) {

		def map = [

			'Mood or Affect'        : 'MOOD_AFFECT',
			'Orientation'			: 'ORIENTATION',
			'Functional Status'		: 'FUNCTIONAL_STATUS',
			'Other Disability'		: 'OTHER_DISABILITY'
		]

		map.find { k, v -> txt.containsIgnoreCase(k) }?.value
	}

	static String getEyeDiseases(String txt) {

		def map = [

			'Amblyopia'        		: 'AMBLYOPIA_NOTES',
			'Blepharitis'			: 'BLEPHARITIS_NOTES',
			'Blindness'				: 'BLINDNESS_NOTES',
			'Cataract'				: 'CATARACT_NOTES',
			'Color Blind'			: 'COLOR_BLINDNESS_NOTES',
			'Diab. Retinopathy'		: 'DIABETIC_RETINOPATHY_NOTES',
			'Dry Eye Synd'			: 'DRY_EYE_SYNDROME_NOTES',
			'Eye Injuries'			: 'EYE_INJURIES_NOTES',
			'Glaucoma'				: 'GLAUCOMA_NOTES',
			'Glaucoma Suspect'		: 'GLAUCOMA_SUSPECT_NOTES',
			'High Risk Med'			: 'HIGH_RISK_MEDICATION_NOTES',
			'Macular Degen'			: 'MACULAR_DEGENERATION_NOTES',
			'PVD'					: 'PVD_NOTES',
			'Retinal Detach.'		: 'RETINAL_DETACHMENT_NOTES',
			'Strabismus'			: 'STRABISMUS_NOTES',
			'Keratoconus'			: 'KERATOCONUS_NOTES',
			'Corneal Disease'		: 'CORNEAL_DISEASE_NOTES',
			'Other'					: 'OTHER_NOTES',
			'Additional'        	: 'ADDITIONAL_NOTES'
		]

		map.find { k, v -> txt.containsIgnoreCase(k) }?.value
	}

	static Map<String, TestObject> sectionMapForDirectDictation = [
		ChiefComplaint: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/ChiefComplaint'),
		HPI: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/HPI'),
		CurrentEyeSymptoms: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Current Eye Symptoms'),
		Allergies: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Allergies'),
		Medications: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Medications'),
		ReviewOfSystems: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Review Of Systems'),
		Problems: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Problems'),
		Refractions: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Refractions'),
		AuxiliaryLabTests: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Auxiliary Lab Tests'),
		DifferentialDiagnosis: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Differential Diagnosis'),
		Assessment: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Assessment'),
		Plan: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Plans'),
		EyeDiseases: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Eye Diseases'),
		MentalAndFunctionalStatus: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Mental and Functional Status')
	] 

	static Map<String, TestObject> sectionMapForSOAPNote = [
		ChiefComplaint: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/ChiefComplaint'),
		HPI: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/HPI'),
		CurrentEyeSymptoms: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Current Eye Symptoms'),
		Allergies: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Allergies'),
		Medications: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Medications'),
		ReviewOfSystems: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Review Of Systems'),
		Problems: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Problems'),
		Refractions: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Refractions'),
		AuxiliaryLabTests: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Auxiliary Lab Tests'),
		DifferentialDiagnosis: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Differential Diagnosis'),
		Assessment: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Assessment'),
		Plan: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Plans'),
		EyeDiseases: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Eye Diseases'),
		MentalAndFunctionalStatus: findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Note/Mental and Functional Status')
	]

	static Map<String, String> sectionMapForStorageKey = [
		ChiefComplaint: 'SOAP_NOTE_CHIEF_COMPLAINT',
		HPI: 'SOAP_NOTE_HPI',
		CurrentEyeSymptoms: 'SOAP_NOTE_CURRENT_EYE_SYMPTOMS',
		Allergies:'SOAP_NOTE_ALLERGIES',
		Medications:'SOAP_NOTE_MEDICATION',
		ReviewOfSystems:'SOAP_NOTE_REVIEW_OF_SYSTEMS',
		Problems: 'SOAP_NOTE_PROBLEMS',
		Refractions: 'SOAP_NOTE_REFRACTIONS',
		AuxiliaryLabTests:'SOAP_NOTE_AUX_LAB_TESTS',
		DifferentialDiagnosis:'SOAP_NOTE_DIFF_DIAGNOSIS',
		Assessment: 'SOAP_NOTE_ASSESSMENT',
		Plan:'SOAP_NOTE_PLANS',
		EyeDiseases:'SOAP_NOTE_EYE_DISEASES',
		MentalAndFunctionalStatus: 'SOAP_NOTE_MENTAL_AND_FUNCTIONAL_STATUS'
	]

	static Map<String, String> moduleMapForDirectDictation = [
		ChiefComplaint: 'ChiefComplaint:',
		HPI: 'HPI:',
		CurrentEyeSymptoms: 'Current Eye Symptoms:',
		Allergies: 'Allergies:',
		Medications: 'Medications:',
		ReviewOfSystems: 'Review Of Systems - Brief:',
		Problems: 'Problems:',
		Refractions: 'Refractions:',
		AuxiliaryLabTests: 'Auxiliary/Lab Tests:',
		DifferentialDiagnosis: 'Differential Diagnosis:',
		Assessment: 'Assessment:',
		Plan: 'Plan:',
		EyeDiseases: 'Eye Diseases:',
		MentalAndFunctionalStatus: 'Mental and Functional Status:',
	]
}

