package mk.run4rebate;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ActivitySearch extends Activity implements SeekBar.OnSeekBarChangeListener {

	String lang;
	SeekBar seekBar;
	TextView tV;
	Account account = new Account();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		lang = getApplicationContext().getResources().getConfiguration().locale.getLanguage();
		setContentView(R.layout.search_object2);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(this);
		tV = (TextView) findViewById(R.id.textRadius);
		account.restore(this);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public void MenuOnClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityMenu.class);
		startActivity(intent);
		finish();
	}
	
	public void AddSearchOnClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityAddSearch.class);
		startActivity(intent);
		finish();
	}

	public void SettingsOnClick(View view) {
		Toast.makeText(getApplicationContext(), "Лепесток в разработке", Toast.LENGTH_LONG).show();
		/*
		 * Intent intent = new Intent(); intent.setClass(this,
		 * ActivityAccount.class); startActivity(intent); finish();
		 */
	}

	public boolean accessToken() {
		if (account.access_token != null) {
			return true;
		} else {
			return false;
		}

	}
	
	public void AccountOnClick(View view) {
		Intent intent = new Intent();
		if (accessToken()) {
			intent.setClass(this, ActivityAccount.class);
		} else {
			intent.setClass(this, ActivityRegistration.class);
		}
		startActivity(intent);
		finish();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		if (seekBar.getProgress() == 1) {
			tV.setText("Что-то тут должно выводиться до 500 м.");
		} else if (seekBar.getProgress() == 2) {
			tV.setText("Что-то тут должно выводиться до 2 км.");
		} else if (seekBar.getProgress() == 3) {
			tV.setText("Что-то тут должно выводиться до 5 км.");
		} else if (seekBar.getProgress() == 4) {
			tV.setText("Что-то тут должно выводиться до 15 км.");
		} else if (seekBar.getProgress() == 5) {
			tV.setText("Что-то тут должно выводиться до 30 км.");
		}
	}
}
