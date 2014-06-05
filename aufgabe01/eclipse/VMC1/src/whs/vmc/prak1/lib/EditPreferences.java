
package whs.vmc.prak1.lib;

import com.example.vmc1.R;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;


public class EditPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String LOG_TAG = "NetworkMonitor.EditPreferences";
	private final static boolean localLOGV = false;
	
	/** 
	 * Expand the xml preferences view
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if(localLOGV) Log.v(LOG_TAG, "Generating preferences dialog");       
		addPreferencesFromResource(R.xml.preferences);
	}
	
	/**
	 * Enable an onchange listener
	 */
    @Override
    protected void onResume() {
        super.onResume();
        if(localLOGV) Log.v(LOG_TAG, "On Resume");
        
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
    /**
     * Disable the onchange listener
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
    
    /**
     * OnChange listener
     * @param settings	shared preferences object
     * @param key		which item was chose
     */	
	public void onSharedPreferenceChanged(SharedPreferences settings, String key) {
		if(localLOGV) Log.v(LOG_TAG, "SharedPreferenceChanged,  Key: " + key);		
       
		if( key.equals("preference_monitor") ) {
			if(localLOGV) Log.v(LOG_TAG, "  Matched preference_monitor");
			PackageManager pm = getPackageManager();
			ComponentName cn = new ComponentName(getApplicationContext(), NetworkReceiver.class);
			if( settings.getBoolean(key,true)) {
				if(localLOGV) Log.v(LOG_TAG, "  Key is true");
				pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);				
			} else {
				if(localLOGV) Log.v(LOG_TAG, "  Key is false");
				pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);				
			}			
		}
		if( key.equals("preference_monitor_type")) {
			if(localLOGV) Log.v(LOG_TAG, "  Matched preference_monitor_type: " + settings.getString(key, "default"));			
		}
		if( key.equals("preference_log")) {
			if(localLOGV) Log.v(LOG_TAG, "  Matched preference_log: " + settings.getBoolean(key, true));
		}
	
		if( key.equals("preference_ping_host")) {
			if(localLOGV) Log.v(LOG_TAG, "  Matched preference_ping_host" + settings.getString(key, "default"));
		}				
	}
}
