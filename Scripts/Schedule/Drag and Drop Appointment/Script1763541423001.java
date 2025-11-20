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
import java.sql.DriverManager
import java.sql.Connection
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.testdata.DBData
import com.kms.katalon.core.testdata.TestDataFactory

String db_url = "jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_DATABASE}"
String db_username = "${DB_USER}"
String db_password = "${DB_PASSWORD}"

Connection conn = DriverManager.getConnection(db_url, db_username, db_password)

String query  = "SELECT * from patients where isActive = 1 LIMIT 1;"

WebUI.openBrowser('')

'Maximize the window'
WebUI.maximizeWindow()

WebUI.navigateToUrl(SiteURL)

WebUI.setText(findTestObject('Object Repository/FindPatients/UserName'), UserName)

WebUI.setText(findTestObject('Object Repository/FindPatients/Password'), Password)

WebUI.click(findTestObject('Object Repository/FindPatients/LoginBtn'))

WebUI.waitForElementNotVisible(findTestObject('FindPatients/busyIndicator'), 0)

WebUI.waitForElementVisible(findTestObject('FindPatients/WorkQueue'), 0)

try {
	 
	 
	ResultSet rs = conn.createStatement().executeQuery(query)
	
	def result = dbData.executeQuery(query)
	
	
	while (rs.next()) {
		println "LastName : " + rs.getString("LastName")
		println "FirstName : " + rs.getString("FirstName")		
		println "IsActive  : " + rs.getInt("IsActive")
		println "----------------------------"
	
		
		WebUI.click(findTestObject('Object Repository/FindPatients/FindPatient'))
	
		String LastName = rs.getString('LastName')
	
		String FirstName = rs.getString('FirstName')
	
		WebUI.setText(findTestObject('Object Repository/FindPatients/input_Find Patient_LastName'), LastName)
	
		WebUI.setText(findTestObject('FindPatients/input_Find Patient_FirstName'), FirstName)
	
		WebUI.click(findTestObject('Object Repository/FindPatients/input_Active_btnSearchPatient'))
	
		WebUI.waitForElementNotVisible(findTestObject('FindPatients/busyIndicator'), 0)
	
		WebUI.waitForElementVisible(findTestObject('FindPatients/Header Patient Name'), 0)
	
		String PatientName = WebUI.getText(findTestObject('FindPatients/Header Patient Name'))
	
		String expected = "$FirstName $LastName"
	
		WebUI.verifyMatch(PatientName, expected, true)
	}
	
	
    WebUI.click(findTestObject('Object Repository/Schedule/Add Appointment/a_Schedule_dropdown-toggle menu-large recentmodule'))

    WebUI.click(findTestObject('Object Repository/Schedule/Add Appointment/a_concat(Test, , S)_Schedule  Schedule'))

    WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/select_Loc_PatientScheduleLocationId'), 
        '31', true)

    WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/select_Resource_PatientScheduleResourceId'), 
        '25', true)

    WebUI.doubleClick(findTestObject('Object Repository/Schedule/Add Appointment/div_PM_scheduler_commonControlsBlock_select_b30d52'))

    WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/select_Type_TypeID'), '1026', true)

    WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/select_Reason_ReasonId'), '22', 
        true)

    WebUI.selectOptionByValue(findTestObject('Object Repository/Schedule/Add Appointment/select_Status_drdnAppointmentStatus'), 
        'CONFIRMED', true)

    WebUI.click(findTestObject('Object Repository/Schedule/Add Appointment/span_Delete_dx-vam'))

    WebUI.rightClick(findTestObject('Object Repository/Schedule/Add Appointment/div_OPTICAL_wm-hide'))

    WebUI.click(findTestObject('Object Repository/Schedule/Add Appointment/span_Reschedule Appointment_dx-vam'))

    WebUI.click(findTestObject('Object Repository/Schedule/Add Appointment/input_Confirmation_btnOffice'))
}
catch (def e) {
    e.printStackTrace()
} 
finally { 
    WebUI.closeBrowser()
}

