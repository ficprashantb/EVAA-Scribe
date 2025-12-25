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
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.openBrowser('')

'Maximize the window'
WebUI.maximizeWindow()

WebUI.navigateToUrl('https://intakeqa.maximeyes.com/qa8#/home')

WebUI.click(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/span_Location_text-gray-900'))

WebUI.click(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/div_FARGO_px-3 py-2 hoverbg-gray-100 cursor_8446f5'))

WebUI.click(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/button_Patient Intake Form_inline-flex item_8e1f68'))

String FirstName = CustomKeywords.'RandomDataUtils.GenerateFirstName'()

String LastName = CustomKeywords.'RandomDataUtils.GenerateLastName'()

String FullName = "$FirstName $LastName"

println('Full Name: ' + FullName)

WebUI.setText(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/input_First_r0-form-item'), FirstName)

WebUI.setText(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/input_Last_r1-form-item'), LastName)

//WebUI.setText(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/input_Preferred Name_r2-form-item'), FullName)

WebUI.setText(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/input_Mobile_r3-form-item'), '(423)423-4234')

WebUI.setText(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/input_Email_r4-form-item'), 'prashantb@first-insight.com')

WebUI.setText(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/input_Date of Birth_flex h-9 smh-10 w-full _c15bf6_8'), 
    '01/01/2023')

WebUI.click(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/btnProcced'))

WebUI.click(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/canvas__jSignature'))

WebUI.click(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/button_Declined_input_2'))

WebUI.waitForElementClickable(findTestObject('OLD/Patient Intake/Page_Eyeclinic/canvas__jSignature (1)'), 30)

WebUI.click(findTestObject('OLD/Patient Intake/Page_Eyeclinic/canvas__jSignature (1)'))

WebUI.click(findTestObject('OLD/Patient Intake/Page_Eyeclinic/button_Declined_input_2 (1)'))

WebUI.click(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/button_Back_inline-flex items-center justif_2e7acf'))

WebUI.selectOptionByValue(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/select_Sex_input_2010'), 'Male', 
    true)

WebUI.selectOptionByValue(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/select_Marital status_input_2021'), 
    'Married', true)

WebUI.click(findTestObject('Object Repository/OLD/Patient Intake/Page_Eyeclinic/button_Save and Complete Later_form-pagebre_19b155'))

WebUI.click(findTestObject('OLD/Patient Intake/Page_Eyeclinic/Social History Next'))

WebUI.click(findTestObject('OLD/Patient Intake/Page_Eyeclinic/Medical History Next'))

WebUI.click(findTestObject('OLD/Patient Intake/Page_Eyeclinic/Submit Button'))

WebUI.waitForElementVisible(findTestObject('OLD/Patient Intake/Page_Eyeclinic/Intake Form Submitted'), 60)

WebUI.callTestCase(findTestCase('null'), [('SiteURL') : SiteURL, ('UserName') : UserName, ('Password') : Password], 
    FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Object Repository/OLD/FindPatients/FindPatient'))

WebUI.setText(findTestObject('Object Repository/OLD/FindPatients/input_Find Patient_LastName'), LastName)

WebUI.setText(findTestObject('OLD/FindPatients/input_Find Patient_FirstName'), FirstName)

WebUI.click(findTestObject('Object Repository/OLD/FindPatients/input_Active_btnSearchPatient'))

WebUI.waitForElementNotVisible(findTestObject('OLD/FindPatients/busyIndicator'), 0)

WebUI.waitForElementVisible(findTestObject('OLD/FindPatients/Header Patient Name'), 0)

String PatientName = WebUI.getText(findTestObject('OLD/FindPatients/Header Patient Name'))

String expected = "$FirstName $LastName"

WebUI.verifyMatch(PatientName, expected, true)

WebUI.closeBrowser()

