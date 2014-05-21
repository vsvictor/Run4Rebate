package mk.run4rebate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

@SuppressWarnings("deprecation")
public class LoginFbActivity extends Activity {

	public final String[] permissions = { "publish_stream, email" };
	Facebook facebook = new Facebook(Constants.API_ID_FB);
	Account account = new Account();

	// public TextView test;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_fb);
		// test = (TextView) findViewById(R.id.textTest);
		account.restore(this);
		String access_token_FB = account.access_token_FB;
		long access_expires_FB = account.access_expires_FB;
		if (access_token_FB != null) {
			facebook.setAccessToken(access_token_FB);
			// a = access_token;
			// Log.d("FB Sessions", "" + facebook.isSessionValid());
		}
		if (access_expires_FB != 0) {

			facebook.setAccessExpires(access_expires_FB);
			String[] auth = { facebook.getAccessToken(),
					String.valueOf(facebook.getAccessExpires()), "fb" };
			Intent intent = new Intent();
			intent.putExtra("access_token_FB", auth[0]);
			intent.putExtra("access_expires_FB", Long.parseLong(auth[1]));
			intent.putExtra("social", auth[2]);
			setResult(Activity.RESULT_OK, intent);
			finish();
			// 11*9 a = a + "      " + String.valueOf(expires);
			// getProfileInformation();
		}
		if (access_expires_FB == 0) {

			authorizeAndPostMassage();
		}

	}

	public void authorizeAndPostMassage() {
		if (!facebook.isSessionValid()) {
			facebook.authorize(this, permissions, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					Toast.makeText(LoginFbActivity.this,
							"Authorization successful", Toast.LENGTH_SHORT)
							.show();
					/*
					 * SharedPreferences.Editor editor = mPrefs.edit();
					 * editor.putString("access_token",
					 * facebook.getAccessToken());
					 * editor.putLong("access_expires",
					 * facebook.getAccessExpires()); editor.commit();
					 */
					String[] auth = { facebook.getAccessToken(),
							String.valueOf(facebook.getAccessExpires()), "fb" };
					Intent intent = new Intent();
					intent.putExtra("access_token_FB", auth[0]);
					intent.putExtra("access_expires_FB",
							Long.parseLong(auth[1]));
					intent.putExtra("social", auth[2]);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}

				@Override
				public void onFacebookError(FacebookError e) {
					Toast.makeText(LoginFbActivity.this,
							"Facebook error, try again later",
							Toast.LENGTH_SHORT).show();
					finish();
				}

				@Override
				public void onError(DialogError e) {
					Toast.makeText(LoginFbActivity.this,
							"Error, try again later", Toast.LENGTH_SHORT)
							.show();
					finish();
				}

				@Override
				public void onCancel() {
					Toast.makeText(LoginFbActivity.this,
							"Authorization canceled", Toast.LENGTH_SHORT)
							.show();
					finish();
				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}

}
