package stories

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.util.KeywordUtil
import internal.GlobalVariable
import org.openqa.selenium.WebElement
import stories.VariableStories as VariableStories
import com.kms.katalon.core.testobject.ConditionType


public class NavigateStory {

	/**
	 * Retries a given action with exponential backoff
	 *
	 * @param action      Closure containing the action to retry
	 * @param maxRetries  Max retry count (defaults to RETRY_STEPS env var)
	 */
	def retryAction(Closure action, int maxRetries = GlobalVariable.RETRY_COUNT ) {

		int attempt = 0;
		Exception lastError = null;

		while (attempt < maxRetries) {
			try {
				if (attempt > 0) {
					KeywordUtil.logInfo("Retrying action (attempt ${attempt + 1}/${maxRetries})...") ;
				}

				action.call();
				return
			} catch (Exception e) {
				attempt++;
				lastError = e;

				if (attempt == maxRetries) {
					throw new Exception("Failed after ${maxRetries} retries: ${lastError.message}", lastError);
				}

				// Exponential backoff: 2^attempt * 1000 ms
				long backoffMillis = Math.pow(2, attempt) * 1000 as long;
				Thread.sleep(backoffMillis);
			}
		}
	}

	def ClickMegaMenuItems(Map props) {

		WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

		retryAction({

			// Click top menu
			WebUI.waitForElementVisible(
					makeTO("//*[@data-madel='${props.TopMenuOption}']//following-sibling::a[2]"),
					20
					)

			WebUI.click(
					makeTO("//*[@data-madel='${props.TopMenuOption}']//following-sibling::a[2]")
					)

			WebUI.delay(3)
			println "Click 1"

			boolean isBreak = false

			// ---------------- GROUP 1 ----------------
			if (props.SubItem in [
						"Frames",
						"Contacts",
						"Purchase Orders",
						"Miscellaneous",
						"Overview",
						"New Purchase Order",
						"New Inventory Item",
						"New Inventory Task",
						"Physical Inventory & Reconciliation"
					]) {

				KeywordUtil.logInfo("SubItem: ${props.SubItem}")

				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				WebUI.waitForElementVisible(
						makeTO("(//*[@id='top_menu_container']/ul[1]/li/ul/div/li/ul/li/a[contains(text(),'${props.SubItem}')])[1]"),
						20
						)

				WebUI.click(
						makeTO("(//*[@id='top_menu_container']/ul[1]/li/ul/div/li/ul/li/a[contains(text(),'${props.SubItem}')])[1]")
						)

				println "Click 2"


				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				isBreak = true
			}

			// ---------------- GROUP 2 ----------------
			if (!isBreak && props.SubItem in [
						"Superbill",
						"Insurance Ledger",
						"Add Patient Payment",
						"Add New Superbill",
						"Add Insurance Payment",
						"Superbills"
					]) {

				KeywordUtil.logInfo( "SubItem: ${props.SubItem}")

				WebUI.waitForElementVisible(
						makeTO("(//a[text()='${props.SubItem}'])[1]"),
						20
						)

				WebUI.click(
						makeTO("(//a[text()='${props.SubItem}'])[1]")
						)

				println "Click 3"


				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				isBreak = true
			}

			// ---------------- GROUP 3 ----------------
			if (!isBreak && props.SubItem in ["Claim", "Claims Overview"]) {

				KeywordUtil.logInfo( "SubItem: ${props.SubItem}")

				WebUI.waitForElementVisible(
						makeTO("//li/a[@data-tabtitle='${props.SubItem}']"),
						20
						)

				WebUI.click(
						makeTO("//li/a[@data-tabtitle='${props.SubItem}']")
						)

				println "Click 4"


				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				isBreak = true
			}

			// ---------------- CLAIMS ----------------
			if (!isBreak && props.SubItem == "Claims") {

				KeywordUtil.logInfo("SubItem: ${props.SubItem}")

				WebUI.waitForElementVisible(
						makeTO("//li/a[starts-with(@onclick,'loadPatientDependentModules')][text()='Claims']"),
						20
						)

				WebUI.click(
						makeTO("//li/a[starts-with(@onclick,'loadPatientDependentModules')][text()='Claims']")
						)

				println "Click 5"


				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				isBreak = true
			}

			// ---------------- ADD NEW TASK ----------------
			if (!isBreak && props.SubItem == "Add New Task") {

				KeywordUtil.logInfo( "SubItem: ${props.SubItem}")

				WebUI.waitForElementVisible(
						makeTO("//*[@data-madel='${props.TopMenuOption}']//a[text()='Add New Task']"),
						20
						)

				WebUI.click(
						makeTO("//*[@data-madel='${props.TopMenuOption}']//a[text()='Add New Task']")
						)

				println "Click 6"


				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				isBreak = true
			}

			// ---------------- DEFAULT ----------------
			if (!isBreak && props.TopMenuOption && props.SubItem && props.SubItem != "Superbill") {

				KeywordUtil.logInfo("SubItem: ${props.SubItem}")

				if (props.TopMenuOption == "Encounters") {

					WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

					WebUI.click(
							makeTO("//ul/li[@class='dropdown-header mega-menu-patient-name truncateWord wm-hide']/following-sibling::li/a[contains(text(),'${props.SubItem}')]")
							)

					println "Click 7"

					if (props.SubItem == "Encounter Details") {
						WebUI.waitForElementVisible(
								makeTO(".row.cells12.marB0.patientInfo.encpatientheader"),
								20
								)
					}

					if (props.SubItem == "New Encounter") {
						WebUI.waitForElementVisible(
								makeTO("//div[contains(text(), 'New Patient Encounter')]"),
								20
								)
					}
				}
				else if (props.TopMenuOption != "Inventory") {

					TestObject linkTO = makeTO("(//a[contains(text(),'${props.SubItem}')][@href='#'])[1]")

					if (WebUI.verifyElementVisible(linkTO, FailureHandling.OPTIONAL)) {
						WebUI.click(linkTO)
						println "Click 8"
					}
					else {
						WebUI.click(
								makeTO("//li//a[contains(normalize-space(text()), '${props.SubItem}')]")
								)
						println "Click 9"
					}

					WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

					if (props.SubItem == "Add New Patient") {
						WebUI.waitForElementVisible(
								makeTO("//*[@id='AddNew_Patient_FirstName']"),
								20
								)
					}
				}
			}

			WebUI.delay(2)
		})

		KeywordUtil.logInfo("Clicked on ${props.SubItem} from ${props.TopMenuOption}")
	}

