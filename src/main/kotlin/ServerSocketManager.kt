import request.HttpRequest
import request.HttpRequestLine
import java.io.BufferedReader
import java.io.OutputStream
import java.net.ServerSocket

object ServerSocketManager {
	private val serverSocket = ServerSocket(CommonConstant.PORT)

	fun init() {
		serverSocket.reuseAddress = true
	}

	fun process() {
		val socket = serverSocket.accept()
		val inputStream = socket.getInputStream()
		inputStream.bufferedReader().use {
			val httpRequest = readRequest(it)
			val outputStream = socket.getOutputStream()

			val result = processData(httpRequest)
			val responseHeader = createResponseHeader(result)
			writeResponse(outputStream, responseHeader, result.first)
		}
	}

	private fun readRequest(bufferedReader: BufferedReader): HttpRequest {
		val requestLine = bufferedReader.readLine()
		val httpRequestLine = HttpRequestLine.createHttpRequest(requestLine)
		val httpHeader = parseRequestHeader(bufferedReader)
		val body = if (httpHeader.containsKey("Content-Length")) bufferedReader.readLine() else null

		return HttpRequest(httpRequestLine, httpHeader, body)
	}

	private fun parseRequestHeader(bufferedReader: BufferedReader): Map<String, String> {
		return bufferedReader.lineSequence()
			.takeWhile { it.isNotEmpty() && it != "\r\n" }
			.map {
				val (key, value) = it.split(": ", limit = 2)
				key to value
			}
			.toMap()
	}

	private fun processData(httpRequest: HttpRequest): Pair<String?, HttpStatusCode> {
		val path = httpRequest.httpRequestLine.url

		return when {
			path == "/" -> null to HttpStatusCode.OK
			path == "/user-agent" -> httpRequest.header["User-Agent"] to HttpStatusCode.OK
			path.startsWith("/echo/") -> path.substringAfter("/echo/") to HttpStatusCode.OK
			else -> null to HttpStatusCode.NOT_FOUND
		}
	}

	private fun createResponseHeader(result: Pair<String?, HttpStatusCode>): String {
		val (response, httpStatusCode) = result
		val statusLine = httpStatusCode.statusLine
		val responseHeader = StringBuilder(statusLine)

		if (response != null) {
			responseHeader.append("Content-Type: text/plain\r\n")
			responseHeader.append("Content-Length: ${response.length}\r\n")
		}

		responseHeader.append("\r\n")
		return responseHeader.toString()
	}

	private fun writeResponse(outputStream: OutputStream, header: String, response: String?) {
		outputStream.write(header.toByteArray())
		response?.let { outputStream.write(it.toByteArray()) }
		outputStream.flush()
		outputStream.close()
	}
}
