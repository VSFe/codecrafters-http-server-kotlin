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
			val outputStream = socket.getOutputStream()

			val result = processData(it)
			val responseHeader = createResponseHeader(result)
			writeResponse(outputStream, responseHeader, result.first)
		}
	}

	private fun processData(bufferedReader: BufferedReader): Pair<String?, HttpStatusCode> {
		val line = bufferedReader.readLine()
		val splitLine = line.split(' ')
		val path = splitLine[1]

		return if (path == "/") {
			Pair(null, HttpStatusCode.OK)
		} else if (path.startsWith("/echo/")) {
			Pair(path.substringAfter("/echo/"), HttpStatusCode.OK)
		} else {
			Pair(null, HttpStatusCode.NOT_FOUND)
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
