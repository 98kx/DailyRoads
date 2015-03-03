package com.konka.dialyroads.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konka.dialyroads.R;
/**
 * 自定义吐司
 * @author Administrator
 *
 */
@SuppressLint("ResourceAsColor")
public class MyToast {

	public static void show(Context context, String text) {

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setBackgroundResource(R.drawable.tm); 
		//linearLayout.setBackgroundColor(color)
		TextView textView = new TextView(context);
		textView.setBackgroundResource(R.drawable.tm);

		textView.setTextColor(context.getResources().getColorStateList(R.color.huang));

		textView.setText(text);
		textView.setTextSize(18);
		linearLayout.addView(textView);
		Toast toast = new Toast(context);
		//toast.setGravity(Gravity.CENTER | Gravity.CENTER, 12, 40);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(linearLayout);
		toast.show();

		// Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	public static void show1(Context context, String text1,String text2,String text3) {

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setBackgroundResource(R.drawable.tm); 
		//linearLayout.setBackgroundColor(color)
		TextView textView1 = new TextView(context);
		TextView textView2 = new TextView(context);
		TextView textView3 = new TextView(context);
		
		textView1.setBackgroundResource(R.drawable.tm);
		textView1.setTextColor(context.getResources().getColorStateList(R.color.huang));
		textView1.setText(text1);
		textView1.setTextSize(18);
		linearLayout.addView(textView1);
		
		textView2.setBackgroundResource(R.drawable.tm);
		textView2.setTextColor(context.getResources().getColorStateList(R.color.red));
		textView2.setText(text2);
		textView2.setTextSize(18);
		linearLayout.addView(textView2);
		
		textView3.setBackgroundResource(R.drawable.tm);
		textView3.setTextColor(context.getResources().getColorStateList(R.color.huang));
		textView3.setText(text3);
		textView3.setTextSize(18);
		linearLayout.addView(textView3);
		
		Toast toast = new Toast(context);
		//toast.setGravity(Gravity.CENTER | Gravity.CENTER, 12, 40);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(linearLayout);
		toast.show();
//		Toast toast1 = new Toast(context);
//		//toast.setGravity(Gravity.CENTER | Gravity.CENTER, 12, 40);
//		toast1.setDuration(Toast.LENGTH_SHORT);
//		toast1.setView(linearLayout);
//		toast1.show();

		// Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
}
