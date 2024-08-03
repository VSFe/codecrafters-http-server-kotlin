import request.HttpRequest
import request.HttpRequestLine
import resolver.FileResolver
import response.HttpResponse
import java.io.BufferedReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

object ServerSocketManager {
	private val serverSocket = ServerSocket(CommonConstant.PORT)
	private val requestExecutor = Executors.newFixedThreadPool(10)
	private lateinit var param: Map<String, String>

	fun init(param: Map<String, String>) {
		this.param = param
		serverSocket.reuseAddress = true
	}

	fun process() {
		val socket = serverSocket.accept()
		requestExecutor.execute {
			processInner(socket)
		}
	}

	private fun processInner(socket: Socket) {
		val inputStream = socket.getInputStream()
		inputStream.bufferedReader().use {
			val httpRequest = readRequest(it)
			val outputStream = socket.getOutputStream()

			val result = processData(httpRequest)
			val responseHeader = createResponseHeader(result)
			writeResponse(outputStream, responseHeader, result.body)
		}
	}

	private fun readRequest(bufferedReader: BufferedReader): HttpRequest {
		val requestLine = bufferedReader.readLine()
		val httpRequestLine = HttpRequestLine.createHttpRequest(requestLine)
		val httpHeader = parseRequestHeader(bufferedReader)
		val body = if (httpHeader.containsKey("Content-Length")) bufferedReader.readLetter(httpHeader["Content-Length"]!!.toInt()) else null

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

	private fun processData(httpRequest: HttpRequest): HttpResponse {
		val path = httpRequest.httpRequestLine.url
		val method = httpRequest.httpRequestLine.httpMethod
		val header = httpRequest.header

		return when {
			path == "/" -> HttpResponse.withoutBody(header, HttpStatusCode.OK)
			path == "/user-agent" -> HttpResponse.withTextBody(header, HttpStatusCode.OK, header["User-Agent"]!!)
			path.startsWith("/echo/") -> HttpResponse.withTextBody(header, HttpStatusCode.OK, path.substringAfter("/echo/"))
			path.startsWith("/files/") && method == HttpMethod.GET -> {
				val fileDir = "${param["directory"]}${path.substringAfter("/files/")}"
				if (FileResolver.existFile(fileDir)) HttpResponse.withFileBody(
					header,
					HttpStatusCode.OK,
					FileResolver.readFile(fileDir)
				) else HttpResponse.withoutBody(header, HttpStatusCode.NOT_FOUND)
			}

			path.startsWith("/files/") && method == HttpMethod.POST -> {
				FileResolver.writeFile("${param["directory"]}${path.substringAfter("/files/")}", httpRequest.body!!)
				HttpResponse.withoutBody(header, HttpStatusCode.CREATED)
			}

			else -> HttpResponse.withoutBody(header, HttpStatusCode.NOT_FOUND)
		}
	}

	private fun createResponseHeader(response: HttpResponse): String {
		val (header, statusCode, _) = response
		val statusLine = statusCode.statusLine
		val responseHeader = StringBuilder(statusLine)

		header.forEach {
			responseHeader.append("${it.key}: ${it.value}\r\n")
		}

		responseHeader.append("\r\n")
		return responseHeader.toString()
	}

	private fun writeResponse(outputStream: OutputStream, header: String, response: ByteArray?) {
		outputStream.write(header.toByteArray())
		response?.let { outputStream.write(it) }
		outputStream.flush()
		outputStream.close()
	}
}


fun BufferedReader.readLetter(size: Int): String =
	(1..size)
		.map { read().toChar() }
		.joinToString("")
