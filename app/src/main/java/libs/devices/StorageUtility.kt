package libs.devices

import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.os.StatFs

/**
 * A utility object that provides functions for retrieving internal and external
 * storage information such as total space, free space, and available space percentage.
 */
object StorageUtility {

	/**
	 * Calculates the total internal storage space on the device.
	 *
	 * @return Total internal storage space in bytes. This refers to the space
	 *         available on the device's internal memory (typically `/data`).
	 */
	@JvmStatic
	fun getTotalStorageSpace(): Long {
		val path = Environment.getDataDirectory()
		val stat = StatFs(path.path)
		val blockSize = stat.blockSizeLong
		val totalBlocks = stat.blockCountLong
		return totalBlocks * blockSize
	}

	/**
	 * Calculates the available (free) internal storage space on the device.
	 *
	 * @return Free internal storage space in bytes.
	 */
	@JvmStatic
	fun getFreeStorageSpace(): Long {
		val path = Environment.getDataDirectory()
		val stat = StatFs(path.path)
		val blockSize = stat.blockSizeLong
		val availableBlocks = stat.availableBlocksLong
		return availableBlocks * blockSize
	}

	/**
	 * Calculates the percentage of free space available in internal storage.
	 *
	 * @return The percentage of available internal storage space as a float
	 *         between 0 and 100.
	 */
	@JvmStatic
	fun getFreeStoragePercentage(): Float {
		val totalSpace = getTotalStorageSpace()
		val freeSpace = getFreeStorageSpace()
		return (freeSpace.toFloat() / totalSpace) * 100
	}

	/**
	 * Calculates the total external storage space (e.g., SD card).
	 * Only returns valid data if the external storage is mounted.
	 *
	 * @return Total external storage space in bytes,
	 *         or 0 if the storage is not currently mounted.
	 */
	@JvmStatic
	fun getTotalExternalStorageSpace(): Long {
		return if (Environment.getExternalStorageState() == MEDIA_MOUNTED) {
			val path = Environment.getExternalStorageDirectory()
			val stat = StatFs(path.path)
			val blockSize = stat.blockSizeLong
			val totalBlocks = stat.blockCountLong
			totalBlocks * blockSize
		} else 0
	}

	/**
	 * Calculates the available (free) external storage space (e.g., SD card).
	 * Only returns valid data if the external storage is mounted.
	 *
	 * @return Free external storage space in bytes,
	 *         or 0 if the storage is not currently mounted.
	 */
	@JvmStatic
	fun getFreeExternalStorageSpace(): Long {
		return if (Environment.getExternalStorageState() == MEDIA_MOUNTED) {
			val path = Environment.getExternalStorageDirectory()
			val stat = StatFs(path.path)
			val blockSize = stat.blockSizeLong
			val availableBlocks = stat.availableBlocksLong
			availableBlocks * blockSize
		} else 0
	}

	/**
	 * Calculates the percentage of free space available in external storage.
	 * Only returns valid data if the external storage is mounted.
	 *
	 * @return The percentage of available external storage space as a float
	 *         between 0 and 100, or 0 if storage is not mounted.
	 */
	@JvmStatic
	fun getFreeExternalStoragePercentage(): Float {
		val totalSpace = getTotalExternalStorageSpace()
		val freeSpace = getFreeExternalStorageSpace()
		return if (totalSpace != 0L) {
			(freeSpace.toFloat() / totalSpace) * 100
		} else 0f
	}
}