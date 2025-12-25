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

import com.kms.katalon.core.util.KeywordUtil as KeywordUtil

import groovy.json.JsonOutput as JsonOutput
import groovy.json.JsonSlurper as JsonSlurper


public class CommonStory {

	static boolean isNullOrEmpty(def val) {
		return val == null || val == '' || val == 'null'
	}

	static def getListObject(def expectedObj) {
		List reviewList = []
		try {
			if (!(expectedObj)) {
				KeywordUtil.logInfo('No Review Of Systems found')

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
				KeywordUtil.logInfo('No Review Of Systems found')

				reviewList= []
			}
		} catch (e) {
			reviewList= []
		}

		return reviewList;
	}

	static def getKeyValueDetails(def listData, def keyName) {

		def delimeter = ':'
		switch (keyName) {
			case "DD":
				delimeter = ','
				break
		}

		def parts = listData.toString()
				.split(delimeter)
				.collect { it.trim() }
				.findAll { it }

		if (parts.size() < 2) {
			KeywordUtil.logInfo("Invalid Data format => $listData")
			return null
		}

		def key = parts[0].trim()
		def expected = parts[1].trim()
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
			'Other'        		: 'OTHER_NOTES'
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
			'Nursing'        			: 'NURSING_NOTES'

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
			'Other'					: 'OTHER_NOTES'
		]

		map.find { k, v -> txt.containsIgnoreCase(k) }?.value
	}
}

