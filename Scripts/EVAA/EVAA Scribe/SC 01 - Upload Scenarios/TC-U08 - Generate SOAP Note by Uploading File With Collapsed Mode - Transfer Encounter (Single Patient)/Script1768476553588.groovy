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

NavigateStory navigateStory = new NavigateStory()

GlobalVariable.EVAA_SC_NO = 'EVAA_SCRIBE_TC_U08'

VariableStories.clearItem(GlobalVariable.EVAA_SC_NO)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 1~~~~~~~~~~~~~~~~~~~~~~')
  
CustomKeywords.'steps.CommonSteps.maximeyesLogin'(GlobalVariable.EVAA_UserName, GlobalVariable.EVAA_Password)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 2~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

String ProviderName = "$Provider_FirstName $Provider_LastName"

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 3~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.createNewEncounter'(FirstName, LastName, EncounterType, ExamLocation, ProviderName, Technician)

def uploadFilePath = RunConfiguration.getProjectDir() + "/Files/$UploadFilePath"

KeywordUtil.logInfo("Upload File Path=> $uploadFilePath")

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 4~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.commonStepsForEVAA'(FirstName, LastName,DOB )

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 5~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.UploadingSOAPNoteFile'(uploadFilePath)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 6~~~~~~~~~~~~~~~~~~~~~~')

// Collapse Expand Recording Screen
CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 7~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 8~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.createNewEncounter'(FirstName, LastName, EncounterType, ExamLocation, Provider, Technician, false)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 9~~~~~~~~~~~~~~~~~~~~~~')

navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])

String encounterId = VariableStories.getItem('ENCOUNTER_ID')

KeywordUtil.logInfo("Encounter Id=> $encounterId")

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 10~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 11~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 12~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifySOAPNoteGenerateSucessfully'()

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 13~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifyEVAAScribeAllDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 14~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.finalizedAndSendToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 15~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'(Provider_FirstName, Provider_LastName)

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 16~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.TransferEncounterDataToSuperbill'()

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 17~~~~~~~~~~~~~~~~~~~~~~')

WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/iframeContainer'), 60, FailureHandling.STOP_ON_FAILURE)

KeywordUtil.logInfo('iframeContainer found')

WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Menu/Expand Recording'), 10, FailureHandling.STOP_ON_FAILURE)

KeywordUtil.logInfo('Expand Recording found')

LogStories.logInfo('~~~~~~~~~~~~~~~~~~~~~~Step 18~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'(Provider_FirstName, Provider_LastName)