package response

import HttpStatusCode

data class HttpResponse(
	val header: Map<String, String>,
	val httpStatusCode: HttpStatusCode,
	val body: String?
) {
	companion object {
		fun withoutBody(httpStatusCode: HttpStatusCode): HttpResponse = HttpResponse(emptyMap(), httpStatusCode, null)

		fun withTextBody(requestHeader: Map<String, String>, httpStatusCode: HttpStatusCode, body: String): HttpResponse {
			val header = buildMap<String, String> {
				"Content-Type" to "text/plain"
				"Content-Length" to body.length.toString()
				if (requestHeader["Accept-Encoding"] == "gzip") {
					"Content-Encoding" to "gzip"
				}
			}
			return HttpResponse(header, httpStatusCode, body)
		}

		fun withFileBody(requestHeader: Map<String, String>, httpStatusCode: HttpStatusCode, body: String): HttpResponse {
			val header = buildMap<String, String> {
				"Content-Type" to "application/octet-stream"
				"Content-Length" to body.length.toString()
				if (requestHeader["Accept-Encoding"] == "gzip") {
					"Content-Encoding" to "gzip"
				}
			}
			return HttpResponse(header, httpStatusCode, body)
		}
	}
}
