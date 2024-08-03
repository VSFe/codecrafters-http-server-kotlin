package response

import HttpStatusCode
import resolver.GzipResolver

data class HttpResponse(
	val header: Map<String, String>,
	val httpStatusCode: HttpStatusCode,
	val body: ByteArray?
) {
	companion object {
		fun withoutBody(requestHeader: Map<String, String>, httpStatusCode: HttpStatusCode): HttpResponse = withTextBody(requestHeader, httpStatusCode, null)

		fun withTextBody(requestHeader: Map<String, String>, httpStatusCode: HttpStatusCode, body: String?): HttpResponse {
			val supportGzip = containsGzip(requestHeader)
			val finalBody = if (body != null && supportGzip) GzipResolver.gzip(body) else body?.toByteArray()
			val header = buildMap {
				put("Content-Type", "text/plain")
				if (finalBody != null) put("Content-Length", finalBody.size.toString())
				if (supportGzip) put("Content-Encoding", "gzip")
			}
			return HttpResponse(header, httpStatusCode, finalBody)
		}

		fun withFileBody(requestHeader: Map<String, String>, httpStatusCode: HttpStatusCode, body: String): HttpResponse {
			val supportGzip = containsGzip(requestHeader)
			val finalBody = if (supportGzip) GzipResolver.gzip(body) else body.toByteArray()
			val header = buildMap {
				put("Content-Type", "application/octet-stream")
				put("Content-Length", finalBody.size.toString())
				if (containsGzip(requestHeader)) put("Content-Encoding", "gzip")
			}
			return HttpResponse(header, httpStatusCode, finalBody)
		}

		private fun containsGzip(requestHeader: Map<String, String>): Boolean =
			requestHeader["Accept-Encoding"]?.let {
				it.split(",")
					.any{ str -> str.trim() == "gzip" }
			} ?: false
	}
}
