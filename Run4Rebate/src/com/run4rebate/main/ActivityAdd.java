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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
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
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

@SuppressLint("NewApi")
public class ActivityAdd extends FragmentActivity implements OnMapClickListener,
		OnMapLongClickListener, OnMarkerDragListener, OnMarkerClickListener, OnCameraChangeListener {

	private static int PHOTO_SELECT = 801;
	private static int VIDEO_SELECT = 802;
	private static int PHOTO_CAPTURE = 803;
	private static int VIDEO_CAPTURE = 804;
	private final int REQUEST_OBJECT = 805;
	private final int REQUEST_VOICE = 806;

	private final int IDD_LIST_CAPTURE = 1;
	private final int IDD_LIST_SELECT = 2;
	private static final String USER_NAME = "user_name";
	private static final String USER_MESSAGE = "user_message";
	private ArrayList<HashMap<String, Object>> myForms;
	AlertDialog.Builder builder;
	public Bitmap bmImg = null;
	private Uri vCaptureUri;
	VideoView videoView;
	ImageView imageBig;
	ImageButton imageButton;
	public ArrayList<String> imagePath, mTypeContent;
	private GoogleMap map, map2;
	private MapView mMapView, mMapView2;
	public String[] mImagePath;
	private Gallery mGallery;
	private ImageAdapter mImageAdapter;
	public SqlAdapterCategories adapterCategories;
	public SqlAdapterDiscountType adapterDiscountType;
	public int countCategories, countDiscountType, objectId;
	ArrayAdapter<String> adapter;
	GetCategoriesTask gC;
	GetObjectsTask gO;
	SendTask sT;
	Button spinnerTypeObject;
	public String objectCategoryId;
	public String objectCategoryName;
	Spinner spinnerTypeDiscount;
	public double markerPositionLat, markerPositionLng, lat, lng;
	GetDiscountTypeTask dTT;
	public Dialog dialog;
	TextView textHint;
	RelativeLayout mapBigLayout;
	boolean markerClicked = false, clicked = false;
	String path = "/Run4Rebate/Images/";
	String objectName;
	// private String[] mStrings = {"video", "image"};
	int height, width;
	LatLng Me;
	String lang, token, name;
	int type_discount_id, category_id, size_discount_id = 0;
	Account account = new Account();
	public EditText eObjectName, editComments;
	public static ArrayList<Integer> photo_ids, video_ids, object_ids;
	uploadTask uT;
	GetObjectTask gOT;
	ArrayList<Marker> markerList;
	RelativeLayout commentsLayout;
	public boolean isUpdated = false;
	public RadioButton radio0, radio1, radio2, radio3;
	public ListView listComments;
	String URL = "http://run4rebate.com/uploads/";
	MyTaskDownload mTD;
	int version;
	String sendObject;
	String mPath;
	CreateRoute cR;
	public String[] data;
	private String TAG = "SpinnerHint";

	private Spinner typeSpinner;

	private LayoutInflater mInflator;
	private boolean selected;

	GPSTracker gps;
	Marker mark;
	float zoom;
	GMapV2Direction md = new GMapV2Direction();
	Polyline polylineBig = null, polylineSmall = null;

	private MMAdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		lang = getApplicationContext().getResources().getConfiguration().locale.getLanguage();
		setContentView(R.layout.add_object2);

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
		ImageButton speakButton;
		speakButton = (ImageButton) findViewById(R.id.speakButton);

		// resultList = (ListView) findViewById(R.id.list);
		// Disable button if no recognition service is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			speakButton.setEnabled(false);
			Toast.makeText(getApplicationContext(), "Recognizer Not Found", 1000).show();
		}
		speakButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startVoiceRecognitionActivity();
			}
		});

		photo_ids = new ArrayList<Integer>();
		video_ids = new ArrayList<Integer>();
		markerList = new ArrayList<Marker>();
		object_ids = new ArrayList<Integer>();
		eObjectName = (EditText) findViewById(R.id.editObjectName);
		editComments = (EditText) findViewById(R.id.editComments);
		adapterCategories = new SqlAdapterCategories(this);
		countCategories = adapterCategories.getCount();
		commentsLayout = (RelativeLayout) findViewById(R.id.commentsLayout);
		commentsLayout.setVisibility(View.GONE);
		adapterDiscountType = new SqlAdapterDiscountType(this);
		countDiscountType = adapterDiscountType.getCount();
		mapBigLayout = (RelativeLayout) findViewById(R.id.mapBigLayout);
		textHint = (TextView) findViewById(R.id.textHint);
		width = getResources().getDimensionPixelOffset(R.dimen.horizontal_view_item_width);
		height = getResources().getDimensionPixelOffset(R.dimen.horizontal_view_item_height);
		spinnerTypeObject = (Button) findViewById(R.id.spinnerTypeObject);
		imagePath = new ArrayList<String>();
		mTypeContent = new ArrayList<String>();
		
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		try {
		    if (status != ConnectionResult.SUCCESS) {
		        GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
		    }
		} catch (Exception e) {
		    Log.e("Error: GooglePlayServiceUtil: ", "" + e);
		}

		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map2 = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2))
				.getMap();
				
