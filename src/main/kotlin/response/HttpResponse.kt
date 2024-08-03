package response

import HttpStatusCode

data class HttpResponse(
	val header: Map<String, String>,
	val httpStatusCode: HttpStatusCode,
	val body: String?
) {
	companion object {
		fun withoutBody(httpStatusCode: HttpStatusCode): HttpResponse = HttpResponse(emptyMap(), httpStatusCode, null)

		fun withTextBody(httpStatusCode: HttpStatusCode, body: String): HttpResponse {
			val header = mapOf(
				"Content-Type" to "text/plain",
				"Content-Length" to body.length.toString()
			)
			return HttpResponse(header, httpStatusCode, body)
		}

		fun withFileBody(httpStatusCode: HttpStatusCode, body: String): HttpResponse {
			val header = mapOf(
				"Content-Type" to "application/octet-stream",
				"Content-Length" to body.length.toString()
			)
			return HttpResponse(header, httpStatusCode, body)
		}
	}
}
