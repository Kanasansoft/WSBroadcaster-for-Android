package com.kanasansoft.android.WSBroadcaster;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class MyWebSocketServlet extends WebSocketServlet {

	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		return new MyWebSocket();
	}

}
