package whs.vmc.prak1.lib;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.util.InetAddressUtils;

import whs.vmc.prak1.NetworkInformations;

import com.example.vmc1.R;
import com.example.vmc1.R.string;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Debug;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
//import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

/**
 * Der Networkadapter stellt Informationen �ber das Netzwerk und das Telefon
 * bereit.
 * 
 */
public class NetworkAdapter {
	private static final String LOG_TAG = "NetworkMonitor.Tools";
	private final static boolean localLOGV = false;
	
	//TODO: Aufgabe 1a.
	private static final String SPEED_GPRS = "x - 53.6 kbits/s";
	private static final String SPEED_EDGE = "x - 220 kbits/s";
	private static final String SPEED_HSDPA = "x - 42.2 mbits/s";
	private static final String SPEED_HSPA = "x - 168.0 mbits/s";
	private static final String SPEED_HSPA_PLUS = "x - 168.0 mbits/s";
	private static final String SPEED_UMTS = "x - 42.0 mbits/s";

	private static final String CON_GPRS = "GPRS";
	private static final String CON_EDGE = "EDGE";
	private static final String CON_HSDPA = "HSDPA";
	private static final String CON_HSPA = "HSPA";
	private static final String CON_HSPA_PLUS = "HSPA+";
	private static final String CON_UMTS = "UMTS";

	private static final String PHONE_TYPE_NONE = "None";
	private static final String PHONE_TYPE_GSM = "GSM";
	private static final String PHONE_TYPE_CDMA = "CDMA";

	public static Map<String, String> netMap = new HashMap<String, String>();
	private static Map<Integer, String> phoneType = new HashMap<Integer, String>();
	private static Map<Integer, String> networkType = new HashMap<Integer, String>();
	private boolean netExists = false;
	private String ipv4;
	private Context mContext;
	private String CELL_COMPERATOR = "0";
	/**
	 * Enthaelt eine List mit den Nachbarzellen von der aktuellen Funkzelle.
	 */
	public List<NeighboringCellInfo> nachbarzellen = null;

