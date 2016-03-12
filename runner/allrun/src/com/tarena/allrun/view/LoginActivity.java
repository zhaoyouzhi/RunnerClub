package com.tarena.allrun.view;

import com.dd.CircularProgressButton;
import com.tarena.allrun.TApplication;
import com.tarena.allrun.biz.Factory;
import com.tarena.allrun.biz.ILoginBiz;
import com.tarena.allrun.biz.implAsmack.LoginBiz;
import com.tarena.allrun.entity.UserEntity;
import com.tarena.allrun.util.Const;
import com.tarena.allrun.util.NetworkUtil;
import com.tarena.allrun.util.Tools;
import com.tarena.allrun.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
	EditText etUserName, etPassword;
	Button tvToRegister;
	CircularProgressButton btn;
	LoginReceiver loginReceiver;
	String username, password;
	UserEntity userEntity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try {
			NetworkUtil.checkNetworkState(this);

			setContentView(R.layout.activity_login);
			setView();
			addListener();
			loginReceiver = new LoginReceiver();
			this.registerReceiver(loginReceiver, new IntentFilter(
					Const.ACTION_LOGIN));
			for (int i = 0; i < 10000; i++) {
				TApplication.listUserEntity.add(new UserEntity());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(loginReceiver);
	}

	private void addListener() {
		MyListener myListener = new MyListener();
		btn.setOnClickListener(myListener);
		tvToRegister.setOnClickListener(myListener);

	}

	private void setView() {
		etUserName = (EditText) findViewById(R.id.et_login_username);
		etPassword = (EditText) findViewById(R.id.et_login_password);

		btn = (CircularProgressButton) findViewById(R.id.btn_login_submit);
		btn.setIndeterminateProgressMode(true);
		btn.setProgress(0);
		tvToRegister = (Button) findViewById(R.id.btn_login_register);
	}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	try {
		if (requestCode==100&& resultCode==0)
		{
			String username=data.getStringExtra("username");
			String pwd=data.getStringExtra("pwd");
			etUserName.setText(username);
			etPassword.setText(pwd);
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	class MyListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (v.getId() == R.id.btn_login_register) {
				startActivityForResult(new Intent(LoginActivity.this,
						RegisterActivity_.class),100);
			}
			if (v.getId() == R.id.btn_login_submit) {

				username = etUserName.getText().toString();
				password = etPassword.getText().toString();
				StringBuilder builder = new StringBuilder();
				if (TextUtils.isEmpty(username)) {
					builder.append("�û���Ϊ��\n");
				}
				if (TextUtils.isEmpty(password)) {
					builder.append("����Ϊ��\n");
				}
				if (!TextUtils.isEmpty(builder.toString())) {
					Toast.makeText(LoginActivity.this, builder.toString(),
							Toast.LENGTH_LONG).show();
					return;
				}
				btn.setProgress(50);
				// Tools.showProgressDialog(LoginActivity.this, "�ף�����Ŭ��Ϊ���¼");

				userEntity = new UserEntity();
				userEntity.setUsername(username);
				userEntity.setPassword(password);
				// ֱ�Ӵ���һ�㴴��������϶ȸ�
				// LoginBiz loginBiz = new LoginBiz();
				// loginBiz.login(userEntity);

				// ioc
				ILoginBiz iLoginBiz = Factory.getLoginBizInstance();
				iLoginBiz.login(userEntity);

				// ������ִ��onClick()
				btn.setEnabled(false);

			}
		}
	}

	class LoginReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(final Context context, Intent intent) {
			btn.setEnabled(true);
			Tools.closeProgressDialog();
			// ���û�һ����ϸ����ʾ
			// �ɹ������ӷ�����ʧ�ܣ��������
			int status = intent.getIntExtra(Const.KEY_DATA, -1);
			if (status == Const.STATUS_OK) {
				btn.setProgress(100);
				userEntity.setUser(username + "@" + TApplication.serviceName);
				TApplication.currentUser = userEntity;
//				Toast.makeText(LoginActivity.this, "�ɹ�", Toast.LENGTH_LONG)
//						.show();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						btn.setProgress(0);
						startActivity(new Intent(context, MainFragmentActivity.class));


					}
				}, 1000);
			}

			if (status == Const.STATUS_PASSWORD_ERROR) {
				btn.setProgress(-1);

				Tools.showInfo(context, "�������");
				// startActivity(new Intent(context,
				// MainFragmentActivity.class));
			}
			if (status == Const.STATUS_CONNECT_FAILURE) {
				Tools.showInfo(context, "����ʧ��");
				// startActivity(new Intent(context,
				// MainFragmentActivity.class));
				btn.setProgress(-1);

			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					btn.setProgress(0);

				}
			}, 1000);
		}

	}
}