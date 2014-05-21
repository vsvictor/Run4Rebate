package mk.run4rebate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mk.run4rebate.ActivityAdd.CreateRoute;
import mk.run4rebate.ActivityAdd.uploadTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

@SuppressLint("NewApi")
public class ActivityOption extends FragmentActivity implements OnCameraChangeListener {

	private static int PHOTO_SELECT = 801;
	private static int VIDEO_SELECT = 802;
	private static int PHOTO_CAPTURE = 803;
	private static int VIDEO_CAPTURE = 804;
	private final int IDD_LIST_CAPTURE = 1;
	private final int IDD_LIST_SELECT = 2;

	private static final String USER_NAME = "user_name";
	private static final String USER_MESSAGE = "user_message";
	private ArrayList<HashMap<String, Object>> myForms;

	public static final String jsonObjectGet = "jsonObjectGet";
	public static final String stringGalleryContentPath = "stringGalleryContentPath ";
	public static final String stringGalleryContentType = "stringGalleryContentType";
	public static final String isUpdated = "isUpdated";

	public String jsonObjectGetExtras = "jsonObjectGetExtras";
	public String stringGalleryContentPathExtras = "stringGalleryContentPathExtras";
	public String stringGalleryContentTypeExtras = "stringGalleryContentTypeExtras";
	public boolean isUpdatedExtras = false;

	public Dialog dialog;