	public NetworkAdapter(Context context) {
		// Initialise some mappings
		phoneType.put(0, PHONE_TYPE_NONE);
		phoneType.put(1, PHONE_TYPE_GSM);
		phoneType.put(2, PHONE_TYPE_CDMA);

		networkType.put(0, "Unknown");
		networkType.put(1, "GPRS");
		networkType.put(2, "EDGE");
		networkType.put(3, "UMTS");
		networkType.put(4, "CDMA");
		networkType.put(5, "EVDO_0");
		networkType.put(6, "EVDO_A");
		networkType.put(7, "1xRTT");
		networkType.put(8, "HSDPA");
		networkType.put(9, "HSUPA");
		networkType.put(10, "HSPA");
		networkType.put(11, "IDEN");

		mContext = context;
		resetNetmap(mContext);
		setCellValues(mContext);
		CELL_COMPERATOR = netMap.get("cell_id");
		setNetworkValues(mContext);

		class MyPhoneStateListener extends PhoneStateListener {
			public int singalStenths = 0;

			@Override
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {

				super.onSignalStrengthsChanged(signalStrength);
				int singalStrength = signalStrength.getGsmSignalStrength();

				singalStenths = (int) ((100.0 / 31.0) * singalStrength);

				resetNetmap(mContext);
				setCellValues(mContext);
				setNetworkValues(mContext);
				netMap.put("signalstrength", singalStenths + "%");
				if (CELL_COMPERATOR.compareTo(netMap.get("cell_id")) != 0) {
					NetworkInformations.speed_old = true;
					NetworkInformations.cellloc_old = true;
					NetworkInformations.gps_old = true;
					CELL_COMPERATOR = netMap.get("cell_id");
				}
			}
		}
		;

		PhoneStateListener myListener = new MyPhoneStateListener();
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		telManager.listen(myListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	private void resetNetmap(Context mContext2) {

		netMap = new HashMap<String, String>();
		// Initialise the network information mapping
		netMap.put("state", "");
		netMap.put("interface", "");
		netMap.put("type", "");
		netMap.put("netID", "");
		// netMap.put("speed", "");
		netMap.put("signalstrength", "0");
		netMap.put("ip", "");
		// netMap.put("gateway", "");
		// netMap.put("dns", "");
		netMap.put("cell_location", "n/a");
		netMap.put("cell_type", "n/a");
		netMap.put("cell_id", "n/a");
		netMap.put("cell_lac", "n/a");
		netMap.put("cell_mnc", "n/a");
		netMap.put("cell_mcc", "n/a");
		netMap.put("Phone_type", "n/a");
	}

	private void setNetworkValues(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = (NetworkInfo) cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected()) {
			if (localLOGV)
				Log.v(LOG_TAG, "Network is connected");

			netExists = true;
			netMap.put("state", context.getString(R.string.connected));
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			NetworkInterface intf = getInternetInterface();
			netMap.put("interface", intf.getName());
			String ip = (getIPAddress(intf).compareTo("") != 0) ? getIPAddress(intf)
					: getLocalIpAddress();
			netMap.put("ip", ip);
			String type = (String) ni.getTypeName();

			if (wifi.isWifiEnabled()) {

				netMap.put("type", context.getString(R.string.net_type_wifi));
				WifiInfo wi = wifi.getConnectionInfo();
				netMap.put("netID", wi.getSSID());
				netMap.put("speed", Integer.toString(wi.getLinkSpeed())
						+ "Mbit/s");
				// netMap.put("gateway", Tools.mySystem("/system/bin/getprop",
				// "dhcp." + intf.getName() + ".gateway", "").trim());
			} else if (type.equals("mobile") || type.equals("MOBILE")) {

				netMap.put("type", context.getString(R.string.net_type_mobile));
				netMap.put("netID", ni.getExtraInfo());
				TelephonyManager teleMan = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				int networkType = teleMan.getNetworkType();
				String ntype = getNetworkType(networkType);
				String speed = "N/A";
				// System.out.println("mylog mynet "+ntype);
				if (ntype.compareTo(CON_GPRS) == 0) {
					speed = SPEED_GPRS;
				} else if (ntype.compareTo(CON_EDGE) == 0) {
					speed = SPEED_EDGE;
				}
				if (ntype.compareTo(CON_HSDPA) == 0) {
					speed = SPEED_HSDPA;
				}
				if (ntype.compareTo(CON_HSPA) == 0) {
					speed = SPEED_HSPA;
				}
				if (ntype.compareTo(CON_HSPA_PLUS) == 0) {
					speed = SPEED_HSPA_PLUS;
				}
				if (ntype.compareTo(CON_UMTS) == 0) {
					speed = SPEED_UMTS;
				}
				netMap.put("speed", speed);
				netMap.put("cell_type", ntype);

			} else {
				// Unsupported network type
				if (localLOGV)
					Log.v(LOG_TAG, "Unknown/unsupported network type");
				netMap.put(
						"type",
						type
								+ " "
								+ context
										.getString(R.string.net_type_unsupported));
			}
			// netMap.put("dns", Tools.mySystem("/system/bin/getprop",
			// "net.dns1", "").trim());
		} else {
			netMap.put("state", context.getString(R.string.not_connected));
			netMap.put("dns", "");
		}
	}

	private void setCellValues(Context context) {

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		if (tm != null && tm.getCellLocation() != null) {
			netMap.put("data_activity", Integer.toString(tm.getDataActivity()));
			netMap.put("cell_location", tm.getCellLocation().toString());
			netMap.put("cell_type", getNetworkType(tm.getNetworkType()));
			netMap.put("cell_id", getCID(context) + "");
			netMap.put("cell_lac", getLAC(context) + "");
			netMap.put("cell_mnc", getMNC(context) + "");
			netMap.put("cell_mcc", getMCC(context) + "");
			netMap.put("phone_type", getPhoneType(tm.getPhoneType()));
			Log.v("networkadapter", tm.getCellLocation().toString());

			// Nachbarzellen ermitteln
			// int zellen=1;
//			nachbarzellen = tm.getNeighboringCellInfo();

		} else {
			Toast.makeText(context, "No SIM-Card", Toast.LENGTH_LONG).show();
		}
	}


	/**
	 * Return a string representation of the phone type given the integer
	 * returned from TelephonyManager.getPhoneType()
	 * 
	 * @param key
	 *            key for the info required
	 * @return string information relating to the key
	 */
	public String getPhoneType(Integer key) {
		if (phoneType.containsKey(key)) {
			return phoneType.get(key);
		} else {
			return "unknown";
		}
	}

	/**
	 * Return a string representation of the network type type given the integer
	 * returned from TelephonyManager.getNetworkType()
	 * 
	 * @param key
	 *            key for the info required
	 * @return string information relating to the key
	 */
	public String getNetworkType(Integer key) {
		if (networkType.containsKey(key)) {
			return networkType.get(key);
		} else {
			return "unknown";
		}
	}

	/**
	 * Return information relating to a key
	 * 
	 * @param key
	 *            key for the info required
	 * @return string information relating to the key
	 */
	public String getInformationAbout(String key) {
		return exists(key) ? netMap.get(key) : "";
	}

	/**
	 * Returns if this key exists in the HashMap
	 * 
	 * @param key
	 *            key to look for
	 * @return boolean exits or not
	 */
	public boolean exists(String key) {
		return netMap.containsKey(key);
	}

	/**
	 * Returns the network state
	 * 
	 * @return boolean connected or not connected
	 */
	public boolean isConnected() {
		return netExists;
	}

	/**
	 * Returns the IP address of the supplied network interface
	 * 
	 * @param intf
	 *            interface to check
	 * @return dotted quad IP address
	 */
	private static String getIPAddress(NetworkInterface intf) {
		String result = "";
		for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
				.hasMoreElements();) {
			InetAddress inetAddress = enumIpAddr.nextElement();
			result = inetAddress.getHostAddress();
		}
		return result;
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					// System.out.println("ip1--:" + inetAddress);
					// System.out.println("ip2--:" +
					// inetAddress.getHostAddress());

					// for getting IPV4 format
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils
									.isIPv4Address(ipv4 = inetAddress
											.getHostAddress())) {

						String ip = inetAddress.getHostAddress().toString();
						System.out.println("ip---::" + ip);

						return ipv4;
					}
				}
			}
		} catch (Exception ex) {
			Log.e("IP Address", ex.toString());
		}
		return null;
	}

	/**
	 * Gibt das erste Netzwerk Interface zuruck das gefunden wird und nicht
	 * Localhost ist.
	 * 
	 * @return erste Interface das nicht Localhost ist.
	 */
	private static NetworkInterface getInternetInterface() {
		
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				if (!intf.equals(NetworkInterface.getByName("lo"))) {
					return intf;
				}
			}
		} catch (SocketException ex) {
			Log.w(LOG_TAG, ex.toString());
		}
		return null;
	}

	/**
	 * Gibt die CellId zurueck.<br>
	 * <b>Bitte implementieren</b>
	 * 
	 * @param ctx
	 *            Context
	 * @return CellID als int
	 */
	public int getCID(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();
		return loc.getCid();
	}

	/**
	 * Gibt die LAC zurueck.<br>
	 * <b>Bitte implementieren</b>
	 * 
	 * @param ctx
	 *            Context
	 * @return LAC als int
	 */
	public int getLAC(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();
		return loc.getLac();
	}

	/**
	 * Gibt den MCC (Mobile Country Code) zurueck.<br>
	 * <b>Bitte implementieren</b>
	 * 
	 * @param context
	 *            Context
	 * @return MCC als String
	 */
	public String getMCC(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String no = tm.getNetworkOperator();
		return no.substring(0, 3);
	}

	/**
	 * Gibt den MNC (Mobile Network Code) zurueck.<br>
	 * <b>Bitte implementieren</b>
	 * 
	 * @param context
	 *            Context
	 * @return MNC als String
	 */
	public String getMNC(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String no = tm.getNetworkOperator();
		return no.substring(3);

	}

	/**
	 * Gibt die IMSI zurueck.<br>
	 * <b>Bitte implementieren</b>
	 * 
	 * @param context
	 *            Context
	 * @return IMSI als String
	 */
	public String getIMSI(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		return imsi;

	}

	/**
	 * Gibt den NetzwerkOperator zur�ck.<br>
	 * <b>Bitte implementieren</b>
	 * 
	 * @param ctx
	 *            Context
	 * @return NetzwerkOperator als String
	 */
	public String getNetOperatorName(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkOperatorName();

	}

}
