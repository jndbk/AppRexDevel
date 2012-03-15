package rex.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class AlarmReceiver extends BroadcastReceiver 
{
	private static PowerManager.WakeLock wakeLock = null;
	private static final String LOCK_TAG = "com.manning.aip.portfolio";
	
	/**
	 * Method used to share the <code>WakeLock</code> created by this 
	 * <code>BroadcastReceiver</code>. 
	 * Note: this method is <code>synchronized</code> as it lazily creates
	 * the <code>WakeLock</code>
	 * 
	 * @param 		ctx			The <code>Context</code> object acquiring the
	 * 							lock.
	 */
	public static synchronized void acquireLock(Context ctx){
		if (wakeLock == null){
			PowerManager mgr = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
			wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_TAG);
			wakeLock.setReferenceCounted(true);
		}
		wakeLock.acquire();
	}
	
	/**
	 * Method used to release the shared <code>WakeLock</code>.
	 */
	public static synchronized void releaseLock(){
		if (wakeLock != null){
			wakeLock.release();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		//acquireLock(context);
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		 boolean isScreenOn = pm.isScreenOn();

		 if(isScreenOn)
		 {
			 Intent appsService = new Intent(context, MonitorService.class);
			 context.startService(appsService);
		 }
	}


}
