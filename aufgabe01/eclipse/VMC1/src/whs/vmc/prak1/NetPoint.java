package whs.vmc.prak1;

import android.location.Location;

/**
 * NetPoint repraesentiert einen Netzpunkt, welcher speater in der Map angezeigt
 * werden soll.
 * 
 */
public class NetPoint {

	// HH:MM:SS /24h
	public String time = "";
	public String networkState = "";
	// Mobile / WLAN
	public String networkType = "";
	// z.B. internet.eplus.de
	public String networkID = "";
	// Speed-Konstanten SPEED_UMTS...
	public String networkSpeed = "";
	// 0-100
	public String gsmSignalstrength = "";
	public String networkInterface = "";
	public String ipadress = "0.0.0.0";
	// Connection-Konstanten CON_UMTS
	public String connectionType = "";
	public String cellLocation = "0,0,0";
	public String cellId = "0";
	public String mnc = "0";
	public String mcc = "0";
	public String areacode = "";
	public String phoneRadio = "";
	public String date = "";

	public String speedtest = "0";
	public String gpsLocation = "";
	public String googleLocation = "";
	public String anonym_id = "myid";
	public Location funkmast = null;

	public NetPoint(String date, String time, String networkType,
			String networkState, String networkID, String networkSpeed,
			String gsmSignalstrength, String networkInterface, String ipadress,
			String connectionType, String cellLocation, String cellId,
			String phoneRadio, String areacode) {
		super();
		this.date = date;
		this.time = time;
		this.networkState = networkState;
		this.networkType = networkType;
		this.networkID = networkID;
		this.networkSpeed = networkSpeed;
		this.gsmSignalstrength = gsmSignalstrength;
		this.networkInterface = networkInterface;
		this.ipadress = ipadress;
		this.connectionType = connectionType;
		this.cellLocation = cellLocation;
		this.cellId = cellId;
		this.phoneRadio = phoneRadio;
		this.areacode = areacode;

	}

	public NetPoint() {

	}

	@Override
	public boolean equals(Object o) {
		NetPoint anderer_Point = (NetPoint) o;

		if (anderer_Point.cellId == this.cellId
				&& anderer_Point.areacode == this.areacode
				&& anderer_Point.mcc == this.mcc
				&& anderer_Point.mnc == this.mnc) {
			return true;
		} else {
			return false;
		}

	}

}
