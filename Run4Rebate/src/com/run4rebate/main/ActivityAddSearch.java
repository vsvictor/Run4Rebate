package mk.run4rebate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

// http://developer.android.com/guide/practices/screens_support.html

public class ActivityAddSearch extends Activity {
	final int search = 0, add = 1, settings = 2, menu = 3, account = 4, registration = 5;
	Account accountData = new Account();
	private final int REQUEST_OBJECT = 805;
	public Dialog dialog;
	// private AdView adView;

	public GetCategoriesTask gC;
	int countCategories;
	public SqlAdapterCategories adapterCategories;
	private MMAdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_search);
		accountData.restore(this);
		/*
		 * adView = new AdView(this, AdSize.BANNER, Constants.MY_AD_UNIT_ID);
		 * RelativeLayout layout = (RelativeLayout) findViewById(R.id.banner);
		 * layout.addView(adView); adView.loadAd(new AdRequest());
		 */

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

		adapterCategories = new SqlAdapterCategories(this);
		countCategories = adapterCategories.getCount();
	}

	public void SearchOnClick(View view) {
		activities(search);
		/*
		 * if (accessToken()) {
		 * 
		 * } else { activities(registration); }
		 */
	}

	public void AddOnClick(View view) {
		activities(add);
		/*
		 * if (accessToken()) { activities(add); } else {
		 * activities(registration); }
		 */
	}

	public void SettingsOnClick(View view) {
		activities(settings);
		/*
		 * if (accessToken()) {
		 * 
		 * } else { activities(registration); }
		 */
	}

	public void MenuOnClick(View view) {
		activities(menu);
		/*
		 * if (accessToken()) {
		 * 
		 * } else { activities(registration); }
		 */

	}

	public void AccountOnClick(View view) {
		if (accessToken()) {
			activities(account);
		} else {
			activities(registration);
		}
	}

	public void activities(int action) {
		switch (action) {
		case search:
			startSearchActivity();
			break;
		case add:
			startAddActivity();
			break;
		case settings:
			startSettingsActivity();
			break;
		case menu:
			startMenuActivity();
			break;
		case account:
			startAccountActivity();
			break;
		case registration:
			startRegistrationActivity();
			break;
		default:
		}
	}

	public void startSearchActivity() {
		// Toast.makeText(getApplicationContext(),
		// "search Лепесток в разработке", Toast.LENGTH_LONG).show();
		Intent intent = new Intent();
		intent.setClass(this, ActivitySearch.class);
		startActivity(intent);
		finish();
	}

	public void startAddActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityAdd.class);
		startActivity(intent);
		finish();
		// onSpinnerClick();
	}

	public void startMenuActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityMenu.class);
		startActivity(intent);
		finish();
	}

	public void startSettingsActivity() {
		Toast.makeText(getApplicationContext(), "settings Лепесток в разработке", Toast.LENGTH_LONG)
				.show();
	}

	public void startAccountActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityAccount.class);
		startActivity(intent);
		finish();
	}

	public void startRegistrationActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityRegistration.class);
		startActivity(intent);
		finish();
	}

	private boolean accessToken() {
		if (accountData.access_token != null) {
			Log.d("", "token: " + accountData.access_token);
			return true;
		} else {
			Log.d("", "token is null");
			return false;
		}

	}

	public void onSpinnerClick() {
		if (countCategories == 0) {
			dialog = new Dialog(ActivityAddSearch.this);
			dialog.setContentView(R.layout.wait);
			dialog.setTitle("Getting categories data");
			dialog.show();
			gC = new GetCategoriesTask();
			gC.execute("");
		} else {
			StartListView();
		}
	}

	public void StartListView() {

		Intent intent = new Intent();
		intent.setClass(this, ActivityCategoryList.class);
		startActivityForResult(intent, REQUEST_OBJECT);

		if (dialog != null) {
			if (dialog.isShowing()) {

				dialog.dismiss();
				adapterCategories.onDestroy();
			}
		}
	}

	class GetCategoriesTask extends AsyncTask<String, Void, String> {
		String resultString;
		String jsonArray;
		String token = "token";
		String error = "error";
		String tree = "tree";
		String id = "id";
		String name = "name";
		String subcategories = "subcategories";

		String get_id_main = "id";
		String get_id_sub = "id";
		String get_id_sub_sub = "id";
		String get_name_main = "name";
		String get_name_sub = "name";
		String get_name_sub_sub = "name";
		String get_subcategories = "subcategories";

		@Override
		protected String doInBackground(String... categories) {

			countCategories = adapterCategories.getCount();
			if (countCategories == 0) {
				try {

					HttpClient httpclient = new DefaultHttpClient();

					HttpPost httppost = new HttpPost(
							"http://api.run4rebate.com/objects/get_categories/&");

					HttpResponse response = httpclient.execute(httppost);

					BufferedReader reader = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent(), "utf-8"));
					String line = null;

					while ((line = reader.readLine()) != null) {
						resultString = line;

					}

					JSONObject jsonObj = new JSONObject(resultString);

					for (int i = 0; i < jsonObj.length(); i++) {

						if (jsonObj.names().getString(i).equals(tree)) {
							jsonArray = jsonObj.getString(tree);
							JSONArray jsonTree = new JSONArray(jsonArray);
							for (int j = 0; j < jsonTree.length(); j++) {
								get_id_main = jsonTree.getJSONObject(j).getString(id);
								get_name_main = jsonTree.getJSONObject(j).getString(name);
								get_subcategories = jsonTree.getJSONObject(j).getString(
										subcategories);
								Categories newStreet = new Categories(Long.parseLong(get_id_main),
										get_name_main, "0");
								if (countCategories == 0) {
									long last_id = adapterCategories.addItem(newStreet);
									adapterCategories.updateItem(last_id, get_id_main,
											get_name_main, "0", "");
									Log.d("Main", "Äîáàâëåíà: " + get_name_main);
								}
								JSONArray jsonSub_1 = new JSONArray(get_subcategories);
								for (int k = 0; k < jsonSub_1.length(); k++) {
									get_id_sub = jsonSub_1.getJSONObject(k).getString(id);
									get_name_sub = jsonSub_1.getJSONObject(k).getString(name);
									get_subcategories = jsonSub_1.getJSONObject(k).getString(
											subcategories);
									Categories newStreetSub = new Categories(
											Long.parseLong(get_id_sub), get_name_sub, get_id_main);
									if (countCategories == 0) {
										long last_id = adapterCategories.addItem(newStreetSub);
										adapterCategories.updateItem(last_id, get_id_sub,
												get_name_sub, get_id_main, "");
										Log.d("Sub", "Äîáàâëåíà: " + get_name_sub);
									}
									if (!get_subcategories.equals("[]")) {
										JSONArray jsonSub_2 = new JSONArray(get_subcategories);
										for (int m = 0; m < jsonSub_2.length(); m++) {
											get_id_sub_sub = jsonSub_2.getJSONObject(m).getString(
													id);
											get_name_sub_sub = jsonSub_2.getJSONObject(m)
													.getString(name);
											get_subcategories = jsonSub_2.getJSONObject(m)
													.getString(subcategories);
											Categories newStreetSubSub = new Categories(
													Long.parseLong(get_id_sub_sub),
													get_name_sub_sub, get_id_sub);
											if (countCategories == 0) {
												long last_id = adapterCategories
														.addItem(newStreetSubSub);
												adapterCategories.updateItem(last_id,
														get_id_sub_sub, get_name_sub_sub,
														get_id_sub, "");
												Log.d("Sub_Sub", "Äîáàâëåíà: " + get_name_sub_sub);
											}
										}
									}
								}

							}

						}

						else if (jsonObj.names().getString(i).equals(error)) {
							resultString = jsonObj.getJSONObject("error").getString("message");

						}

					}

				} catch (MalformedURLException e) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"MalformedURLException e " + e.getMessage(), Toast.LENGTH_SHORT);
					toast.show();

				} catch (IOException e) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"IOException " + e.getMessage(), Toast.LENGTH_SHORT);
					toast.show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//
			StartListView();
		}

	}
}
