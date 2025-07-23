package libs.process

import java.util.Random

/**
 * Utility object for generating unique numbers and making random-based decisions.
 *
 * This class provides functions that are useful for generating IDs or filenames
 * in a unique manner and for determining whether to display ads randomly.
 */
object UniqueNumberUtils {
	
	/**
	 * Generates a pseudo-unique long number using the current time in milliseconds and a random component.
	 *
	 * This can be used as a quick method to generate unique identifiers, such as session IDs or filenames.
	 *
	 * @return A pseudo-unique [Long] number.
	 */
	@JvmStatic
	fun generateUniqueNumber(): Long {
		val random = Random()
		val currentTime = System.currentTimeMillis() % 1_000_000L
		val randomComponent = random.nextInt(1000)
		return currentTime * 1000 + randomComponent
	}
}
