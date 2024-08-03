fun main() {
	// You can use print statements as follows for debugging, they'll be visible when running tests.
	println("Logs from your program will appear here!")
	ServerSocketManager.init()
	println("accepted new connection")

	while (true) {
		var aa = ServerSocketManager.process()
		println(aa)
	}
}
