package mk.run4rebate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import ru.ok.android.sdk.Odnoklassniki;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;
import com.perm.kate.api.Api;
import com.perm.kate.api.KException;
import com.perm.kate.api.User;

public class ActivityRegistration extends Activity {
	private static final int CAMERA_CAPTURE = 0;
	private static final int IMAGE_SELECT = 1;
	private final int REQUEST_LOGIN = 2;
	private final int REGISTRATION = 3;
	public Dialog dialog;
	public Bitmap bmImg = null;
	public String url = "http://api.run4rebate.com/user/upload_img/&";
	public static String avatar_id = "0";
	Account account = new Account();
	Api vkApi;
	double lat, lng;
	Facebook fbApi;
	Odnoklassniki okApi;
	EditText editName, editLogin, editPassword, editEmail, editPhone, editAddress, editSkype;
	File file;
	ImageView avatarImage;
	int width = 0;
	int height = 0;
	int version;
	Session sessionCurrent;
	@SuppressLint("SdCardPath")
	String path = "/Run4Rebate/Images/";
	String mPathPhoto, mPathLastPhoto;
	String path_photo;
	String city = null, country = null, last_name = null, first_name = null, phone = null,
			skype = null, photo = null, email = null, avatar_file = null;
	String Name = "", Phone = "", Address = "", Skype = "", Photo = "", Email = "", Null = "";
	taskSocial taskSoc;
	taskDownloadPhoto taskDP;
	taskRegistration taskReg;
	taskLogin taskLogin;
	Uri vImageUri;
	String userName, userPassword;
	// private AdView adView;
	private MMAdView adView;
	GPSTracker gps;
	String URL = "http://run4rebate.com/uploads/";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration2);

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
		version = Integer.valueOf(android.os.Build.VERSION.SDK);
		avatarImage = (ImageView) findViewById(R.id.imageAvatar);
		editName = (EditText) findViewById(R.id.editNameReg);
		editLogin = (EditText) findViewById(R.id.editLoginReg);
		editPassword = (EditText) findViewById(R.id.editPasswordReg);
		editEmail = (EditText) findViewById(R.id.editEmailReg);
		editPhone = (EditText) findViewById(R.id.editPhoneReg);
		editAddress = (EditText) findViewById(R.id.editAddressReg);
		editSkype = (EditText) findViewById(R.id.editSkypeReg);
		ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView1);
		if (version > 10) {
			scrollView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
		}
		if (account.access_token_VK != null) {
			vkApi = new Api(account.access_token_VK, Constants.API_ID_VK);

		}
		if (account.access_token_OK != null) {
			okApi = Odnoklassniki.createInstance(this, Constants.API_ID_OK,
					Constants.APP_SECRET_OK, Constants.APP_KEY_OK);
			okApi.clearTokens(this);
		}

		/*
		 * final LocationManager locationManager = (LocationManager)
		 * this.getSystemService(Context.LOCATION_SERVICE);
		 * 
		 * final LocationListener locationListener = new LocationListener() {
		 * 
		 * @Override public void onStatusChanged(String provider, int status,
		 * Bundle extras) {
		 * 
		 * 
		 * }
		 * 
		 * @Override public void onProviderEnabled(String provider) {}
		 * 
		 * @Override public void onProviderDisabled(String provider) {}
		 * 
		 * 
		 * @Override public void onLocationChanged(Location location) { // TODO
		 * Auto-generated method stub lat = location.getLatitude(); lng =
		 * location.getLongitude(); } };
		 * 
		 * Criteria criteria = new Criteria();
		 * criteria.setAccuracy(Criteria.ACCURACY_COARSE); String provider =
		 * locationManager.getBestProvider(criteria, true);
		 * locationManager.requestLocationUpdates
		 * (LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		 */
		gps = new GPSTracker(this);

		// check if GPS enabled
		if (gps.canGetLocation()) {

			lat = gps.getLatitude();
			lng = gps.getLongitude();

			// \n is for new line
			/*
			 * Toast.makeText( getApplicationContext(),
			 * "Your Location is - \nLat: " + latitude + "\nLong: " + longitude,
			 * Toast.LENGTH_LONG).show();
			 */
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}

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

	public void AccountLoginOnClick(View view) {
		final Dialog dialog = new Dialog(ActivityRegistration.this);

		dialog.setContentView(R.layout.login);
		dialog.setTitle("Login");

		// get the Refferences of views
		final EditText editTextUserName = (EditText) dialog
				.findViewById(R.id.editTextUserNameToLogin);
		final EditText editTextPassword = (EditText) dialog
				.findViewById(R.id.editTextPasswordToLogin);

		Button btnSignIn = (Button) dialog.findViewById(R.id.buttonSignIn);

		// Set On ClickListener
		btnSignIn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// get The User name and Password
				userName = editTextUserName.getText().toString();
				userPassword = editTextPassword.getText().toString();

				// fetch the Password form database for respective user name
				taskLogin = new taskLogin();
				taskLogin.execute("");

			}
		});

		dialog.show();
	}

	public void imageButtonCameraOnClick(View view) {
		actions(CAMERA_CAPTURE);
	}

	public void imageButtonImageOnClick(View view) {
		actions(IMAGE_SELECT);
	}

	public void AccountRegistrationOnClick(View view) {
		actions(REGISTRATION);
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

	public void actions(int action) {
		switch (action) {
		case 0:
			LoadImageFromCamera();
			break;
		case 1:
			LoadImageFromFile();
			break;
		case 3:
			RegisterProfile();
			break;
		default:
		}
	}

	public void LoadImageFromFile() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, IMAGE_SELECT);
	}

	public void LoadImageFromCamera()

	{

		try {
			Toast toast = Toast.makeText(getApplicationContext(), "Обработчик нажатия кнопки",
					Toast.LENGTH_SHORT);
			toast.show();
			long FileName = System.currentTimeMillis();
			File saveDir = new File(Environment.getExternalStorageDirectory() + path);
			if (!saveDir.exists()) {
				saveDir.mkdirs();
				toast = Toast.makeText(getApplicationContext(),
						"Создаём папку " + saveDir.getAbsolutePath(), Toast.LENGTH_SHORT);
				toast.show();
			}
			File f;
			f = File.createTempFile(String.valueOf(FileName), ".jpg", saveDir);
			toast = Toast.makeText(getApplicationContext(),
					"Создаём временный файл " + f.getAbsolutePath(), Toast.LENGTH_SHORT);
			toast.show();
			mPathLastPhoto = f.getAbsolutePath();
			// (saveDir.getAbsolutePath()+"/"+FileName+".jpg");

			vImageUri = Uri.fromFile(f);
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, vImageUri);
			toast = Toast
					.makeText(getApplicationContext(), "Запускаем камеру  ", Toast.LENGTH_SHORT);
			toast.show();
			startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast toast = Toast.makeText(getApplicationContext(),
					"Ошибка ввода/вывода  " + e.toString(), Toast.LENGTH_SHORT);
			toast.show();
		}

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

	public void startAccountActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityAccount.class);
		startActivity(intent);
		finish();
	}

	private void startAddSearchActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityAddSearch.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	public void RegisterProfile() {
		taskReg = new taskRegistration();
		taskReg.execute("start");
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
				true);
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

	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(ActivityRegistration.this, contentUri, proj, null,
				null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);

	}

	public void setImageFile(File file) {
		Toast toast = Toast.makeText(getApplicationContext(), "Запускаем обработчик фотографии ",
				Toast.LENGTH_SHORT);
		toast.show();
		int rotate = 0;
		if (file.exists()) {
			toast = Toast.makeText(getApplicationContext(), "Файл есть ", Toast.LENGTH_SHORT);
			toast.show();
			ExifInterface exif;
			try {
				exif = new ExifInterface(file.getAbsolutePath());

				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

				toast = Toast.makeText(getApplicationContext(),
						"Запускаем обработчик поворота фотографии ", Toast.LENGTH_SHORT);
				toast.show();

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
				if (pinthumb.getWidth() <= pinthumb.getHeight()) {
					secondPartOfImageW = pinthumb.getWidth() / 10;
				} else if (pinthumb.getWidth() > pinthumb.getHeight()) {
					secondPartOfImageW = pinthumb.getHeight() / 10;
				}
				toast = Toast.makeText(getApplicationContext(), "Обрезаем фото ",
						Toast.LENGTH_SHORT);
				toast.show();
				// final int secondPartOfImageH = pinthumb.getHeight()/3;

				final Bitmap processed = ImageProccessor.getCroppedBitmap(pinthumb,
						secondPartOfImageW / 8, secondPartOfImageW / 8, 8 * secondPartOfImageW,
						8 * secondPartOfImageW);
				toast = Toast.makeText(getApplicationContext(), "Делаем его круглым ",
						Toast.LENGTH_SHORT);
				toast.show();
				c.drawCircle(processed.getWidth() / 2, processed.getHeight() / 2,
						processed.getWidth() / 2, paint);

				toast = Toast.makeText(getApplicationContext(), "Сжимаем до нужного размера "
						+ width + " " + height, Toast.LENGTH_SHORT);
				toast.show();
				Bitmap resized = Bitmap.createScaledBitmap(processed, width, height, true);

				// Bitmap conv_bm = getRoundedShape(circleBitmap);
				Bitmap conv_bm = getRoundedRectBitmap(resized, width);

				toast = Toast.makeText(getApplicationContext(), "Вставляем его в ImageView",
						Toast.LENGTH_SHORT);
				toast.show();
				avatarImage.setImageBitmap(conv_bm);
				mPathPhoto = file.getAbsolutePath();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				toast = Toast.makeText(getApplicationContext(),
						"Ошибка ввода/вывода в обработчике вывода изображения" + e.toString(),
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast toast = Toast.makeText(getApplicationContext(), "Возврат в приложение ",
				Toast.LENGTH_SHORT);
		toast.show();
		if (requestCode == CAMERA_CAPTURE) {

			toast = Toast.makeText(getApplicationContext(), "Получаем код возврата из камеры "
					+ requestCode + " = " + CAMERA_CAPTURE, Toast.LENGTH_SHORT);
			toast.show();

			if (resultCode == Activity.RESULT_OK) {
				toast = Toast.makeText(getApplicationContext(), "Получаем код ответа от камеры "
						+ resultCode + " = " + Activity.RESULT_OK, Toast.LENGTH_SHORT);
				toast.show();
				// String path_this = getRealPathFromURI(vImageUri);

				File fileC = new File(mPathLastPhoto);
				toast = Toast.makeText(getApplicationContext(), "Получаем адрес файла изображения "
						+ fileC.getAbsolutePath(), Toast.LENGTH_SHORT);
				toast.show();
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
		if (requestCode == REQUEST_LOGIN) {
			if (resultCode == RESULT_OK) {
				account.access_token_VK = data.getStringExtra("access_token_VK");
				account.access_token_FB = data.getStringExtra("access_token_FB");
				account.access_token_TW = data.getStringExtra("access_token_TW");
				account.access_secret_TW = data.getStringExtra("access_secret_TW");
				account.access_token_OK = data.getStringExtra("access_token_OK");
				account.user_name = data.getStringExtra("user_name");
				account.user_photo = data.getStringExtra("user_photo");
				account.user_first_name = data.getStringExtra("user_first_name");
				account.user_last_name = data.getStringExtra("user_last_name");
				account.user_email = data.getStringExtra("user_email");

				account.user_address = data.getStringExtra("user_address");
				account.user_city = data.getStringExtra("user_city");
				account.user_country = data.getStringExtra("user_country");

				account.access_expires_FB = data.getLongExtra("access_expires_FB", 0);
				account.user_id_VK = data.getLongExtra("user_id_VK", 0);
				account.user_id_TW = data.getLongExtra("user_id_TW", 0);
				account.user_id_GP = data.getLongExtra("user_id_GP", 0);
				account.save(ActivityRegistration.this);
				taskSoc = new taskSocial();
				taskSoc.execute(data.getStringExtra("social"));
				dialog = new Dialog(ActivityRegistration.this);
				dialog.setContentView(R.layout.wait);
				dialog.setTitle("Getting profile data");
				dialog.show();

			}

		}
	}

	public void setProfileSocial(String city, String country, String last_name, String first_name,
			String phone, String skype, String photo, String email) {
		editName.setText(Null);
		editLogin.setText(Null);
		editPassword.setText(Null);
		editEmail.setText(Null);
		editPhone.setText(Null);
		editAddress.setText(Null);
		editSkype.setText(Null);

		Name = Null;
		Email = Null;
		Phone = Null;
		Address = Null;
		Skype = Null;
		Photo = Null;

		if (last_name != null) {
			Name = Name + last_name.toString();
		}
		if (first_name != null) {
			Name = Name + " " + first_name.toString();
		}

		if (country != null) {
			Address = Address + country.toString();
		}
		if (city != null) {
			Address = Address + " " + city.toString();
		}
		if (phone != null) {
			Phone = Phone + phone.toString();
		}
		if (skype != null) {
			Skype = Skype + skype.toString();
		}

		if (email != null) {
			Email = Email + email.toString();
		}

		if (Name != null || Name.equals(Null)) {
			editName.setText(Name);
		}
		if (Address != null || Address.equals(Null)) {
			editAddress.setText(Address);
		}
		if (Phone != null || Phone.equals(Null)) {
			editPhone.setText(Phone);
		}
		if (Skype != null || Skype.equals(Null)) {
			editSkype.setText(Skype);
		}
		if (Email != null || Email.equals(Null)) {
			editEmail.setText(Email);
		}
		if (photo != null) {
			taskDP = new taskDownloadPhoto();
			taskDP.execute(photo);
			Photo = Photo + photo;
		}

		if (dialog.isShowing()) {
			try {
				dialog.dismiss();
			} catch (Exception e) {
			}
		}

	}

	public void getProfileFB(final Session vSession) {

		// final Session rSession = vSession;
		Request request = Request.newMeRequest(vSession, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If the response is successful
				if (Session.getActiveSession().isOpened()) {
					if (user != null) {
						// Set the id for the ProfilePictureView
						// view that in turn displays the profile
						// picture.
						// Set the Textview's text to the user's name.
						// textInstructionsOrLink.setText(user.getName()+" "+user.asMap().get("email"));
						last_name = user.getLastName();
						first_name = user.getFirstName();
						// downloadFile("http://graph.facebook.com/"+user.getId()+"/picture?type=large");
						String URL = "http://graph.facebook.com/" + user.getId()
								+ "/picture?type=large";
						taskDP = new taskDownloadPhoto();
						taskDP.execute(URL);
						email = user.asMap().get("email").toString();
						String hometown = user.asMap().get("hometown").toString();
						try {
							JSONObject jsonObj = new JSONObject(hometown);
							city = jsonObj.getString("name");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						setProfileSocial(city, country, last_name, first_name, phone, skype, photo,
								email);
					}
				}
				if (response.getError() != null) {
					// Handle errors, will do so later.
				}
			}
		});

		request.executeAsync();
	}

	class taskDownloadPhoto extends AsyncTask<String, Integer, Void> {
		String operation;

		File file;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(String... urls) {
			try {
				for (String url : urls) {
					operation = url;
					File myDir = new File(Environment.getExternalStorageDirectory(), path);

					myDir.mkdirs();

					Random generator = new Random();

					int n = 10000;

					n = generator.nextInt(n);

					String fname = "avatar.jpg";

					file = new File(myDir, fname);

					if (file.exists()) {
						file.delete();
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
				TimeUnit.SECONDS.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (operation.equals("share")) {

			} else {
				setImageFile(file);

				Toast.makeText(getApplicationContext(), "Изображение успешно сохранено",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	class taskLogin extends AsyncTask<String, Integer, Void> {
		String token = "token";
		String error = "error";
		String resultS = "anyresult";
		String resultString = null;
		String name = "";
		String login = "";
		String address = "";

		@Override
		protected Void doInBackground(String... arg0) {
			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpPost httppost = new HttpPost("http://api.run4rebate.com/user/login/&");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);

				nameValuePairs.add(new BasicNameValuePair("login", userName));
				nameValuePairs.add(new BasicNameValuePair("password", userPassword));
				String Hash = nameValuePairs.toString().replace(", ", "&").replace("[", "&")
						.replace("]", "")
						+ Constants.CONTROL_SUM;
				String send_hash = md5(URLEncoder.encode(Hash, "UTF-8"));
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

					if (jsonObj.names().getString(i).equals(token)) {
						resultString = jsonObj.getString("token");
						account.access_token = resultString;
						resultS = jsonObj.names().getString(i);
						HttpClient httpclientL = new DefaultHttpClient();

						HttpPost httppostL = new HttpPost(
								"http://api.run4rebate.com/user/profile/&");
						List<NameValuePair> nameValuePairsL = new ArrayList<NameValuePair>(7);

						nameValuePairsL.add(new BasicNameValuePair("token", jsonObj
								.getString("token")));
						Hash = nameValuePairs.toString().replace(", ", "&").replace("[", "&")
								.replace("]", "")
								+ Constants.CONTROL_SUM;
						send_hash = md5(URLEncoder.encode(Hash, "UTF-8").replace("+", "%20"));
						nameValuePairs.add(new BasicNameValuePair("hash", send_hash));
						httppostL.setEntity(new UrlEncodedFormEntity(nameValuePairsL, "utf-8"));

						HttpResponse responseL = httpclientL.execute(httppostL);

						BufferedReader readerL = new BufferedReader(new InputStreamReader(responseL
								.getEntity().getContent(), "utf-8"));
						// StringBuilder sb = new StringBuilder();
						String lineL = null;

						while ((lineL = readerL.readLine()) != null) {
							resultString = lineL;

						}

						JSONObject jsonObjL = new JSONObject(resultString);

						for (int j = 0; j < jsonObjL.length(); j++) {

							if (jsonObjL.names().getString(j).equals("name")) {

								name = jsonObjL.getString("name");
								account.user_name = name;
								continue;
							} else if (jsonObjL.names().getString(j).equals("login")) {

								login = jsonObjL.getString("login");
								account.user_login = login;
								continue;
							} else if (jsonObjL.names().getString(j).equals("phone")) {

								phone = jsonObjL.getString("phone");
								account.user_phone = phone;
								continue;
							} else if (jsonObjL.names().getString(j).equals("email")) {

								email = jsonObjL.getString("email");
								account.user_email = email;
								continue;
							} else if (jsonObjL.names().getString(j).equals("address")) {

								address = jsonObjL.getString("address");
								account.user_address = address;
								continue;
							} else if (jsonObjL.names().getString(j).equals("skype")) {

								skype = jsonObjL.getString("skype");
								account.user_skype = skype;
								continue;
							} else if (jsonObjL.names().getString(j).equals("avatar")) {

								avatar_file = jsonObjL.getString("avatar");

								continue;
							} else if (jsonObjL.names().getString(j).equals("bonuses")) {

								account.user_bonuses = jsonObjL.getString("bonuses");
								continue;
							} else if (jsonObjL.names().getString(j).equals("notifications")) {

								account.user_notification = jsonObjL.getString("notifications");
								continue;
							}

						}
					}

					else if (jsonObj.names().getString(i).equals(error)) {
						resultString = jsonObj.getJSONObject("error").getString("message");
						resultS = jsonObj.names().getString(i);
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
				taskDP = new taskDownloadPhoto();
				taskDP.execute(URL + avatar_file);
				account.save(ActivityRegistration.this);
				startAccountActivity();
			} else {
				Toast.makeText(getApplicationContext(), resultString, Toast.LENGTH_LONG).show();
			}
			// mTest.setText(resultString);

		}

	}

	class taskRegistration extends AsyncTask<String, Integer, Void> {
		String resultString = null;
		String name = editName.getText().toString();
		String login = editLogin.getText().toString();
		String password = editPassword.getText().toString();
		String phone = editPhone.getText().toString();
		String email = editEmail.getText().toString();
		String address = editAddress.getText().toString().replace(", ", ",");
		String skype = editSkype.getText().toString();

		// String avatar_id = "0";
		String token = "token";
		String error = "error";
		String resultS = "anyresult";

		@Override
		protected Void doInBackground(String... categories) {

			if (/* name.equals(Null) || */login.equals(Null) || password.equals(Null)
					|| email.equals(Null) /*
										 * || phone.equals (Null) || address
										 * .equals (Null)
										 */) {
				resultString = "Заполнены не все поля";
			} else {
				try {
					if (path_photo != null) {
						String send_hash = md5(URLEncoder.encode(Constants.CONTROL_SUM, "UTF-8"));
						sendMedia(url + "&hash=" + send_hash, path_photo, "");
					}
					HttpClient httpclient = new DefaultHttpClient();

					HttpPost httppost = new HttpPost("http://api.run4rebate.com/user/signup/&");
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
					account.user_name = name;
					account.user_login = login;
					account.user_phone = phone;
					account.user_email = email;
					account.user_address = address;
					account.user_skype = skype;
					account.user_photo = mPathPhoto;

					nameValuePairs.add(new BasicNameValuePair("address", address));
					nameValuePairs.add(new BasicNameValuePair("avatar_id", avatar_id));
					nameValuePairs.add(new BasicNameValuePair("email", email));
					nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(lat)));
					nameValuePairs.add(new BasicNameValuePair("lng", String.valueOf(lng)));
					nameValuePairs.add(new BasicNameValuePair("login", login));
					nameValuePairs.add(new BasicNameValuePair("name", name));
					nameValuePairs.add(new BasicNameValuePair("password", password));
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

						if (jsonObj.names().getString(i).equals(token)) {
							resultString = jsonObj.getString("token");
							account.access_token = resultString;
							resultS = jsonObj.names().getString(i);
						}

						else if (jsonObj.names().getString(i).equals(error)) {
							resultString = jsonObj.getJSONObject("error").getString("message");
							resultS = jsonObj.names().getString(i);
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
				account.save(ActivityRegistration.this);
				startAccountActivity();
			} else {
				Toast.makeText(getApplicationContext(), resultString, Toast.LENGTH_LONG).show();
			}
			// mTest.setText(resultString);
		}
	}

	class taskSocial extends AsyncTask<String, Integer, Void> {

		Session currentSession;
		String currentSocial;

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(String... categories) {

			if (categories[0].equals("vk")) {
				currentSocial = categories[0];
				vkApi = new Api(account.access_token_VK, Constants.API_ID_VK);
				Collection<Long> userIds = new ArrayList<Long>();
				userIds.add(account.user_id_VK);
				try {/*
					 * photo_50, photo_100,photo_200_orig, photo_200,
					 * photo_400_orig,
					 */
					ArrayList<User> users = vkApi
							.getProfiles(
									userIds,
									null,
									" photo_50, photo_100, photo_200_orig, photo_400_orig, photo_max_orig, photo_200, photo_max,uid, first_name, last_name, city, country, sex, connections, contacts",
									null, null, null);
					Collection<Long> cityIds = new ArrayList<Long>();
					Collection<Long> countryIds = new ArrayList<Long>();
					cityIds.add(Long.parseLong(users.get(0).city.toString()));
					countryIds.add(Long.parseLong(users.get(0).country.toString()));
					city = vkApi.getCities(cityIds).get(0).name;
					country = vkApi.getCountries(countryIds).get(0).name;
					last_name = users.get(0).last_name;
					first_name = users.get(0).first_name;

					phone = users.get(0).mobile_phone;
					skype = users.get(0).skype;
					if (users.get(0).photo_200 != null) {
						photo = users.get(0).photo_200;
					}
					if (users.get(0).photo != null) {
						photo = users.get(0).photo;
					}
					if (users.get(0).photo_big != null) {
						photo = users.get(0).photo_big;
					}
					if (users.get(0).photo_medium_rec != null) {
						photo = users.get(0).photo_medium_rec;
					}

				}

				catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (KException e) {
					e.printStackTrace();
				}

			} else if (categories[0].equals("fb")) {
				currentSocial = categories[0];
				fbApi = new Facebook(Constants.API_ID_FB);
				fbApi.setAccessToken(account.access_token_FB);
				fbApi.setAccessExpires(account.access_expires_FB);
				currentSession = fbApi.getSession();
				sessionCurrent = Session.getActiveSession();
				if (sessionCurrent == null) {
					sessionCurrent = new Session(ActivityRegistration.this);
				}
				Session.setActiveSession(currentSession);

			} else if (categories[0].equals("tw")) {
				currentSocial = categories[0];
				last_name = account.user_name;
				first_name = "";
				photo = account.user_photo;
			} else if (categories[0].equals("gp")) {
				currentSocial = categories[0];
				last_name = account.user_last_name;
				first_name = account.user_first_name;
				email = account.user_email;
				photo = account.user_photo;
			} else if (categories[0].equals("ok")) {
				currentSocial = categories[0];
				if (account.access_token_OK != null) {
					try {
						okApi = Odnoklassniki.createInstance(ActivityRegistration.this,
								Constants.API_ID_OK, Constants.APP_SECRET_OK, Constants.APP_KEY_OK);
						String result = okApi.request("users.getCurrentUser", null, "get");

						JSONObject jsonObj = new JSONObject(result);
						for (int i = 0; i < jsonObj.length(); i++) {

							if (jsonObj.names().getString(i).equals("last_name")) {
								last_name = jsonObj.getString("last_name");
							}

							else if (jsonObj.names().getString(i).equals("first_name")) {
								first_name = jsonObj.getString("first_name");
							} else if (jsonObj.names().getString(i).equals("location")) {
								city = jsonObj.getJSONObject("location").getString("city");
								country = jsonObj.getJSONObject("location").getString("country");
							} else if (jsonObj.names().getString(i).equals("pic_2")) {
								photo = jsonObj.getString("pic_2");
							}

						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {

					last_name = account.user_last_name;
					first_name = account.user_first_name;
					city = account.user_city;
					country = account.user_country;
					photo = account.user_photo;
				}
				// downloadFile(photo);
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (currentSocial.equals("vk")) {

				setProfileSocial(city, country, last_name, first_name, phone, skype, photo, email);
			} else if (currentSocial.equals("fb")) {
				getProfileFB(currentSession);
			} else if (currentSocial.equals("tw")) {
				setProfileSocial(city, country, last_name, first_name, phone, skype, photo, email);
			} else if (currentSocial.equals("gp")) {
				setProfileSocial(city, country, last_name, first_name, phone, skype, photo, email);
			} else if (currentSocial.equals("ok")) {
				setProfileSocial(city, country, last_name, first_name, phone, skype, photo, email);
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

					// ������� ����� ��� �������� �������
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

					// ��������� Content-Length

					endBlockSize = BOUNDRY.length() + 6;

					httpConnection.setRequestProperty(
							"Content-Length",
							String.valueOf(requestBody.toString().length() + bufferSize
									+ endBlockSize));

					// ����������
					httpConnection.connect();

					// �������� ������ ����� �������
					DataOutputStream dataOS = new DataOutputStream(httpConnection.getOutputStream());
					dataOS.writeBytes(requestBody.toString());

					// �������� �����
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
						// ���� ��� ������ ���������, �������� ���������
						// ����� ���� ������ ���, ��.
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
						resultString = "������ �� �������";
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

	Bitmap downloadFile(String fileUrl) {
		URL myFileUrl = null;
		try {
			myFileUrl = new URL(fileUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bmImg = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmImg;
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
