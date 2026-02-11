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
import stories.VariableStories as VariableStories 

GlobalVariable.EVAA_SC_NO = 'EVAA_SCRIBE_TC_U11'

VariableStories.clearItem(GlobalVariable.EVAA_SC_NO)

String expectedPtName = "$FirstName $LastName"

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 1~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.maximeyesLogin'(GlobalVariable.EVAA_UserName, GlobalVariable.EVAA_Password)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 2~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

String ProviderName = "$Provider_FirstName $Provider_LastName"

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 3~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.createNewEncounter'(FirstName, LastName, EncounterType, ExamLocation, ProviderName, Technician)

def uploadFilePath = RunConfiguration.getProjectDir() + "/Files/${UploadFilePath}"

KeywordUtil.logInfo("Upload File Path=> $uploadFilePath")

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 4~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.commonStepsForEVAA'(FirstName, LastName,DOB)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 5~~~~~~~~~~~~~~~~~~~~~~')

// Log the file path
LogStories.logInfo('File Path: ' + uploadFilePath)

TestObject upload = findTestObject('EVAAPage/EVAA Scribe/Menu/defile input')

//WebUI.uploadFile(upload, uploadFilePath)
CustomKeywords.'com.katalon.testcloud.FileExecutor.uploadFileToWeb'(upload, uploadFilePath)

LogStories.logInfo('File uploaded: ' + uploadFilePath)

LogStories.logInfo('Awaiting file upload...')

// Wait for toast message confirming file processed
WebUI.waitForElementPresent(findTestObject('EVAAPage/EVAA Scribe/Toast/Error uploading file. Please try again'), 180, FailureHandling.STOP_ON_FAILURE)

LogStories.markPassed('Error uploading file. Please try again.')

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 6~~~~~~~~~~~~~~~~~~~~~~')
CustomKeywords.'steps.EVAASteps.verifySOAPNotesAndSpeakerNotesNotGenerated'(expectedPtName)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 7~~~~~~~~~~~~~~~~~~~~~~')
CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('false')

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 8~~~~~~~~~~~~~~~~~~~~~~')

String FinalizedStatus = 'Pending' 
String MicStatus='Recording Not Started'
CustomKeywords.'steps.EVAASteps.verifyEVAAScribeLeftSidePanel'(expectedPtName, DOB, FinalizedStatus, MicStatus) 