	def ClickOnOfficeAdminNavigationTabs(Map props) {

		// Normalize
		if (!props.SubTabL2) {
			props.SubTabL2 = null
		}

		retryAction({

			WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

			// ---------------- MAIN TAB : GENERAL ----------------
			if (props.MainTab == "General") {

				WebUI.waitForElementVisible(
						makeTO("//*[@id='navMenuOfficeAdmin']/ul[1]/li[3]/a"),
						20
						)

				WebUI.click(makeTO("//a[text()='General']"))
				KeywordUtil.logInfo("Click 1")

				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				WebUI.waitForElementVisible(
						makeTO("//*[@id='Genral_menu']/ul/li/a[contains(text(),'${props.SubTabL1}')]"),
						20
						)

				WebUI.click(
						makeTO("//*[@id='Genral_menu']/ul/li/a[contains(text(),'${props.SubTabL1}')]")
						)

				KeywordUtil.logInfo("Click 2")

				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				if (props.SubTabL1 in [
							"Quick Search",
							"Alerts",
							"Audit Trail"
						] && props.SubTabL2 == null) {

					if (props.SubTabL1 == "Alerts") {
						WebUI.waitForElementVisible(makeTO("//*[@value='Add Alert']"), 20)
					}

					if (props.SubTabL1 == "Audit Trail") {
						WebUI.waitForElementVisible(makeTO("//*[@id='btnFind']"), 20)
					}
				}

				if (props.SubTabL1 == "Document" && props.SubTabL2) {

					WebUI.waitForElementVisible(
							makeTO("//*[@id='DocumentManagement']//a[contains(text(),'${props.SubTabL2}')]"),
							20
							)

					WebUI.click(
							makeTO("//*[@id='DocumentManagement']//a[contains(text(),'${props.SubTabL2}')]")
							)

					KeywordUtil.logInfo("Click 3")
					WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)
				}
			}

			// ---------------- MAIN TAB : BUSINESS ADMIN ----------------
			if (props.MainTab == "Business Administration") {

				WebUI.click(
						makeTO("//*[@data-officeadminelementtitle='Business Details']")
						)

				KeywordUtil.logInfo("Click 6")

				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				WebUI.click(
						makeTO("//*[@id='Business_Details_menu']//a[contains(text(),'${props.SubTabL1}')]")
						)

				KeywordUtil.logInfo("Click 7")
			}

			// ---------------- MAIN TAB : MODULES ----------------
			if (props.MainTab == "Modules") {

				WebUI.click(
						makeTO("//*[@data-officeadminelementtitle='Modules']")
						)

				KeywordUtil.logInfo("Click 9")
				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				WebUI.click(
						makeTO("//*[@id='Modules_menu']//a[text()='${props.SubTabL1}']")
						)

				KeywordUtil.logInfo("Click 10")
			}

			// ---------------- MAIN TAB : VALUE LISTS ----------------
			if (props.MainTab == "Value Lists") {

				WebUI.click(
						makeTO("//*[@data-officeadminelementtitle='Value List']")
						)

				KeywordUtil.logInfo("Click 19")

				WebUI.waitForElementVisible(
						makeTO("//*[@id='ValueList_menu']//a[text()='${props.SubTabL1}']"),
						20
						)

				WebUI.click(
						makeTO("//*[@id='ValueList_menu']//a[text()='${props.SubTabL1}']")
						)

				KeywordUtil.logInfo("Click 20")
			}
		})

