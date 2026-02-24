

import com.kms.katalon.core.webui.driver.DriverFactory

import internal.GlobalVariable
import stories.UtilHelper

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import com.kms.katalon.core.configuration.RunConfiguration
import org.openqa.selenium.remote.DesiredCapabilities
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import org.openqa.selenium.WebDriver

public class Keywords_DesiredCapabilities {

	static void addDesiredCapabilities() {
		Map<String, Object> prefs = new HashMap<>()
		prefs.put("profile.default_content_setting_values.media_stream_camera", 1)
		prefs.put("profile.default_content_setting_values.media_stream_mic", 1)
		prefs.put("profile.default_content_setting_values.geolocation", 1)
		prefs.put("profile.default_content_setting_values.notifications", 1)
		prefs.put("profile.default_content_setting_values.popups", 1)
		prefs.put("profile.default_content_setting_values.automatic_downloads", 1)
		prefs.put("profile.default_content_setting_values.clipboard", 1)

		RunConfiguration.setWebDriverPreferencesProperty("prefs", prefs)
	}

	static void addCapabilities() {
		// Preferences dictionary
		Map<String, Object> prefs = new HashMap<>()
		prefs.put("profile.default_content_setting_values.media_stream_camera", 1)
		prefs.put("profile.default_content_setting_values.media_stream_mic", 1)
		prefs.put("profile.default_content_setting_values.geolocation", 1)
		prefs.put("profile.default_content_setting_values.notifications", 1)
		prefs.put("profile.default_content_setting_values.popups", 1)
		prefs.put("profile.default_content_setting_values.automatic_downloads", 1)
		prefs.put("profile.default_content_setting_values.mixed_script", 1)
		prefs.put("profile.default_content_setting_values.media_stream", 1)

		// Optional – Chrome will ignore this, but harmless
		prefs.put("profile.default_content_setting_values.clipboard", 1)

		RunConfiguration.setWebDriverPreferencesProperty("prefs", prefs)

		Boolean IS_FAKE_MIC = GlobalVariable.G_IS_FAKE_MIC
		if(!IS_FAKE_MIC) { 
			// Build a single args list (all entries must be pure java.lang.String, no GString)
			List<String> args = new ArrayList<>()
			args.add("--use-fake-ui-for-media-stream")
			args.add("--disable-notifications")
			// Clipboard / security workarounds
			args.add("--disable-blink-features=BlockClipboardAPI")
			args.add("--unsafely-treat-insecure-origin-as-secure=" + GlobalVariable.EVAA_SiteURL.toString())

			// Fake audio device for media stream
			String wavPath = UtilHelper.getFilePath(GlobalVariable.G_FILE_NAME)
			args.add("--use-fake-device-for-media-stream")
			args.add("--no-sandbox")
			args.add("--disable-dev-shm-usage")
			args.add("--use-file-for-fake-audio-capture=" + wavPath)

			RunConfiguration.setWebDriverPreferencesProperty("args", args)
		}
	}
	
}

