import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
 
import javax.mail.internet.*
import javax.mail.*
import javax.mail.search.FlagTerm

import javax.mail.search.ReceivedDateTerm
import javax.mail.search.ComparisonTerm

public class KW_GetGmailOTP {

	def String getTextFromMessage(Message message) {
		if (message.isMimeType("text/plain")) {
			return message.getContent().toString()
		} else if (message.isMimeType("text/html")) {
			return message.getContent().toString()
		} else if (message.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) message.getContent()
			for (int i = 0; i < multipart.count; i++) {
				BodyPart part = multipart.getBodyPart(i)
				if (part.isMimeType("text/plain")) {
					return part.getContent().toString()
				} else if (part.isMimeType("text/html")) {
					return part.getContent().toString()
				}
			}
		}
		return ""
	}

	@Keyword
	def String getGmailOTP(String host, String username, String password, String from, String subject) {
		String otp = ""

		try {

			WebUI.delay(10)

			Properties props = new Properties()
			props.put("mail.store.protocol", "imaps")
			Session session = Session.getDefaultInstance(props, null)
			Store store = session.getStore("imaps")
			store.connect(host, username, password)

			Folder inbox = store.getFolder("INBOX")
			inbox.open(Folder.READ_ONLY)

			//		Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false))

			// Example: messages received today
			def today = new Date()
			Message[] messages = inbox.search(new ReceivedDateTerm(ComparisonTerm.GE, today))

			messages.sort { a, b -> b.receivedDate <=> a.receivedDate }

			messages.each { msg ->
				def fromAddr = msg.from[0].toString()
				def subj = msg.subject ?: ""
				if (fromAddr.contains(from) && subj.toLowerCase().contains(subject.toLowerCase())) {
					def bodyText = getTextFromMessage(msg)
					println "bodyText: $bodyText"

					// Extract OTP (4–8 digits)
					def matcher = bodyText =~ /\b\d{4,8}\b/
					if (matcher.find()) {
						otp = matcher.group()
						println "OTP found: $otp"
					}
				}
			}
			inbox.close(false)
			store.close()
		} catch (e) {
			e.printStackTrace()
		}

		return otp
	}
}