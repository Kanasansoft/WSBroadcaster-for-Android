package com.kanasansoft.android.WSBroadcaster;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.websocket.WebSocket;

public class MyWebSocket implements WebSocket.OnTextMessage, WebSocket.OnBinaryMessage {

	private Connection connection_;
	private static Set<MyWebSocket> members_ = new CopyOnWriteArraySet<MyWebSocket>();

	public void onOpen(Connection connection) {
		connection_ = connection;
		members_.add(this);
	}

	public void onClose(int closeCode, String message) {
		members_.remove(this);
	}

	public void onMessage(String data) {
		for(MyWebSocket member : members_) {
			try {
				member.connection_.sendMessage(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void onMessage(byte[] data, int offset, int length) {
		for(MyWebSocket member : members_) {
			try {
				member.connection_.sendMessage(data, offset, length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
