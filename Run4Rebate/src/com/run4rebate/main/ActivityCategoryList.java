package mk.run4rebate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

@SuppressLint("NewApi")
public class ActivityCategoryList extends Activity {
	private ArrayList<HashMap<String, Object>> myFormsU, myFormsT;
	private final int REQUEST_VOICE = 806;
	private static final String CATEGORY_MAIN = "category_main";
	private static final String CATEGORY_SUB = "category_sub";
	private static final String CATEGORY = "category";
	private static final String IDS = "id";
	public static final String userName = "username";
	// public static final String Password = "password";
	public static final String Operation = "operation";
	public String userNameExtras = "userNameExtras";
	// public String passwordExtras = "passwordExtras";
	public String operationExtras = "operationExtras";
	public SqlAdapterCategories adapterCategories;
	public String operationEdit, operationExport, operationUpload,
			operationNew;
	public Intent intent;
	public String answer_1, answer_2, answer_2_1, answer_2_2, answer_2_3,
			answer_2_4, answer_3, answer_4, answer_9;
	public int Id;

	public String LastName, FirstName, MiddleName, BirthDate, Age, District,
			Street, StreetReg, House, HouseReg, UIK, Companions, Phone, Sex,
			Tour, TourComment, Meeting, MeetingComment, Note1, Note2, Note3,
			Date, UserId, newChecked, newIdExtras, Checked, Sending, FlatReg,
			Flat;
	public File fileLoad;
	public Dialog dialog;
	final Context context = this;
	int adapterCount, state;
	int version;
	public TextView stateView;
	Button title_uslugi, title_tovary;
	
