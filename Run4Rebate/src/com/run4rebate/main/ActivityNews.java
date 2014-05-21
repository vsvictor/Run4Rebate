package mk.run4rebate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan.LeadingMarginSpan2;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

@SuppressLint("NewApi")
public class ActivityNews extends Activity {
	public TextView stateView;
	public Dialog dialog;
	static String TAG = "TestImageLoad";
	String path = "/Run4Rebate/Images/";
	String URL = "http://run4rebate.com/uploads/";
	ImageView imgV;
	RegistrationTask regTask;
	MyTaskDownload mtD;
	int version;
	ArrayList<News> news = new ArrayList<News>();
	Adapter adapter;
	// private AdView adView;
	Account account = new Account();
	private MMAdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news2);
		account.restore(this);
		version = Integer.valueOf(android.os.Build.VERSION.SDK);

		regTask = new RegistrationTask();
		regTask.execute("");

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
		 */// listCreator();

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

	public void MenuBackOnClick(View view) {
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

	private void listCreator(String jsonArray) {
		final ListView listView = (ListView) findViewById(R.id.list);

		listView.setDrawingCacheEnabled(false);
		if (version > 10) {
			listView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
		}

		String content = null;
		String image = null;
		String date = null;

		try {
			JSONArray jsonArr = new JSONArray(jsonArray);
			for (int j = 0; j < jsonArr.length(); j++) {

				content = jsonArr.getJSONObject(j).getString("content");
				image = jsonArr.getJSONObject(j).getString("image");
				date = jsonArr.getJSONObject(j).getString("date");

				File myDir = new File(Environment.getExternalStorageDirectory(), path);

				String fname = image;
				File file = new File(myDir, fname);
				// Bitmap pinthumb =
				// BitmapFactory.decodeFile(file.getAbsolutePath());
				Uri a = Uri.fromFile(file);

				int width = getResources().getDimensionPixelOffset(R.dimen.news_size);
				news.add(new News(date, content, a, width));

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter = new Adapter(this, news);
		listView.setAdapter(adapter);

		dismissProgressDialog();
	}

	private void dismissProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			try {
				dialog.dismiss();
			} catch (Exception e) {
			}
		}
	}
	
	class RegistrationTask extends AsyncTask<String, Integer, Void> {
		String resultString = null;
		String jsonArray = null;
		// String avatar_id = "0";
		String news = "news";
		String error = "error";
		String resultS = "anyresult";
		String lang = "ru";
		String page_size = "20";
		String page = "0";

		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new Dialog(ActivityNews.this);
			dialog.setContentView(R.layout.wait);
			dialog.setTitle(getString(R.string.getting_news));
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... categories) {

			try {

				HttpClient httpclient = new DefaultHttpClient();

				HttpPost httppost = new HttpPost("http://api.run4rebate.com/news/get/&");
				//HttpPost httppost = new HttpPost("http://api.run4rebate.com/news/get/&lang=ru&page=0&page_size=20veq_yfc_rnj_dpkjvftn_blbnt_r_vezv");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);

				nameValuePairs.add(new BasicNameValuePair("lang", lang));
				nameValuePairs.add(new BasicNameValuePair("page", page));
				nameValuePairs.add(new BasicNameValuePair("page_size", page_size));

				String Hash = nameValuePairs.toString().replace(", ", "&").replace("[", "&")
						.replace("]", "")
						+ Constants.CONTROL_SUM;
				Log.d("", "hash: " + Hash);
				String send_hash = md5(URLEncoder.encode(Hash, "UTF-8"));
				nameValuePairs.add(new BasicNameValuePair("hash", send_hash));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

				HttpResponse response = httpclient.execute(httppost);

				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

					BufferedReader reader = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent(), "utf-8"));
					String line = null;

					while ((line = reader.readLine()) != null) {
						resultString = line;
						// Log.d("", "line: " + line);
					}

					JSONObject jsonObj = new JSONObject(resultString);
					if (!TextUtils.isEmpty(resultString)) {
						Log.d("", "result str: " + resultString);
					}
					for (int i = 0; i < jsonObj.length(); i++) {

						if (jsonObj.names().getString(i).equals(news)) {
							jsonArray = jsonObj.getString("news");
							resultS = jsonObj.names().getString(i);
							// resultS = jsonObj.names().getString(i);
						} else if (jsonObj.names().getString(i).equals(error)) {
							resultString = jsonObj.getJSONObject("error").getString("message");
							resultS = jsonObj.names().getString(i);
						}
					}
				}

			} catch (MalformedURLException e) {
				Toast toast = Toast.makeText(getApplicationContext(), "MalformedURLException e "
						+ e.getMessage(), Toast.LENGTH_SHORT);
				toast.show();
			} catch (IOException e) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"IOException " + e.getMessage(), Toast.LENGTH_SHORT);
				toast.show();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (resultS.equals(news)) {
				mtD = new MyTaskDownload();
				mtD.execute(jsonArray);
			} else {
				dismissProgressDialog();
				if (TextUtils.isEmpty(resultString)) {
					resultString = getString(R.string.news_error);
				}
				Toast.makeText(getApplicationContext(), resultString, Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	class MyTaskDownload extends AsyncTask<String, Integer, Void> {
		String operation;
		String jsonArray;
		File file;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(String... urls) {

			try {
				for (String url : urls) {
					jsonArray = url;
					JSONArray jsonArr = new JSONArray(url);
					for (int j = 0; j < jsonArr.length(); j++) {
						String fname = jsonArr.getJSONObject(j).getString("image");

						operation = URL + fname;
						File myDir = new File(Environment.getExternalStorageDirectory(), path);

						myDir.mkdirs();

						file = new File(myDir, fname);

						if (file.exists()) {
							continue;
						}

						try {

							FileOutputStream out = new FileOutputStream(file);

							Bitmap finalBitmap = downloadFile(operation);
							finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

							out.flush();

							out.close();

						} catch (Exception e) {

							e.printStackTrace();

						}

					}
				}
				TimeUnit.SECONDS.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (operation.equals("share")) {
				//!! show error?
				dismissProgressDialog();
				finish();
			} else {
				listCreator(jsonArray);

			}
		}
	}

	Bitmap downloadFile(String fileUrl) {
		Bitmap bmImg = null;
		Log.d(TAG, "downloadFile");
		Log.d(TAG, "fileUrl=" + fileUrl);
		URL myFileUrl = null;
		try {
			myFileUrl = new URL(fileUrl);
			Log.d(TAG, "fileUrl is OK URL");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.d(TAG, "fileUrl is FUCKEN SHIT URL");
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			Log.d(TAG, "going to connect.....");
			conn.connect();
			Log.d(TAG, "connected OK");
			InputStream is = conn.getInputStream();
			Log.d(TAG, "got InputStream");

			bmImg = BitmapFactory.decodeStream(is);

			Log.d(TAG, "decoded InputStream");

			Log.d(TAG, "just show image");
		} catch (IOException e) {
			Log.d(TAG, "oops, ERROR");
			e.printStackTrace();
		}
		return bmImg;
	}

	class MyLeadingMarginSpan2 implements LeadingMarginSpan2 {
		private int margin;
		private int lines;

		MyLeadingMarginSpan2(int lines, int margin) {
			this.margin = margin;
			this.lines = lines;
		}

		@Override
		public int getLeadingMargin(boolean first) {
			if (first) {
				return margin;
			} else {
				return 0;
			}
		}

		@Override
		public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline,
				int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
		}

		@Override
		public int getLeadingMarginLineCount() {
			return lines;
		}
	};

	public static final String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public boolean accessToken() {
		if (account.access_token != null) {
			return true;
		} else {
			return false;
		}

	}
}