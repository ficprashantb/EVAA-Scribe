package database

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

import java.sql.DriverManager as DriverManager
import java.sql.Connection as Connection
import internal.GlobalVariable
import java.util.concurrent.ArrayBlockingQueue

public class ConnectionPool  {

	private static ArrayBlockingQueue<Connection> pool
	private static int poolSize

	static synchronized void init() {
		if (pool) return

			String host = GlobalVariable.MySQL_Host
		String port = GlobalVariable.MySQL_Port
		String db   =  GlobalVariable.MySQL_Database
		String user =  GlobalVariable.MySQL_UserName
		String pass = GlobalVariable.MySQL_Password

		poolSize = (GlobalVariable.RETRY_COUNT ?: '5') as int
		pool = new ArrayBlockingQueue<>(poolSize)

		String url = "jdbc:mysql://${host}:${port}/${db}?useSSL=false&allowPublicKeyRetrieval=true"

		(1..poolSize).each {
			pool.offer(DriverManager.getConnection(url, user, pass))
		}
	}

	static Connection getConnection() {
		init()
		return pool.take()
	}

	static void release(Connection conn) {
		if (conn) pool.offer(conn)
	}
}
