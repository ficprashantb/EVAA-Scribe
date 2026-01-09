
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions

public class PermissionManager {

	/**
	 * Launch Chrome with ALL permissions auto-allowed
	 */
	static void openChromeWithAllPermissions(String url) {

		ChromeOptions options = new ChromeOptions()

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

		// Disable the permission bubbles UI
		prefs.put("profile.default_content_setting_values.media_stream_camera", 1)
		prefs.put("profile.default_content_setting_values.media_stream_mic", 1)

		options.setExperimentalOption("prefs", prefs)

		ChromeDriver driver = new ChromeDriver(options)
		DriverFactory.changeWebDriver(driver)

		driver.get(url)
	}


	/**
	 * Launch Firefox with ALL permissions auto-allowed
	 */
	static void openFirefoxWithAllPermissions(String url) {

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
		DriverFactory.changeWebDriver(driver)

		driver.get(url)
	}


	/**
	 * Launch Edge with ALL permissions auto-allowed
	 */
	static void openEdgeWithAllPermissions(String url) {

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

		EdgeDriver driver = new EdgeDriver(options)
		DriverFactory.changeWebDriver(driver)

		driver.get(url)
	}
}