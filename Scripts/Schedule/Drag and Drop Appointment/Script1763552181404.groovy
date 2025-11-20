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

'Maximize the window'
WebUI.maximizeWindow()


    WebUI.navigateToUrl(SiteURL)

    WebUI.setText(findTestObject('Object Repository/FindPatients/UserName'), UserName)

    WebUI.setText(findTestObject('Object Repository/FindPatients/Password'), Password)

    WebUI.click(findTestObject('Object Repository/FindPatients/LoginBtn'))

    WebUI.waitForElementNotVisible(findTestObject('FindPatients/busyIndicator'), 0)

    WebUI.waitForElementVisible(findTestObject('FindPatients/WorkQueue'), 0)

    // ---------------------------
    // 1. Connection String
    // ---------------------------
    String url = "jdbc:mysql://$DB_HOST:$DB_PORT/$DB_DATABASE"

    String user = "$DB_USER"

    String pass = "$DB_PASSWORD"

    // ---------------------------
    // 2. Make connection
    // ---------------------------
    Connection conn = DriverManager.getConnection(url, user, pass)

    // ---------------------------
    // 3. Your query (Top 2 Active)
    // ---------------------------
    String query = 'SELECT * from patients where isActive = 1 LIMIT 1'

    // ---------------------------
    // 4. Execute query
    // ---------------------------
    ResultSet rs = conn.createStatement().executeQuery(query)

    // ---------------------------
    // 5. Fetch data
    // ---------------------------
    while (rs.next()) {
        println('LastName : ' + rs.getString('LastName'))

        println('FirstName : ' + rs.getString('FirstName'))

        println('IsActive  : ' + rs.getInt('IsActive'))

        println('----------------------------')

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
    
    // ---------------------------
    // 6. Close connection
    // ---------------------------
    conn.close()

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

