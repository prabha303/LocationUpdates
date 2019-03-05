package prabhalab.client.location;

import android.app.Activity;
import android.text.TextUtils;
import android.view.WindowManager;

public class Utility {
    private static Utility utility = null;

    public static Utility getInstance()
    {
        if (utility != null)
        {
            return utility;
        }
        else
        {
            utility = new Utility();
            return utility;
        }
    }
    public static boolean isNotEmpty(String Value) {
        boolean flag =false;
        try {
            if(!TextUtils.isEmpty(Value)){
                Value = Value.replace("null", "");
            }else{
                Value = "";
            }
            if(!TextUtils.isEmpty(Value)){
                Value = Value.trim();
            }
            if(!TextUtils.isEmpty(Value)){
                flag =true;
            }else{
                flag =false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void stayScreenOn(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
