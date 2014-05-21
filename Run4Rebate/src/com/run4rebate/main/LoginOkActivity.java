package mk.run4rebate;

import org.json.JSONException;
import org.json.JSONObject;

import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.OkTokenRequestListener;
import ru.ok.android.sdk.util.OkScope;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LoginOkActivity extends Activity {
	protected Odnoklassniki mOdnoklassniki;
	protected final Context mContext = this;

	GetCurrentUserTask gt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_ok);

		mOdnoklassniki = Odnoklassniki.createInstance(this,
				Constants.API_ID_OK, Constants.APP_SECRET_OK,
				Constants.APP_KEY_OK);
		mOdnoklassniki.setTokenRequestListener(new OkTokenRequestListener() {
			@Override
			public void onSuccess(final String accessToken) {

				gt = new GetCurrentUserTask();
				gt.execute(new Void[0]);
				Toast.makeText(mContext, "Recieved token : " + accessToken,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				Toast.makeText(mContext, "Authorization was canceled",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError() {
				Toast.makeText(mContext, "Error getting token",
						Toast.LENGTH_SHORT).show();
			}
		});
		if (!mOdnoklassniki.hasAccessToken()) {
			mOdnoklassniki.requestAuthorization(mContext, false,
					OkScope.VALUABLE_ACCESS);

		} else {
			gt = new GetCurrentUserTask();
			gt.execute(new Void[0]);
		}
	}

	protected final class GetCurrentUserTask extends
			AsyncTask<Void, Void, String> {
		String first_name = "first_name";
		String location = "location";
		String last_name = "last_name";
		String photo, photo_id = "photo";
		String city = "city";
		String country = "country";

		@Override
		protected String doInBackground(final Void... params) {
			try {

				return mOdnoklassniki.request("users.getCurrentUser", null,
						"get");
			} catch (Exception exc) {
				Log.e("Odnoklassniki", "Failed to get current user info", exc);
			}
			return null;
		}

		@Override
		protected void onPostExecute(final String result) {
			if (result != null) {
				// Toast.makeText(mContext, "Get current user result: " +
				// result, Toast.LENGTH_SHORT).show();

				try {
					JSONObject jsonObj = new JSONObject(result);

					Intent intent = new Intent();

					for (int i = 0; i < jsonObj.length(); i++) {

						if (jsonObj.names().getString(i).equals("last_name")) {
							last_name = jsonObj.getString("last_name");
						}

						else if (jsonObj.names().getString(i)
								.equals("first_name")) {
							first_name = jsonObj.getString("first_name");
						} else if (jsonObj.names().getString(i)
								.equals("location")) {
							city = jsonObj.getJSONObject("location").getString(
									"city");
							country = jsonObj.getJSONObject("location")
									.getString("country");
						} else if (jsonObj.names().getString(i)
								.equals("photo_id")) {
							photo_id = jsonObj.getString("photo_id");
							photo = "http://uld9.mycdn.me/getImage?photoId="
									+ photo_id + "&photoType=3";
						}

					}

					intent.putExtra("access_token_OK",
							mOdnoklassniki.getCurrentAccessToken());
					intent.putExtra("user_last_name", last_name);
					intent.putExtra("user_first_name", first_name);
					intent.putExtra("user_city", city);
					intent.putExtra("user_country", country);
					intent.putExtra("user_photo", photo);
					intent.putExtra("social", "ok");
					setResult(Activity.RESULT_OK, intent);
					finish();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}
}
