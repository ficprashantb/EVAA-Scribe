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
import stories.LogStories as LogStories
import stories.NavigateStory as NavigateStory
import stories.UtilHelper as UtilHelper
import stories.VariableStories as VariableStories
NavigateStory navigateStory = new NavigateStory()

GlobalVariable.EVAA_SC_NO = 'EVAA_SCRIBE_TC_R09'

VariableStories.clearItem(GlobalVariable.EVAA_SC_NO)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 1~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.MaximeyesLoginAndFindPatient'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName, 
    EncounterType, ExamLocation, Technician)

def uploadFilePath = UtilHelper.getFilePath(UploadFilePath)

LogStories.logInfo("Record File Path=> $uploadFilePath")

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 2~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.generateSOAPNoteByRecordStartStop'(FileTime, uploadFilePath)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 3~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifyEVAAScribeAllDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 4~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 5~~~~~~~~~~~~~~~~~~~~~~')

navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])

String encounterId = VariableStories.getItem('ENCOUNTER_ID')

LogStories.logInfo("Encounter Id=> $encounterId")

CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 6~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 7~~~~~~~~~~~~~~~~~~~~~~')

//Direct Dictation By Record Start Stop
def recordFilePath = UtilHelper.getFilePath(RecordFilePath)

LogStories.logInfo("Upload File Path=> $recordFilePath")

CustomKeywords.'steps.EVAASteps.directDictationByRecordStartStopOnElements'(recordFilePath)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 8~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifyRecordedDirectDictationAddedOnEVAAScribe'()

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 9~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifyEVAAScribeAllDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 10~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.finalizedAndSendToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 11~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'()