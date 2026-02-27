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

GlobalVariable.EVAA_SC_NO = 'EVAA_SCRIBE_TC_R014'

VariableStories.clearItem(GlobalVariable.EVAA_SC_NO)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 1~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.MaximeyesLoginAndFindPatient'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName, 
    EncounterType, ExamLocation, Technician)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 2~~~~~~~~~~~~~~~~~~~~~~')

def recordFilePath = UtilHelper.getFilePath(RecordFilePath)

LogStories.logInfo("Record File Path=> $recordFilePath")

CustomKeywords.'steps.EVAASteps.RecordStartStop'(FileTime, recordFilePath)

LogStories.logInfo('Awaiting file upload...')

// Wait for toast message confirming file processed
WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/Error uploading file. Please try again'), 60, FailureHandling.STOP_ON_FAILURE)

LogStories.markPassed('Error uploading file. Please try again.')

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 3~~~~~~~~~~~~~~~~~~~~~~')

String expectedPtName = "$FirstName $LastName"

CustomKeywords.'steps.EVAASteps.verifySOAPNotesAndSpeakerNotesNotGenerated'(expectedPtName)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 4~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('false')

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 5~~~~~~~~~~~~~~~~~~~~~~')

String FinalizedStatus = 'Pending'

String MicStatus = 'Recording Not Started'

CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, DOB, FinalizedStatus, MicStatus)