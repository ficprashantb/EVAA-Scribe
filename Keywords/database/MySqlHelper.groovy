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

import groovy.sql.Sql
import internal.GlobalVariable

import java.sql.Connection
import com.kms.katalon.core.util.KeywordUtil


public class MySqlHelper {

	private static int MAX_RETRY =
	(GlobalVariable.RETRY_COUNT ?: '3') as int

	/* ---------------- CORE EXECUTION WITH RETRY ---------------- */

	private static def executeWithRetry(Closure action) {
		int attempt = 0
		Exception lastError

		while (attempt < MAX_RETRY) {
			try {
				return action()
			} catch (Exception e) {
				lastError = e
				if (e.message?.toLowerCase()?.contains('lock')) {
					attempt++
					KeywordUtil.logInfo("DB lock detected, retrying (${attempt}/${MAX_RETRY})")
					sleep(1000)
				} else {
					throw e
				}
			}
		}
		throw lastError
	}

	/* ---------------- INSERT / UPDATE / DELETE ---------------- */

	static int executeUpdate(String query, List params = []) {
		return executeWithRetry {
			Connection conn = ConnectionPool.getConnection()
			try {
				Sql sql = new Sql(conn)
				return sql.executeUpdate(query, params)
			} finally {
				ConnectionPool.release(conn)
			}
		}
	}

	/* ---------------- SELECT ONE ---------------- */

	static Map selectOne(String query, List params = []) {
		return executeWithRetry {
			Connection conn = ConnectionPool.getConnection()
			try {
				Sql sql = new Sql(conn)
				return sql.firstRow(query, params)
			} finally {
				ConnectionPool.release(conn)
			}
		}
	}

	/* ---------------- SELECT ALL ---------------- */

	static List<Map> selectAll(String query, List params = []) {
		return executeWithRetry {
			Connection conn = ConnectionPool.getConnection()
			try {
				Sql sql = new Sql(conn)
				return sql.rows(query, params)
			} finally {
				ConnectionPool.release(conn)
			}
		}
	}

	/* ---------------- TRANSACTION SUPPORT ---------------- */

	static void withTransaction(Closure work) {
		Connection conn = ConnectionPool.getConnection()
		Sql sql = new Sql(conn)
		conn.autoCommit = false

		try {
			work(sql)
			conn.commit()
		} catch (Exception e) {
			conn.rollback()
			throw e
		} finally {
			conn.autoCommit = true
			ConnectionPool.release(conn)
		}
	}
}