package http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import manager.GameManager;

/**
 * 
 * @author Sebastian Düringer, Hamza Ammar
 * @version 0.1
 * 
 *          The class WebServer creates a http server with a handler for the "/"
 *          context
 */
public class WebServer {

	private GameManager gm;
	private int port;

	public WebServer(GameManager gm, int port) {
		this.gm = gm;
		this.port = port;
	}

	public void startServer() {
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext("/", new RootHandler(gm));
			server.start();
			System.out.println("Webserver started and listening on Port 80...");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
