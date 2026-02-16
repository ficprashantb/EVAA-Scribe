import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.beans.Customizer as Customizer
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
import stories.LogStories as LogStories
import stories.NavigateStory as NavigateStory
import stories.VariableStories as VariableStories
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory

NavigateStory navigateStory = new NavigateStory()

GlobalVariable.EVAA_SC_NO = 'EVAA_SCRIBE_TC_U09'

VariableStories.clearItem(GlobalVariable.EVAA_SC_NO)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 1~~~~~~~~~~~~~~~~~~~~~~')

TestData patientData = TestDataFactory.findTestData('Data Files/PatientData')

def ptIndex = 9

def LastName = patientData.getValue('LastName', ptIndex)

def FirstName = patientData.getValue('FirstName', ptIndex)

def DOB = patientData.getValue('DOB', ptIndex)

def Provider_FirstName = patientData.getValue('Provider_FirstName', ptIndex)

def Provider_LastName = patientData.getValue('Provider_LastName', ptIndex)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 2~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.MaximeyesLoginAndFindPatient'(FirstName,LastName,  DOB, Provider_FirstName, Provider_LastName ,EncounterType, ExamLocation,Technician) 

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 2~~~~~~~~~~~~~~~~~~~~~~')

def uploadFilePath = RunConfiguration.getProjectDir() + "/Files/${UploadFilePath}"

KeywordUtil.logInfo("Upload File Path=> $uploadFilePath")

CustomKeywords.'steps.EVAASteps.UploadingSOAPNoteFile'(uploadFilePath)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 3~~~~~~~~~~~~~~~~~~~~~~')

// Collapse Expand Recording Screen
CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

def LastName2 = patientData.getValue('LastName', 2)

def FirstName2 = patientData.getValue('FirstName', 2)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 4~~~~~~~~~~~~~~~~~~~~~~')

//Find Patient 2

CustomKeywords.'steps.EVAASteps.MaximeyesLoginAndFindPatient'(FirstName2,LastName2,  DOB, Provider_FirstName, Provider_LastName ,EncounterType, ExamLocation,Technician, false, false, false)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 5~~~~~~~~~~~~~~~~~~~~~~')

//Find patient 1
CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 6~~~~~~~~~~~~~~~~~~~~~~')

navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])

String encounterId = VariableStories.getItem('ENCOUNTER_ID')

KeywordUtil.logInfo("Encounter Id=> $encounterId")

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 7~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 8~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 9~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifySOAPNoteGenerateSucessfully'()

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 10~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifyEVAAScribeAllDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 11~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.finalizedAndSendToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 12~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'()

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 13~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.TransferEncounterDataToSuperbill'()

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 14~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 15~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'()