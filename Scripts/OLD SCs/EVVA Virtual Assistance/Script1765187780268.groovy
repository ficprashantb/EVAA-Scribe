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

//WebUI.openBrowser('')
//WebUI.navigateToUrl(SiteURL)
PermissionManager.openChromeWithAllPermissions(SiteURL)

WebUI.maximizeWindow()

WebUI.waitForElementVisible(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/img'), 120)

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/img'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/button_chevron_left_btn suggestion-btn book-appointment'))

WebUI.waitForElementVisible(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/button_Book Appointment_yesBookAppt'), 
    60)

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/button_Book Appointment_yesBookAppt'))

String FirstName = CustomKeywords.'RandomDataUtils.GenerateFirstName'()

String LastName = CustomKeywords.'RandomDataUtils.GenerateLastName'()

String FullName = "$FirstName $LastName"

println('Full Name: ' + FullName)

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input__idFirstName'), FirstName)

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input__idLastName'), LastName)

WebUI.setText(findTestObject('OLD/EVAA/EVVA Virtual Assistance/input__idDOB 2'), '01/02/1990')

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input__idPhoneNumber'), '956-119-1918')

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input_Email_idEmail'), G_EmailId)

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/span_BACK_v-btn__content'))

WebUI.waitForElementVisible(findTestObject('OLD/EVAA/EVVA Virtual Assistance/New Patient'), 10, FailureHandling.CONTINUE_ON_FAILURE)

WebUI.click(findTestObject('OLD/EVAA/EVVA Virtual Assistance/New Patient'), FailureHandling.CONTINUE_ON_FAILURE)

WebUI.waitForElementVisible(findTestObject('OLD/EVAA/EVVA Virtual Assistance/input_Please validate OTP sent to entered m_1e6e55'), 
    120)

// Read OTP
//String otp = CustomKeywords.'OTPReader.readGmailOTP'(G_Host, G_EmailId, G_Password, G_From, G_Subject)
String otp = CustomKeywords.'KW_GetGmailOTP.getGmailOTP'(G_Host, G_EmailId, G_Password, G_From, G_Subject)

println("OTP found: $otp")

// Validate OTP
if (!(otp) || otp.trim().isEmpty()) {
    otp = '9753'
}

// Split into characters safely
def splitOTP = otp.toCharArray( // ['9','7','5','3'] 
    )

// Assign digits
def otp1 = (splitOTP[0]).toString()

def otp2 = (splitOTP[1]).toString()

def otp3 = (splitOTP[2]).toString()

def otp4 = (splitOTP[3]).toString()

println("Digit 1: $otp1")

println("Digit 2: $otp2")

println("Digit 3: $otp3")

println("Digit 4: $otp4")

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input_Please validate OTP sent to entered m_1e6e55'), 
    otp1)

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input_Please validate OTP sent to entered m_5f8c6a'), 
    otp2)

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input_Please validate OTP sent to entered m_ffa016'), 
    otp3)

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input_Please validate OTP sent to entered m_84e08b'), 
    otp4)

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/span_BACK_v-btn__content_1'))

WebUI.waitForElementVisible(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/div__v-select__selections'), 
    120)

WebUI.click(findTestObject('OLD/EVAA/EVVA Virtual Assistance/div__v-select__selections'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/Location Pune'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/div__v-select__selections_1'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/Provider Dr Smith'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/div__v-select__selections_2'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/Reason Vision Exam -Comprehensive Eye Exam'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/span'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/i_November 2025_v-icon notranslate mdi mdi-_834188'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/i_November 2025_v-icon notranslate mdi mdi-_834188'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/div_S_v-btn__content'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/button_Evening_btn p-3 slot-btn mx-1'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/span_BACK_v-btn__content_2'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/span_BACK_v-btn__content_3'))

String insuranceN = CustomKeywords.'RandomDataUtils.GenerateFullName'()

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input_Insurance Name_InsuranceNamestep8'), 
    insuranceN)

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/span_upload_material-symbols-outlined uploadBtn'))

WebUI.waitForElementVisible(findTestObject('OLD/EVAA/EVVA Virtual Assistance/Camera Capture Button'), 30, FailureHandling.STOP_ON_FAILURE)

WebUI.delay(5)

WebUI.click(findTestObject('OLD/EVAA/EVVA Virtual Assistance/Camera Capture Button'))

WebUI.waitForElementVisible(findTestObject('OLD/EVAA/EVVA Virtual Assistance/Camera Capture Done Button'), 30, FailureHandling.STOP_ON_FAILURE)

WebUI.delay(5)

WebUI.click(findTestObject('OLD/EVAA/EVVA Virtual Assistance/Camera Capture Done Button'))

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/span_BACK_v-btn__content_4'))

WebUI.waitForElementVisible(findTestObject('OLD/EVAA/EVVA Virtual Assistance/Enter Details Manually'), 30, FailureHandling.CONTINUE_ON_FAILURE)

WebUI.click(findTestObject('OLD/EVAA/EVVA Virtual Assistance/Enter Details Manually'), FailureHandling.CONTINUE_ON_FAILURE)

String insuranceFN = CustomKeywords.'RandomDataUtils.GenerateFirstName'()

String insuranceLN = CustomKeywords.'RandomDataUtils.GenerateLastName'()

String insuranceID = CustomKeywords.'RandomDataUtils.GenerateNumber'()

String InsuranceName = "$insuranceFN $insuranceLN"

println('Insurance Name: ' + InsuranceName)

WebUI.setText(findTestObject('OLD/EVAA/EVVA Virtual Assistance/input__insuranceCN'), InsuranceName)

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input__insuranceID'), insuranceID)

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input__insuranceFN'), insuranceFN)

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input__insuranceLN'), insuranceLN)

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input__idDOB'), '11/02/1990')

WebUI.selectOptionByValue(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/select__genderSelect'), 'M', true)

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/span_BACK_v-btn__content_5'))

WebUI.selectOptionByValue(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/select_Patient Relationship to insured_insu_8935df'), 
    'Self', true)

String insuranceGName = CustomKeywords.'RandomDataUtils.GenerateFullName'()

String insuranceGID = CustomKeywords.'RandomDataUtils.GenerateNumber'()

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input_Group_insuranceGID'), insuranceGID)

WebUI.setText(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/input_Group Name_insuranceGName'), insuranceGName)

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/span_BACK_v-btn__content_6'))

WebUI.waitForElementVisible(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/Insurance Details saved successfully'), 
    120)

WebUI.click(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/Finish Booking'))

WebUI.waitForElementVisible(findTestObject('Object Repository/OLD/EVAA/EVVA Virtual Assistance/Your appointment has been booked'), 
    120)

WebUI.waitForElementVisible(findTestObject('OLD/EVAA/EVVA Virtual Assistance/Appointment Pt Name'), 30)

String PatientName = WebUI.getText(findTestObject('OLD/EVAA/EVVA Virtual Assistance/Appointment Pt Name'))

WebUI.verifyMatch(PatientName, FullName, true)

WebUI.closeBrowser()


