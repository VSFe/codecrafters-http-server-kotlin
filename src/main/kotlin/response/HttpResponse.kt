package response

import HttpStatusCode

data class HttpResponse(
	val header: Map<String, String>,
	val httpStatusCode: HttpStatusCode,
	val body: String?
) {
	companion object {
		fun withoutBody(requestHeader: Map<String, String>, httpStatusCode: HttpStatusCode): HttpResponse = withTextBody(requestHeader, httpStatusCode, null)

		fun withTextBody(requestHeader: Map<String, String>, httpStatusCode: HttpStatusCode, body: String?): HttpResponse {
			println(requestHeader)
			println(requestHeader["Accept-Encoding"])

			val header = buildMap {
				put("Content-Type", "text/plain")
				if (body != null) put("Content-Length", body.length.toString())
				if (requestHeader["Accept-Encoding"] == "gzip") put("Content-Encoding", "gzip")
			}
			return HttpResponse(header, httpStatusCode, body)
		}

		fun withFileBody(requestHeader: Map<String, String>, httpStatusCode: HttpStatusCode, body: String): HttpResponse {
			val header = buildMap {
				put("Content-Type", "application/octet-stream")
				put("Content-Length", body.length.toString())
				if (requestHeader["Accept-Encoding"] == "gzip") put("Content-Encoding", "gzip")
			}
			return HttpResponse(header, httpStatusCode, body)
		}
	}
}