/*		
		mMapView = (MapView) findViewById(R.id.mvMap);
		mMapView.onCreate(savedInstanceState);
		mMapView.onResume();
		try {
			MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
		map = mMapView.getMap();
		mMapView2 = (MapView) findViewById(R.id.mvMap2);
		mMapView2.onCreate(savedInstanceState);
		mMapView2.onResume();
		try {
			MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
		map2 = mMapView2.getMap();
*/
		//videoView = (VideoView) findViewById(R.id.videoView1);
		imageBig = (ImageView) findViewById(R.id.imageBig);
		imageButton = (ImageButton) findViewById(R.id.imageButton7);
		radio0 = (RadioButton) findViewById(R.id.radio0);
		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio2 = (RadioButton) findViewById(R.id.radio2);
		radio3 = (RadioButton) findViewById(R.id.radio3);
		listComments = (ListView) findViewById(R.id.listComments);
		version = Integer.valueOf(android.os.Build.VERSION.SDK);

		ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView2);
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
		/*
		 * final LocationManager locationManager = (LocationManager)
		 * this.getSystemService(Context.LOCATION_SERVICE); final
		 * LocationListener locationListener = new LocationListener() {
		 * 
		 * @Override public void onStatusChanged(String provider, int status,
		 * Bundle extras) {}
		 * 
		 * @Override public void onProviderEnabled(String provider) {}
		 * 
		 * @Override public void onProviderDisabled(String provider) {}
		 * 
		 * 
		 * @Override public void onLocationChanged(Location arg0) { // TODO
		 * Auto-generated method stub
		 * 
		 * } };
		 * locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
		 * , 0, 0, locationListener); Location location =
		 * locationManager.getLastKnownLocation
		 * (LocationManager.NETWORK_PROVIDER);
		 */

		gps = new GPSTracker(this);
		LatLng location = null;
		// check if GPS enabled
		if (gps.canGetLocation()) {

//			double latitude = gps.getLatitude();
//			double longitude = gps.getLongitude();
			double latitude = 46.4774700;
			double longitude = 30.7326200;

			location = new LatLng(latitude, longitude);
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

		if (location != null && map != null && map2 != null) {
			fillMap(location);
		}
		fillSpinner();

		RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup1);

		radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case -1:
					Toast.makeText(getApplicationContext(), "No choice", Toast.LENGTH_SHORT).show();
					break;
				case R.id.radio0:
					size_discount_id = 1;
					// Toast.makeText(getApplicationContext(), "Radio0",
					// Toast.LENGTH_SHORT).show();
					break;
				case R.id.radio1:
					size_discount_id = 2;
					// Toast.makeText(getApplicationContext(), "Radio1",
					// Toast.LENGTH_SHORT).show();
					break;
				case R.id.radio2:
					size_discount_id = 3;
					// Toast.makeText(getApplicationContext(), "Radio2",
					// Toast.LENGTH_SHORT).show();
					break;

				case R.id.radio3:
					size_discount_id = 4;
					// Toast.makeText(getApplicationContext(), "Radio2",
					// Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
			}
		});

	
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
		if (accessToken()) {
			intent.setClass(this, ActivityAccount.class);
		} else {
			intent.setClass(this, ActivityRegistration.class);
		}
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

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "AndroidBite Voice Recognition...");
		startActivityForResult(intent, REQUEST_VOICE);
	}

	private void alert() {
		if (account.isFirst) {
			final Dialog dialog = new Dialog(ActivityAdd.this);
			account.isFirst = true;
			account.save(ActivityAdd.this);
			dialog.setContentView(R.layout.info);

			TextView add = (TextView) dialog.findViewById(R.id.textInfoTitle);
			Resources res = getResources();
			String text = String.format(res.getString(R.string.info));

			dialog.setTitle(text);
			text = String.format(res.getString(R.string.add_allert));
			add.setText(text);
			Button btnSignIn = (Button) dialog.findViewById(R.id.ok);

			// get the References of views

			// Set On ClickListener
			btnSignIn.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					dialog.dismiss();
					account.isFirst = false;
					account.save(ActivityAdd.this);
				}
			});

			dialog.show();
		}

	}

	public void closeMapOnClick(View view) {
		hideMap();
	}

	public void hideMap() {
		map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 1, null);
		mapBigLayout.setVisibility(View.GONE);
	}

	public void saveObjectClick(View view) {
		dialog = new Dialog(ActivityAdd.this);
		dialog.setContentView(R.layout.wait);
		String title = getResources().getString(R.string.title_object);
		dialog.setTitle(title);
		dialog.show();
		sT = new SendTask();
		sT.execute("send");
	}

	public void fillMap(LatLng location) {
		Me = location;// new LatLng(location.getLatitude(),
						// location.getLongitude());
		String me = getResources().getString(R.string.me_marker);
		map2.addMarker(new MarkerOptions().position(Me).title(me)
		// .snippet(me)
				.draggable(false));
		/*
		 * .icon(BitmapDescriptorFactory
		 * .fromResource(R.drawable.icon_pin_percent)));
		 */
		map.addMarker(new MarkerOptions().position(Me).title(me)
		// .snippet(me)
				.draggable(false));

		// .icon(BitmapDescriptorFactory
		// .fromResource(R.drawable.icon_pin_percent)));

		map2.moveCamera(CameraUpdateFactory.newLatLngZoom(Me, 15));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(Me, 15));
		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(15), 1, null);
		map2.animateCamera(CameraUpdateFactory.zoomTo(15), 1, null);

		map.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				mapBigLayout.setVisibility(View.VISIBLE);

			}
		});

		map2.setOnMapClickListener(this);
		map2.setOnMapLongClickListener(this);
		map2.setOnMarkerDragListener(this);
		map2.setOnMarkerClickListener(this);
		map2.setOnCameraChangeListener(this);

	}

	@Override
	public void onMapClick(LatLng point) {
		map2.animateCamera(CameraUpdateFactory.newLatLng(point));
		markerClicked = false;
	}

	@Override
	public void onMapLongClick(LatLng point) {
		if (!clicked) {
			zoom = map2.getCameraPosition().zoom;

			map2.addMarker(new MarkerOptions().position(point).draggable(true)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_percent)));
			mark = map.addMarker(new MarkerOptions().position(point).draggable(false)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_percent)));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom));
			clicked = true;
			markerPositionLat = point.latitude;
			markerPositionLng = point.longitude;
			lat = markerPositionLat;
			lng = markerPositionLng;
			LatLng destination = new LatLng(lat, lng);
			cR = new CreateRoute();
			cR.execute(String.valueOf(lat), String.valueOf(lng));
			// createRoute(destination);
			gO = new GetObjectsTask();
			gO.execute("");
		}
	}

	@Override
	public void onMarkerDrag(Marker marker) {
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		map2.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoom));
		mark.setPosition(marker.getPosition());
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoom));
		markerPositionLat = marker.getPosition().latitude;
		markerPositionLng = marker.getPosition().longitude;
		lat = markerPositionLat;
		lng = markerPositionLng;
		LatLng destination = new LatLng(lat, lng);
		cR = new CreateRoute();
		cR.execute(String.valueOf(lat), String.valueOf(lng));
		hideMap();
	}

	@Override
	public void onMarkerDragStart(Marker marker) {

	}

	@Override
	public void onCameraChange(CameraPosition position) {
		zoom = map2.getCameraPosition().zoom;
		LatLng markerpos = new LatLng(lat, lng);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerpos, zoom));
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		for (int i = 0; i < markerList.size(); i++) {
			if (markerList.get(i).equals(marker)) {
				zoom = map2.getCameraPosition().zoom;
				markerPositionLat = marker.getPosition().latitude;
				markerPositionLng = marker.getPosition().longitude;
				lat = markerPositionLat;
				lng = markerPositionLng;
				map.addMarker(new MarkerOptions().position(marker.getPosition()).draggable(true)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_percent)));
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoom));
				map2.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoom));
				objectId = object_ids.get(i);
				commentsLayout.setVisibility(View.VISIBLE);
				isUpdated = true;
				cR = new CreateRoute();
				cR.execute(String.valueOf(lat), String.valueOf(lng));
				gOT = new GetObjectTask();
				gOT.execute("");
			}
		}
		return false;
	}

	public void closeImage(View view) {
		imageButton.setVisibility(View.INVISIBLE);
		imageBig.setVisibility(View.INVISIBLE);
		//videoView.setVisibility(View.INVISIBLE);
	}

	public void onSpinnerClick(View view) {
		if (countCategories == 0)

		{
			dialog = new Dialog(ActivityAdd.this);
			dialog.setContentView(R.layout.wait);
			dialog.setTitle("Getting categories data");
			dialog.show();
			gC = new GetCategoriesTask();
			gC.execute("");
		} else {
			StartListView();
		}
	}

	public void fillSpinner() {

		countDiscountType = adapterDiscountType.getCount();
		if (countDiscountType == 0) {
			String text = getResources().getString(R.string.discount_type_title);
			dialog = new Dialog(ActivityAdd.this);
			dialog.setContentView(R.layout.wait);
			dialog.setTitle(text);
			dialog.show();
			dTT = new GetDiscountTypeTask();
			dTT.execute("");
		} else {
			data = new String[countDiscountType];

			for (int i = 0; i < data.length; i++) {
				data[i] = adapterDiscountType.getItem(i).getName();
			}

			selected = false;
			typeSpinner = (Spinner) findViewById(R.id.spinnerTypeDiscount);
			typeSpinner.setAdapter(typeSpinnerAdapter);
			typeSpinner.setOnItemSelectedListener(typeSelectedListener);
			typeSpinner.setOnTouchListener(typeSpinnerTouchListener);
			mInflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			Log.d(TAG, "UI Initialized");

			/*
			 * spinnerTypeDiscount =
			 * (Spinner)findViewById(R.id.spinnerTypeDiscount); adapter = new
			 * ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
			 * data);
			 */

			if (dialog != null) {
				if (dialog.isShowing()) {
					adapterDiscountType.onDestroy();
					dialog.dismiss();
					alert();
				}
			}

		}
	}

	/*
	 * public void hintSpinner(View view) {
	 * 
	 * hideHint(); }
	 * 
	 * public void hideHint() { textHint.setVisibility(View.GONE);
	 * adapter.setDropDownViewResource
	 * (android.R.layout.simple_spinner_dropdown_item);
	 * 
	 * spinnerTypeDiscount.setAdapter(adapter); // çàãîëîâîê
	 * spinnerTypeDiscount.setPrompt("Title"); // âûäåëÿåì ýëåìåíò
	 * spinnerTypeDiscount.setSelection(1); // óñòàíàâëèâàåì îáðàáîò÷èê íàæàòèÿ
	 * spinnerTypeDiscount .setOnItemSelectedListener(new
	 * OnItemSelectedListener() {
	 * 
	 * @Override public void onItemSelected(AdapterView<?> parent, View view,
	 * int position, long id) { type_discount_id = position + 1; // ïîêàçûâàåì
	 * ïîçèöèÿ íàæàòîãî ýëåìåíòà // Toast.makeText(getBaseContext(),
	 * "Position = " + // position, Toast.LENGTH_SHORT).show();
	 * 
	 * }
	 * 
	 * @Override public void onNothingSelected(AdapterView<?> arg0) {
	 * textHint.setVisibility(View.VISIBLE); } }); }
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case IDD_LIST_CAPTURE:

			final String[] mCaptureType = { "Photo", "Video" };

			builder = new AlertDialog.Builder(this);
			// builder.setTitle("Âûáèðàåì êîòà"); // çàãîëîâîê äëÿ äèàëîãà

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
			// builder.setTitle("Âûáèðàåì êîòà"); // çàãîëîâîê äëÿ äèàëîãà

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
				File file = new File(imagePath.get(position).toString());
				if (mTypeContent.get(position).equals("video")) {
					//videoView.setVisibility(View.VISIBLE);
					imageButton.setVisibility(View.VISIBLE);
					Uri videoF = Uri.fromFile(file);
					//videoView.setVideoURI(Uri.parse(String.valueOf(videoF)));
					//videoView.setMediaController(new MediaController(ActivityAdd.this));
					/*videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							// videoView.setVisibility(View.INVISIBLE);
						}
					});*/
					//videoView.requestFocus();
					//videoView.start();
				} else {

					Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
					imageBig.setImageBitmap(bitmap);
					imageBig.setVisibility(View.VISIBLE);
					imageButton.setVisibility(View.VISIBLE);
				}

			}
		});

	}

	public void LoadImageFromFileOnClick(View view) {
		showDialog(IDD_LIST_SELECT);
	}

	public void LoadImageFromCameraOnClick(View view) {

		showDialog(IDD_LIST_CAPTURE);
		// LoadImageFromCamera();
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

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return imagePath.size();
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

			String fileType = mTypeContent.get(position).toString();
			String filePath = imagePath.get(position).toString();
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
			textView.setText(mTypeContent.get(position).toString());
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

	private String getRealVideoPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Video.Media.DATA };
		CursorLoader loader = new CursorLoader(ActivityAdd.this, contentUri, proj, null, null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private String getRealImagePathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(ActivityAdd.this, contentUri, proj, null, null, null);
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
				if (!isUpdated) {
					urlAddr = "http://api.run4rebate.com/user/upload_img/&hash=" + send_hash;
				} else {
					obj = "&obj_id=" + objectId + Hash;
					try {
						send_hash = md5(URLEncoder.encode(obj, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					urlAddr = "http://api.run4rebate.com/user/upload_img/&obj_id=" + objectId
							+ "&hash=" + send_hash;
				}
				File fileC = new File(path_this);
				imagePath.add(fileC.getAbsolutePath());
				mTypeContent.add("photo");

				String filePath = fileC.getAbsolutePath();
				uT = new uploadTask();
				uT.execute(urlAddr, filePath, String.valueOf(requestCode));
				// sendMedia(urlAddr, filePath, requestCode) ;
				setGallery();
			}

		}

		else if (requestCode == VIDEO_CAPTURE) {

			if (resultCode == Activity.RESULT_CANCELED || resultCode == Activity.RESULT_OK) {

				String path_this = mPath;// getRealVideoPathFromURI(vCaptureUri);
				String urlAddr;
				if (!isUpdated) {
					urlAddr = "http://api.run4rebate.com/user/upload_video/&hash=" + send_hash;
				} else {
					obj = "&obj_id=" + objectId + Hash;
					try {
						send_hash = md5(URLEncoder.encode(obj, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					urlAddr = "http://api.run4rebate.com/user/upload_video/&obj_id=" + objectId
							+ "&hash=" + send_hash;
				}

				File fileC = new File(path_this);
				imagePath.add(fileC.getAbsolutePath());
				mTypeContent.add("video");

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
				if (!isUpdated) {
					urlAddr = "http://api.run4rebate.com/user/upload_img/&hash=" + send_hash;
				} else {
					obj = "&obj_id=" + objectId + Hash;
					try {
						send_hash = md5(URLEncoder.encode(obj, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					urlAddr = "http://api.run4rebate.com/user/upload_img/&obj_id=" + objectId
							+ "&hash=" + send_hash;
				}
				// setImageUri(imageUri);
				File fileG = new File(path_this);
				imagePath.add(fileG.getAbsolutePath());
				mTypeContent.add("photo");
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
				if (!isUpdated) {
					urlAddr = "http://api.run4rebate.com/user/upload_video/&hash=" + send_hash;
				} else {
					obj = "&obj_id=" + objectId + Hash;
					try {
						send_hash = md5(URLEncoder.encode(obj, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					urlAddr = "http://api.run4rebate.com/user/upload_video/&obj_id=" + objectId
							+ "&hash=" + send_hash;
				}
				Uri imageUri = data.getData();
				String path_this = getRealVideoPathFromURI(imageUri);
				File fileG = new File(path_this);
				imagePath.add(fileG.getAbsolutePath());
				mTypeContent.add("video");
				String filePath = fileG.getAbsolutePath();
				uT = new uploadTask();
				uT.execute(urlAddr, filePath, String.valueOf(requestCode));
				setGallery();

			}
		} else if (requestCode == REQUEST_OBJECT) {
			if (resultCode == Activity.RESULT_OK) {
				objectCategoryId = String.valueOf(data.getIntExtra("objectTypeId", 0));
				category_id = data.getIntExtra("objectTypeId", 0);
				objectCategoryName = data.getStringExtra("objectTypeName");
				spinnerTypeObject.setText(objectCategoryName);
			}

		} else if (requestCode == REQUEST_VOICE) {

			if (resultCode == RESULT_OK) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				eObjectName.setText(matches.get(0).toString());
			}
		}
	}

	class GetDiscountTypeTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String resultString = null;
			String types = "types";
			String jsonArray;
			String id = "id";
			String name = "name";
			String get_id = "id";
			String get_name = "name";
			countDiscountType = adapterDiscountType.getCount();
			if (countDiscountType == 0) {
				try {

					HttpClient httpclient = new DefaultHttpClient();

					HttpPost httppost = new HttpPost(
							"http://api.run4rebate.com/objects/get_types_discount/&lang=" + lang);

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
					String Hash = Constants.CONTROL_SUM;
					String send_hash = md5(URLEncoder.encode(Hash, "UTF-8").replace("+", "%20"));
					nameValuePairs.add(new BasicNameValuePair("hash", send_hash));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
					HttpResponse response = httpclient.execute(httppost);

					BufferedReader reader = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent(), "utf-8"));
					String line = null;

					while ((line = reader.readLine()) != null) {
						resultString = line;

					}

					JSONObject jsonObj = new JSONObject(resultString);

					for (int i = 0; i < jsonObj.length(); i++) {
						if (jsonObj.names().getString(i).equals(types)) {
							jsonArray = jsonObj.getString(types);
							JSONArray jsonTree = new JSONArray(jsonArray);
							for (int j = 0; j < jsonTree.length(); j++) {
								get_id = jsonTree.getJSONObject(j).getString(id);
								get_name = jsonTree.getJSONObject(j).getString(name);
								DiscountType newDiscount = new DiscountType(Long.parseLong(get_id),
										get_name);
								if (countDiscountType == 0) {
									long last_id = adapterDiscountType.addItem(newDiscount);
									adapterDiscountType.updateItem(last_id, get_id, get_name);
								}
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

		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			fillSpinner();
			// StartListView();
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
							"http://api.run4rebate.com/objects/get_categories/&lang=" + lang);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
					String Hash = Constants.CONTROL_SUM;
					String send_hash = md5(URLEncoder.encode(Hash, "UTF-8").replace("+", "%20"));
					nameValuePairs.add(new BasicNameValuePair("hash", send_hash));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
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

	class GetObjectsTask extends AsyncTask<String, Void, String> {
		int radius = 1;
		String resultString;
		JSONObject jsonObj;

		@Override
		protected String doInBackground(String... arg0) {

			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpPost httppost = new HttpPost("http://api.run4rebate.com/objects/gets/&");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);

				nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(lat)));
				nameValuePairs.add(new BasicNameValuePair("lng", String.valueOf(lng)));
				nameValuePairs.add(new BasicNameValuePair("radius", String.valueOf(radius)));
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

				jsonObj = new JSONObject(resultString);

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
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setMarkers(jsonObj);
		}

	}

	class SendTask extends AsyncTask<String, Void, String> {
		String resultString = null;
		String resultS = "anyresult";
		String send_token = account.access_token;
		int send_type_discount_id = type_discount_id;
		int send_category_id = category_id;
		int send_size_discount_id = size_discount_id;
		String obj_name;
		String send_name = objectName;
		String send_lat = String.valueOf(markerPositionLat);
		String send_lng = String.valueOf(markerPositionLng);
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
				send_name = eObjectName.getText().toString();
				HttpClient httpclient = new DefaultHttpClient();
				if (!isUpdated) {
					httppost = new HttpPost("http://api.run4rebate.com/objects/add/&");
				} else if (isUpdated && !operation.equals("comment")) {
					httppost = new HttpPost("http://api.run4rebate.com/objects/update/&");
				} else if (isUpdated && operation.equals("comment")) {
					httppost = new HttpPost("http://api.run4rebate.com/objects/add_comment/&");
				}
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);

				// nameValuePairs.add(new BasicNameValuePair("token",
				// send_token));
				if (!isUpdated) {

					nameValuePairs.add(new BasicNameValuePair("category_id", String
							.valueOf(send_category_id)));
					nameValuePairs.add(new BasicNameValuePair("lat", send_lat));
					nameValuePairs.add(new BasicNameValuePair("lng", send_lng));
					nameValuePairs.add(new BasicNameValuePair("name", send_name));
					for (int i = 0; i < photo_ids.size(); i++) {
						nameValuePairs.add(new BasicNameValuePair("photo_ids[]", String
								.valueOf(photo_ids.get(i).toString())));
					}
					nameValuePairs.add(new BasicNameValuePair("size_discount_id", String
							.valueOf(send_size_discount_id)));
					nameValuePairs.add(new BasicNameValuePair("type_discount_id", String
							.valueOf(send_type_discount_id)));

					for (int i = 0; i < video_ids.size(); i++) {
						nameValuePairs.add(new BasicNameValuePair("video_ids[]", String
								.valueOf(video_ids.get(i).toString())));
					}

				} else if (isUpdated && !operation.equals("comment")) {

					nameValuePairs.add(new BasicNameValuePair("obj_id", String.valueOf(objectId)));
					nameValuePairs.add(new BasicNameValuePair("size_discount_id", String
							.valueOf(send_size_discount_id)));

					nameValuePairs.add(new BasicNameValuePair("type_discount_id", String
							.valueOf(send_type_discount_id)));

				} else if (isUpdated && operation.equals("comment")) {
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

				jsonObj = new JSONObject(resultString);

				for (int i = 0; i < jsonObj.length(); i++) {

					if (jsonObj.names().getString(i).equals(id)) {

					} else if (jsonObj.names().getString(i).equals(type_discount_id)) {

					} else if (jsonObj.names().getString(i).equals(category_id)) {

					} else if (jsonObj.names().getString(i).equals(size_discount_id)) {

					} else if (jsonObj.names().getString(i).equals(name)) {

					}

					else if (jsonObj.names().getString(i).equals(lat)) {

					} else if (jsonObj.names().getString(i).equals(lng)) {

					} else if (jsonObj.names().getString(i).equals(type_discount_name)) {

					} else if (jsonObj.names().getString(i).equals(category_name)) {

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
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
					// isUpdated = true;
				}
			}
			sendObject = resultString;
			;
			if (operation.equals("comment")) {

				setObject(jsonObj);

			} else if (operation.equals("send")) {

				startActivityOptions();

			}
		}

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
	public void setMarkers(JSONObject jsonObj) {
		String id = null, image, name;
		double lat = 0, lng = 0;
		LatLng point = null;
		try {
			for (int i = 0; i < jsonObj.length(); i++) {

				if (jsonObj.names().getString(i).equals("objects")) {
					JSONArray jsonArray = new JSONArray(jsonObj.getString("objects"));
					for (int j = 0; j < jsonArray.length(); j++) {
						JSONObject jsonObject = jsonArray.getJSONObject(j);
						for (int k = 0; k < jsonObject.length(); k++) {
							if (jsonObject.names().getString(k).equals("id")) {
								id = jsonObject.getString("id");
								object_ids.add(Integer.parseInt(id));
							} else if (jsonObject.names().getString(k).equals("image")) {
								image = jsonObject.getString("image");
							} else if (jsonObject.names().getString(k).equals("lng")) {
								lng = jsonObject.getDouble("lng");
							} else if (jsonObject.names().getString(k).equals("lat")) {
								lat = jsonObject.getDouble("lat");
							} else if (jsonObject.names().getString(k).equals("name")) {
								name = jsonObject.getString("name");
							}

						}

						if (lat != 0 && lng != 0) {
							point = new LatLng(lat, lng);
						}

						if (point != null && id != "" || id != null) {

							markerList.add(map2.addMarker(new MarkerOptions()
									.position(point)
									.draggable(false)
									.snippet(id)
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.icon_pin_percent))));
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class uploadTask extends AsyncTask<String, Integer, File> {
		final ProgressDialog progressDialog = new ProgressDialog(ActivityAdd.this);
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

				// ñîáðàòü áóôåð äëÿ îòïðàâêè çàïðîñà
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

				// óñòàíîâêà Content-Length

				endBlockSize = BOUNDRY.length() + 6;

				httpConnection
						.setRequestProperty(
								"Content-Length",
								String.valueOf(requestBody.toString().length() + bufferSize
										+ endBlockSize));

				// ñîåäèíåíèå
				httpConnection.connect();

				// îòïðàâêà ïåðâîé ÷àñòè çàïðîñà
				DataOutputStream dataOS = new DataOutputStream(httpConnection.getOutputStream());
				publishProgress(80, 100);
				dataOS.writeBytes(requestBody.toString());

				// îòïðàâêà ôàéëà
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
					// åñëè âñå ïðîøëî íîðìàëüíî, ïîëó÷àåì ðåçóëüòàò
					// ìîæåò áûòü äðóãîé êîä, ñì.
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
					resultString = "ñåðâåð íå îòâåòèë";
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
			// îòîáðàæàåì ñîîáùåíèå, åñëè âîçíèêëà îøèáêà
			if (m_error != null) {
				m_error.printStackTrace();
				return;
			}
			// çàêðûâàåì ïðîãðåññ è óäàëÿåì âðåìåííûé ôàéë
			progressDialog.hide();
			// file.delete();
		}
	}
	public static void sendMedia(String urlAddr, String filePath, int type) {
		final String serverAddress = urlAddr;
		final File file = new File(filePath);
		final int typeCapture = type;
		Runnable r = new Runnable() {
			public void run() {
				String requestString = serverAddress;
				String resultString = new String("");
				StringBuffer requestBody = new StringBuffer();
				int typeCaptureServ = typeCapture;

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

					// ñîáðàòü áóôåð äëÿ îòïðàâêè çàïðîñà
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

					// óñòàíîâêà Content-Length

					endBlockSize = BOUNDRY.length() + 6;

					httpConnection.setRequestProperty(
							"Content-Length",
							String.valueOf(requestBody.toString().length() + bufferSize
									+ endBlockSize));

					// ñîåäèíåíèå
					httpConnection.connect();

					// îòïðàâêà ïåðâîé ÷àñòè çàïðîñà
					DataOutputStream dataOS = new DataOutputStream(httpConnection.getOutputStream());
					dataOS.writeBytes(requestBody.toString());

					// îòïðàâêà ôàéëà
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
						// åñëè âñå ïðîøëî íîðìàëüíî, ïîëó÷àåì ðåçóëüòàò
						// ìîæåò áûòü äðóãîé êîä, ñì.
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
						resultString = "ñåðâåð íå îòâåòèë";
					}

					JSONObject jsonObj = new JSONObject(resultString);
					for (int i = 0; i < jsonObj.length(); i++) {
						if (typeCaptureServ == PHOTO_CAPTURE) {
							if (jsonObj.names().getString(i).equals("img_id")) {
								resultString = jsonObj.getString("img_id");
								photo_ids.add(jsonObj.getInt("img_id"));
							}

							else if (jsonObj.names().getString(i).equals("error")) {
								resultString = jsonObj.getJSONObject("error").getString("message");
							}
						} else if (typeCaptureServ == VIDEO_CAPTURE) {
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

			}
		};
		new Thread(r).start();
	}

	// http://api.run4rebate.com/objects/get/&

	class GetObjectTask extends AsyncTask<String, Void, String> {
		String resultString;
		JSONObject jsonObj;

		@Override
		protected String doInBackground(String... arg0) {

			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpPost httppost = new HttpPost("http://api.run4rebate.com/objects/get/&");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);

				nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(objectId)));
				/*
				 * nameValuePairs.add(new BasicNameValuePair("lat",
				 * String.valueOf(lat))); nameValuePairs.add(new
				 * BasicNameValuePair("lng", String.valueOf(lng)));
				 */

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

				jsonObj = new JSONObject(resultString);

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
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setObject(jsonObj);
		}
	}
	public void setObject(JSONObject jsonObj) {
		String id = null, name, category_name;
		double lat = 0, lng = 0;
		int type_discount_id, category_id, get_size_discount_id;
		LatLng point = null;
		String jsonComments = "", jsonImages = "", jsonVideo = "";
		try {
			for (int i = 0; i < jsonObj.length(); i++) {

				if (jsonObj.names().getString(i).equals("id")) {
					id = jsonObj.getString("id");
				} else if (jsonObj.names().getString(i).equals("category_name")) {
					category_name = jsonObj.getString("category_name");
					spinnerTypeObject.setText(category_name);
				} else if (jsonObj.names().getString(i).equals("name")) {
					name = jsonObj.getString("name");
					eObjectName.setText(name);
				} else if (jsonObj.names().getString(i).equals("type_discount_id")) {
					type_discount_id = jsonObj.getInt("type_discount_id");
					selected = true;
					// typeSelectedListener.onItemSelected((AdapterView<?>)
					// typeSpinner.getAdapter(), typeSpinner,
					// type_discount_id-1, type_discount_id-1);
					// typeSpinner.setId(type_discount_id-1);
					typeSpinner.setSelection(type_discount_id - 1);
					// hideHint();
				} else if (jsonObj.names().getString(i).equals("category_id")) {
					category_id = jsonObj.getInt("category_id");
					objectCategoryId = String.valueOf(category_id);
				} else if (jsonObj.names().getString(i).equals("size_discount_id")) {
					get_size_discount_id = jsonObj.getInt("size_discount_id");
					size_discount_id = get_size_discount_id;

					if (size_discount_id == 1) {
						radio0.setChecked(true);
					} else if (size_discount_id == 2) {
						radio1.setChecked(true);
					} else if (size_discount_id == 3) {
						radio2.setChecked(true);
					} else if (size_discount_id == 4) {
						radio3.setChecked(true);
					}

				} else if (jsonObj.names().getString(i).equals("comments")) {
					jsonComments = jsonObj.getString("comments");
					listCreator(jsonComments);
				} else if (jsonObj.names().getString(i).equals("images")) {
					jsonImages = jsonObj.getString("images");
				}

			}
			if (dialog != null) {
				if (dialog.isShowing()) {
					try {
						dialog.dismiss();
					} catch (Exception e) {
					}
				}
			}
			mTD = new MyTaskDownload();
			mTD.execute(jsonImages.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	class MyTaskDownload extends AsyncTask<String, Integer, Void> {
		String operation;
		String jsonArray;
		File file;
		int count;
		int countU, last = 0;
		int downloadedSize = 0;
		int totalSize = 0;
		final ProgressDialog progressDialog = new ProgressDialog(ActivityAdd.this);

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
								if (mTypeContent.size() == countU) {
									break;
								} else if (jsonArr.getJSONObject(j).getString("type")
										.equals("image")) {
									imagePath.add(file.getAbsolutePath());
									mTypeContent.add("photo");
								} else if (jsonArr.getJSONObject(j).getString("type")
										.equals("video")) {
									imagePath.add(file.getAbsolutePath());
									mTypeContent.add("video");
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
				// ðàçúåäèíÿåìñÿ
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
			if (mTypeContent.size() == countU && countU != 0) {
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
				new String[] { USER_NAME, // âåðõíèé
											// òåêñò
						USER_MESSAGE }, new int[] { R.id.userName, // ññûëêà íà
																	// îáúåêò
																	// îòîáðàæàþùèé
																	// òåêñò
						R.id.userComment,

				}); // äîáàâèëè ññûëêó â ÷åì îòîáðàæàòü êàðòèíêè èç list.xml

		listComments.setAdapter(adapter);
		listComments.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	public void Comment(View view) {
		sT = new SendTask();
		sT.execute("comment");
	}
	private void initUI() {
		selected = false;
		typeSpinner = (Spinner) findViewById(R.id.spinnerTypeDiscount);
		typeSpinner.setAdapter(typeSpinnerAdapter);
		typeSpinner.setOnItemSelectedListener(typeSelectedListener);
		typeSpinner.setOnTouchListener(typeSpinnerTouchListener);
		mInflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		Log.d(TAG, "UI Initialized");
	}
	private SpinnerAdapter typeSpinnerAdapter = new BaseAdapter() {

		private TextView text;

		// private String[] data = { "Selection 1", "Selection 2", "Selection 3"
		// };

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflator.inflate(R.layout.row_spinner, null);
			}
			text = (TextView) convertView.findViewById(R.id.spinnerTarget);
			if (!selected) {
				text.setText(getResources().getString(R.string.option));
				text.setTextColor(getResources().getColor(R.color.color1));
			} else {
				text.setText(data[position]);
			}
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return data[position];
		}

		@Override
		public int getCount() {
			return data.length;
		}

		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflator
						.inflate(android.R.layout.simple_spinner_dropdown_item, null);
			}
			text = (TextView) convertView.findViewById(android.R.id.text1);
			text.setBackgroundColor(getResources().getColor(R.color.color2));
			text.setTextColor(getResources().getColor(R.color.color3));
			text.setHeight(getResources().getDimensionPixelSize(R.dimen.objects_edit_height));
			text.setTextSize(getResources().getDimensionPixelOffset(R.dimen.text_size_name));
			text.setText(data[position]);
			return convertView;
		};
	};
	private OnItemSelectedListener typeSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, "user selected : " + typeSpinner.getSelectedItem().toString());
			type_discount_id = position + 1;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	private OnTouchListener typeSpinnerTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			selected = true;
			((BaseAdapter) typeSpinnerAdapter).notifyDataSetChanged();
			return false;
		}
	};
	public void startActivityOptions() {

		Intent intent = new Intent();
		intent.setClass(this, ActivityOption.class);
		intent.putExtra(ActivityOption.jsonObjectGet, sendObject);
		intent.putExtra(ActivityOption.stringGalleryContentPath, imagePath.toString());
		intent.putExtra(ActivityOption.stringGalleryContentType, mTypeContent.toString());
		intent.putExtra(ActivityOption.isUpdated, isUpdated);

		startActivity(intent);
		finish();
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
	public boolean accessToken() {
		if (account.access_token != null) {
			return true;
		} else {
			return false;
		}

	}
}
