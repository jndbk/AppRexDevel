package rex.login;

import java.util.ArrayList;
import java.util.Calendar;
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
    private static final int DB_VERSION = 8;

    private static final String PTABLE_NAME = "processes";
    private static final String CTABLE_NAME = "categories";

    // processes column names
    private static final String APPNAME = "appname";
    private static final String CATEGORY = "category";
    private static final String START_TIME = "start_time";
    private static final String STOP_TIME = "stop_time";
    private static final String PACKAGENAME = "package";

    // SQL statements
    private static final String CREATE_PTABLE = "CREATE TABLE " + PTABLE_NAME
            + " (ID INTEGER PRIMARY KEY, " + APPNAME + " TEXT, " + PACKAGENAME + " TEXT, " + START_TIME 
            + " INTEGER, " + STOP_TIME + " INTEGER)";
    private static final String CREATE_CTABLE = "CREATE TABLE " + CTABLE_NAME
            + " (" + APPNAME + " TEXT PRIMARY KEY, " + CATEGORY + " TEXT)";
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
                if(oldVersion < 20)
                {
                    db.execSQL("drop table if exists " +  PTABLE_NAME);
                    db.execSQL("drop table if exists currentid");
                    db.execSQL(CREATE_PTABLE);
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

    public ArrayList<AppInfo> getAppInfo()
    {
        Cursor results = db.rawQuery("SELECT * from processes order by start_time desc", null);
        int num = results.getCount();
        ArrayList<AppInfo> allApps = new ArrayList<AppInfo>();
        if (num != 0)
        {
            if (results.moveToFirst())
            {
                do
                {
                    int appnameCol = results.getColumnIndex(APPNAME);
                    String appName = results.getString(appnameCol);
                    if(appName == null)
                        continue;
                    int startCol = results.getColumnIndex(START_TIME);
                    int stopCol = results.getColumnIndex(STOP_TIME);
                    int packageCol = results.getColumnIndex(PACKAGENAME);
                    AppInfo thisApp = new AppInfo(appName, results.getString(packageCol), 
                            "Uncategorized", results.getLong(startCol), results.getLong(stopCol));
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

    public String getCategory(String app)
    {
        String ret = null;
        try
        {
            Cursor results = db.rawQuery(
                    "SELECT category FROM categories WHERE appname = '" + app
                            + "'", null);
            if (results.getCount() != 0)
            {
                results.moveToFirst();
                ret = results.getString(1);
                if (!results.isClosed())
                    results.close();
            }
        } catch (Exception e)
        {
            Log.e("Exception 1", e.toString());
        }

        return ret;
    }

    public void setCategory(String app, String cat)
    {
        try
        {
            db.execSQL("INSERT INTO categories ('" + APPNAME + "', '" + CATEGORY
                    + "') VALUES('" + app + "','" + cat + "')");
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
                            app + "','" + packageName + "','" + curTime + "','" + curTime + "')";
                Log.d("AppsDb", q);
                db.execSQL(q);
                q = "update currentid set id = '" + Integer.toString(id) + "'";
                Log.d("AppsDb", q);
                db.execSQL(q);
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
