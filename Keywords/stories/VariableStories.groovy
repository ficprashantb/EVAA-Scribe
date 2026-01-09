package stories


import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import CustomKeywords
import database.MySqlHelper as MySqlHelper

import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import internal.GlobalVariable as GlobalVariable

public class VariableStories {

	/* ---------------- LOCAL STORAGE ---------------- */

	static void setItemToDB(String key, Object value) {
		key = "${GlobalVariable.EVAA_SC_NO}_${key}"

		String jsonData = (value instanceof String)
				? value
				: groovy.json.JsonOutput.toJson(value)

		CustomKeywords.'database.DBKeywords.insertOrUpdate'(
				"""
        INSERT INTO enc_localstorage (`Key`, `Value`)
        VALUES (?, ?)
        ON DUPLICATE KEY UPDATE `Value`=VALUES(`Value`)
        """,
				[
					key.toString(),
					jsonData.toString()
				]
				)
	}

	static Object getItemFromDB(String key) {
		key = "${GlobalVariable.EVAA_SC_NO}_${key}"

		def row = CustomKeywords.'database.DBKeywords.selectOne'(
				"SELECT `Value` FROM enc_localstorage WHERE `Key`=?",
				[key.toString()]
				)

		if (!row || !row.Value) {
			return null
		}

		String value = row.Value.toString()

		// Try JSON parsing, fallback to string
		try {
			return new JsonSlurper().parseText(value)
		} catch (Exception e) {
			return value
		}
	}

	static void clearAllItemFromDB(String key) {
		String pattern = "${key}_%"

		CustomKeywords.'database.DBKeywords.insertOrUpdate'(
				"""
        UPDATE enc_localstorage
        SET `Value` = ''
        WHERE `Key` LIKE ?
        """,
				[pattern]
				)
	}

	static void setItem(String key = 'EVAA', Object value) {
		setItemToDB(key, value)
	}

	static Object getItem(String key = 'EVAA') {
		return getItemFromDB(key)
	}

	static void clearItem(String key = 'EVAA') {
		clearAllItemFromDB(key)
	}

	/* ---------------- IN-MEMORY STORAGE ---------------- */

	public static List<String> elementStorage = []
	public static List<String> elementStorageForDirectDictation = []

	private static Map<String, String> storage = [:]

	static void setItemToLocal(String key = 'EVAA', Object value) {
		key = "${GlobalVariable.EVAA_SC_NO}_${key}"

		String jsonData = (value instanceof String)
				? value
				: groovy.json.JsonOutput.toJson(value)

		storage[key] = JsonOutput.toJson(jsonData)
	}

	static Object getItemFromLocal(String key = 'EVAA') {
		key = "${GlobalVariable.EVAA_SC_NO}_${key}"

		def item = storage[key]

		if (!item ) {
			return null
		}

		String value = item.toString()

		// Try JSON parsing, fallback to string
		try {
			return new JsonSlurper().parseText(item)
		} catch (Exception e) {
			return item
		}
	}

	static void clearItemFromLocal(String key = 'EVAA') {
		key = "${GlobalVariable.EVAA_SC_NO}_${key}"
		storage.remove(key)
	}
}