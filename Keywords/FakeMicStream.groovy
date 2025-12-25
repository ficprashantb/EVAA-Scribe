

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
import java.io.File

import javazoom.jl.player.advanced.AdvancedPlayer
import javazoom.jl.player.advanced.PlaybackListener
import javazoom.jl.player.advanced.PlaybackEvent
import javazoom.jl.player.AudioDevice
import javazoom.jl.player.FactoryRegistry
import javazoom.jl.decoder.*

//class FakeMicStream {
//	private Player player
//
//	FakeMicStream(String mp3Path) {
//		FileInputStream fis = new FileInputStream(mp3Path)
//		player = new Player(fis)
//	}
//	
//	FakeMicStream() { 
//	}
//
//	void start() {
//		// Play in a separate thread so it doesn’t block
//		Thread.start {
//			player.play()
//		}
//	}
//
//	void stop() {
//		player.close()
//	}
// 
//}



class FakeMicStream {

    private String mp3Path
    private Thread playThread
    private volatile boolean isPaused = false
    private volatile boolean isStopped = false

    private int currentFrame = 0
    private int totalFrames = Integer.MAX_VALUE

    FakeMicStream(String mp3Path) {
        this.mp3Path = mp3Path
    }

    void start() {
        playFrom(currentFrame)
    }

    void playFrom(int frame) {

        playThread = Thread.start {

            try {
                FileInputStream fis = new FileInputStream(mp3Path)
                Bitstream bit = new Bitstream(fis)
                Decoder decoder = new Decoder()

                AudioDevice device = FactoryRegistry.systemRegistry().createAudioDevice()
                device.open(decoder)

                // Skip frames if resuming
                for (int i = 0; i < frame; i++) {
                    Header h = bit.readFrame()
                    if (h == null) break
                    bit.closeFrame()
                }

                while (!isStopped) {

                    if (isPaused) {
                        break
                    }

                    Header h = bit.readFrame()
                    if (h == null) break

                    SampleBuffer output = decoder.decodeFrame(h, bit)
                    device.write(output.getBuffer(), 0, output.getBufferLength())

                    currentFrame++
                    bit.closeFrame()
                }

                device.close()
                bit.close()

            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    void pause() {
        isPaused = true
    }

    void resume() {
        isPaused = false
        playFrom(currentFrame)
    }

    void stop() {
        isStopped = true
        currentFrame = 0
    }
}
