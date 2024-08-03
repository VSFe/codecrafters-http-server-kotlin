package resolver

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream
import kotlin.text.Charsets.UTF_8

object GzipResolver {
	fun gzip(content: String): ByteArray {
		val bos = ByteArrayOutputStream()
		GZIPOutputStream(bos).bufferedWriter(UTF_8).use { it.write(content) }
		return bos.toByteArray()
	}
}
