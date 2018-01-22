package http;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpHandler;

import entities.Player;
import manager.GameManager;

public class RootHandler implements HttpHandler {

	private GameManager gm;

	public RootHandler(GameManager gm) {
		this.gm = gm;
	}

	@Override
	public void handle(com.sun.net.httpserver.HttpExchange httpExchange) throws IOException {
		httpExchange.getResponseHeaders().add("Content-type", "text/html");

		String response = "";
		// get all players
		for (Player p : gm.getPlayers()) {
			response += p.getName() + "   Getroffen: " + p.getScore() + "/" + gm.getBallCount() + "</br>";
		}
		httpExchange.sendResponseHeaders(200, response.length());

		OutputStream os = httpExchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
