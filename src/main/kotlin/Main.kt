fun main(args: Array<String>) {
	// You can use print statements as follows for debugging, they'll be visible when running tests.
	println("Logs from your program will appear here!")
	val param = parseParam(args)
	ServerSocketManager.init(param)
	println("accepted new connection")

	while (true) {
		ServerSocketManager.process()
	}
}

private fun parseParam(args: Array<String>): Map<String, String> =
	args.asSequence().chunked(2)
		.map { (a, b) -> a.substringAfter("--") to b }
		.toMap()
