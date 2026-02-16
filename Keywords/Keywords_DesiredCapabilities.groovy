

import com.kms.katalon.core.webui.driver.DriverFactory

import internal.GlobalVariable
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
		// Define all capabilities before browser launch
		DesiredCapabilities caps = new DesiredCapabilities()

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
  
		// Build a single args list
		List<String> args = new ArrayList<>()
		args.add("--use-fake-ui-for-media-stream")
		args.add("--disable-notifications")
		// Clipboard / security workarounds
		args.add("--disable-blink-features=BlockClipboardAPI")
		args.add("--unsafely-treat-insecure-origin-as-secure=${GlobalVariable.EVAA_SiteURL}")

		// Fake audio device for media stream
		String wavPath = RunConfiguration.getProjectDir() + "/Files/Cadence_Kingele_1.mp3"
		args.add("--use-fake-device-for-media-stream")
		args.add("--no-sandbox")
		args.add("--disable-dev-shm-usage")
		args.add("--use-file-for-fake-audio-capture=" + wavPath)

		// Apply to TestCloud run BEFORE browser launch
		RunConfiguration.setWebDriverPreferencesProperty("prefs", prefs)
		RunConfiguration.setWebDriverPreferencesProperty("args", args)
	}

	static void addCapabilities() {

		ChromeOptions options = new ChromeOptions() 
		
		// 1️⃣ Auto allow mic & camera
		options.addArguments("--use-fake-ui-for-media-stream")

		// 2️⃣ Provide fake media stream (prevents device selection popup)
		options.addArguments("--use-fake-device-for-media-stream")

		// 3️⃣ Optional: disable notification popups
		options.addArguments("--disable-notifications")

		// prefs (optional but good to keep)
		Map<String, Object> prefs = [
			'profile.default_content_setting_values.media_stream_mic'   : 1,
			'profile.default_content_setting_values.media_stream_camera': 1
		]

		options.setExperimentalOption('prefs', prefs)

		WebDriver driver = new ChromeDriver(options)
		DriverFactory.changeWebDriver(driver)
	}
}

