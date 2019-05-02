package in.vendor.rides;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This class handles the all the shared preference operation.
 * .i.e., creating shared preference and to set and get value.
 *
 * @author Jeevanandhan
 */

public class SharedPref  {


    // Single ton objects...
    private static SharedPreferences preference = null;
    private static SharedPref sharedPref = null;
    public static String  preferenceName = "LocationTrack";
    //Single ton method for this class...
    public static SharedPref getInstance() {
        if (sharedPref != null) {
            return sharedPref;
        } else {
            sharedPref = new SharedPref();
            return sharedPref;
        }
    }





    /*boolean setLabour = SharedPref.getInstance().getBoolean(Home.this,SetLabour);
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    boolean setHelper = sharedPreferences.getBoolean(SetHelper, false);


        if(!Utility.isNotEmpty(""+setLabour))
    {
        withlabour.setVisibility(View.GONE);
    }
        if(!Utility.isNotEmpty(""+setHelper))
    {
        drhelper.setVisibility(View.GONE);
    }*/


    /**
     * Singleton object for the shared preference.
     *
     * @param context
     * @return SharedPreferences
     */



    /**
     * Set the String value in the shared preference with the given key.
     *
     */

    private static SharedPreferences getPreferenceInstance(Context context) {

        if (preference != null) {
            return preference;
        } else {
            preference = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            return preference;
        }
    }

    public void setSharedValue(Context context, String key, String value) {
        getPreferenceInstance(context);
        Editor editor = preference.edit();
        editor.putString(key, value);
        //\22Apr16. Harish.Calling 'apply' instead 'commit' since it is faster and not a blocking call.
        editor.apply();

        //editor.commit();

    }


    /**
     * Set the Integer value in the shared preference with the given key.
     *
     * @param context
     * @param key
     * @param value
     */

    public void setSharedValue(Context context, String key, int value) {
        getPreferenceInstance(context);
        Editor editor = preference.edit();
        editor.putInt(key, value);
        //\22Apr16. Harish.Calling 'apply' instead 'commit' since it is faster and not a blocking call.
        editor.apply();
        //editor.commit();
    }

    /**
     * Set the boolean value in the shared preference with the given key.
     *
     * @param context
     * @param key
     * @param value
     */

    public void setSharedValue(Context context, String key, Boolean value) {
        getPreferenceInstance(context);
        Editor editor = preference.edit();
        editor.putBoolean(key, value);
        //\22Apr16. Harish.Calling 'apply' instead 'commit' since it is faster and not a blocking call.
        editor.apply();
        //editor.commit();
    }

    /**
     * Get Boolean value for the given key.
     *
     * @param context
     * @param key
     * @return Boolean
     */

    public static  Boolean getBooleanValue(Context context, String key) {
        return getPreferenceInstance(context).getBoolean(key, false);
    }

    /**
     * Get Integer value for the given key.
     *
     * @param context
     * @param key
     * @return Int
     */

    public int getIntValue(Context context, String key) {
        return getPreferenceInstance(context).getInt(key, 0);
    }


    /**
     * Get String value for the given key.
     *
     * @param context
     * @param key
     * @return String
     */

    public static String getStringValue(Context context, String key) {
        return getPreferenceInstance(context).getString(key, null);
    }

}
