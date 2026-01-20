import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import stories.NavigateStory as NavigateStory
import stories.VariableStories as VariableStories
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory

GlobalVariable.EVAA_SC_NO = 'EVAA_SCRIBE_TC_U08'

VariableStories.clearItem(GlobalVariable.EVAA_SC_NO)

TestData patientData = TestDataFactory.findTestData('Data Files/PatientData')

def ptIndex = 8

def LastName = patientData.getValue('LastName', ptIndex)

def FirstName = patientData.getValue('FirstName', ptIndex)

def DOB = patientData.getValue('DOB', ptIndex)

def Provider_FirstName = patientData.getValue('Provider_FirstName', ptIndex)

def Provider_LastName = patientData.getValue('Provider_LastName', ptIndex)

NavigateStory navigateStory = new NavigateStory()

CustomKeywords.'steps.CommonSteps.maximeyesLogin'(GlobalVariable.EVAA_SiteURL, GlobalVariable.EVAA_UserName, GlobalVariable.EVAA_Password)

CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

String ProviderName = "$Provider_FirstName $Provider_LastName"

CustomKeywords.'steps.CommonSteps.createNewEncounter'(FirstName, LastName, EncounterType, ExamLocation, ProviderName, Technician)

def uploadFilePath = RunConfiguration.getProjectDir() + "/Files/$UploadFilePath"

KeywordUtil.logInfo("Upload File Path=> $uploadFilePath")

CustomKeywords.'steps.EVAASteps.commonStepsForEVAA'(FirstName, LastName)

CustomKeywords.'steps.EVAASteps.generateSOAPNoteByUploadingFile'(uploadFilePath)

CustomKeywords.'steps.EVAASteps.generateSOAPNoteByUploadingFileAndSwitchPatient'(uploadFilePath)

CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

def LastName2 = patientData.getValue('LastName', 2)

def FirstName2 = patientData.getValue('FirstName', 2)

CustomKeywords.'steps.CommonSteps.findPatient'(LastName2, FirstName2)

//navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])
CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])

String encounterId = VariableStories.getItem('ENCOUNTER_ID')

KeywordUtil.logInfo("Encounter Id=> $encounterId")

CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

CustomKeywords.'steps.EVAASteps.verifySOAPNoteGenerateSucessfully'()

CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

CustomKeywords.'steps.EVAASteps.finalizedAndSendToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)