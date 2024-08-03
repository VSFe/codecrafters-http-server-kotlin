package resolver

import java.io.File

object FileResolver {
	fun writeFile(dir: String, body: String) =
		File(dir).writeText(body)

	fun readFile(dir: String): String =
		File(dir).readText(Charsets.UTF_8)

	fun existFile(dir: String): Boolean = File(dir).exists()
}
