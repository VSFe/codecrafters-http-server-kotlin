package resolver

import HttpStatusCode
import response.HttpResponse
import java.io.File

object FileResolver {
	fun resolveFile(dir: String): HttpResponse =
		when {
			existFile(dir) -> HttpResponse.withFileBody(HttpStatusCode.OK, readFile(dir))
			else -> HttpResponse.withoutBody(HttpStatusCode.NOT_FOUND)
		}

	fun writeFile(dir: String, body: String) =
		File(dir).writeText(body)

	private fun readFile(dir: String): String =
		File(dir).readText(Charsets.UTF_8)

	private fun existFile(dir: String): Boolean = File(dir).exists()
}
