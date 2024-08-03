import java.io.OutputStream
import java.net.ServerSocket

object ServerSocketManager {
	private val serverSocket = ServerSocket(4221)

	fun init() {
		serverSocket.reuseAddress = true
	}

	fun process() {
		val socket = serverSocket.accept()
		val inputStream = socket.getInputStream()
		val outputStream = socket.getOutputStream()

		inputStream.bufferedReader().use {
			val line = it.readLine()
			val splitLine = line.split(' ')
			val path = splitLine[1]

			val response = when (path) {
				"/" -> "HTTP/1.1 200 OK\r\n\r\n"
				else -> "HTTP/1.1 404 Not Found\r\n\r\n"
			}

			writeResponse(outputStream, response)
		}
	}

	private fun writeResponse(outputStream: OutputStream, response: String) {
		outputStream.write(response.toByteArray())
		outputStream.flush()
		outputStream.close()
	}
}
