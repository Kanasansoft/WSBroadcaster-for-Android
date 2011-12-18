package com.kanasansoft.android.WSBroadcaster;

import org.eclipse.jetty.websocket.WebSocket;

public class MyWebSocket implements WebSocket.OnTextMessage, WebSocket.OnBinaryMessage {

	private Listener listener_ = null;
	private Connection connection_ = null;

	public MyWebSocket(Listener listener) {
		listener_ = listener;
	}

	public void onOpen(Connection connection) {
		connection_ = connection;
		listener_.onOpen(this);
	}

	public void onClose(int closeCode, String message) {
		listener_.onClose(this, closeCode, message);
	}

	public void onMessage(String data) {
		listener_.onMessage(this, data);
	}

	public void onMessage(byte[] data, int offset, int length) {
		listener_.onMessage(this, data, offset, length);
	}

	Connection getConnection() {
		return connection_;
	}

	public interface Listener {
		void onOpen(MyWebSocket myWebSocket);
		void onClose(MyWebSocket myWebSocket, int closeCode, String message);
		void onMessage(MyWebSocket myWebSocket, String data);
		void onMessage(MyWebSocket myWebSocket, byte[] data, int offset, int length);
	}

}
