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
import com.kms.katalon.core.util.KeywordUtil

import stories.AssertStory
import stories.ExceptionHelper as ExceptionHelper
import stories.LogStories as LogStories
import stories.NavigateStory as NavigateStory
import stories.VariableStories as VariableStories

GlobalVariable.EVAA_SC_NO = 'EVAA_SCRIBE_TC_U13'

VariableStories.clearItem(GlobalVariable.EVAA_SC_NO)

GlobalVariable.G_IS_LIMITED_ELEMENTS = true

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 1~~~~~~~~~~~~~~~~~~~~~~')
CustomKeywords.'steps.EVAASteps.GenerateSOAPNoteByUploadingFileForSinglePatient'(UploadFilePath,FirstName,LastName,  DOB, Provider_FirstName, Provider_LastName ,EncounterType, ExamLocation,Technician,false,false)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 2~~~~~~~~~~~~~~~~~~~~~~') 
CustomKeywords.'steps.EVAASteps.VerifyCopiedIndividualElementSOAPNotes'()

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 3~~~~~~~~~~~~~~~~~~~~~~') 
CustomKeywords.'steps.EVAASteps.finalizedAndSendToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName, false, false)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 4~~~~~~~~~~~~~~~~~~~~~~')
Boolean isVisible = WebUI.verifyElementNotPresent(findTestObject('EVAAPage/EVAA Scribe/SOAP Notes/Direct Dictation/Buttons/img_Copy_Note'), 5, FailureHandling.OPTIONAL)
if(isVisible) {
	LogStories.markPassed("Copy button is not displayed or is disabled when dictation is finalized.")
}
else {
	LogStories.markFailedAndStop("Copy button is displayed or is enabled when dictation is finalized.")
}


