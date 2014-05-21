package mk.run4rebate;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

@SuppressLint("NewApi")
public class ActivityRules extends Activity {

	public Account account = new Account();
	// private AdView adView;
	int version;
	private MMAdView adView;
	private CheckBox checkBox;

	protected void onCreate(Bundle savedInstanceState) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rules2);
		account.restore(this);

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
		checkBox = (CheckBox) findViewById(R.id.checkBox1);
		TextView tV1 = (TextView) findViewById(R.id.textView1);
		TextView tV2 = (TextView) findViewById(R.id.textView2);
		version = Integer.valueOf(android.os.Build.VERSION.SDK);

		ScrollView scrollView = (ScrollView) findViewById(R.id.rules_list);
		if (version > 10) {
			scrollView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
		}

		Resources res = getResources();
		String text = String.format(res.getString(R.string.rules));
		CharSequence styledText = Html.fromHtml(text);
		tV2.setText(styledText);
		text = String.format(res.getString(R.string.rules_title));
		styledText = Html.fromHtml(text);
		tV1.setText(styledText);
		Linkify.addLinks(tV2, Linkify.WEB_URLS);

		if (account.checked) {
			checkBox.setChecked(true);
		} else {
			checkBox.setChecked(false);
		}

		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				account.checked = isChecked;
				account.save(ActivityRules.this);
			}
		});
	}

	private void showRegistrationAlert() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				account.checked = true;
				account.save(ActivityRules.this);
				finishActivity();
			}
		});
		b.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finishActivity();
			}
		});
		b.setMessage(R.string.register);
		b.setCancelable(false);
		b.show();
	}

	public void MenuBackOnClick(View view) {
		if (checkBox.isChecked()) {
			finishActivity();
		} else {
			showRegistrationAlert();
		}
	}

	private void finishActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityMenu.class);
		startActivity(intent);
		finish();
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

	public void SettingsOnClick(View view) {
		Toast.makeText(getApplicationContext(), "Лепесток в разработке", Toast.LENGTH_LONG).show();
		/*
		 * Intent intent = new Intent(); intent.setClass(this,
		 * ActivityAccount.class); startActivity(intent); finish();
		 */
	}
}
