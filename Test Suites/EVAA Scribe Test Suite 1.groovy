import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testcase.TestCaseFactory as TestCaseFactory
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository as ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import internal.GlobalVariable as GlobalVariable

import com.kms.katalon.core.annotation.SetUp
import com.kms.katalon.core.annotation.SetupTestCase
import com.kms.katalon.core.annotation.TearDown
import com.kms.katalon.core.annotation.TearDownTestCase
import com.kms.katalon.core.annotation.*
import com.kms.katalon.core.util.KeywordUtil
import javax.mail.*
import javax.mail.internet.*
import javax.activation.*


/**
 * Some methods below are samples for using SetUp/TearDown in a test suite.
 */

/**
 * Setup test suite environment.
 */
@SetUp(skipped = true) // Please change skipped to be false to activate this method.
def setUp() {
	KeywordUtil.logInfo("Starting Test Suite execution...")
}

/**
 * Clean test suites environment.
 */
@TearDown(skipped = true) // Please change skipped to be false to activate this method.
def tearDown() {
	KeywordUtil.logInfo("Test Suite execution finished. Preparing email with report...")
	
//	// Email configuration
//	String host = "smtp.yourmailserver.com"
//	String from = "automation@yourdomain.com"
//	String to = "qa-team@yourdomain.com"
//	String username = "automation@yourdomain.com"
//	String password = "yourPassword"
//
//	Properties props = new Properties()
//	props.put("mail.smtp.auth", "true")
//	props.put("mail.smtp.starttls.enable", "true")
//	props.put("mail.smtp.host", host)
//	props.put("mail.smtp.port", "587")
//
//	Session session = Session.getInstance(props,
//		new javax.mail.Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(username, password)
//			}
//		})
//
//	try {
//		// Build the message
//		MimeMessage message = new MimeMessage(session)
//		message.setFrom(new InternetAddress(from))
//		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
//		message.setSubject("Automation Test Suite Report")
//
//		// Body text
//		MimeBodyPart bodyPart = new MimeBodyPart()
//		bodyPart.setText("The Test Suite execution has completed. Please find the attached HTML report.")
//
//		// Attach the Katalon report (adjust path as needed)
//		String reportPath = RunConfiguration.getReportFolder() + "/report.html"
//		MimeBodyPart attachmentPart = new MimeBodyPart()
//		DataSource source = new FileDataSource(reportPath)
//		attachmentPart.setDataHandler(new DataHandler(source))
//		attachmentPart.setFileName("TestSuiteReport.html")
//
//		// Combine parts
//		Multipart multipart = new MimeMultipart()
//		multipart.addBodyPart(bodyPart)
//		multipart.addBodyPart(attachmentPart)
//
//		message.setContent(multipart)
//
//		// Send
//		Transport.send(message)
//		KeywordUtil.logInfo("Email with report sent successfully to QA team.")
//	} catch (Exception e) {
//		KeywordUtil.markWarning("Failed to send email with report: " + e.message)
//	}

}

/**
 * Run before each test case starts.
 */
@SetupTestCase(skipped = true) // Please change skipped to be false to activate this method.
def setupTestCase() {
	// Put your code here.
}

/**
 * Run after each test case ends.
 */
@TearDownTestCase(skipped = true) // Please change skipped to be false to activate this method.
def tearDownTestCase() {
	KeywordUtil.logInfo("Test Case execution finished...") 
}

/**
 * References:
 * Groovy tutorial page: http://docs.groovy-lang.org/next/html/documentation/
 */