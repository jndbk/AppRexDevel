package rex.login;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAppList {
	private View myappslist;
	    static int num = 0;
		public MyAppList(ViewGroup parent, String appName, long timeLastPlayed, String icon, Activity act) {
			setMyappslist(act.getLayoutInflater().inflate(R.layout.myapps1, null));
			TextView tv = (TextView) myappslist.findViewById(R.id.atpd);
			tv.setText(Integer.toString(num + 10));
			++num;
			
	}

		public View getMyappslist() {
			return myappslist;
		}

		public void setMyappslist(View myappslist) {
			this.myappslist = myappslist;
		}
}
