package whs.vmc.prak1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import whs.vmc.prak1.lib.MyCellIDRequestEntity;

import android.content.Context;
import android.location.Location;

public class ServerConnector {

	public static final String API_KEY = "AIzaSyA1xfm54a_ZOgxHzPtKTmy9EY1bWh6MWew";

	public static final String SERVER_PATH = "http://mytestftp.bplaced.net/server/json/";

	public static final String API_URL = "https://www.googleapis.com/geolocation/v1/geolocate?key="
			+ API_KEY;

	private static Context mContext;

	public ServerConnector(Context context) {

		mContext = context;

	}

	/**
	 * Laed den uebergebenen NetPoint hoch.
	 * 
	 * @param currentNetPoint Der Hochzuladende NetPoint
	 * @return
	 */
	public static boolean uploadNetPoint(NetPoint currentNetPoint) {
		
		boolean erfolgreich = false;
		// Next block of code needs to be surrounded by try/catch block for
		// it to work
		String url = "";

		try {
			url = SERVER_PATH
					+ "?uploadnetpoint"
					+ "&time"
					+ "="
					+ URLEncoder.encode(currentNetPoint.time, "UTF-8")
					+ "&date"
					+ "="
					+ URLEncoder.encode(currentNetPoint.date, "UTF-8")
					+ "&network_state"
					+ "="
					+ URLEncoder.encode(currentNetPoint.networkState, "UTF-8")
					+ "&network_type"
					+ "="
					+ URLEncoder.encode(currentNetPoint.networkType, "UTF-8")
					+ "&network_id"
					+ "="
					+ URLEncoder.encode(currentNetPoint.networkID, "UTF-8")
					+ "&network_speed"
					+ "="
					+ URLEncoder.encode(currentNetPoint.networkSpeed, "UTF-8")
					+ "&gsm_signalstrength"
					+ "="
					+ URLEncoder.encode(
							currentNetPoint.gsmSignalstrength.replace("%", ""),
							"UTF-8")
					+ "&network_interface"
					+ "="
					+ URLEncoder.encode(currentNetPoint.networkInterface,
							"UTF-8")
					+ "&ip_adress"
					+ "="
					+ URLEncoder.encode(currentNetPoint.ipadress, "UTF-8")
					+ "&connection_type"
					+ "="
					+ URLEncoder
							.encode(currentNetPoint.connectionType, "UTF-8")
					+ "&anonym_id"
					+ "=" 
					+ URLEncoder.encode(
							currentNetPoint.anonym_id)
					+ "&cell_id"
					+ "="
					+ URLEncoder.encode(currentNetPoint.cellId, "UTF-8")
					+ "&phone_radio"
					+ "="
					+ URLEncoder.encode(currentNetPoint.phoneRadio, "UTF-8")
					+ "&speedtest"
					+ "="
					+ URLEncoder.encode(currentNetPoint.speedtest, "UTF-8")
					+ "&gps_location"
					+ "="
					+ URLEncoder.encode(
							currentNetPoint.gpsLocation.replace("[", "")
									.replace("]", ""), "UTF-8")
					+ "&google_location"
					+ "="
					+ URLEncoder
							.encode(currentNetPoint.googleLocation, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}

		try {
			HttpGet httpget;
			httpget = new HttpGet(url);

			System.out.println("mylog url " + url);
			HttpResponse responseGet = null;
			DefaultHttpClient httpClient = new DefaultHttpClient();
			responseGet = httpClient.execute(httpget);
			erfolgreich = true;
			StatusLine statusLine = responseGet.getStatusLine();

		} catch (ClientProtocolException e1) {
			
			e1.printStackTrace();
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}

		return erfolgreich;
		

	}

	/**
	 * Gibt die GPS Location des Funkmastes über die Hiden-API von Google 
	 * zurueck.
	 * @param lac Location Area Code
	 * @param cid Cell id
	 * 
	 * @return GPS Position als Location
	 */
	public static Location getGoogleCellID(int lac, int cid) {
		// lac= 6015;
		// cid = 20442;
		
		int shortcid = cid & 0xffff;
		Location location = new Location("");
		try {
			String surl = "http://www.google.com/glm/mmap";

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(surl);
			httppost.setEntity(new MyCellIDRequestEntity(shortcid, lac));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			DataInputStream dis = new DataInputStream(entity.getContent());

			// Read some prior data
			dis.readShort();
			dis.readByte();
			// Read the error-code
			int errorCode = dis.readInt();
			if (errorCode == 0) {
				location.setLatitude((double) dis.readInt() / 1000000D);
				location.setLongitude((double) dis.readInt() / 1000000D);

			} else {
				location.setLatitude(0);
				location.setLongitude(0);
			}
		} catch (Exception e) {
		}
		return location;
	}

	/**
	 * Gibt die GPS Location des Funkmastes über OpenCellID zurueck.
	 * 
	 * @param lac Location Area Code
	 * @param cellid Cell id
	 * 
	 * @return GPS Position als Location
	 */
	public static Location getOpenCellID(int lac, int cellid, int mnc, int mcc) {
		Location location = new Location("");
		boolean error = false;
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(prepareURLSent(lac, cellid, mnc, mcc));
		HttpResponse response;
		try {
			response = client.execute(request);
			String result = EntityUtils.toString(response.getEntity());
			if (result.equalsIgnoreCase("err")) {
				error = true;
				location.setLatitude(0);
				location.setLongitude(0);
			} else {
				error = false;
				String[] tResult = result.split(",");
				location.setLatitude((double) Double.parseDouble(tResult[0]));
				location.setLongitude((double) Double.parseDouble(tResult[1]));
			}
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return location;
	}

	public static String prepareURLSent(int lac, int cellid, int mnc, int mcc) {
		String strURLSent = "http://www.opencellid.org/cell/get?mcc=" + mcc
				+ "&mnc=" + mnc + "&cellid=" + cellid + "&lac=" + lac
				+ "&fmt=txt";
		System.out.println("mylog cellloc " + strURLSent);
		return strURLSent;
		
	}
}
