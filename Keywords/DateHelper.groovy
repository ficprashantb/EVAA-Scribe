
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

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Random
import com.github.javafaker.Faker

public class DateHelper {

	@Keyword
	String GetISTDate() {
		LocalDate date = LocalDate.now(ZoneId.of("Asia/Kolkata"));

		String formattedDate = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

		return formattedDate;
	}

	@Keyword
	String GetUTCDate() {
		LocalDate date = LocalDate.now(ZoneId.of("UTC"));

		String formattedDate = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

		return formattedDate;
	}

	@Keyword
	String GetPSTDate() {
		LocalDate date = LocalDate.now(ZoneId.of("Pacific Standard Time"));

		String formattedDate = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

		return formattedDate;
	}

	@Keyword
	String GetFormattedDate(String date, String dateFormat) {
		def inputDate = Date.parse("MM/dd/yyyy", date)
		def formattedDate = inputDate.format(dateFormat)
		return formattedDate;
	}
}
