package com.run4rebate.main;
import java.util.concurrent.TimeUnit;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SplashActivity extends Activity {
	Intent intent;
	Starter st;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.);
		startAddSearchActivity();
	}

	private void startAddSearchActivity() {
		intent = new Intent();
		// intent.setClass(this, ActivityAdd.class);
		intent.setClass(this,MainActivity.class);
		st = new Starter();
		st.execute("");
	}
	class Starter extends AsyncTask<String, Integer, Void> {
		@Override
		protected Void doInBackground(String... arg0) {
			try {
				TimeUnit.SECONDS.sleep(4);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			startActivity(intent);
			finish();
		}
	}
}
