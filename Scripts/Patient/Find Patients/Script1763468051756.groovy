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
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory
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

WebUI.navigateToUrl(SiteURL)

WebUI.setText(findTestObject('Object Repository/FindPatients/UserName'), UserName)

WebUI.setText(findTestObject('Object Repository/FindPatients/Password'), Password)

WebUI.click(findTestObject('Object Repository/FindPatients/LoginBtn'))

WebUI.waitForElementNotVisible(findTestObject('FindPatients/busyIndicator'), 0)

WebUI.waitForElementVisible(findTestObject('FindPatients/WorkQueue'), 0)

TestData data = TestDataFactory.findTestData('PatientData')

for (int row = 1; row <= 2; row++) {
    WebUI.click(findTestObject('Object Repository/FindPatients/FindPatient'))

    String LastName = data.getValue('LastName', row)

    String FirstName = data.getValue('FirstName', row)

    WebUI.setText(findTestObject('Object Repository/FindPatients/input_Find Patient_LastName'), LastName)

    WebUI.setText(findTestObject('FindPatients/input_Find Patient_FirstName'), FirstName)

    WebUI.click(findTestObject('Object Repository/FindPatients/input_Active_btnSearchPatient'))

    WebUI.waitForElementNotVisible(findTestObject('FindPatients/busyIndicator'), 0)

    WebUI.waitForElementVisible(findTestObject('FindPatients/Header Patient Name'), 0)

    String PatientName = WebUI.getText(findTestObject('FindPatients/Header Patient Name'))

    String expected = "$FirstName $LastName"

    WebUI.verifyMatch(PatientName, expected, true)
}

WebUI.closeBrowser()