		KeywordUtil.logInfo(
				"Navigated: ${props.MainTab} → ${props.SubTabL1} → ${props.SubTabL2}"
				)
	}

	//	boolean isElementClickable(TestObject to) {
	//		try {
	//			WebUI.verifyElementClickable(to, FailureHandling.STOP_ON_FAILURE)
	//			return true
	//		} catch (Exception e) {
	//			return false
	//		}
	//	}
	//
	//	void clickElementWithJS(TestObject to) {
	//		WebUI.executeJavaScript('arguments[0].click();', Arrays.asList(WebUI.findWebElement(to, 20)))
	//	}
	//
	//	def SelectEncounterElementFromLeftNavOnEncounter(Map props) {
	//
	//		retryAction({
	//			String pageTitle = props.pElementPage
	//			String element = props.pElement
	//			TestObject tabExpandedTO = makeTO( "//a[@title='${pageTitle}']/following-sibling::ul" )
	//
	//			if (WebUI.verifyElementVisible(tabExpandedTO, FailureHandling.OPTIONAL)) {
	//				KeywordUtil.logInfo("${pageTitle} Tab already open")
	//			} else {
	//				WebUI.waitForElementVisible( makeTO("//a[@title='${pageTitle}']/preceding-sibling::span"), 30 )
	//				WebUI.click( makeTO("//a[@title='${pageTitle}']/preceding-sibling::span") )
	//				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 60)
	//				WebUI.waitForElementVisible( makeTO("//a[@title='${pageTitle}']/following-sibling::ul/li/a[starts-with(@title,'${element}')]"), 30 )
	//			}
	//
	//			TestObject subElementTO = makeTO("//a[@title='${pageTitle}']/following-sibling::ul/li/a[starts-with(@title,'${element}')]" )
	//			WebUI.waitForElementVisible(subElementTO, 30)
	//
	//			if (isElementClickable(subElementTO)) {
	//				WebUI.click(subElementTO)
	//			} else {
	//				KeywordUtil.logInfo("Element not clickable by WebUI.click, trying with JavaScript click")
	//				clickElementWithJS(subElementTO)
	//			}
	//
	//			WebUI.waitForElementVisible( makeTO("//div/span[contains(text(), '${element}')]"), 30 )
	//			WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 60)
	//			WebUI.delay(5)
	//
	//			KeywordUtil.logInfo("Clicked on Element ${element}")
	//		})
	//	}

	boolean isPresent(TestObject to, int timeout = 3) {
		return WebUI.findWebElements(to, timeout).size() > 0
	}

	static void safeClick(TestObject to) {
		WebElement el = WebUI.findWebElement(to, 10)
		try {
			el.click()
		} catch (Exception e) {
			WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(el))
		}
	}

	@Keyword
	def SelectEncounterElementFromLeftNavOnEncounter(Map props) {

		retryAction({

			String pageTitle = props.pElementPage
			String element   = props.pElement

			assert pageTitle?.trim()
			assert element?.trim()

			TestObject tabTO     = makeTO("//a[@title='${pageTitle}']")
			TestObject listTO    = makeTO("//a[@title='${pageTitle}']/following-sibling::ul")
			TestObject childTO   = makeTO("//a[@title='${pageTitle}']/following-sibling::ul//a[contains(@title,'${element}')]")

			// Expand tab if collapsed
			if (!isPresent(listTO)) {
				safeClick(tabTO)
				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)
			}

			// Click child safely
			assert isPresent(childTO, 10) : "Element '${element}' not found under '${pageTitle}'"
			safeClick(childTO)

			// Validate page
			WebUI.waitForElementVisible(
					makeTO("//span[contains(normalize-space(.),'${element}')]"),
					20
					)

			KeywordUtil.logInfo("Navigated to ${pageTitle} → ${element}")
		})
	}

	/* ---------------- HELPER ---------------- */
	TestObject makeTO(String xpath) {
			TestObject to = new TestObject()
			to.addProperty("xpath", ConditionType.EQUALS, xpath)
			return to
		}
}
