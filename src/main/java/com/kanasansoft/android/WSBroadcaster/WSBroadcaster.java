package com.kanasansoft.android.WSBroadcaster;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class WSBroadcaster extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startWebSocketServer();
	}

	void startWebSocketServer() {

		Server server = new Server(40320);

		MyWebSocketServlet wss = new MyWebSocketServlet();
		ServletHolder sh = new ServletHolder(wss);
		ServletContextHandler sch = new ServletContextHandler();
		sch.addServlet(sh, "/*");

		HandlerList hl = new HandlerList();
		hl.setHandlers(new Handler[] {sch});
		server.setHandler(hl);

		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
