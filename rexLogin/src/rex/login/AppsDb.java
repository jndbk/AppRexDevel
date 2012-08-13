package rex.login;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A data access object for persisting and retrieving stock data. This uses a
 * SQLite database for persistence and retrieval.
 * 
 * @author Michael Galpin
 * 
 */
public class AppsDb
{
    // database metadata
    private static final String DB_NAME = "apps.db";
    private static final int DB_VERSION = 14;

    private static final String PTABLE_NAME = "processes";
    private static final String CTABLE_NAME = "categories";

    // processes column names
    private static final String APPNAME = "appname";
    private static final String CATEGORY = "category";
    private static final String ICON = "icon";
    private static final String START_TIME = "start_time";
    private static final String STOP_TIME = "stop_time";
    private static final String PACKAGENAME = "package";

    // SQL statements
    private static final String CREATE_PTABLE = "CREATE TABLE " + PTABLE_NAME
            + " (ID INTEGER PRIMARY KEY, " + PACKAGENAME + " TEXT, " + START_TIME 
            + " INTEGER, " + STOP_TIME + " INTEGER)";
    private static final String CREATE_CTABLE = "CREATE TABLE " + CTABLE_NAME
            + " (" + PACKAGENAME + " TEXT PRIMARY KEY, " + APPNAME + " TEXT, " + CATEGORY + " TEXT, " + ICON + " TEXT)";
    private static final String CREATE_ID_TABLE = "create table currentid(id integer primary key)";

    // The Context object that created this AppsDb
    private final Context context;
    private final SQLiteOpenHelper helper;
    private final SQLiteDatabase db;

    /**
     * Constructor that takes a <code>Context</code> object, usually the
     * <code>Service</code> or <code>Activity</code> that created this instance.
     * This will initialize the SQLiteOpenHelper used for the database, and
     * pre-compile the insert and update SQL statements.
     * 
     * @param ctx
     *            The <code>Context</code> that created this instance
     */
    public AppsDb(Context ctx)
    {
        context = ctx;

        // initialize the database helper
        helper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)
        {
            @Override
            public void onCreate(SQLiteDatabase db)
            {
                Log.d("AppsDb", "create db");
                try
                {
                    db.execSQL(CREATE_PTABLE);
                    db.execSQL(CREATE_CTABLE);
                    db.execSQL(CREATE_ID_TABLE);
                    db.execSQL("insert into currentid values('1')");
                } catch (Exception e)
                {
                    Log.d("RexLogin", e.getLocalizedMessage());
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion,
                    int newVersion)
            {
                if(oldVersion < 15)
                {
                    db.execSQL("drop table if exists " +  PTABLE_NAME);
                    db.execSQL("drop table if exists " +  CTABLE_NAME);
                    db.execSQL("drop table if exists currentid");
                    db.execSQL(CREATE_PTABLE);
                    db.execSQL(CREATE_CTABLE);
                    db.execSQL(CREATE_ID_TABLE);
                    db.execSQL("insert into currentid values('1')");
                }
                Log.d("AppsDb", "Upgrade");
            }
        };

        // open the database
        Log.d("AppsDb", "Starting db");
        db = helper.getWritableDatabase();
    }

    public void clearAppInfo()
    {
        db.delete(PTABLE_NAME, null, null);
        db.execSQL(CREATE_PTABLE);        
    }

    public ArrayList<AppInfo> getAppInfo(String pkg)
    {
        return getAppInfo(pkg, true);
    }
    
