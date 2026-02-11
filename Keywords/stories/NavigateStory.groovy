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
import internal.GlobalVariable
import org.openqa.selenium.WebElement
import stories.VariableStories as VariableStories
import com.kms.katalon.core.testobject.ConditionType


public class NavigateStory {
	WaitStory waitStory = new WaitStory()
	
	/**
	 * Retries a given action with exponential backoff.
	 *
	 * @param action      Closure containing the action to retry
	 * @param maxRetries  Max retry count (defaults to GlobalVariable.RETRY_COUNT)
	 */
	def retryAction(Closure action, int maxRetries = GlobalVariable.RETRY_COUNT) {
		Exception lastError

		for (int attempt = 0; attempt < maxRetries; attempt++) {
			try {
				if (attempt > 0) {
					LogStories.logInfo("Retrying action (attempt ${attempt + 1}/${maxRetries})...")
				}
				action.call()
				return // success, exit immediately
			} catch (Exception e) {
				lastError = e
				if (attempt == maxRetries - 1) {
					throw new Exception("Failed after ${maxRetries} retries: ${lastError.message}", lastError)
				}
				// Exponential backoff: 2^attempt * 1000 ms
				long backoffMillis = (1 << attempt) * 1000L
				Thread.sleep(backoffMillis)
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

				LogStories.logInfo("SubItem: ${props.SubItem}")

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

				LogStories.logInfo( "SubItem: ${props.SubItem}")

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

				LogStories.logInfo( "SubItem: ${props.SubItem}")

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

				LogStories.logInfo("SubItem: ${props.SubItem}")

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

				LogStories.logInfo( "SubItem: ${props.SubItem}")

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

				LogStories.logInfo("SubItem: ${props.SubItem}")

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

		LogStories.logInfo("Clicked on ${props.SubItem} from ${props.TopMenuOption}")
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
				LogStories.logInfo("Click 1")

				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				WebUI.waitForElementVisible(
						makeTO("//*[@id='Genral_menu']/ul/li/a[contains(text(),'${props.SubTabL1}')]"),
						20
						)

				WebUI.click(
						makeTO("//*[@id='Genral_menu']/ul/li/a[contains(text(),'${props.SubTabL1}')]")
						)

				LogStories.logInfo("Click 2")

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

					LogStories.logInfo("Click 3")
					WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)
				}
			}

			// ---------------- MAIN TAB : BUSINESS ADMIN ----------------
			if (props.MainTab == "Business Administration") {

				WebUI.click(
						makeTO("//*[@data-officeadminelementtitle='Business Details']")
						)

				LogStories.logInfo("Click 6")

				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				WebUI.click(
						makeTO("//*[@id='Business_Details_menu']//a[contains(text(),'${props.SubTabL1}')]")
						)

				LogStories.logInfo("Click 7")
			}

			// ---------------- MAIN TAB : MODULES ----------------
			if (props.MainTab == "Modules") {

				WebUI.click(
						makeTO("//*[@data-officeadminelementtitle='Modules']")
						)

				LogStories.logInfo("Click 9")
				WebUI.waitForElementNotVisible(findTestObject('CommonPage/busyIndicator'), 30)

				WebUI.click(
						makeTO("//*[@id='Modules_menu']//a[text()='${props.SubTabL1}']")
						)

				LogStories.logInfo("Click 10")
			}

			// ---------------- MAIN TAB : VALUE LISTS ----------------
			if (props.MainTab == "Value Lists") {

				WebUI.click(
						makeTO("//*[@data-officeadminelementtitle='Value List']")
						)

				LogStories.logInfo("Click 19")

				WebUI.waitForElementVisible(
						makeTO("//*[@id='ValueList_menu']//a[text()='${props.SubTabL1}']"),
						20
						)

				WebUI.click(
						makeTO("//*[@id='ValueList_menu']//a[text()='${props.SubTabL1}']")
						)

				LogStories.logInfo("Click 20")
			}
		})

		LogStories.logInfo(
				"Navigated: ${props.MainTab} → ${props.SubTabL1} → ${props.SubTabL2}"
				)
	}

	/* ---------------- HELPER ---------------- */
	TestObject makeTO(String xpath) {
		assert xpath?.trim() : "XPath cannot be empty"
		TestObject to = new TestObject(xpath)
		to.addProperty("xpath", ConditionType.EQUALS, xpath)
		return to
	}

	boolean isPresent(TestObject to, int timeout = 3) {
		return WebUI.waitForElementVisible(to, timeout, FailureHandling.OPTIONAL)
	}

	static void safeClick(TestObject to) {
		boolean isPresent = WebUI.waitForElementVisible(to, 5, FailureHandling.OPTIONAL)
		if(isPresent) {
			WebUI.click(to)
			LogStories.logInfo("Clicked on Test Object.")
		}
	}

	@Keyword
