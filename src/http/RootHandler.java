package http;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpHandler;

import entities.Player;
import manager.GameManager;

/**
 * 
 * @author Sebastian Düringer, Hamza Ammar
 * @version 0.1
 * 
 *          The class RootHandler handels the incoming http requests for the "/"
 *          context
 */
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

		// add ticks per second
		response += "Ticks per second: " + gm.getTicksPerSecond();
		httpExchange.sendResponseHeaders(200, response.length());

		OutputStream os = httpExchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
