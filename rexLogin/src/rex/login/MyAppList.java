package rex.login;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

public class MyAppList {
	private View myappslist;
	
		public MyAppList(String appName, long timeLastPlayed, String icon, Activity act) {
			myappslist = (View) act.findViewById(R.id.myappslist);
	}
}