def navigateToEncounterElement(String key, Boolean isElementText = false, Boolean isRefreshPresent = false) {
    LogStories.logInfo('----------------------Step AAI----------------------')

    // Map encounter keys to page, element, and test object
    def encounterMap = [
        "ChiefComplaint"         : ["CC & History Review", "Chief Complaint", 'EncounterPage/Encounter Details/textarea Patient Chief Complaint'],
        "HPI"                    : ["CC & History Review", "Chief Complaint", 'EncounterPage/Encounter Details/textarea HPI Notes'],
        "CurrentEyeSymptoms"     : ["Medical History", "Current Eye Symptoms", 'EncounterPage/Encounter Details/Current Eye Symptoms/divCurrentEyeSymptoms'],
        "Allergies"              : ["CC & History Review", "Allergies", 'EncounterPage/Encounter Details/trAllergies'],
        "Medications"            : ["CC & History Review", "Medications", 'EncounterPage/Encounter Details/trMedications'],
        "ReviewOfSystems"        : ["Medical History", "Review of Systems - Brief", 'EncounterPage/Encounter Details/Review Of Systems/divReviewOfSystems'],
        "Problems"               : ["CC & History Review", "Problems", 'EncounterPage/Encounter Details/trProblems'],
        "DifferentialDiagnosis"  : ["Final Findings", "Final Diagnoses", 'EncounterPage/Encounter Details/trFDDifferentialDiagnosis'],
        "Assessment"             : ["Final Findings", "Final Diagnoses", 'EncounterPage/Encounter Details/textarea Assessments'],
        "Plan"                   : ["Final Findings", "Final Diagnoses", 'EncounterPage/Encounter Details/div Plans'],
        "EyeDiseases"            : ["Medical History", "Eye Diseases", 'EncounterPage/Encounter Details/Eye Diseases/textarea_Additional_Notes_EyeDiseases'],
        "MentalAndFunctionalStatus": ["Medical History", "Mental and Functional Status", 'EncounterPage/Encounter Details/Mental and Functional Status/input_MOOD_AFFECT']
    ]

		def config = encounterMap[key]
    if (!config) {
        LogStories.markWarning("Unknown encounter key: ${key}")
        return
    }

    String page    = config[0]
    String element = config[1]
    TestObject testObj = findTestObject(config[2])

    // Navigate only if element not already present
    if (!isRefreshPresent) {
        LogStories.logInfo('----------------------Step Z----------------------')
        SelectEncounterElementFromLeftNavOnEncounter([
            pElementPage: page,
            pElement    : element
        ])
    }

    // Wait for the target element
    WebUI.waitForElementVisible(testObj, 8, FailureHandling.OPTIONAL)
    if (isElementText) {
        waitStory.waitForElementText(testObj, 20)
    }

    LogStories.logInfo("Navigated to Encounter Element: ${key}")
}

	@Keyword
	def SelectEncounterElementFromLeftNavOnEncounter(Map props) {
		String pageTitle = props?.get('pElementPage')?.toString()?.trim() ?: ''
		String element   = props?.get('pElement')?.toString()?.trim() ?: ''

		assert pageTitle
		assert element

		// Cache objects once
		TestObject mainTo       = makeTO("//li[a[@title='${pageTitle}']]")
		TestObject tabPlus      = makeTO("//li[a[@title='${pageTitle}']]//span[contains(@class,'enctPlusIcon')]")
		TestObject tabMinus     = makeTO("//li[a[@title='${pageTitle}']]//span[contains(@class,'enctMinuIcon')]")
		TestObject childTO      = makeTO("//a[@title='${pageTitle}']/following-sibling::ul//a[contains(@title,'${element}')]")
		TestObject activeToggle = makeTO("//li[a[@title='${pageTitle}'] and contains(@class,'active-toggle')]")
		TestObject navValidation= makeTO("//div/span[contains(normalize-space(),'${element}')]")
		TestObject busyIndicator= findTestObject('CommonPage/busyIndicator')

		try {
			if (isPresent(activeToggle, 3)) {
				LogStories.logInfo("${pageTitle} is already active → skipping click")
			} else {
				// Expand only if collapsed
				if (!isPresent(tabMinus, 3)) {
					LogStories.logInfo("${pageTitle} tab collapsed → expanding")
					safeClick(tabPlus)
					WebUI.waitForElementVisible(childTO, 10)
				} else {
					LogStories.logInfo("${pageTitle} tab already expanded")
				}

				WebUI.waitForElementClickable(childTO, 10)
				safeClick(childTO)
				LogStories.logInfo("Clicked on Element ${element}")
			}
		} catch (Exception e) {
			safeClick(mainTo)
			LogStories.logInfo("Fallback: Clicked on Page ${pageTitle}")
		}

		// Validate navigation
		WebUI.waitForElementVisible(navValidation, 20)
		WebUI.waitForElementNotVisible(busyIndicator, 30)

		LogStories.logInfo("Navigated to ${pageTitle} → ${element}")
	}
}