    public ArrayList<AppInfo> getAppInfo(String pkg, boolean descendingOrder)
    {
        String orderStr = null;
        if(descendingOrder)
            orderStr = "desc";
        else
            orderStr = "asc";
            
        String queryStr;
        if(pkg == null)
            queryStr = "SELECT * from processes order by start_time " + orderStr;
        else
            queryStr = "SELECT * from processes where " + PACKAGENAME + " = '" + pkg + "' order by start_time " + orderStr;
       
        Cursor results = db.rawQuery(queryStr, null);
        int num = results.getCount();
        ArrayList<AppInfo> allApps = new ArrayList<AppInfo>();
        if (num != 0)
        {
            if (results.moveToFirst())
            {
                do
                {
                    int startCol = results.getColumnIndex(START_TIME);
                    int stopCol = results.getColumnIndex(STOP_TIME);
                    int packageCol = results.getColumnIndex(PACKAGENAME);
                    AppInfo thisApp = new AppInfo(results.getString(packageCol), 
                            results.getLong(startCol), results.getLong(stopCol));
                    allApps.add(thisApp);
                } while (results.moveToNext());
            }
        }
        if (!results.isClosed())
        {
            results.close();
        }
        return allApps;
    }
    public List<AppAttributes> getAttributes(String pkg)
    {
        List<AppAttributes> ret = new LinkedList<AppAttributes>();
        try
        {
            Cursor results = null;
            if(pkg == null)
                results = db.rawQuery("SELECT * FROM categories", null);
            else
                results = db.rawQuery("SELECT * FROM categories WHERE " + PACKAGENAME + " = '" + pkg + "'", null);
            if (results.getCount() != 0)
            {
                if (results.moveToFirst())
                {
                    do
                    {
                        int catCol = results.getColumnIndex(CATEGORY);
                        int iconCol = results.getColumnIndex(ICON);
                        int pkgCol = results.getColumnIndex(PACKAGENAME);
                        int appCol = results.getColumnIndex(APPNAME);
                        AppAttributes aa = new AppAttributes();
                        aa.setCategory(results.getString(catCol));
                        aa.setIcon(results.getString(iconCol));
                        aa.setPackageName(results.getString(pkgCol));
                        aa.setAppName(results.getString(appCol));
                        ret.add(aa);
                    } while (results.moveToNext());
                }
                if (!results.isClosed())
                    results.close();
            }
        } catch (Exception e)
        {
            Log.e("Exception 1", e.toString());
        }

        return ret;
    }

    public void setAttributes(String pkg, String appName, String cat, String icon)
    {
        try
        {
            Cursor results = db.rawQuery("select * from categories where " + PACKAGENAME + " = '" + pkg + "'", null);
            if(results.getCount() != 0)
            {
                if(appName != null)
                    db.execSQL("update categories set " + APPNAME + " = '" + appName + "' where " + PACKAGENAME + " = '" + pkg + "'");
                if(cat != null)
                    db.execSQL("update categories set " + CATEGORY + " = '" + cat + "' where " + PACKAGENAME + " = '" + pkg + "'");
                if(icon != null)
                    db.execSQL("update categories set " + ICON + " = '" + icon + "' where " + PACKAGENAME + " = '" + pkg + "'");
            }
            else
            {
                if(appName == null)
                    appName = "";
                if(cat == null)
                    cat = "";
                if(icon == null)
                    icon = "";
                db.execSQL("INSERT INTO categories ('" + PACKAGENAME + "', '" + APPNAME + "', '" + CATEGORY + "', '" + ICON + "')" + 
                        " VALUES('" + pkg + "','" + appName + "','" + cat + "','" + icon + "')");
            }
        } catch (Exception e)
        {
            Log.e("Exception 1", e.toString());
        }
    }

    public int startApp(String app, String packageName)
    {
        int id = -1;
        try
        {
            Cursor result = db.rawQuery("select * from currentid", null);
            if(result.moveToFirst())
            {
                id = result.getInt(0);
                ++id;
                String curTime = Long.toString(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());
                String q = "insert into processes values('" + Integer.toString(id) + "','" + 
                            packageName + "','" + curTime + "','" + curTime + "')";
                Log.d("AppsDb", q);
                db.execSQL(q);
                q = "update currentid set id = '" + Integer.toString(id) + "'";
                Log.d("AppsDb", q);
                db.execSQL(q);
                this.setAttributes(packageName, app, null, null);
            }
        } catch (Exception e)
        {
            Log.e("Exception 1", e.toString());
            return -1;
        }
        return id;
    }

    public void updateApp(int id)
    {
        try
        {
            String curTime = Long.toString(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());
            db.execSQL("update processes set stop_time = " + curTime + " where id = " + Integer.toString(id));
        } catch (Exception e)
        {
            Log.e("Exception 1", e.toString());
        }
    }

    /**
     * Method to close the underlying database connection.
     */
    public void close()
    {
        helper.close();
    }
}