	JSONObject objectJson;
	Account account = new Account();
	TextView objectName, typeDiscount, fromText, toText, typeObject;
	Button fromButton, toButton;
	EditText editComments;
	public static ArrayList<Integer> photo_ids, video_ids;
	int version, width, height, objectId;
	public ArrayList<String> mGalleryContent, mGalleryTypeContent;
	public ListView listComments;
	private Gallery mGallery;
	private ImageAdapter mImageAdapter;
	VideoView videoView;
	ImageView imageBig;
	ImageButton imageButton;
	private GoogleMap map, map2;
	RelativeLayout mapBigLayout;
	float zoom;
	GPSTracker gps;
	SendTask sT;
	String path = "/Run4Rebate/Images/", mPath;
	public Uri vCaptureUri;
	AlertDialog.Builder builder;
	uploadTask uT;
	GMapV2Direction md = new GMapV2Direction();
	Polyline polylineBig = null, polylineSmall = null;
	LatLng Me;
	CreateRoute cR;
	String URL = "http://run4rebate.com/uploads/";
	MyTaskDownload mtD;
	private MMAdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option_object);

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

		dialog = new Dialog(ActivityOption.this);
		dialog.setContentView(R.layout.wait);
		String title = getResources().getString(R.string.title_object);
		dialog.setTitle(title);
		dialog.show();
		account.restore(this);
		version = Integer.valueOf(android.os.Build.VERSION.SDK);

		photo_ids = new ArrayList<Integer>();
		video_ids = new ArrayList<Integer>();
		mGalleryContent = new ArrayList<String>();
		mGalleryTypeContent = new ArrayList<String>();
		objectName = (TextView) findViewById(R.id.textObjectName);
		typeDiscount = (TextView) findViewById(R.id.textTypeDiscount);
		fromText = (TextView) findViewById(R.id.textFrom);
		toText = (TextView) findViewById(R.id.textTo);
		typeObject = (TextView) findViewById(R.id.textObjectType);

		fromButton = (Button) findViewById(R.id.buttonFrom);
		toButton = (Button) findViewById(R.id.buttonTo);

		videoView = (VideoView) findViewById(R.id.videoView1);
		imageBig = (ImageView) findViewById(R.id.imageBig);
		imageButton = (ImageButton) findViewById(R.id.imageButton7);

		listComments = (ListView) findViewById(R.id.listComments);
		editComments = (EditText) findViewById(R.id.editComments);

		width = getResources().getDimensionPixelOffset(R.dimen.horizontal_view_item_width);
		height = getResources().getDimensionPixelOffset(R.dimen.horizontal_view_item_height);

		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map2 = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2))
				.getMap();
		mapBigLayout = (RelativeLayout) findViewById(R.id.mapBigLayout);
		ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView2);
		gps = new GPSTracker(this);
		Me = null;
		// check if GPS enabled
		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			Me = new LatLng(latitude, longitude);
			// \n is for new line
			// Toast.makeText(getApplicationContext(),
			// "Your Location is - \nLat: " + latitude + "\nLong: " + longitude,
			// Toast.LENGTH_LONG).show();
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}

		if (Me != null && map != null && map2 != null) {
			fillMap(Me, true);
		}
		Bundle extras = getIntent().getExtras();
		try {

			jsonObjectGetExtras = extras.getString(jsonObjectGet);
			stringGalleryContentPathExtras = extras.getString(stringGalleryContentPath);
			stringGalleryContentTypeExtras = extras.getString(stringGalleryContentType);
			isUpdatedExtras = extras.getBoolean(isUpdated);
			mGalleryContent.addAll(Arrays.asList(stringGalleryContentPathExtras.replace("[", "")
					.replace("]", "").split("\\s*,\\s*")));
			mGalleryTypeContent.addAll(Arrays.asList(stringGalleryContentTypeExtras
					.replace("[", "").replace("]", "").split("\\s*,\\s*")));

			Toast.makeText(getApplicationContext(), String.valueOf(isUpdatedExtras), 5000).show();

			objectJson = new JSONObject(jsonObjectGetExtras);
			fillForm(objectJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
		scrollView.setFocusable(true);
		scrollView.setFocusableInTouchMode(true);
		scrollView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// v.requestFocusFromTouch();
				editComments.getParent().requestDisallowInterceptTouchEvent(false);
				listComments.getParent().requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});
		editComments.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		listComments.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		if (version > 10) {
			scrollView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
		}
	}

	public void AddSearchOnClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityAddSearch.class);
		startActivity(intent);
		finish();
	}

	public void MenuOnClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityMenu.class);
		startActivity(intent);
		finish();
	}

	public void AccountOnClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ActivityAccount.class);
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

	public void closeMapOnClick(View view) {
		hideMap();
	}

	public void hideMap() {
		map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 1, null);
		mapBigLayout.setVisibility(View.GONE);
	}

	public void LoadImageFromFileOnClick(View view) {
		showDialog(IDD_LIST_SELECT);
	}

	public void LoadImageFromCameraOnClick(View view) {

		showDialog(IDD_LIST_CAPTURE);
		// LoadImageFromCamera();
	}

	public void saveComment(View view) {
		sT = new SendTask();
		sT.execute("comment");
	}

	public void LoadImageFromFile(int t) {
		if (t == PHOTO_SELECT) {
			Intent intent = new Intent(Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, PHOTO_SELECT);
		} else if (t == VIDEO_SELECT) {
			Intent intent = new Intent(Intent.ACTION_PICK,
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, VIDEO_SELECT);
		}
	}

	public void LoadImageFromCamera(int t)

	{

		String fileName = "temp";
		ContentValues values = new ContentValues();

		if (t == PHOTO_CAPTURE) {

			try {
				long FileName = System.currentTimeMillis();
				File saveDir = new File(Environment.getExternalStorageDirectory() + path);
				if (!saveDir.exists()) {
					saveDir.mkdirs();
				}
				File f;
				f = File.createTempFile(String.valueOf(FileName), ".jpg", saveDir);
				mPath = f.getAbsolutePath();
				vCaptureUri = Uri.fromFile(f);
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, vCaptureUri);
				startActivityForResult(takePictureIntent, PHOTO_CAPTURE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * values.put(MediaStore.Images.Media.TITLE, fileName); vCaptureUri
			 * = getContentResolver().insert(MediaStore.Images.Media.
			 * EXTERNAL_CONTENT_URI, values); Intent intent = new
			 * Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			 * intent.putExtra(MediaStore.EXTRA_OUTPUT, vCaptureUri);
			 * startActivityForResult(intent, PHOTO_CAPTURE);
			 */
		} else if (t == VIDEO_CAPTURE) {
			try {
				long FileName = System.currentTimeMillis();
				File saveDir = new File(Environment.getExternalStorageDirectory() + path);
				if (!saveDir.exists()) {
					saveDir.mkdirs();
				}
				File f;
				f = File.createTempFile(String.valueOf(FileName), ".3gp", saveDir);
				mPath = f.getAbsolutePath();
				vCaptureUri = Uri.fromFile(f);
				Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, vCaptureUri);
				startActivityForResult(takeVideoIntent, VIDEO_CAPTURE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * values.put(MediaStore.Video.Media.TITLE, fileName); vCaptureUri =
			 * getContentResolver
			 * ().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
			 * Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			 * intent.putExtra(MediaStore.EXTRA_OUTPUT, vCaptureUri);
			 * startActivityForResult(intent, VIDEO_CAPTURE);
			 */
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case IDD_LIST_CAPTURE:

			final String[] mCaptureType = { "Photo", "Video" };

			builder = new AlertDialog.Builder(this);
			// builder.setTitle("Выбираем кота"); // заголовок для диалога

			builder.setItems(mCaptureType, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO Auto-generated method stub
					if (mCaptureType[item].equals("Video")) {
						LoadImageFromCamera(VIDEO_CAPTURE);
					} else if (mCaptureType[item].equals("Photo")) {
						LoadImageFromCamera(PHOTO_CAPTURE);
					}
				}
			});
			builder.setCancelable(false);
			return builder.create();
		case IDD_LIST_SELECT:

			final String[] mSelectType = { "Photo", "Video" };

			builder = new AlertDialog.Builder(this);
			// builder.setTitle("Выбираем кота"); // заголовок для диалога

			builder.setItems(mSelectType, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO Auto-generated method stub
					if (mSelectType[item].equals("Video")) {
						LoadImageFromFile(VIDEO_SELECT);
					} else if (mSelectType[item].equals("Photo")) {
						LoadImageFromFile(PHOTO_SELECT);
					}
				}
			});
			builder.setCancelable(false);
			return builder.create();
		default:
			return null;
		}
	}

	public void fillMap(LatLng location, boolean action) {
		LatLng objectLocation = location;// new LatLng(location.getLatitude(),
		String me = ""; // location.getLongitude());
		if (action) {
			me = getResources().getString(R.string.me_marker);
			map2.addMarker(new MarkerOptions().position(objectLocation).title(me)
			// .snippet(me)
					.draggable(false));

			map.addMarker(new MarkerOptions().position(objectLocation).title(me)
			// .snippet(me)
					.draggable(false));
			map2.moveCamera(CameraUpdateFactory.newLatLngZoom(objectLocation, 15));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(objectLocation, 15));
			// Zoom in, animating the camera.
			map.animateCamera(CameraUpdateFactory.zoomTo(15), 1, null);
			map2.animateCamera(CameraUpdateFactory.zoomTo(15), 1, null);
		} else {
			map2.addMarker(new MarkerOptions().position(objectLocation)
					// .snippet(me)
					.draggable(false)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_percent)));

			map.addMarker(new MarkerOptions().position(objectLocation)
					// .snippet(me)
					.draggable(false)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_percent)));

			map2.moveCamera(CameraUpdateFactory.newLatLngZoom(objectLocation, 15));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(objectLocation, 15));
			// Zoom in, animating the camera.
			map.animateCamera(CameraUpdateFactory.zoomTo(15), 1, null);
			map2.animateCamera(CameraUpdateFactory.zoomTo(15), 1, null);

			cR = new CreateRoute();
			cR.execute(String.valueOf(location.latitude), String.valueOf(location.longitude));
		}

		map.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				mapBigLayout.setVisibility(View.VISIBLE);

			}
		});

		map2.setOnCameraChangeListener(this);

	}

	@Override
	public void onCameraChange(CameraPosition position) {
		zoom = map2.getCameraPosition().zoom;
	}

	public void fillForm(JSONObject jsonObj)

	{
		String id = null, name, category_name;
		double lat = 0, lng = 0;
		int size_discount_id, get_size_discount_id;
		LatLng point = null;
		String jsonComments = "", jsonImages = "", jsonVideo = "", type_discount_text = "", category_text = "";
		try {
			for (int i = 0; i < jsonObj.length(); i++) {

				if (jsonObj.names().getString(i).equals("id")) {
					id = jsonObj.getString("id");
				} else if (jsonObj.names().getString(i).equals("category_name")) {
					category_name = jsonObj.getString("category_name");
					typeObject.setText(category_name);
				} else if (jsonObj.names().getString(i).equals("name")) {
					name = jsonObj.getString("name");
					objectName.setText(name);
				} else if (jsonObj.names().getString(i).equals("type_discount_id")) {
					type_discount_text = jsonObj.getString("type_discount_name");
					typeDiscount.setText(type_discount_text);
				} else if (jsonObj.names().getString(i).equals("size_discount_id")) {
					get_size_discount_id = jsonObj.getInt("size_discount_id");
					size_discount_id = get_size_discount_id;

					if (size_discount_id == 1) {
						toText.setText(getResources().getString(R.string.to));
						toButton.setText("10%");
						fromText.setVisibility(View.GONE);
						fromButton.setVisibility(View.GONE);
					} else if (size_discount_id == 2) {
						fromText.setText(getResources().getString(R.string.from));
						fromButton.setText("10%");
						toText.setText(getResources().getString(R.string.to));
						toButton.setText("25%");
					} else if (size_discount_id == 3) {
						fromText.setText(getResources().getString(R.string.from));
						fromButton.setText("25%");
						toText.setText(getResources().getString(R.string.to));
						toButton.setText("50%");
					} else if (size_discount_id == 4) {
						fromText.setText(getResources().getString(R.string.over));
						fromButton.setText("50%");
						toText.setVisibility(View.GONE);
						toButton.setVisibility(View.GONE);
					}

				} else if (jsonObj.names().getString(i).equals("comments")) {
					jsonComments = jsonObj.getString("comments");
					listCreator(jsonComments);
				} else if (jsonObj.names().getString(i).equals("comments")) {

				} else if (jsonObj.names().getString(i).equals("images")) {
					jsonImages = jsonObj.getString("images");
				}

				lat = jsonObj.getDouble("lat");
				lng = jsonObj.getDouble("lng");

			}
			if (lat != 0 && lng != 0) {
				point = new LatLng(lat, lng);
				fillMap(point, false);
			}
			if (dialog.isShowing()) {
				try {
					dialog.dismiss();
				} catch (Exception e) {
				}
			}
			mtD = new MyTaskDownload();
			mtD.execute(jsonImages.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setGallery() {

		mGallery = (Gallery) findViewById(R.id.gallery);
		mGallery.setScrollbarFadingEnabled(false);
		mImageAdapter = new ImageAdapter(this);
		mGallery.setAdapter(mImageAdapter);
		// mGallery.setSelection(1);
		if (mImageAdapter.getCount() > 1) {
			mGallery.setSelection(mImageAdapter.getCount() / mImageAdapter.getCount());
		} else

		{
			mGallery.setSelection(mImageAdapter.getCount() / mImageAdapter.getCount() - 1);
		}
		mGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				File file = new File(mGalleryContent.get(position).toString());
				if (mGalleryTypeContent.get(position).equals("video")) {
					videoView.setVisibility(View.VISIBLE);
					imageButton.setVisibility(View.VISIBLE);
					Uri videoF = Uri.fromFile(file);
					videoView.setVideoURI(Uri.parse(String.valueOf(videoF)));
					videoView.setMediaController(new MediaController(ActivityOption.this));
					videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							// videoView.setVisibility(View.INVISIBLE);
						}
					});
					videoView.requestFocus();
					videoView.start();
				} else {

					Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
					imageBig.setImageBitmap(bitmap);
					imageBig.setVisibility(View.VISIBLE);
					imageButton.setVisibility(View.VISIBLE);
				}

			}
		});

	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mGalleryContent.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView imageView = new ImageView(mContext);
			// String filePath = mImagePath[position];

			String fileType = mGalleryTypeContent.get(position).toString();
			String filePath = mGalleryContent.get(position).toString();
			Matrix mat = new Matrix();

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 10;
			Bitmap bmp = null;

			if (fileType.equals("video")) {
				bmp = BitmapFactory.decodeFile(filePath, options);
				Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath,
						MediaStore.Images.Thumbnails.MINI_KIND);
				Bitmap resized = Bitmap.createScaledBitmap(thumb, width, height, true);
				bmp = Bitmap.createBitmap(resized/*
												 * , 0, 0, width, height/*, mat,
												 * true
												 */);

			}

			else if (fileType.equals("photo")) {

				bmp = BitmapFactory.decodeFile(filePath, options);

				Bitmap resized = Bitmap.createScaledBitmap(bmp, width, height, true);
				bmp = Bitmap.createBitmap(resized/*
												 * , 0, 0, width, height/*, mat,
												 * true
												 */);
			}

			imageView.setScaleType(ImageView.ScaleType.CENTER);
			imageView.setPadding(20, 2, 20, 2);
			imageView.setImageBitmap(bmp);

			TextView textView = new TextView(mContext);
			textView.setText(mGalleryTypeContent.get(position).toString());
			textView.setGravity(Gravity.CENTER);
			textView.setTypeface(Typeface.MONOSPACE);
			textView.setVisibility(View.INVISIBLE);
			LinearLayout layout = new LinearLayout(getApplicationContext());
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(imageView);
			layout.addView(textView);

			return layout;
		}
	}

	public void listCreator(String jsonArray) {
		HashMap<String, Object> hm;
		myForms = new ArrayList<HashMap<String, Object>>();

		String userName, userComments;
		try {
			JSONArray jsonArr = new JSONArray(jsonArray);
			for (int j = 0; j < jsonArr.length(); j++) {
				userName = jsonArr.getJSONObject(j).getString("user_name");

				userComments = jsonArr.getJSONObject(j).getString("message");
				hm = new HashMap<String, Object>();
				hm.put(USER_NAME, userName);
				hm.put(USER_MESSAGE, userComments);
				myForms.add(hm);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final SimpleAdapter adapter = new SimpleAdapter(this, myForms, R.layout.comments_item,
				new String[] { USER_NAME, // верхний
											// текст
						USER_MESSAGE }, new int[] { R.id.userName, // ссылка на
																	// объект
																	// отображающий
																	// текст
						R.id.userComment,

				}); // добавили ссылку в чем отображать картинки из list.xml

		listComments.setAdapter(adapter);
		listComments.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	class MyTaskDownload extends AsyncTask<String, Integer, Void> {
		String operation;
		String jsonArray;
		File file;
		int count;
		int countU, last = 0;
		int downloadedSize = 0;
		int totalSize = 0;
		final ProgressDialog progressDialog = new ProgressDialog(ActivityOption.this);

		@Override
		protected void onPreExecute() {

			progressDialog.setMessage("Downloading ...");
			progressDialog.setCancelable(false);
			progressDialog.setMax(100);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			progressDialog.show();
		}

		@Override
		protected Void doInBackground(String... urls) {

			try {
				for (String url : urls) {

					jsonArray = url;
					JSONArray jsonArr;
					String fname = "";
					if (!jsonArray.equals("")) {
						jsonArr = new JSONArray(url);

						if (jsonArr.length() != 0) {
							for (int j = 0; j < jsonArr.length(); j++) {
								countU = jsonArr.length();
								if (jsonArr.getJSONObject(j).getString("type").equals("image")) {
									fname = jsonArr.getJSONObject(j).getString("image");
								} else if (jsonArr.getJSONObject(j).getString("type")
										.equals("video")) {
									fname = jsonArr.getJSONObject(j).getString("file");
								}
								operation = URL + fname;
								URL url_d = new URL(operation);
								URLConnection conection = url_d.openConnection();
								conection.connect();
								// getting file length

								HttpURLConnection urlConnection = (HttpURLConnection) url_d
										.openConnection();

								urlConnection.setRequestMethod("GET");
								urlConnection.setDoOutput(true);

								// connect
								urlConnection.connect();
								// input stream to read file - with 8k buffer
								InputStream input = urlConnection.getInputStream();
								int lenghtOfFile = conection.getContentLength();
								File myDir = new File(Environment.getExternalStorageDirectory(),
										path);

								myDir.mkdirs();

								file = new File(myDir, fname);
								if (mGalleryTypeContent.size() == countU) {
									break;
								} else if (jsonArr.getJSONObject(j).getString("type")
										.equals("image")) {
									mGalleryContent.add(file.getAbsolutePath());
									mGalleryTypeContent.add("photo");
								} else if (jsonArr.getJSONObject(j).getString("type")
										.equals("video")) {
									mGalleryContent.add(file.getAbsolutePath());
									mGalleryTypeContent.add("video");
								}

								if (file.exists()) {
									continue;
								}
								FileOutputStream output = new FileOutputStream(file);
								byte data[] = new byte[1024];

								int bufferLength = 0;
								while ((bufferLength = input.read(data)) > 0) {
									output.write(data, 0, bufferLength);
									downloadedSize += bufferLength;
									// total += count;
									publishProgress(downloadedSize, lenghtOfFile);
								}
								/*
								 * while ((count = input.read(data)) != -1) {
								 * total += count; publishProgress((int)
								 * (total), lenghtOfFile); }
								 */

								// closing streams
								output.close();
								input.close();

								/*
								 * try {
								 * 
								 * FileOutputStream out = new
								 * FileOutputStream(file);
								 * 
								 * 
								 * Bitmap finalBitmap = downloadFile(operation)
								 * ;
								 * finalBitmap.compress(Bitmap.CompressFormat.JPEG
								 * , 100, out);
								 * 
								 * 
								 * out.flush();
								 * 
								 * out.close();
								 * 
								 * } catch (Exception e) {
								 * 
								 * e.printStackTrace();
								 * 
								 * }
								 */

							}
						}
					}
				}
				// разъединяемся
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(Integer... values) {
			progressDialog.setProgress((int) ((values[0] / (int) values[1]) * 100));
		};

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mGalleryTypeContent.size() == countU && countU != 0) {
				setGallery();
				progressDialog.hide();
				hideMap();
			} else {
				progressDialog.hide();
				hideMap();
			}
			//
			/*
			 * if (operation.equals("share")) {
			 * 
			 * 
			 * } else {
			 * 
			 * }
			 */
		}
	}

	class SendTask extends AsyncTask<String, Void, String> {
		String resultString = null;
		String resultS = "anyresult";
		String send_token = account.access_token;
		String obj_name;
		String id = "id";

		String name = "name";
		String lat = "lat";
		String lng = "lng";
		String type_discount_name = "type_discount_name";
		String category_name = "category_name";
		String images = "images";
		String video = "video";
		String comments = "comments";
		String errors = "errors";
		HttpPost httppost;

		JSONObject jsonObj;
		String operation;

		/*
		 * photo_ids: array of int video_ids: array of int
		 */

		@Override
		protected String doInBackground(String... arg0) {
			try {
				operation = arg0[0];
				HttpClient httpclient = new DefaultHttpClient();
				if (operation.equals("comment")) {
					httppost = new HttpPost("http://api.run4rebate.com/objects/add_comment/&");
				}
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);

				// nameValuePairs.add(new BasicNameValuePair("token",
				// send_token));

				for (int i = 0; i < photo_ids.size(); i++) {
					nameValuePairs.add(new BasicNameValuePair("photo_ids[]", String
							.valueOf(photo_ids.get(i).toString())));
				}
				for (int i = 0; i < video_ids.size(); i++) {
					nameValuePairs.add(new BasicNameValuePair("video_ids[]", String
							.valueOf(video_ids.get(i).toString())));
				}
				if (operation.equals("comment")) {
					nameValuePairs.add(new BasicNameValuePair("obj_id", String.valueOf(objectId)));
					nameValuePairs.add(new BasicNameValuePair("message", editComments.getText()
							.toString()));
				}
				/*
				 * nameValuePairs.add(new BasicNameValuePair("photo_ids",
				 * String.valueOf(photo_ids))); nameValuePairs.add(new
				 * BasicNameValuePair("video_ids", String.valueOf(video_ids)));
				 *//*
					 * nameValuePairs.add(new BasicNameValuePair("avatar_id",
					 * avatar_id));
					 */
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

				HttpResponse response = httpclient.execute(httppost);

				BufferedReader reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent(), "utf-8"));
				// StringBuilder sb = new StringBuilder();
				String line = null;

				/*
				 * while ((line = reader.readLine()) != null) { resultString =
				 * line;
				 * 
				 * }
				 * 
				 * jsonObj = new JSONObject(resultString);
				 * 
				 * for (int i = 0; i < jsonObj.length(); i++) {
				 * 
				 * if (jsonObj.names().getString(i).equals(id)) {
				 * 
				 * } else if (jsonObj.names().getString(i)
				 * .equals(type_discount_id)) {
				 * 
				 * } else if (jsonObj.names().getString(i).equals(category_id))
				 * {
				 * 
				 * } else if (jsonObj.names().getString(i)
				 * .equals(size_discount_id)) {
				 * 
				 * } else if (jsonObj.names().getString(i).equals(name)) {
				 * 
				 * }
				 * 
				 * else if (jsonObj.names().getString(i).equals(lat)) {
				 * 
				 * } else if (jsonObj.names().getString(i).equals(lng)) {
				 * 
				 * } else if (jsonObj.names().getString(i)
				 * .equals(type_discount_name)) {
				 * 
				 * } else if (jsonObj.names().getString(i)
				 * .equals(category_name)) {
				 * 
				 * } }
				 */

			} catch (MalformedURLException e) {
				Toast toast = Toast.makeText(getApplicationContext(), "MalformedURLException e "
						+ e.getMessage(), Toast.LENGTH_SHORT);
				toast.show();

			} catch (IOException e) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"IOException " + e.getMessage(), Toast.LENGTH_SHORT);
				toast.show();
			} /*
			 * catch (JSONException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			/*
			 * if (dialog != null) { if (dialog.isShowing()) {
			 * 
			 * dialog.dismiss(); //isUpdated = true; } } sendObject =
			 * resultString;; if (operation.equals("comment")) {
			 * 
			 * setObject(jsonObj);
			 * 
			 * } else if (operation.equals("send")) {
			 * 
			 * 
			 * }
			 */
		}

	}

	class uploadTask extends AsyncTask<String, Integer, File> {
		final ProgressDialog progressDialog = new ProgressDialog(ActivityOption.this);
		private Exception m_error = null;

		@Override
		protected void onPreExecute() {

			progressDialog.setMessage("Uploading ...");
			progressDialog.setCancelable(false);
			progressDialog.setMax(100);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			progressDialog.show();
		}

		@Override
		protected File doInBackground(String... params) {

			String requestString = params[0];
			final File file = new File(params[1]);
			String resultString = new String("");
			StringBuffer requestBody = new StringBuffer();
			int typeCaptureServ = Integer.parseInt(params[2]);

			try {

				URLConnection connection = null;
				URL url = new URL(requestString);

				connection = url.openConnection();

				// ///////////////////////////

				String BOUNDRY = "*****";

				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				publishProgress(10, 100);
				httpConnection.setRequestMethod("POST");

				httpConnection.setUseCaches(false);

				httpConnection.setDoOutput(true);

				httpConnection.setDoInput(true);

				httpConnection.setRequestProperty("User-Agent", "MyAndroid/1.6");
				publishProgress(20, 100);
				httpConnection.setRequestProperty("Content-Language", "ru-RU");
				httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary="
						+ BOUNDRY);
				// httpConnection.setRequestProperty("Content-Type",
				// "image/jpeg; boundary=" + BOUNDRY);
				httpConnection.setRequestProperty("Connection", "Keep-Alive");

				// собрать буфер для отправки запроса
				publishProgress(30, 100);
				String contentDisposition = "Content-Disposition: form-data; name=\"data\"; filename=\""
						+ file.getName() + "\"";
				String contentType = "Content-Type: application/octet-stream\nContent-Transfer-Encoding: binary";

				requestBody.append("--");
				requestBody.append(BOUNDRY);
				requestBody.append('\n');
				publishProgress(40, 100);
				requestBody.append(contentDisposition);
				requestBody.append('\n');
				requestBody.append(contentType);
				requestBody.append('\n');
				requestBody.append('\n');
				publishProgress(50, 100);

				int bytesAvailable, bufferSize, bytesRead, endBlockSize;
				byte[] buffer;
				int maxBufferSize = 1 * 1024 * 1024;
				FileInputStream fileInputStream = new FileInputStream(file);

				bytesAvailable = fileInputStream.available();
				publishProgress(60, 100);
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// установка Content-Length

				endBlockSize = BOUNDRY.length() + 6;

				httpConnection
						.setRequestProperty(
								"Content-Length",
								String.valueOf(requestBody.toString().length() + bufferSize
										+ endBlockSize));

				// соединение
				httpConnection.connect();

				// отправка первой части запроса
				DataOutputStream dataOS = new DataOutputStream(httpConnection.getOutputStream());
				publishProgress(80, 100);
				dataOS.writeBytes(requestBody.toString());

				// отправка файла
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				dataOS.write(buffer, 0, bytesRead);

				// ////////////////////////////////////////////

				dataOS.writeBytes("\n");
				dataOS.writeBytes("--");
				publishProgress(90, 100);
				dataOS.writeBytes(BOUNDRY);
				dataOS.writeBytes("--");
				dataOS.writeBytes("\n");

				fileInputStream.close();

				dataOS.flush();
				publishProgress(100, 100);
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
					if (typeCaptureServ == PHOTO_CAPTURE || typeCaptureServ == PHOTO_SELECT) {
						if (jsonObj.names().getString(i).equals("img_id")) {
							resultString = jsonObj.getString("img_id");
							photo_ids.add(jsonObj.getInt("img_id"));
						}

						else if (jsonObj.names().getString(i).equals("error")) {
							resultString = jsonObj.getJSONObject("error").getString("message");
						}
					} else if (typeCaptureServ == VIDEO_CAPTURE || typeCaptureServ == VIDEO_SELECT) {
						if (jsonObj.names().getString(i).equals("video_id")) {
							resultString = jsonObj.getString("video_id");
							video_ids.add(jsonObj.getInt("video_id"));
						}

						else if (jsonObj.names().getString(i).equals("error")) {
							resultString = jsonObj.getJSONObject("error").getString("message");
						}
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

			return null;
		}

		protected void onProgressUpdate(Integer... values) {
			progressDialog.setProgress((int) ((values[0] / (float) values[1]) * 100));
		};

		@Override
		protected void onPostExecute(File file) {
			// отображаем сообщение, если возникла ошибка
			if (m_error != null) {
				m_error.printStackTrace();
				return;
			}
			// закрываем прогресс и удаляем временный файл
			progressDialog.hide();
			// file.delete();
		}
	}

	private String getRealVideoPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Video.Media.DATA };
		CursorLoader loader = new CursorLoader(ActivityOption.this, contentUri, proj, null, null,
				null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private String getRealImagePathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(ActivityOption.this, contentUri, proj, null, null,
				null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final String Hash = Constants.CONTROL_SUM;
		String obj;
		String send_hash = md5(Hash);
		if (requestCode == PHOTO_CAPTURE) {

			if (resultCode == Activity.RESULT_OK) {

				String path_this = mPath;// getRealImagePathFromURI(vCaptureUri);
				String urlAddr;
				/*
				 * if (!isUpdated) { urlAddr =
				 * "http://api.run4rebate.com/user/upload_img/&"; } else {
				 */
				obj = "&obj_id=" + objectId + Hash;
				try {
					send_hash = md5(URLEncoder.encode(obj, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				urlAddr = "http://api.run4rebate.com/user/upload_img/&obj_id=" + objectId
						+ "&hash=" + send_hash;
				// }
				File fileC = new File(path_this);
				mGalleryContent.add(fileC.getAbsolutePath());
				mGalleryTypeContent.add("photo");

				String filePath = fileC.getAbsolutePath();
				uT = new uploadTask();
				uT.execute(urlAddr, filePath, String.valueOf(requestCode));
				// sendMedia(urlAddr, filePath, requestCode) ;
				setGallery();
			}

		} else if (requestCode == VIDEO_CAPTURE) {

			if (resultCode == Activity.RESULT_CANCELED || resultCode == Activity.RESULT_OK) {

				String path_this = mPath;// getRealVideoPathFromURI(vCaptureUri);
				String urlAddr;
				obj = "&obj_id=" + objectId + Hash;
				try {
					send_hash = md5(URLEncoder.encode(obj, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				urlAddr = "http://api.run4rebate.com/user/upload_video/&obj_id=" + objectId
						+ "&hash=" + send_hash;

				File fileC = new File(path_this);
				mGalleryContent.add(fileC.getAbsolutePath());
				mGalleryTypeContent.add("video");

				String filePath = fileC.getAbsolutePath();
				uT = new uploadTask();
				uT.execute(urlAddr, filePath, String.valueOf(requestCode));
				// sendMedia(urlAddr, filePath, requestCode) ;
				setGallery();
			}

		}

		else if (requestCode == PHOTO_SELECT) {
			if (resultCode == Activity.RESULT_OK) {
				// Check for returned image from gallery
				if (data == null)
					return;

				Uri imageUri = data.getData();
				String path_this = getRealImagePathFromURI(imageUri);
				String urlAddr;
				obj = "&obj_id=" + objectId + Hash;
				try {
					send_hash = md5(URLEncoder.encode(obj, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				urlAddr = "http://api.run4rebate.com/user/upload_img/&obj_id=" + objectId
						+ "&hash=" + send_hash;
				// setImageUri(imageUri);
				File fileG = new File(path_this);
				mGalleryContent.add(fileG.getAbsolutePath());
				mGalleryTypeContent.add("photo");
				String filePath = fileG.getAbsolutePath();
				uT = new uploadTask();
				uT.execute(urlAddr, filePath, String.valueOf(requestCode));
				setGallery();

			}
		} else if (requestCode == VIDEO_SELECT) {
			if (resultCode == Activity.RESULT_OK) {
				// Check for returned image from gallery
				if (data == null)
					return;

				String urlAddr;
				obj = "&obj_id=" + objectId + Hash;
				try {
					send_hash = md5(URLEncoder.encode(obj, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				urlAddr = "http://api.run4rebate.com/user/upload_video/&obj_id=" + objectId
						+ "&hash=" + send_hash;
				Uri imageUri = data.getData();
				String path_this = getRealVideoPathFromURI(imageUri);
				File fileG = new File(path_this);
				mGalleryContent.add(fileG.getAbsolutePath());
				mGalleryTypeContent.add("video");
				String filePath = fileG.getAbsolutePath();
				uT = new uploadTask();
				uT.execute(urlAddr, filePath, String.valueOf(requestCode));
				setGallery();

			}
		}
	}

	class CreateRoute extends AsyncTask<String, Void, String> {
		Document doc;

		@Override
		protected String doInBackground(String... location) {
			md = new GMapV2Direction();
			LatLng destination = new LatLng(Double.parseDouble(location[0]),
					Double.parseDouble(location[1]));
			doc = md.getDocument(Me, destination, GMapV2Direction.MODE_DRIVING);
			return null;
		}

		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (doc != null) {
				createRoute(doc);
			}
			// StartListView();
		}

	}

	public void createRoute(Document doc) {

		if (polylineBig != null && polylineSmall != null) {
			polylineBig.remove();
			polylineSmall.remove();
		}
		ArrayList<LatLng> directionPoint = md.getDirection(doc);
		PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.CYAN);

		for (int i = 0; i < directionPoint.size(); i++) {
			rectLine.add(directionPoint.get(i));
		}
		polylineSmall = map.addPolyline(rectLine);
		polylineBig = map2.addPolyline(rectLine);

		/*
		 * PolylineOptions testPolyline = new PolylineOptions() .add(Me,
		 * destination) .width(5) .color(Color.RED).geodesic(true); Polyline
		 * line = map.addPolyline(testPolyline);// =
		 * map.addPolyline(testPolyline); Polyline line2 =
		 * map2.addPolyline(testPolyline);
		 */
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
