package com.konka.dialyroads.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.konka.dialyroads.R;

public class Login {
	static Dialog dialog = null;

	public static void show(Context context) {
		if (dialog == null) {
			dialog = new Dialog(context){
				
			};
			// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setTitle("USER");
			View mview = ((Activity) context).getLayoutInflater().inflate(R.layout.loginfragment, null);
			dialog.setContentView(mview, new LayoutParams(LayoutParams.WRAP_CONTENT, 350));
//			dialog.setContentView(mview, new LayoutParams(500, 500));
		}
		dialog.show();
	}
}
