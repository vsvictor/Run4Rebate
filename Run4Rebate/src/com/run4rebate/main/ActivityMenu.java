package mk.run4rebate;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

public class ActivityMenu extends Activity {
	String lang;
	// private AdView adView;
	private MMAdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu2);
		lang = getApplicationContext().getResources().getConfiguration().locale.getLanguage();

		adView = new MMAdView(this);

		adView.setApid(Constants.BANNER_APID);
		adView.setId(MMSDK.getDefaultAdId());

		int placementWidth = Constants.BANNER_AD_WIDTH;
		int placementHeight = Constants.BANNER_AD_HEIGHT;
		adView.setWidth(placementWidth);
		adView.setHeight(placementHeight);
		int layoutWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				placementWidth, getResources().getDisplayMetrics());
		int layoutHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				placementHeight, getResources().getDisplayMetrics());

		RelativeLayout adRelativeLayout = (RelativeLayout) findViewById(R.id.banner);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(layoutWidth,
				layoutHeight);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		adRelativeLayout.addView(adView, layoutParams);
		// Map<String, String> metaData = createMetaData();
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put(MMRequest.KEY_AGE, "14");
		MMRequest request = new MMRequest();
		request.setMetaValues(metaData);
		adView.setMMRequest(request);
		adView.setListener(new AdListener());
		MMSDK.trackConversion(this, Constants.MY_GOAL_ID);
		adView.getAd();

		/*
		 * adView = new AdView(this, AdSize.BANNER, Constants.MY_AD_UNIT_ID);
		 * RelativeLayout layout = (RelativeLayout) findViewById(R.id.banner);
		 * layout.addView(adView); adView.loadAd(new AdRequest());
		 */
	}

	public void onNewsClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityNews.class);
		startActivity(intent);
	}

	public void onBonusClick(View view) {
		final Dialog dialog = new Dialog(ActivityMenu.this);
		dialog.setContentView(R.layout.bonuses);

		Resources res = getResources();
		String text = String.format(res.getString(R.string.bonuses_title));

		dialog.setTitle(text);

		Button btnSignIn = (Button) dialog.findViewById(R.id.ok);

		// Set On ClickListener
		btnSignIn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dialog.dismiss();

			}
		});

		dialog.show();
	}

	public void onRulesClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityRules.class);
		startActivity(intent);
		finish();
	}

	public void onInfoClick(View view) {

		final Dialog dialog = new Dialog(ActivityMenu.this);

		dialog.setContentView(R.layout.info);

		Resources res = getResources();
		String text = String.format(res.getString(R.string.title_about));

		dialog.setTitle(text);

		// get the Refferences of views

		Button btnSignIn = (Button) dialog.findViewById(R.id.ok);

		// Set On ClickListener
		btnSignIn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dialog.dismiss();

			}
		});

		dialog.show();

	}

	public void SettingsOnClick(View view) {
		Toast.makeText(getApplicationContext(), "Лепесток в разработке", Toast.LENGTH_LONG).show();
		/*
		 * Intent intent = new Intent(); intent.setClass(this,
		 * ActivityAccount.class); startActivity(intent); finish();
		 */
	}

	public void BackOnClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityAccount.class);
		startActivity(intent);
	}
	
	public void AccountOncClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityAccount.class);
		startActivity(intent);
	}

	public void AddSearchOnClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityAddSearch.class);
		startActivity(intent);
	}
}
