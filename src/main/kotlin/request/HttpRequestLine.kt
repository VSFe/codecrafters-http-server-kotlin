package request

import HttpMethod

data class HttpRequestLine(
	val httpMethod: HttpMethod,
	val url: String,
	val httpVersion: String
) {
	companion object {
		fun createHttpRequest(requestLine: String): HttpRequestLine {
			val inputList = requestLine.split(" ")
			return HttpRequestLine(
				httpMethod = HttpMethod.valueOf(inputList[0]),
				url = inputList[1],
				httpVersion = inputList[2]
			)
		}
	}
}
