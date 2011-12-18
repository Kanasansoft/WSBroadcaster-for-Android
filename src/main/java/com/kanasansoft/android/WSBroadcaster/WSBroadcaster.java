package com.kanasansoft.android.WSBroadcaster;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class WSBroadcaster extends Activity {

	Server server = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startWebSocketServer();
	}

	void startWebSocketServer() {

		server = new Server(40320);

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

	void displayPreferenceValue() {

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		String prefKeyPortNumber              = getString(R.string.preference_key_port_number);
		String prefKeyResponseType            = getString(R.string.preference_key_response_type);
		String prefKeyPeriodicMessage         = getString(R.string.preference_key_periodic_message);
		String prefKeyPeriodicMessageInterval = getString(R.string.preference_key_periodic_message_interval);
		String prefKeyPeriodicMessageText     = getString(R.string.preference_key_periodic_message_text);

		String  defaultValuePortNumber              =                 getString(R.string.default_value_port_number);
		String  defaultValueResponseType            =                 getString(R.string.default_value_response_type);
		boolean defaultValuePeriodicMessage         = Boolean.valueOf(getString(R.string.default_value_periodic_message));
		String  defaultValuePeriodicMessageInterval =                 getString(R.string.default_value_periodic_message_interval);
		String  defaultValuePeriodicMessageText     =                 getString(R.string.default_value_periodic_message_text);

		String currentValuePortNumber              = pref.getString (prefKeyPortNumber,              defaultValuePortNumber);
		String currentValueResponseType            = pref.getString (prefKeyResponseType,            defaultValueResponseType);
		String currentValuePeriodicMessage         = pref.getBoolean(prefKeyPeriodicMessage,         defaultValuePeriodicMessage)?"ON":"OFF";
		String currentValuePeriodicMessageInterval = pref.getString (prefKeyPeriodicMessageInterval, defaultValuePeriodicMessageInterval);
		String currentValuePeriodicMessageText     = pref.getString (prefKeyPeriodicMessageText,     defaultValuePeriodicMessageText);

		TextView displayAreaPortNumber              = (TextView)findViewById(R.id.display_area_port_number);
		TextView displayAreaResponseType            = (TextView)findViewById(R.id.display_area_response_type);
		TextView displayAreaPeriodicMessage         = (TextView)findViewById(R.id.display_area_periodic_message);
		TextView displayAreaPeriodicMessageInterval = (TextView)findViewById(R.id.display_area_periodic_message_interval);
		TextView displayAreaPeriodicMessageText     = (TextView)findViewById(R.id.display_area_periodic_message_text);

		displayAreaPortNumber.setText(currentValuePortNumber);
		displayAreaResponseType.setText(currentValueResponseType);
		displayAreaPeriodicMessage.setText(currentValuePeriodicMessage);
		displayAreaPeriodicMessageInterval.setText(currentValuePeriodicMessageInterval);
		displayAreaPeriodicMessageText.setText(currentValuePeriodicMessageText);

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
	}

}