	//private AdView adView;
	private MMAdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categories);
		title_uslugi = (Button) findViewById(R.id.button1);
		title_tovary = (Button) findViewById(R.id.button2);
		adapterCategories = new SqlAdapterCategories(this);
		version = Integer.valueOf(android.os.Build.VERSION.SDK);
		listCreator(state);
		
		
		
		
		adView = new MMAdView(this);

		adView.setApid(Constants.BANNER_APID);
		adView.setId(MMSDK.getDefaultAdId());

		int placementWidth = Constants.BANNER_AD_WIDTH;
		int placementHeight = Constants.BANNER_AD_HEIGHT;
		adView.setWidth(placementWidth);
		adView.setHeight(placementHeight);
		int layoutWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, placementWidth, getResources().getDisplayMetrics());
		int layoutHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, placementHeight, getResources().getDisplayMetrics());

		RelativeLayout adRelativeLayout = (RelativeLayout) findViewById(R.id.banner);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		adRelativeLayout.addView(adView, layoutParams);
		//Map<String, String> metaData = createMetaData();
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put(MMRequest.KEY_AGE, "14");
		MMRequest request = new MMRequest();
		request.setMetaValues(metaData);
		adView.setMMRequest(request);
		adView.setListener(new AdListener());
		MMSDK.trackConversion(this, Constants.MY_GOAL_ID);
		adView.getAd();
		
		
		
		
	/*	adView = new AdView(this, AdSize.BANNER, Constants.MY_AD_UNIT_ID);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.banner);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
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
			Toast.makeText(getApplicationContext(), "Recognizer Not Found",
					1000).show();
		}
		speakButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startVoiceRecognitionActivity();
			}
		});
	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"AndroidBite Voice Recognition...");
		startActivityForResult(intent, REQUEST_VOICE);
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
		Toast.makeText(getApplicationContext(), "Лепесток в разработке",
				Toast.LENGTH_LONG).show();
		/*
		 * Intent intent = new Intent(); intent.setClass(this,
		 * ActivityAccount.class); startActivity(intent); finish();
		 */
	}

	public void listCreator(int state) {
		final ListView listViewU = (ListView) findViewById(R.id.list);
		final ListView listViewT = (ListView) findViewById(R.id.list2);
		if (version > 10) {
			listViewU
					.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
		}
		String categoryName, parentId, parentIdUslugi = null, parentIdTovar = null, parentIdSubUslugi = null, parentIdSubTovar = null;// ,answer_9;
		long id;
		myFormsU = new ArrayList<HashMap<String, Object>>();
		myFormsT = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hmU, hmT;

		adapterCount = adapterCategories.getCount();

		for (int i = 0; i < adapterCount; i++) {

			id = adapterCategories.getItem(i).getId();
			categoryName = adapterCategories.getItem(i).getName();
			parentId = adapterCategories.getItem(i).getDistrictId();

			hmU = new HashMap<String, Object>();
			hmT = new HashMap<String, Object>();
			if (parentId.equals("0") && id == 1) {
				// hmU.put(CATEGORY_MAIN, categoryName);
				// hmU.put(IDS, id);
				parentIdUslugi = "1";
				title_uslugi.setText(categoryName);
				// myFormsU.add(hmU);
			} else if (parentId.equals("0") && id == 2) {
				// hmT.put(CATEGORY_MAIN, categoryName);
				// hmT.put(IDS, id);
				parentIdTovar = "2";
				title_tovary.setText(categoryName);

				// myFormsT.add(hmT);
			}
			if (parentId.equals(parentIdUslugi)) {
				hmU.put(CATEGORY_SUB, categoryName);
				hmU.put(IDS, id);
				parentIdSubUslugi = String.valueOf(id);
				myFormsU.add(hmU);
			} else if (parentId.equals(parentIdTovar)) {
				hmT.put(CATEGORY_SUB, categoryName);
				hmT.put(IDS, id);
				parentIdSubTovar = String.valueOf(id);
				myFormsT.add(hmT);
			}
			if (parentId.equals(parentIdSubUslugi)) {
				String s = categoryName;
				SpannableString ss = new SpannableString(s);
				ss.setSpan(new UnderlineSpan(), 0, s.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				String htmlTaggedString = "<u>" + categoryName + "</u>";
				Spanned textSpan = android.text.Html.fromHtml(htmlTaggedString);
				hmU.put(CATEGORY, ss);
				hmU.put(IDS, id);
				myFormsU.add(hmU);
			} else if (parentId.equals(parentIdSubTovar)) {
				String s = categoryName;
				SpannableString ss = new SpannableString(s);
				ss.setSpan(new UnderlineSpan(), 0, s.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				String htmlTaggedString = "<u>" + categoryName + "</u>";
				Spanned textSpan = android.text.Html.fromHtml(htmlTaggedString);
				hmT.put(CATEGORY, ss);
				hmT.put(IDS, id);
				myFormsT.add(hmT);
			}

			/*
			 * if (firstName.equals("0")) { hm.put(BOOKKEY, lastName);
			 * parentIdMain = String.valueOf(id);
			 * 
			 * } else if (firstName.equals("1") || firstName.equals("2")) {
			 * hm.put(DATEKEY, lastName); parentIdSub = String.valueOf(id); }
			 * else if(firstName.equals(parentIdSub)) { hm.put(PRICEKEY,
			 * lastName); }
			 */

		}

		final SimpleAdapter adapterU = new SimpleAdapter(this, myFormsU,
				R.layout.category_list, new String[] { CATEGORY_SUB, // нижний
																		// теккт
						CATEGORY, IDS, }, new int[] { R.id.textView2, // ссылка
																		// на
																		// объект
																		// отображающий
																		// текст
						R.id.textView3, R.id.recordId,

				}); // добавили ссылку в чем отображать картинки из list.xml

		listViewU.setAdapter(adapterU);
		listViewU.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listViewU.setOnItemClickListener(new OnItemClickListener() {
			Intent intent = new Intent();

			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Map<String, Object> map = (Map<String, Object>) adapterU
						.getItem(position);

				intent = new Intent();
				int idRecord = Integer.parseInt((String) map.get(IDS)
						.toString());
				if (map.get(CATEGORY) != null) {
					/*String categoryName = (String) map.get(CATEGORY).toString();

					intent.putExtra("objectTypeId", idRecord);
					intent.putExtra("objectTypeName", categoryName);
					setResult(Activity.RESULT_OK, intent);
					finish();*/
				} else if (map.get(CATEGORY_SUB) != null) {
					String categoryName = (String) map.get(CATEGORY_SUB)
							.toString();

					intent.putExtra("objectTypeId", idRecord);
					intent.putExtra("objectTypeName", categoryName);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}

				// long idRow =
				// Long.parseLong((String)map.get(IDSS).toString());
				// long idRecord =
				// Long.parseLong((String)map.get(IDS).toString());
				// long man2 = Long.parseLong((String)map.get(IDS).toString());
				/*
				 * Toast toast = Toast.makeText(getApplicationContext(),
				 * "вывод  " + man, Toast.LENGTH_LONG); toast.show();
				 */
			}
		});

		final SimpleAdapter adapterT = new SimpleAdapter(this, myFormsT,
				R.layout.category_list, new String[] { CATEGORY_MAIN, // верхний
																		// текст
						CATEGORY_SUB, // нижний теккт
						CATEGORY, IDS, }, new int[] { R.id.textView1, // ссылка
																		// на
																		// объект
																		// отображающий
																		// текст
						R.id.textView2, // ссылка на объект отображающий текст
						R.id.textView3, R.id.recordId,

				}); // добавили ссылку в чем отображать картинки из list.xml

		listViewT.setAdapter(adapterT);
		listViewT.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listViewT.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Map<String, Object> map = (Map<String, Object>) adapterT
						.getItem(position);
				intent = new Intent();
				int idRecord = Integer.parseInt((String) map.get(IDS)
						.toString());
				if (map.get(CATEGORY) != null) {
				/*	String categoryName = (String) map.get(CATEGORY).toString();

					intent.putExtra("objectTypeId", idRecord);
					intent.putExtra("objectTypeName", categoryName);
					setResult(Activity.RESULT_OK, intent);
					finish();*/
				} else if (map.get(CATEGORY_SUB) != null) {
					String categoryName = (String) map.get(CATEGORY_SUB)
							.toString();

					intent.putExtra("objectTypeId", idRecord);
					intent.putExtra("objectTypeName", categoryName);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}

			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_VOICE) {

			if (resultCode == RESULT_OK) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				for (int i = 0; i < myFormsT.size(); i++) {
					String categories = null;
					if (myFormsT.get(i).get(CATEGORY) != null) {
						if (myFormsT
								.get(i)
								.get(CATEGORY)
								.toString()
								.toLowerCase()
								.contains(
										matches.get(0).toLowerCase().toString())) {
							categories = "";/*myFormsT.get(i).get(CATEGORY)
									.toString();*/
						} else {
							continue;
						}
					} else if (myFormsT.get(i).get(CATEGORY_SUB) != null) {
						if (myFormsT
								.get(i)
								.get(CATEGORY_SUB)
								.toString()
								.toLowerCase()
								.contains(
										matches.get(0).toLowerCase().toString())) {
							categories = myFormsT.get(i).get(CATEGORY_SUB)
									.toString();
						} else {
							continue;
						}
					}
					if (categories != null && !categories.equals("")) {
						intent = new Intent();
						intent.putExtra("objectTypeId", myFormsT.get(i)
								.get(IDS).toString());
						intent.putExtra("objectTypeName", categories);
						setResult(Activity.RESULT_OK, intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(),
								"Совпадений не найдено", 1000).show();
					}
				}
				for (int i = 0; i < myFormsU.size(); i++) {
					String categories = null;
					if (myFormsU.get(i).get(CATEGORY) != null) {
						if (myFormsU
								.get(i)
								.get(CATEGORY)
								.toString()
								.toLowerCase()
								.contains(
										matches.get(0).toLowerCase().toString())) {
							categories = "";/*myFormsU.get(i).get(CATEGORY)
									.toString();*/
						} else {
							continue;
						}
					} else if (myFormsU.get(i).get(CATEGORY_SUB) != null) {
						if (myFormsU
								.get(i)
								.get(CATEGORY_SUB)
								.toString()
								.toLowerCase()
								.contains(
										matches.get(0).toLowerCase().toString())) {
							categories = myFormsU.get(i).get(CATEGORY_SUB)
									.toString();
						} else {
							continue;
						}
					}
					if (categories != null && !categories.equals("")) {
						intent = new Intent();
						intent.putExtra("objectTypeId", myFormsU.get(i)
								.get(IDS).toString());
						intent.putExtra("objectTypeName", categories);
						setResult(Activity.RESULT_OK, intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(),
								"Совпадений не найдено", 1000).show();
					}
				}
			}
		}
	}

}