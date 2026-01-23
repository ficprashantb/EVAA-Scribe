package stories

import org.monte.screenrecorder.ScreenRecorder
import org.monte.media.Format
import org.monte.media.Registry
import org.monte.media.math.Rational
import org.monte.media.FormatKeys
import org.monte.media.VideoFormatKeys

import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.awt.Toolkit
import java.io.File



public class VideoRecorderHelper {
	private ScreenRecorder recorder

	void startRecording(String dirPath, String fileName) {
		GraphicsConfiguration gc = GraphicsEnvironment
				.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration()

		File movieFolder = new File(dirPath)
		if (!movieFolder.exists()) {
			movieFolder.mkdirs()
		}

		// File format (AVI)
		Format fileFormat = new Format(
				FormatKeys.MediaTypeKey, FormatKeys.MediaType.FILE,
				FormatKeys.MimeTypeKey, FormatKeys.MIME_AVI // ✅ use constant
				)

		// Video format (TechSmith Screen Capture)
		Format screenFormat = new Format(
				FormatKeys.MediaTypeKey, FormatKeys.MediaType.VIDEO,
				FormatKeys.EncodingKey, VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
				VideoFormatKeys.DepthKey, 24,
				FormatKeys.FrameRateKey, new Rational(15, 1),
				VideoFormatKeys.QualityKey, 1.0f,
				VideoFormatKeys.KeyFrameIntervalKey, 15
				)
		recorder = new ScreenRecorder(gc,
				new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()),
				fileFormat, screenFormat, null,null, movieFolder)

		recorder.start()
		println "🎥 Recording started → ${dirPath}/${fileName}.avi"
	}

	void stopRecording() {
		if (recorder != null) {
			recorder.stop()
			println "🛑 Recording stopped."
		}
	}
}
