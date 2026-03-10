import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory
import internal.GlobalVariable as GlobalVariable
import stories.LogStories as LogStories
import stories.NavigateStory as NavigateStory
import stories.UtilHelper as UtilHelper
import stories.VariableStories as VariableStories
import com.kms.katalon.core.testdata.TestData as TestData

GlobalVariable.EVAA_SC_NO = 'EVAA_SCRIBE_TC_R14'

VariableStories.clearItem(GlobalVariable.EVAA_SC_NO)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 1~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.MaximeyesLoginAndFindPatient'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName, 
    EncounterType, ExamLocation, Technician)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 2~~~~~~~~~~~~~~~~~~~~~~')

def recordFilePath = UtilHelper.getFilePath(RecordFilePath)

LogStories.logInfo("Record File Path=> $recordFilePath")

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 3~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.generateSOAPNoteByRecordPauseResumeStop'(FileTime, recordFilePath, false, false, false)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 4~~~~~~~~~~~~~~~~~~~~~~')

// Collapse Recording Screen
CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 5~~~~~~~~~~~~~~~~~~~~~~')

TestData patientData = TestDataFactory.findTestData('Data Files/PatientData')

def LastName2 = patientData.getValue('LastName', 16)

def FirstName2 = patientData.getValue('FirstName', 16)

def DOB2 = patientData.getValue('DOB', 16)

CustomKeywords.'steps.EVAASteps.MaximeyesLoginAndFindPatient'(FirstName2, LastName2, DOB2, Provider_FirstName, Provider_LastName, 
    EncounterType, ExamLocation, Technician, false, true, false)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 6~~~~~~~~~~~~~~~~~~~~~~')

// Expand Recording Screen
CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(true)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 7~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.clickOnCalenderTodayDate'()

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 8~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.clickOnPatientLeftSidePanel'(FirstName, LastName)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 9~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.stopRecording'()

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 10~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.getAndStoreEVAAScribeSOAPNote'()

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 11~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.finalizedAndSendToMaximEyes'(FirstName, LastName, DOB, Provider_FirstName, Provider_LastName)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 12~~~~~~~~~~~~~~~~~~~~~~')

// Collapse Recording Screen
CustomKeywords.'steps.CommonSteps.clickOnExpandRecording'(false)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 13~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.CommonSteps.findPatient'(LastName, FirstName)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 14~~~~~~~~~~~~~~~~~~~~~~')

NavigateStory navigateStory = new NavigateStory()

navigateStory.ClickMegaMenuItems([('TopMenuOption') : 'Encounters', ('SubItem') : 'Encounter Hx'])

String key = "ENC_${FirstName}_${LastName}".toUpperCase() + '_ENCOUNTER_ID'

String encounterId = VariableStories.getItem(key)

LogStories.logInfo("Encounter Id=> $encounterId")

CustomKeywords.'steps.CommonSteps.findEncounterByEncounterId'(encounterId)

LogStories.log('~~~~~~~~~~~~~~~~~~~~~~Step 15~~~~~~~~~~~~~~~~~~~~~~')

CustomKeywords.'steps.EVAASteps.verifySOAPNoteSentToMaximeyes'()