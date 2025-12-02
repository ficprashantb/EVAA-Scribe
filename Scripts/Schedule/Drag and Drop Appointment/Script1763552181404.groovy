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
import java.sql.DriverManager as DriverManager
import java.sql.Connection as Connection
import java.sql.ResultSet as ResultSet

WebUI.openBrowser('')

WebUI.callTestCase(findTestCase('Common/Maximeyes Login'), [('SiteURL') : SiteURL, ('UserName') : UserName, ('Password') : Password], 
    FailureHandling.STOP_ON_FAILURE)

WebUI.maximizeWindow()

WebUI.callTestCase(findTestCase('Common/Find Patient'), [('DB_HOST') : DB_HOST, ('DB_USER') : DB_USER, ('DB_PASSWORD') : DB_PASSWORD
        , ('DB_PORT') : DB_PORT, ('DB_DATABASE') : DB_DATABASE, ('Index') : Index], FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/a_Schedule_dropdown-toggle menu-large recentmodule'))

WebUI.click(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/a_concat(Ralph, , S)_Schedule  Schedule'))

WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/select_Loc_PatientScheduleLocationId'), 
    '31', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/select_Resource_PatientScheduleResourceId'), 
    '25', true)

WebUI.doubleClick(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/div_PM_scheduler_commonControlsBlock_select_1'))

WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/select_Type_TypeID'), 
    '1034', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/select_Type_TypeID'), 
    '1035', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/select_Reason_ReasonId'), 
    '45', true)

WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/select_Status_drdnAppointmentStatus'), 
    'CONFIRMED', true)

WebUI.click(findTestObject('Schedule/Add Appointment/Page_MaximEyes/span_Delete_dx-vam'))

WebUI.delay(5)

TestObject source = findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/div_PM_scheduler_commonControlsBlock_select_1')

TestObject target = findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/div_PM_scheduler_commonControlsBlock_select_2')

WebUI.dragAndDropToObject(source, target)

WebUI.delay(5)

WebUI.click(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/btnOfficeApptRechedule'))

WebUI.rightClick(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/div_PM_scheduler_commonControlsBlock_select_2'))

WebUI.delay(5)

WebUI.click(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/span_Reschedule Appointment_dx-vam'))

WebUI.delay(5)

WebUI.click(findTestObject('Object Repository/Schedule/Add Appointment/Page_MaximEyes/input_Confirmation_btnOffice'))

WebUI.delay(5)

WebUI.closeBrowser()

