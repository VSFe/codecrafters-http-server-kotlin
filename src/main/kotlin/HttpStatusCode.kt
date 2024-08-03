enum class HttpStatusCode(
	private val description: String
) {
	OK("200 OK"),
	CREATED("201 Created"),
	NOT_FOUND("404 Not Found");

	val statusLine: String
		get() = "${CommonConstant.HTTP_VERSION} ${description}\r\n"
}
