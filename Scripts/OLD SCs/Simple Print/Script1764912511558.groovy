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
import java.awt.Robot as Robot
import java.awt.event.KeyEvent as KeyEvent

Robot robot = new Robot()

WebUI.openBrowser('')

'Maximize the window'
WebUI.maximizeWindow()

WebUI.navigateToUrl(SiteURL)

WebUI.setText(findTestObject('Object Repository/OLD/FindPatients/UserName'), UserName)

WebUI.setText(findTestObject('Object Repository/OLD/FindPatients/Password'), Password)

WebUI.click(findTestObject('Object Repository/OLD/FindPatients/LoginBtn'))

WebUI.waitForElementNotVisible(findTestObject('OLD/FindPatients/busyIndicator'), 0)

WebUI.waitForElementVisible(findTestObject('OLD/FindPatients/WorkQueue'), 0)

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

    WebUI.click(findTestObject('Object Repository/OLD/FindPatients/FindPatient'))

    String LastName = rs.getString('LastName')

    String FirstName = rs.getString('FirstName')

    WebUI.setText(findTestObject('Object Repository/OLD/FindPatients/input_Find Patient_LastName'), LastName)

    WebUI.setText(findTestObject('OLD/FindPatients/input_Find Patient_FirstName'), FirstName)

    WebUI.click(findTestObject('Object Repository/OLD/FindPatients/input_Active_btnSearchPatient'))

    WebUI.waitForElementNotVisible(findTestObject('OLD/FindPatients/busyIndicator'), 0)

    WebUI.waitForElementVisible(findTestObject('OLD/FindPatients/Header Patient Name'), 0)

    String PatientName = WebUI.getText(findTestObject('OLD/FindPatients/Header Patient Name'))

    String expected = "$FirstName $LastName"

    WebUI.verifyMatch(PatientName, expected, true)
}

// ---------------------------
// 6. Close connection
// ---------------------------
conn.close()

WebUI.click(findTestObject('Object Repository/OLD/Simple Print/span_SAVED RESULTS_navLink'))

WebUI.click(findTestObject('Object Repository/OLD/Simple Print/span__Contextmenu_Print_Opt'))

WebUI.click(findTestObject('Object Repository/OLD/Simple Print/li'))

WebUI.delay(10)

// or press ENTER
robot.keyPress(KeyEvent.VK_ENTER)

robot.keyRelease(KeyEvent.VK_ENTER)

//// Cancel print dialog (ESC)
//robot.keyPress(KeyEvent.VK_ESCAPE)
//robot.keyRelease(KeyEvent.VK_ESCAPE)
WebUI.delay(10)

WebUI.closeBrowser()

