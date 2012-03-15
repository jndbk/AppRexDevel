package rex.login;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AppsStartupReceiver extends BroadcastReceiver {
	private static final int FIFTEEN_MINUTES = 10*1000;
//	private static final int FIFTEEN_MINUTES = 120*1000;

	// Uncomment this constant to use C2DM and insert the email address that
	// you use to submit your apps to the Android Market
	//private static final String DEVELOPER_EMAIL_ADDRESS = "";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		// Begin AlarmManager code. Delete this to use C2DM
		AlarmManager mgr = 
			(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, 
				i, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, 2);
		mgr.setRepeating(AlarmManager.RTC, 
				now.getTimeInMillis(), FIFTEEN_MINUTES, sender);
		// End AlarmManager code
		
		// Uncomment out the following code to use C2DM
//		Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
//		registrationIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0)); // boilerplate
//		registrationIntent.putExtra("sender", DEVELOPER_EMAIL_ADDRESS);
//		context.startService(registrationIntent);
	}
}