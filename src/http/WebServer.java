package http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import manager.GameManager;

public class WebServer {

	private GameManager gm;

	public WebServer(GameManager gm) {
		this.gm = gm;
	}

	public void startServer() {
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(80), 0);
			server.createContext("/", new RootHandler(gm));
			server.start();
			System.out.println("Webserver started and listening on Port 80...");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
