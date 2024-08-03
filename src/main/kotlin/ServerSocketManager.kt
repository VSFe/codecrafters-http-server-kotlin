import java.net.ServerSocket

object ServerSocketManager {
	private val serverSocket = ServerSocket(4221)

	fun init() {
		serverSocket.reuseAddress = true
	}

	fun process() {
		val socket = serverSocket.accept()
		val outputStream = socket.getOutputStream()

		outputStream.write("HTTP/1.1 200 OK\r\n\r\n".toByteArray())
		outputStream.flush()
		outputStream.close()
	}
}
