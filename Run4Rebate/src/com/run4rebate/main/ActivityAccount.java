package mk.run4rebate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

public class ActivityAccount extends Activity {

	private final int REQUEST_LOGIN = 1;
	private static final int IMAGE_SELECT = 801;
	private static final int CAMERA_CAPTURE = 802;
	private Uri mImageUri;
	private Uri vImageUri;

	public int width = 0;
	public int height = 0;
	public String Null = "";
	public String url = "http://api.run4rebate.com/user/upload_img/&";
	public String path_photo;
	public static String avatar_id = "0";
	EditText eLogin, ePassword, eEmail, ePhone, eAddress, eSkype;
	TextView tBonus, tNotification, tPhone, tEmail, tAddress, tName, tSkype;
	Account account = new Account();
	ImageView avatarImage;
	File file;
	String path = "/Run4Rebate/Images/";
	String mPathPhoto;
	String mPathLastPhoto;
	OnFocusChangeListener filter_listener;
	TextWatcher textWatcher;
	public UpdateTask upT;
	// public GetTask gT;
	// private AdView adView;
	private MMAdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account2);

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

		account.restore(this);
		/*
		 * adView = new AdView(this, AdSize.BANNER, Constants.MY_AD_UNIT_ID);
		 * RelativeLayout layout = (RelativeLayout) findViewById(R.id.banner);
		 * layout.addView(adView); adView.loadAd(new AdRequest());
		 */
		path_photo = account.user_photo;
		eLogin = (EditText) findViewById(R.id.editLogin);
		ePassword = (EditText) findViewById(R.id.editPassword);
		eSkype = (EditText) findViewById(R.id.editSkype);
		eEmail = (EditText) findViewById(R.id.editEmail);
		eAddress = (EditText) findViewById(R.id.editAddress);
		ePhone = (EditText) findViewById(R.id.editPhone);

		tBonus = (TextView) findViewById(R.id.textBonus);
		tNotification = (TextView) findViewById(R.id.textMessage);
		tPhone = (TextView) findViewById(R.id.textPhone);
		tEmail = (TextView) findViewById(R.id.textEmail);
		tAddress = (TextView) findViewById(R.id.textAddress);
		tName = (TextView) findViewById(R.id.accName);
		tSkype = (TextView) findViewById(R.id.textSkype);

		avatarImage = (ImageView) findViewById(R.id.imageAvatarAccount);

		GetTask gT = new GetTask();
		gT.execute("");
		fillform();

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

	public void UpdateProfileOnclick(View view) {
		upT = new UpdateTask();
		upT.execute("");
	}

	public void imageButtonCameraOnClick(View view) {
		LoadImageFromCamera();
	}

	public void imageButtonImageOnClick(View view) {
		LoadImageFromFile();
	}

	public void phoneOnClick(View view) {
		ePhone.setVisibility(View.VISIBLE);
		ePhone.setText(tPhone.getText());
		ePhone.requestFocus();
		tPhone.setVisibility(View.INVISIBLE);
	}

	public void emailOnClick(View view) {
		eEmail.setVisibility(View.VISIBLE);
		eEmail.setText(tEmail.getText());
		eEmail.requestFocus();
		tEmail.setVisibility(View.INVISIBLE);
	}

	public void skypeOnClick(View view) {
		eSkype.setVisibility(View.VISIBLE);
		eSkype.setText(tSkype.getText());
		eSkype.requestFocus();
		tSkype.setVisibility(View.INVISIBLE);
	}

	public void addressOnClick(View view) {
		eAddress.setVisibility(View.VISIBLE);
		eAddress.setText(tAddress.getText());
		eAddress.requestFocus();
		tAddress.setVisibility(View.INVISIBLE);
	}

	public void vkOnClick(View view) {
		startVKLoginActivity();

	}

	public void fbOnClick(View view) {
		startFBLoginActivity();

	}

	public void twOnClick(View view) {
		startTWLoginActivity();

	}

	public void gpOnClick(View view) {
		startGPLoginActivity();

	}

	public void okOnClick(View view) {
		startOKLoginActivity();

	}

	private void startVKLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginVkActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	private void startFBLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginFbActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	private void startTWLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginTwActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	private void startGPLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginGpActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	private void startOKLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginOkActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	public void LoadImageFromFileOnClick(View view) {
		LoadImageFromFile();
	}

	public void LoadImageFromCameraOnClick(View view) {

		LoadImageFromCamera();
	}

	public void LoadImageFromFile() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, IMAGE_SELECT);
	}

	public void LoadImageFromCamera()

	{

		/*
		 * String fileName = "temp.jpg"; ContentValues values = new
		 * ContentValues(); values.put(MediaStore.Images.Media.TITLE, fileName);
		 * vImageUri =
		 * getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		 * , values);
		 * 
		 * Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		 * intent.putExtra(MediaStore.EXTRA_OUTPUT, vImageUri);
		 * startActivityForResult(intent, CAMERA_CAPTURE);
		 */
		/*
		 * try { long FileName = System.currentTimeMillis(); File saveDir = new
		 * File(Environment.getExternalStorageDirectory()+ path); File f;
		 * 
		 * f = File.createTempFile(String.valueOf(FileName), ".jpg", saveDir);
		 * mPathLastPhoto = f.getAbsolutePath();
		 * //(saveDir.getAbsolutePath()+"/"+FileName+".jpg"); if
		 * (!saveDir.exists()) { saveDir.mkdirs(); } vImageUri =
		 * Uri.fromFile(f); Intent takePictureIntent = new
		 * Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		 * takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, vImageUri);
		 * startActivityForResult(takePictureIntent, CAMERA_CAPTURE); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		try {
			long FileName = System.currentTimeMillis();
			File saveDir = new File(Environment.getExternalStorageDirectory() + path);
			if (!saveDir.exists()) {
				saveDir.mkdirs();
			}
			File f;

			f = File.createTempFile(String.valueOf(FileName), ".jpg", saveDir);
			mPathLastPhoto = f.getAbsolutePath();
			// (saveDir.getAbsolutePath()+"/"+FileName+".jpg");

			vImageUri = Uri.fromFile(f);
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, vImageUri);
			startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setImageFile(File file) {
		int rotate = 0;
		if (file.exists()) {

			ExifInterface exif;
			try {
				exif = new ExifInterface(file.getAbsolutePath());

				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
				}

				Bitmap pinthumb = RotateBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()),
						rotate);

				Bitmap circleBitmap = Bitmap.createBitmap(pinthumb.getWidth(),
						pinthumb.getHeight(), Bitmap.Config.ARGB_8888);

				BitmapShader shader = new BitmapShader(pinthumb, TileMode.CLAMP, TileMode.CLAMP);
				Paint paint = new Paint();
				paint.setShader(shader);
				// int color = 0xff424242;
				// paint.setColor(color);
				Canvas c = new Canvas(circleBitmap);
				width = getResources().getDimensionPixelOffset(R.dimen.avatar_radius) * 2;
				height = getResources().getDimensionPixelOffset(R.dimen.avatar_radius) * 2;
				int secondPartOfImageW = 0;
				if (pinthumb.getWidth() < pinthumb.getHeight()) {
					secondPartOfImageW = pinthumb.getWidth() / 10;
				} else if (pinthumb.getWidth() > pinthumb.getHeight()) {
					secondPartOfImageW = pinthumb.getHeight() / 10;
				}
				// final int secondPartOfImageH = pinthumb.getHeight()/3;

				final Bitmap processed = ImageProccessor.getCroppedBitmap(pinthumb,
						secondPartOfImageW / 8, secondPartOfImageW / 8, 8 * secondPartOfImageW,
						8 * secondPartOfImageW);
				c.drawCircle(processed.getWidth() / 2, processed.getHeight() / 2,
						processed.getWidth() / 2, paint);

				Bitmap resized = Bitmap.createScaledBitmap(processed, width, height, true);
				// Bitmap conv_bm = getRoundedShape(circleBitmap);
				Bitmap conv_bm = getRoundedRectBitmap(resized, width);
				avatarImage.setImageBitmap(conv_bm);
				path_photo = file.getAbsolutePath();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void fillform() {
		account.restore(this);
		if (account.user_name != null && !account.user_name.equals("")) {
			tName.setText(account.user_name);
		}
		if (account.user_phone != null && !account.user_phone.equals("")) {
			tPhone.setText(account.user_phone);
			ePhone.setText(account.user_phone);
			ePhone.setVisibility(View.INVISIBLE);
		} else {
			tPhone.setVisibility(View.INVISIBLE);
		}

		if (account.user_email != null && !account.user_email.equals("")) {
			tEmail.setText(account.user_email);
			eEmail.setText(account.user_email);
			eEmail.setVisibility(View.INVISIBLE);
		} else {
			tEmail.setVisibility(View.INVISIBLE);
		}
		if (account.user_address != null && !account.user_address.equals("")) {
			tAddress.setText(account.user_address);
			eAddress.setText(account.user_address);
			eAddress.setVisibility(View.INVISIBLE);
		} else {
			tAddress.setVisibility(View.INVISIBLE);
		}

		if (account.user_skype != null && !account.user_skype.equals("")) {
			tSkype.setText(account.user_skype);
			eSkype.setText(account.user_skype);
			eSkype.setVisibility(View.INVISIBLE);
		} else {
			tSkype.setVisibility(View.INVISIBLE);
		}

		if (account.user_bonuses != null && !account.user_bonuses.equals("")) {
			tBonus.setText(account.user_bonuses);
		}

		if (account.user_notification != null && !account.user_notification.equals("")) {
			tNotification.setText(account.user_notification);
		}

		eLogin.setText(account.user_login);

		textWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				tSkype.setText(eSkype.getText());
				tAddress.setText(eAddress.getText());
				tEmail.setText(eEmail.getText());
				tPhone.setText(ePhone.getText());
			}
		};

		filter_listener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus && account.user_skype != null && !account.user_skype.equals("")
						&& v == eSkype) {
					eSkype.setVisibility(View.INVISIBLE);
					// eSkype.setText(Null);
					tSkype.setVisibility(View.VISIBLE);

				} else if (!hasFocus && account.user_address != null
						&& !account.user_address.equals("") && v == eAddress) {
					eAddress.setVisibility(View.INVISIBLE);
					// eAddress.setText(Null);
					tAddress.setVisibility(View.VISIBLE);
				} else if (!hasFocus && account.user_email != null
						&& !account.user_email.equals("") && v == eEmail) {
					eEmail.setVisibility(View.INVISIBLE);
					// eEmail.setText(Null);
					tEmail.setVisibility(View.VISIBLE);
				} else if (!hasFocus && account.user_phone != null
						&& !account.user_phone.equals("") && v == ePhone) {
					ePhone.setVisibility(View.INVISIBLE);
					// ePhone.setText(Null);
					tPhone.setVisibility(View.VISIBLE);
				}

			}
		};

		eSkype.setOnFocusChangeListener(filter_listener);
		eAddress.setOnFocusChangeListener(filter_listener);
		eEmail.setOnFocusChangeListener(filter_listener);
		ePhone.setOnFocusChangeListener(filter_listener);
		eSkype.addTextChangedListener(textWatcher);
		eAddress.addTextChangedListener(textWatcher);
		eEmail.addTextChangedListener(textWatcher);
		ePhone.addTextChangedListener(textWatcher);
		if (path_photo != null) {
			file = new File(path_photo);
		} else {
			File myDir = new File(Environment.getExternalStorageDirectory(), path);
			String fname = "avatar.jpg";

			file = new File(myDir, fname);
		}
		if (file.exists()) {
			setImageFile(file);
		}
	}

	public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
		Bitmap result = null;
		try {
			result = Bitmap.createBitmap(pixels, pixels, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(result);

			// int color = R.color.white;
			Paint paint = new Paint();
			Rect rect = new Rect(0, 0, pixels, pixels);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			// paint.setColor(color);
			int color = 0xff424242;
			paint.setColor(color);
			canvas.drawCircle(pixels / 2, pixels / 2, pixels / 2, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

		} catch (NullPointerException e) {
		} catch (OutOfMemoryError o) {
		}
		return result;
	}

	class GetTask extends AsyncTask<String, Integer, Void> {
		String resultString = null;
		String token_access = account.access_token;
		String response_bonuses = "bonuses";
		String response_notifications = "notifications";
		String resultS = "anyresult";
		String token = "token";
		String error = "error";

		@Override
		protected Void doInBackground(String... arg0) {
			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpPost httppost = new HttpPost("http://api.run4rebate.com/user/profile/&");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				nameValuePairs.add(new BasicNameValuePair("token", token_access));

				String Hash = nameValuePairs.toString().replace(", ", "&").replace("[", "&")
						.replace("]", "")
						+ Constants.CONTROL_SUM;
				String send_hash = md5(URLEncoder.encode(Hash, "UTF-8").replace("+", "%20"));
				nameValuePairs.add(new BasicNameValuePair("hash", send_hash));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

				HttpResponse response = httpclient.execute(httppost);

				BufferedReader reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent(), "utf-8"));
				// StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = reader.readLine()) != null) {
					resultString = line;
				}

				JSONObject jsonObj = new JSONObject(resultString);

				for (int i = 0; i < jsonObj.length(); i++) {

					if (jsonObj.names().getString(i).equals(response_bonuses)) {
						response_bonuses = jsonObj.getString(response_bonuses);
						account.user_bonuses = response_bonuses;
						resultS = "token";
					}

					else if (jsonObj.names().getString(i).equals(response_notifications)) {
						response_notifications = jsonObj.getString(response_notifications);
						account.user_notification = response_notifications;
						resultS = "token";
					} else if (jsonObj.names().getString(i).equals(error)) {
						resultString = jsonObj.getJSONObject("error").getString("message");
						if (!resultString.equals("null")) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (resultS.equals(token)) {
				account.save(ActivityAccount.this);
				fillform();
				// startAccountActivity();
			} else {
				Toast.makeText(getApplicationContext(), resultString, Toast.LENGTH_LONG).show();
			}
		}

	}

	class UpdateTask extends AsyncTask<String, Integer, Void> {
		String resultString = null;
		String name = tName.getText().toString();
		String login = eLogin.getText().toString();
		String password = ePassword.getText().toString();
		String phone = ePhone.getText().toString();
		String email = eEmail.getText().toString();
		String address = eAddress.getText().toString();
		String skype = eSkype.getText().toString();
		String token_access = account.access_token;
		// String avatar_id = "0";
		String response_phone = "phone";
		String response_bonuses = "bonuses";
		String response_notifications = "notifications";
		String response_address = "address";
		String response_email = "email";
		String response_skype = "skype";

		String error = "error";
		String resultS = "anyresult";
		String token = "token";

		@Override
		protected Void doInBackground(String... categories) {

			if (login.equals(Null) && password.equals(Null) && email.equals(Null)
					&& skype.equals(Null) && phone.equals(Null) && address.equals(Null)) {
				resultString = "Нет данных для обновления";
			} else {
				try {
					if (path_photo != null) {
						String Hash = md5(URLEncoder.encode(Constants.CONTROL_SUM, "UTF-8"));
						sendMedia(url + "&hash=" + Hash, path_photo, "");
					}
					HttpClient httpclient = new DefaultHttpClient();

					HttpPost httppost = new HttpPost("http://api.run4rebate.com/user/update/&");
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
					account.user_name = name;
					account.user_login = login;
					account.user_phone = phone;
					account.user_email = email;
					account.user_address = address;
					account.user_skype = skype;
					account.user_photo = path_photo;

					nameValuePairs.add(new BasicNameValuePair("address", address));
					nameValuePairs.add(new BasicNameValuePair("avatar_id", avatar_id));
					nameValuePairs.add(new BasicNameValuePair("email", email));
					nameValuePairs.add(new BasicNameValuePair("token", token_access));
					nameValuePairs.add(new BasicNameValuePair("login", login));
					nameValuePairs.add(new BasicNameValuePair("name", name));

					nameValuePairs.add(new BasicNameValuePair("phone", phone));
					nameValuePairs.add(new BasicNameValuePair("skype", skype));

					String Hash = nameValuePairs.toString().replace(", ", "&").replace("[", "&")
							.replace("]", "")
							+ Constants.CONTROL_SUM;
					String send_hash = md5(URLEncoder.encode(Hash, "UTF-8").replace("+", "%20"));
					nameValuePairs.add(new BasicNameValuePair("hash", send_hash));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

					HttpResponse response = httpclient.execute(httppost);

					BufferedReader reader = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent(), "utf-8"));
					// StringBuilder sb = new StringBuilder();
					String line = null;

					while ((line = reader.readLine()) != null) {
						resultString = line;

					}

					JSONObject jsonObj = new JSONObject(resultString);

					for (int i = 0; i < jsonObj.length(); i++) {

						if (jsonObj.names().getString(i).equals(response_phone)) {
							response_phone = jsonObj.getString(response_phone);
							account.user_phone = response_phone;
							resultS = "token";
						} else if (jsonObj.names().getString(i).equals(response_bonuses)) {
							response_bonuses = jsonObj.getString(response_bonuses);
							account.user_bonuses = response_bonuses;
							resultS = "token";
						}

						else if (jsonObj.names().getString(i).equals(response_notifications)) {
							response_notifications = jsonObj.getString(response_notifications);
							account.user_notification = response_notifications;
							resultS = "token";
						}

						else if (jsonObj.names().getString(i).equals(response_address)) {
							response_address = jsonObj.getString(response_address);
							account.user_address = response_address;
							resultS = "token";
						} else if (jsonObj.names().getString(i).equals(response_email)) {
							response_email = jsonObj.getString(response_email);
							account.user_email = response_email;
							resultS = "token";
						} else if (jsonObj.names().getString(i).equals(response_skype)) {
							response_skype = jsonObj.getString(response_skype);
							account.user_skype = response_skype;
							resultS = "token";
						} else if (jsonObj.names().getString(i).equals(error)) {
							resultString = jsonObj.getJSONObject("error").getString("message");
							if (!resultString.equals("null")) {
								resultS = jsonObj.names().getString(i);
							}
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
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (resultS.equals(token)) {
				account.save(ActivityAccount.this);
				fillform();
				// startAccountActivity();
			} else {
				Toast.makeText(getApplicationContext(), resultString, Toast.LENGTH_LONG).show();
			}
		}
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
				true);
	}

	public void setImageUri(Uri imageUri) {
		if (imageUri == null)
			return;

		mImageUri = imageUri;
		String path_this = getRealPathFromURI(mImageUri);

		try {

			Bitmap pinthumb = imageFromUri(this, mImageUri, 400, 400);
			Bitmap circleBitmap = Bitmap.createBitmap(pinthumb.getWidth(), pinthumb.getHeight(),
					Bitmap.Config.ARGB_8888);

			BitmapShader shader = new BitmapShader(pinthumb, TileMode.CLAMP, TileMode.CLAMP);
			Paint paint = new Paint();
			paint.setShader(shader);
			// int color = 0xff424242;
			// paint.setColor(color);
			Canvas c = new Canvas(circleBitmap);
			c.drawCircle(pinthumb.getWidth() / 2, pinthumb.getHeight() / 2,
					pinthumb.getWidth() / 2, paint);
			width = avatarImage.getWidth();
			height = avatarImage.getHeight();
			Bitmap resized = Bitmap.createScaledBitmap(pinthumb, height, width, true);
			// Bitmap conv_bm = getRoundedShape(circleBitmap);

			Bitmap conv_bm = getRoundedRectBitmap(resized, width);
			path_photo = path_this;
			// sendMedia(url, path_this, "");
			avatarImage.setImageBitmap(conv_bm);

		} catch (IOException ignored) {
		}
	}

	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(ActivityAccount.this, contentUri, proj, null, null,
				null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static Bitmap imageFromUri(Context context, Uri uri, int width, int height)
			throws IOException {
		ContentResolver resolver = context.getContentResolver();
		InputStream input = resolver.openInputStream(uri);

		// Just get some info
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDensity = Bitmap.DENSITY_NONE;
		onlyBoundsOptions.inPurgeable = true;
		onlyBoundsOptions.inInputShareable = true;
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		int xSample = 0, ySample = 0;
		if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
			return null;
		else {
			xSample = (int) Math.floor(onlyBoundsOptions.outWidth / width);
			ySample = (int) Math.floor(onlyBoundsOptions.outHeight / height);
		}

		// Decode
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmapOptions.inDensity = Bitmap.DENSITY_NONE;
		bitmapOptions.inPurgeable = true;
		bitmapOptions.inInputShareable = true;
		bitmapOptions.inSampleSize = Math.min(xSample, ySample);

		input = context.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();

		return bitmap;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_LOGIN) {
			if (resultCode == RESULT_OK) {
				account.access_token_VK = data.getStringExtra("access_token_VK");
				account.access_token_FB = data.getStringExtra("access_token_FB");
				account.access_token_TW = data.getStringExtra("access_token_TW");
				account.access_secret_TW = data.getStringExtra("access_secret_TW");
				account.access_token_OK = data.getStringExtra("access_token_OK");
				account.access_expires_FB = data.getLongExtra("access_expires_FB", 0);
				account.user_id_VK = data.getLongExtra("user_id_VK", 0);
				account.user_id_TW = data.getLongExtra("user_id_TW", 0);
				account.user_id_GP = data.getLongExtra("user_id_GP", 0);
				account.save(ActivityAccount.this);

			}

		} else if (requestCode == CAMERA_CAPTURE) {

			if (resultCode == Activity.RESULT_OK) {

				// String path_this = getRealPathFromURI(vImageUri);

				File fileC = new File(mPathLastPhoto);
				setImageFile(fileC);

			}

		}

		else if (requestCode == IMAGE_SELECT) {
			if (resultCode == Activity.RESULT_OK) {
				// Check for returned image from gallery
				if (data == null)
					return;

				Uri imageUri = data.getData();
				String path_this = getRealPathFromURI(imageUri);
				// setImageUri(imageUri);
				File fileG = new File(path_this);
				setImageFile(fileG);

			}
		}
	}

	public static void sendMedia(String urlAddr, String filePath, String description) {
		final String serverAddress = urlAddr;
		final File file = new File(filePath);

		Runnable r = new Runnable() {
			public void run() {
				String requestString = serverAddress;
				String resultString = new String("");
				StringBuffer requestBody = new StringBuffer();

				try {

					URLConnection connection = null;
					URL url = new URL(requestString);

					connection = url.openConnection();

					// ///////////////////////////

					String BOUNDRY = "*****";

					HttpURLConnection httpConnection = (HttpURLConnection) connection;
					httpConnection.setRequestMethod("POST");

					httpConnection.setUseCaches(false);

					httpConnection.setDoOutput(true);
					httpConnection.setDoInput(true);

					httpConnection.setRequestProperty("User-Agent", "MyAndroid/1.6");
					httpConnection.setRequestProperty("Content-Language", "ru-RU");
					httpConnection.setRequestProperty("Content-Type",
							"multipart/form-data; boundary=" + BOUNDRY);
					// httpConnection.setRequestProperty("Content-Type",
					// "image/jpeg; boundary=" + BOUNDRY);
					httpConnection.setRequestProperty("Connection", "Keep-Alive");

					// собрать буфер для отправки запроса
					String contentDisposition = "Content-Disposition: form-data; name=\"data\"; filename=\""
							+ file.getName() + "\"";
					String contentType = "Content-Type: application/octet-stream\nContent-Transfer-Encoding: binary";

					requestBody.append("--");
					requestBody.append(BOUNDRY);
					requestBody.append('\n');

					requestBody.append(contentDisposition);
					requestBody.append('\n');
					requestBody.append(contentType);
					requestBody.append('\n');
					requestBody.append('\n');

					int bytesAvailable, bufferSize, bytesRead, endBlockSize;
					byte[] buffer;
					int maxBufferSize = 1 * 1024 * 1024;
					FileInputStream fileInputStream = new FileInputStream(file);

					bytesAvailable = fileInputStream.available();

					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					buffer = new byte[bufferSize];

					// установка Content-Length

					endBlockSize = BOUNDRY.length() + 6;

					httpConnection.setRequestProperty(
							"Content-Length",
							String.valueOf(requestBody.toString().length() + bufferSize
									+ endBlockSize));

					// соединение
					httpConnection.connect();

					// отправка первой части запроса
					DataOutputStream dataOS = new DataOutputStream(httpConnection.getOutputStream());
					dataOS.writeBytes(requestBody.toString());

					// отправка файла
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					dataOS.write(buffer, 0, bytesRead);

					// ////////////////////////////////////////////

					dataOS.writeBytes("\n");
					dataOS.writeBytes("--");
					dataOS.writeBytes(BOUNDRY);
					dataOS.writeBytes("--");
					dataOS.writeBytes("\n");

					fileInputStream.close();

					dataOS.flush();
					dataOS.close();

					int responseCode = httpConnection.getResponseCode();
					if (responseCode == 200) {
						// если все прошло нормально, получаем результат
						// может быть другой код, см.
						// http://developer.android.com/reference/java/net/HttpURLConnection.html

						InputStream in = httpConnection.getInputStream();

						InputStreamReader isr = new InputStreamReader(in, "UTF-8");

						StringBuffer data = new StringBuffer();
						int c;
						while ((c = isr.read()) != -1) {
							data.append((char) c);
						}

						resultString = new String(data.toString());

					} else {
						resultString = "сервер не ответил";
					}

					JSONObject jsonObj = new JSONObject(resultString);
					for (int i = 0; i < jsonObj.length(); i++) {

						if (jsonObj.names().getString(i).equals("img_id")) {
							resultString = jsonObj.getString("img_id");
							avatar_id = resultString;
						}

						else if (jsonObj.names().getString(i).equals("error")) {
							resultString = jsonObj.getJSONObject("error").getString("message");
						}
					}

				} catch (MalformedURLException ex) {
					Log.e("uploader", "URL error: " + ex.getMessage(), ex);
				}

				catch (IOException ioe) {
					Log.e("uploader", "IO error: " + ioe.getMessage(), ioe);
				} catch (Exception e) {

					resultString = "Exception:" + e.getMessage();
				}

				requestBody = null;
				System.gc();

			}
		};
		new Thread(r).start();
	}

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
}
