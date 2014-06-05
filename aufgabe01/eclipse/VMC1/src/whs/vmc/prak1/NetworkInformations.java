package whs.vmc.prak1;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import whs.vmc.prak1.lib.NetworkAdapter;
import whs.vmc.prak1.lib.Tools;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.vmc1.R;

public class NetworkInformations extends Activity {

	public static final String PREFS_NAME = "whs.vmc.vmc_networkmonitor_preferences";
	public static final String NETWORK_MAP_URL = "";
	private NetworkAdapter networkAdapter;
	private NetPoint currentNetPoint;
	LocationManager locationService;
	private double latitude = 51.573;
	private double longitude = 7.0284;
	private boolean isOpen = true;
	private Button button_speedtest;
	private Dialog progress;
	private DecimalFormat mDecimalFormater;
	public static boolean gps_old = true;
	public static boolean cellloc_old = true;
	public static boolean speed_old = true;
	public Location cellLocation;
	public List<Location> Nachbarzellen = null;
	public Set<NetPoint> Nachbar_Netpoints = null;

	/**
	 * Generates the network information view
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new ServerConnector(this);
		setTitle("VMC Network Information");

		networkAdapter = new NetworkAdapter(this);
		locationService = (LocationManager) getSystemService(LOCATION_SERVICE);
		currentNetPoint = new NetPoint();
		mDecimalFormater = new DecimalFormat("##.##");

		setContentView(R.layout.network_info);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {

				while (isOpen) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					setTableHandler.sendEmptyMessage(0);

				}
			}

		});
		thread.start();

		button_speedtest = (Button) findViewById(R.id.start_speedtest);
		button_speedtest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(getApplicationContext(),
						SpeedtestActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onResume() {

		if (SpeedtestActivity.ergebnis != 0.0) {
			TextView geschwindkeit = (TextView) findViewById(R.id.txtview_geschwindigkeit);
			geschwindkeit.setText(mDecimalFormater
					.format(SpeedtestActivity.ergebnis) + " kB/s");
		}

		super.onResume();

	};

	@Override
	protected void onDestroy() {

		isOpen = false;
		super.onDestroy();
	}
	
	/**
	 * 
	 */
	Handler setTableHandler = new Handler() {
		public void handleMessage(Message msg) {
			String[] item = {"state", "type", "netID", "speed", "interface", "ip", "gateway", "dns", "signalstrength", "cell_type", "cell_location", "cell_id", "phone_type"};
			
			Map<String, String> dataTable = new HashMap<String, String>();
			dataTable.put("date", Tools.getTodaysDate());
			dataTable.put("time", Tools.getTodaysTime());

			for (String s:item)
				dataTable.put(s, networkAdapter.getInformationAbout(s));
			
			dataTable.put("imsi", networkAdapter.getIMSI(getApplicationContext()));
			dataTable.put("mnc", networkAdapter.getMNC(getApplicationContext()));
			dataTable.put("mcc", networkAdapter.getMCC(getApplicationContext()));
			dataTable.put("net_op", networkAdapter.getNetOperatorName(getApplicationContext()));

			TableLayout tl = (TableLayout) findViewById(R.id.maintable);
			Tools.MakeTableLayout(NetworkInformations.this, dataTable, tl,
					"net_info");
		}
	};
}