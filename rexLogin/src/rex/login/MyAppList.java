package rex.login;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAppList {
	private View myapps;
		public MyAppList(String appName, long timeLastPlayed, String icon, Activity act) {
			setMyappslist(act.getLayoutInflater().inflate(R.layout.myapps, null));
			TextView tv = (TextView) myapps.findViewById(R.id.appName);
			tv.setText(appName);
			if(!icon.contentEquals("na"))
			{
			    ImageView iv = (ImageView) myapps.findViewById(R.id.icon1);
			    Drawable d = Drawable.createFromPath(icon);
			    iv.setImageDrawable(d);
			}
			String asString = "";
			if(timeLastPlayed != 0)
			{
			    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
			    asString = formatter.format(timeLastPlayed);
			}
			tv = (TextView) myapps.findViewById(R.id.atpd);
			tv.setText(asString);
	}

		public View getMyappslist() {
			return myapps;
		}

		public void setMyappslist(View myappslist) {
			this.myapps = myappslist;
		}

}
