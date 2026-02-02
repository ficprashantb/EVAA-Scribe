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
import stories.CommonStory

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Random
import com.github.javafaker.Faker

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

import java.time.LocalDateTime

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


public class DateHelper {

	// Define an enum for supported time zones
	enum SupportedZone {
		UTC("UTC"),
		IST("Asia/Kolkata"),
		EST("America/New_York"),
		PST("America/Los_Angeles"),
		LONDON("Europe/London")

		private final String zoneId

		SupportedZone(String zoneId) {
			this.zoneId = zoneId
		}

		String getZoneId() {
			return zoneId
		}
	}

	@Keyword
	String GetFormattedDate(String date = null, String dateFormat = "MM/dd/yyyy", SupportedZone zone = SupportedZone.IST) {
		ZoneId zoneId = ZoneId.of(zone.getZoneId())

		if (CommonStory.isNullOrEmpty(date)) {
			// Current date in zone
			LocalDate ldate = LocalDate.now(zoneId)
			String formattedDate = ldate.format(DateTimeFormatter.ofPattern(dateFormat))

			return  formattedDate
		} else {
			// Parse input date string (always MM/dd/yyyy)
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(dateFormat)
			LocalDate localDate = LocalDate.parse(date, inputFormatter)

			// Convert to ZonedDateTime at midnight in the given zone
			ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId)

			// Format into the desired output pattern
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(dateFormat)
			String formattedDate = zonedDateTime.format(outputFormatter)

			return  formattedDate
		}
	}

	@Keyword
	String GetPSTDate(String dateFormat = "MM/dd/yyyy") {
		LocalDate date = LocalDate.now(ZoneId.of("Pacific Standard Time"));

		String formattedDate = date.format(DateTimeFormatter.ofPattern(dateFormat));

		return formattedDate;
	}

	@Keyword
	String GetUTCFormattedDate(String date, String dateFormat) {
		// Parse the input date string using the given format
		def parser = new SimpleDateFormat(dateFormat)
		Date inputDate = parser.parse(date)

		// Create a formatter with IST timezone
		def formatter = new SimpleDateFormat(dateFormat)
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"))

		// Format the date into IST
		return formatter.format(inputDate)
	}

	@Keyword
	String GetISTFormattedDate(String date, String dateFormat) {
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
