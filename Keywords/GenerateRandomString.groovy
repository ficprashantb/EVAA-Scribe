import com.kms.katalon.core.annotation.Keyword
import java.util.Random
import com.github.javafaker.Faker

public class RandomDataUtils {

	Random random = new Random()

	@Keyword
	String GenerateRandomString(int length) {
		def randomName = (1..length).collect {
			(('A'..'Z') + ('a'..'z'))[random.nextInt(52)]
		}.join()

		return randomName
	}

	@Keyword
	String GenerateFirstName() {
		Faker faker = new Faker()

		return faker.name().firstName()
	}

	@Keyword
	String GenerateLastName() {
		Faker faker = new Faker()

		return faker.name().lastName()
	}

	@Keyword
	String GenerateFullName() {
		Faker faker = new Faker()

		return faker.name().fullName()
	}

	@Keyword
	String GenerateNumber() {
		Faker faker = new Faker()

		return faker.number().digits(5)
	}
}