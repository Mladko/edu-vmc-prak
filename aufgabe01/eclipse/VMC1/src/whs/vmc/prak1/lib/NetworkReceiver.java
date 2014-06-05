package whs.vmc.prak1.lib;

import whs.vmc.prak1.NetworkInformations;

import com.example.vmc1.R;
import com.example.vmc1.R.drawable;
import com.example.vmc1.R.string;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {
	public static final String PREFS_NAME = "com.example.vmc_networkmonitor_preferences";
    
	/** Called when the broadcast is received. */
	public void onReceive(Context context, Intent intent) {
     
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE );

        // Get the network information
        NetworkAdapter ni = new NetworkAdapter(context);                  
        
        String tickerText = context.getString(R.string.not_connected);
        if( ni.isConnected()) {
        	tickerText = context.getString(R.string.connected) + " " + ni.getInformationAbout("type");;
        	if( ni.exists("netID")) {
        		tickerText = tickerText + " " + ni.getInformationAbout("netID");
        	}
        	if( ni.exists("speed")) {
        		tickerText = tickerText + " " + ni.getInformationAbout("speed");
        	}
        }
       
      
  
	}


	
	/**
	* Show notification using notifications
 	* 
	* @param context	the context we're working with
	* @param message	the message to display
	* @return 			Nothing
	*/
    protected void showNotification(Context context, CharSequence message) {    
        NotificationManager nm;
        final int NM_ID = 1;
        
      
    	nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    	long when = System.currentTimeMillis();
    	Notification notifyDetails = new Notification(R.drawable.netmon_notify ,message, when);    
    	    	
    	Intent ShowInfoIntent  = new Intent(context, NetworkInformations.class);    	
    	PendingIntent myPendingIntent = PendingIntent.getActivity(context, 0, ShowInfoIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);    	

    	notifyDetails.setLatestEventInfo(context, context.getText(R.string.net_connectivity_changed), context.getText(R.string.more_info), myPendingIntent);
    	notifyDetails.flags |= Notification.FLAG_AUTO_CANCEL;
    	nm.notify(NM_ID, notifyDetails);    	    		
    }
    
    // Helper to inflate the toast "view"
    private View inflateView(Context context, int resource) {
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return vi.inflate(resource, null);
    }
  
}