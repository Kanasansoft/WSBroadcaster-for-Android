package com.kanasansoft.android.WSBroadcaster;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import com.kanasansoft.android.WSBroadcaster.MyWebSocket.Listener;

public class MyWebSocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = 1L;

	private Listener listener_ = null;

	public MyWebSocketServlet(Listener listener) {
		listener_ = listener;
	}

	public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
		return new MyWebSocket(listener_);
	}

}
