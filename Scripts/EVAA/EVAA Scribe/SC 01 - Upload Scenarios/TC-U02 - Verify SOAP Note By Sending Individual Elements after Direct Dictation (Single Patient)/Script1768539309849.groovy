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
import stories.AssertStory as AssertStory
import stories.NavigateStory as NavigateStory
import stories.VariableStories as VariableStories

AssertStory assertStory = new AssertStory()

GlobalVariable.EVAA_SC_NO = 'EVAA_SCRIBE_TC_U02'

CustomKeywords.'steps.CommonSteps.maximeyesLogin'(GlobalVariable.EVAA_SiteURL, GlobalVariable.EVAA_UserName, GlobalVariable.EVAA_Password)

CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

NavigateStory navigateStory = new NavigateStory()

navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])

CustomKeywords.'steps.CommonSteps.getFirstEncounterId'(FirstName, LastName)

String encounterId = VariableStories.getItem('ENCOUNTER_ID')

KeywordUtil.logInfo("Encounter Id=> $encounterId")

CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'()

WebUI.waitForElementVisible(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'), 30, FailureHandling.STOP_ON_FAILURE)

String PtName = WebUI.getText(findTestObject('EVAAPage/EVAA Scribe/Header/PatientName'))

String expectedPtName = "$FirstName $LastName"

assertStory.verifyMatch('PatientName', PtName, expectedPtName)

CustomKeywords.'steps.EVAASteps.verifyPatientConsentReceived'('true')

CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

//Direct Dictation By Typing on Elements
CustomKeywords.'steps.EVAASteps.getAndStoreEVAAScribeDirectDictationNote'()

CustomKeywords.'steps.EVAASteps.directDictationByTypingOnElements'()

CustomKeywords.'steps.EVAASteps.verifyStoredDirectDictationOnEVAAScribe'(1)

CustomKeywords.'steps.EVAASteps.verifyEVAAScribeDetails'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

CustomKeywords.'steps.EVAASteps.finalizedAndSendIndividualElementsToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'(Provider_FirstName, Provider_LastName)

CustomKeywords.'steps.EVAASteps.finalizedAndSendToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'(Provider_FirstName, Provider_LastName)








