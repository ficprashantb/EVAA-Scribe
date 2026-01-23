
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

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


public class DateHelper {

	@Keyword
	String GetISTDate(String dateFormat = "MM/dd/yyyy") {
		LocalDate date = LocalDate.now(ZoneId.of("Asia/Kolkata"));

		String formattedDate = date.format(DateTimeFormatter.ofPattern(dateFormat));

		return formattedDate;
	}

	@Keyword
	String GetUTCDate(String dateFormat = "MM/dd/yyyy") {
		LocalDate date = LocalDate.now(ZoneId.of("UTC"));

		String formattedDate = date.format(DateTimeFormatter.ofPattern(dateFormat));

		return formattedDate;
	}

	@Keyword
	String GetPSTDate(String dateFormat = "MM/dd/yyyy") {
		LocalDate date = LocalDate.now(ZoneId.of("Pacific Standard Time"));

		String formattedDate = date.format(DateTimeFormatter.ofPattern(dateFormat));

		return formattedDate;
	}

	@Keyword
	String GetFormattedDate(String date, String dateFormat) {
		// Parse the input date string using the given format
		def parser = new SimpleDateFormat(dateFormat)
		Date inputDate = parser.parse(date)

		// Create a formatter with IST timezone
		def formatter = new SimpleDateFormat(dateFormat)
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"))

		// Format the date into IST
		return formatter.format(inputDate)
	}
}
