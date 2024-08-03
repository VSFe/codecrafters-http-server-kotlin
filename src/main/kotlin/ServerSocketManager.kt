import java.net.ServerSocket

object ServerSocketManager {
	private val serverSocket = ServerSocket(4221)

	fun run() {
		serverSocket.reuseAddress = true
		serverSocket.accept()
	}
}
