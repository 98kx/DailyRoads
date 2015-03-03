package com.konka.dialyroads.frament;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.konka.dialyroads.R;
import com.konka.dialyroads.dialog.MyDialog;
import com.konka.dialyroads.pojo.User;
import com.konka.dialyroads.util.Assist;
import com.konka.dialyroads.util.MD5;
import com.konka.dialyroads.util.Net;
import com.konka.dialyroads.util.Util;

public class LoginFragment extends Fragment implements OnClickListener {
	private Button submit;
	private EditText mUserName, mPassword;

	public LoginFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.landing, container, false);
		mUserName = (EditText) view.findViewById(R.id.userName);
		mPassword = (EditText) view.findViewById(R.id.password);
		submit = (Button) view.findViewById(R.id.submit);
		submit.setOnClickListener(this);

		return view;
	}

	ProgressDialog progressDialog;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit:
			if (Util.detectionNetwork(getActivity())) {// 首先检测网络
				if (mUserName.getText().toString().equals("") || mPassword.getText().toString().equals("")) {
					Toast.makeText(getActivity(), "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				progressDialog = new ProgressDialog(getActivity());
				progressDialog.setMessage("正在登录...");
				progressDialog.show();

				new Thread() {
					@Override
					public void run() {
						boolean b = Net.login(mUserName.getText().toString(), MD5.getMD5String(mPassword.getText().toString()));
						if (!b) {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(getActivity(), "登录失败", Toast.LENGTH_LONG).show();
									progressDialog.dismiss();
								}
							});
							System.out.println("失败");
						} else {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_LONG).show();
									Assist.user=new User(mUserName.getText().toString(),  mPassword.getText().toString());
//									Assist.username = mUserName.getText().toString();
//									Assist.password = mPassword.getText().toString();
									progressDialog.dismiss();
									Activity mainActivity = getActivity();
									try {
										Field field = mainActivity.getClass().getField("myDialog");
										MyDialog d = (MyDialog) field.get(mainActivity);
										d.cancelUserDialog();
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							});
						}
					};
				}.start();
			}
			break;
		}
	}

}
