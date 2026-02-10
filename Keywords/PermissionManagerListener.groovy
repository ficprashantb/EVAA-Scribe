

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

public class PermissionManagerListener {

	static void enableBrowserPermissions() {

		//		String browser = RunConfiguration.getExecutionProperties().get("execution.default.executionConfiguration")

		String browser = "chrome"

		switch(browser.toLowerCase()) {

			case "chrome":

				ChromeOptions options = new ChromeOptions()

			// Disable Safe Browsing checks
				options.addArguments("--safebrowsing-disable-download-protection")

				Map<String, Object> prefs = new HashMap<>()

			// ========== All Browser Permissions ==========
				prefs.put("profile.default_content_setting_values.media_stream_camera", 1)
				prefs.put("profile.default_content_setting_values.media_stream_mic", 1)
				prefs.put("profile.default_content_setting_values.geolocation", 1)
				prefs.put("profile.default_content_setting_values.notifications", 1)
				prefs.put("profile.default_content_setting_values.popups", 1)
				prefs.put("profile.default_content_setting_values.automatic_downloads", 1)
				prefs.put("profile.default_content_setting_values.mixed_script", 1)
				prefs.put("profile.default_content_setting_values.clipboard", 1)
				prefs.put("profile.default_content_setting_values.background_sync", 1)
				prefs.put("profile.default_content_setting_values.media_stream", 1)
 
//			// Set download directory and auto-allow downloads
//				prefs.put("download.prompt_for_download", false)
//				prefs.put("download.default_directory", System.getProperty("user.home") + "/Downloads")
//				prefs.put("safebrowsing.enabled", true)   // ensures downloads are allowed
//				options.setExperimentalOption("prefs", prefs)


				options.setExperimentalOption("prefs", prefs)

				ChromeDriver driver = new ChromeDriver(options)
				DriverFactory.changeWebDriver(driver)

				break

			case "edge":
				EdgeOptions options = new EdgeOptions()

				Map<String, Object> prefs = new HashMap<>()

			// ========== All Edge Permissions ==========
				prefs.put("profile.default_content_setting_values.media_stream_camera", 1)
				prefs.put("profile.default_content_setting_values.media_stream_mic", 1)
				prefs.put("profile.default_content_setting_values.geolocation", 1)
				prefs.put("profile.default_content_setting_values.notifications", 1)
				prefs.put("profile.default_content_setting_values.popups", 1)
				prefs.put("profile.default_content_setting_values.automatic_downloads", 1)

				options.setExperimentalOption("prefs", prefs)

				DriverFactory.changeWebDriver(new org.openqa.selenium.edge.EdgeDriver(options))
				break

			case "firefox":
				FirefoxProfile profile = new FirefoxProfile()

			// ========== All Firefox Permissions ==========
				profile.setPreference("permissions.default.camera", 1)
				profile.setPreference("permissions.default.microphone", 1)
				profile.setPreference("permissions.default.geo", 1)
				profile.setPreference("permissions.default.desktop-notification", 1)

			// Auto-allow media
				profile.setPreference("media.navigator.permission.disabled", true)
				profile.setPreference("media.autoplay.default", 0)
				profile.setPreference("media.navigator.streams.fake", true)

				FirefoxOptions options = new FirefoxOptions()
				options.setProfile(profile)

				FirefoxDriver driver = new FirefoxDriver(options)
				DriverFactory.changeWebDriver(new org.openqa.selenium.firefox.FirefoxDriver(options))
				break
		}
	}
}