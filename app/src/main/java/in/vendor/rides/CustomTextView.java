package in.vendor.rides;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;



/**
 * Created by prabha on 05/03/2019.
 */

//@SuppressLint("AppCompatCustomView")
public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
	      super(context, attrs, defStyle);
	      init();
	  }

	 public CustomTextView(Context context, AttributeSet attrs) {
	      super(context, attrs);
	      init();
	  }

	 public CustomTextView(Context context) {
	      super(context);
	      init();
	 }


	public void init() {
	    Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
	    setTypeface(tf);
	}
}