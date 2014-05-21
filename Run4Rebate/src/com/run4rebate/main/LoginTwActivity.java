package mk.run4rebate;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.perm.kate.api.Auth;

public class LoginTwActivity extends Activity {
	static String TWITTER_CONSUMER_KEY = Constants.TWITTER_CONSUMER_KEY;
	static String TWITTER_CONSUMER_SECRET = Constants.TWITTER_CONSUMER_SECRET;

	static String PREFERENCE_NAME = Constants.PREFERENCE_NAME;

	static final String PREF_KEY_OAUTH_SECRET = Constants.PREF_KEY_OAUTH_SECRET;
	static final String PREF_KEY_OAUTH_TOKEN = Constants.PREF_KEY_OAUTH_TOKEN;
	static final String PREF_KEY_OAUTH_USER_ID = Constants.PREF_KEY_OAUTH_USER_ID;
	static final String PREF_KEY_TWITTER_LOGIN = Constants.PREF_KEY_TWITTER_LOGIN;

	static final String TWITTER_CALLBACK_URL = Constants.TWITTER_CALLBACK_URL;
	static final String URL_TWITTER_AUTH = Constants.URL_TWITTER_AUTH;
	static final String URL_TWITTER_OAUTH_TOKEN = Constants.URL_TWITTER_OAUTH_TOKEN;
	static final String URL_TWITTER_OAUTH_VERIFIER = Constants.URL_TWITTER_OAUTH_VERIFIER;

	Button btnLoginTwitter;
	Button btnUpdateStatus;
	Button btnLogoutTwitter;
	EditText txtUpdate;
	TextView lblUpdate;
	TextView lblUserName;

	ProgressDialog pDialog;

	private static Twitter twitter;
	private static RequestToken requestToken;
	public Dialog dialog;
	private static SharedPreferences mSharedPreferences;
	WebView webview;
	public taskTwitter taskTw;
	twParseUrlTask twitParseUrlTask;
	AccessToken accessToken;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_tw);
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);
		Uri uri = getIntent().getData();
		if (!isTwitterLoggedInAlready() && uri == null) {
			taskTw = new taskTwitter();
			taskTw.execute("start");

		} else if (!isTwitterLoggedInAlready() && uri != null) {
			taskTw = new taskTwitter();
			taskTw.execute("data");
		}

		/*
		 * dialog = new Dialog(this); dialog.setContentView(R.layout.wait);
		 * dialog.setTitle("Loading"); dialog.show();
		 */

		webview = (WebView) findViewById(R.id.twview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.clearCache(true);

		webview.setWebViewClient(new TwitterWebViewClient());

		CookieSyncManager.createInstance(this);

		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();

		String url = Auth.getUrl(Constants.API_ID_VK, Auth.getSettings());
		// webview.loadUrl(url);

	}

	class TwitterWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			parseUrl(url);
		}
	}

	private void parseUrl(String url) {
		Uri uri = Uri.parse(url);
		try {
			if (url == null)
				return;
			else if (uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				if (uri != null
						&& uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
					twitParseUrlTask = new twParseUrlTask();
					twitParseUrlTask.execute(url);
					dialog = new Dialog(this);
					dialog.setContentView(R.layout.wait);
					dialog.setTitle("Loading");
					dialog.show();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class twParseUrlTask extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... url) {

			Uri uri = Uri.parse(url[0]);
			String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

			try {
				accessToken = twitter.getOAuthAccessToken(requestToken,
						verifier);
				long userID = accessToken.getUserId();
				User user = twitter.showUser(userID);
				String username = user.getName();

				String user_photo = user.getOriginalProfileImageURL();
				Intent intent = new Intent();

				intent.putExtra("access_token_TW", accessToken.getToken());
				intent.putExtra("access_secret_TW",
						accessToken.getTokenSecret());
				intent.putExtra("user_id_TW",
						String.valueOf(accessToken.getUserId()));
				intent.putExtra("user_name", username);
				intent.putExtra("user_photo", user_photo);
				intent.putExtra("social", "tw");
				setResult(Activity.RESULT_OK, intent);
				finish();

			} catch (Exception e) {
				Log.e("Twitter Login Error", "> " + e.getMessage());
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();
			webview.destroy();
		}

	}

	private void loginToTwitter() {
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter
						.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				/*
				 * Intent oauthIntent = new Intent(Intent.ACTION_VIEW, Uri
				 * .parse(requestToken.getAuthenticationURL()));
				 * oauthIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				 * oauthIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				 * oauthIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
				 * LoginTwActivity.this.startActivity(oauthIntent);
				 */

				/*
				 * LoginTwActivity.this.startActivity(new
				 * Intent(Intent.ACTION_VIEW, Uri
				 * .parse(requestToken.getAuthenticationURL())));
				 */

			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Already Logged into twitter", Toast.LENGTH_LONG).show();
		}
	}

	class taskTwitter extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... categories) {

			if (categories[0].equals("start")) {
				loginToTwitter();
			} else if (categories[0].equals("data")) {
				Uri uri = getIntent().getData();
				if (uri != null
						&& uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
					String verifier = uri
							.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

					try {
						accessToken = twitter.getOAuthAccessToken(requestToken,
								verifier);
						long userID = accessToken.getUserId();
						User user = twitter.showUser(userID);
						String username = user.getName();
						String user_photo = user.getProfileImageURL();
						Intent intent = new Intent();
						intent.putExtra("access_token_TW",
								accessToken.getToken());
						intent.putExtra("access_secret_TW",
								accessToken.getTokenSecret());
						intent.putExtra("user_id_TW",
								String.valueOf(accessToken.getUserId()));
						intent.putExtra("user_name", username);
						intent.putExtra("user_photo", user_photo);
						intent.putExtra("social", "tw");
						setResult(Activity.RESULT_OK, intent);
						finish();

					} catch (Exception e) {
						Log.e("Twitter Login Error", "> " + e.getMessage());
					}
				}
			} else if (categories[0].equals("loged")) {

				Log.d("Tweet Text", "> " + categories[0]);
				String status = categories[0];
				try {
					ConfigurationBuilder builder = new ConfigurationBuilder();
					builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
					builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

					String access_token = mSharedPreferences.getString(
							PREF_KEY_OAUTH_TOKEN, "");
					String access_token_secret = mSharedPreferences.getString(
							PREF_KEY_OAUTH_SECRET, "");

					AccessToken accessToken = new AccessToken(access_token,
							access_token_secret);
					Twitter twitter = new TwitterFactory(builder.build())
							.getInstance(accessToken);

					twitter4j.Status response = twitter.updateStatus(status);

					Log.d("Status", "> " + response.getText());
				} catch (TwitterException e) {
					Log.d("Twitter Update Error", e.getMessage());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// dialog.dismiss();
			LoginTwActivity.this.webview.loadUrl(requestToken
					.getAuthenticationURL());
		}

	}

	private boolean isTwitterLoggedInAlready() {
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

}
