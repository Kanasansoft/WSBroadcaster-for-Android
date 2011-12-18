package com.kanasansoft.android.WSBroadcaster;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WSBroadcaster extends Activity implements Listener, OnClickListener, MyWebSocket.Listener {

	String preferenceKeyPortNumber              = null;
	String preferenceKeyResponseType            = null;
	String preferenceKeyPeriodicMessage         = null;
	String preferenceKeyPeriodicMessageInterval = null;
	String preferenceKeyPeriodicMessageText     = null;

	Server server = null;

	private static Set<MyWebSocket> members_ = new CopyOnWriteArraySet<MyWebSocket>();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		preferenceKeyPortNumber              = getString(R.string.preference_key_port_number);
		preferenceKeyResponseType            = getString(R.string.preference_key_response_type);
		preferenceKeyPeriodicMessage         = getString(R.string.preference_key_periodic_message);
		preferenceKeyPeriodicMessageInterval = getString(R.string.preference_key_periodic_message_interval);
		preferenceKeyPeriodicMessageText     = getString(R.string.preference_key_periodic_message_text);

		Button buttonStartStop = (Button)findViewById(R.id.button_start_stop);
		buttonStartStop.setOnClickListener(this);

		displayServerStatus();
		displayPreferenceValue();

	}

	void startWebSocketServer() {

		server = new Server(40320);

		server.addLifeCycleListener(this);

		MyWebSocketServlet wss = new MyWebSocketServlet(this);
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

	void stopWebSocketServer() {

		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Bundle getPreferenceData() {

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		String  defaultValuePortNumber              =                 getString(R.string.default_value_port_number);
		String  defaultValueResponseType            =                 getString(R.string.default_value_response_type);
		boolean defaultValuePeriodicMessage         = Boolean.valueOf(getString(R.string.default_value_periodic_message));
		String  defaultValuePeriodicMessageInterval =                 getString(R.string.default_value_periodic_message_interval);
		String  defaultValuePeriodicMessageText     =                 getString(R.string.default_value_periodic_message_text);

		int     currentValuePortNumber              = Integer.valueOf(pref.getString (preferenceKeyPortNumber,              defaultValuePortNumber), 10);
		String  currentValueResponseType            =                 pref.getString (preferenceKeyResponseType,            defaultValueResponseType);
		boolean currentValuePeriodicMessage         =                 pref.getBoolean(preferenceKeyPeriodicMessage,         defaultValuePeriodicMessage);
		int     currentValuePeriodicMessageInterval = Integer.valueOf(pref.getString (preferenceKeyPeriodicMessageInterval, defaultValuePeriodicMessageInterval), 10);
		String  currentValuePeriodicMessageText     =                 pref.getString (preferenceKeyPeriodicMessageText,     defaultValuePeriodicMessageText);

		Bundle bundle = new Bundle();

		bundle.putInt(preferenceKeyPortNumber,              currentValuePortNumber);
		bundle.putString(preferenceKeyResponseType,         currentValueResponseType);
		bundle.putBoolean(preferenceKeyPeriodicMessage,     currentValuePeriodicMessage);
		bundle.putInt(preferenceKeyPeriodicMessageInterval, currentValuePeriodicMessageInterval);
		bundle.putString(preferenceKeyPeriodicMessageText,  currentValuePeriodicMessageText);

		return bundle;

	}

	void displayServerStatus() {

		String currentValueServerStatus = Server.STOPPED;
		if (server != null) {
			currentValueServerStatus = server.getState();
		}

		Button buttonStartStop = (Button)findViewById(R.id.button_start_stop);

		if (currentValueServerStatus.equals(Server.STOPPED)) {
			buttonStartStop.setText(getString(R.string.button_text_start));
		} else {
			buttonStartStop.setText(getString(R.string.button_text_stop));
		}

		TextView displayAreaServerStatus = (TextView)findViewById(R.id.display_area_server_status);

		displayAreaServerStatus.setText(currentValueServerStatus);

	}

	void displayPreferenceValue() {

		Bundle prefData = getPreferenceData();

		TextView displayAreaPortNumber              = (TextView)findViewById(R.id.display_area_port_number);
		TextView displayAreaResponseType            = (TextView)findViewById(R.id.display_area_response_type);
		TextView displayAreaPeriodicMessage         = (TextView)findViewById(R.id.display_area_periodic_message);
		TextView displayAreaPeriodicMessageInterval = (TextView)findViewById(R.id.display_area_periodic_message_interval);
		TextView displayAreaPeriodicMessageText     = (TextView)findViewById(R.id.display_area_periodic_message_text);

		displayAreaPortNumber.setText             (String.valueOf(prefData.getInt    (preferenceKeyPortNumber)));
		displayAreaResponseType.setText           (               prefData.getString (preferenceKeyResponseType));
		displayAreaPeriodicMessage.setText        (               prefData.getBoolean(preferenceKeyPeriodicMessage)?"ON":"OFF");
		displayAreaPeriodicMessageInterval.setText(String.valueOf(prefData.getInt    (preferenceKeyPeriodicMessageInterval)));
		displayAreaPeriodicMessageText.setText    (               prefData.getString (preferenceKeyPeriodicMessageText));

	}

	public void broadcast(String data) {
		for(MyWebSocket member : members_) {
			try {
				member.getConnection().sendMessage(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void broadcast(byte[] data, int offset, int length) {
		for(MyWebSocket member : members_) {
			try {
				member.getConnection().sendMessage(data, offset, length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.optionmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.optionmenu_preferences:
			startActivityForResult(new Intent(this,MyPreferenceActivity.class), 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		displayPreferenceValue();
	}

	public void lifeCycleFailure(LifeCycle event, Throwable cause) {
	}

	public void lifeCycleStarted(LifeCycle event) {
		displayServerStatus();
	}

	public void lifeCycleStarting(LifeCycle event) {
		displayServerStatus();
	}

	public void lifeCycleStopped(LifeCycle event) {
		displayServerStatus();
	}

	public void lifeCycleStopping(LifeCycle event) {
		displayServerStatus();
	}

	public void onClick(View view) {

		String currentValueServerStatus = Server.STOPPED;
		if (server != null) {
			currentValueServerStatus = server.getState();
		}

		if (currentValueServerStatus.equals(Server.STOPPED)) {
			startWebSocketServer();
		} else {
			stopWebSocketServer();
		}

	}

	public void onOpen(MyWebSocket myWebSocket) {
		members_.add(myWebSocket);
	}

	public void onClose(MyWebSocket myWebSocket, int closeCode, String message) {
		members_.remove(myWebSocket);
	}

	public void onMessage(MyWebSocket myWebSocket, String data) {
		broadcast(data);
	}

	public void onMessage(MyWebSocket myWebSocket, byte[] data, int offset, int length) {
		broadcast(data, offset, length);
	}

}
