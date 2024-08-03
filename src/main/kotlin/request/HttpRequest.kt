package request

data class HttpRequest(
	val httpRequestLine: HttpRequestLine,
	val header: Map<String, String>,
	val body: String?
) {
}
