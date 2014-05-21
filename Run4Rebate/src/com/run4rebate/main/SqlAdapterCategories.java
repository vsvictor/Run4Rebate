package mk.run4rebate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SqlAdapterCategories extends BaseAdapter {

	private static final String DB_NAME = "database.db";
	private static final String CATEGORIES_TABLE = "categories";
	private static final String DISCOUNT_TYPE_TABLE = "discount";
	private static final int DB_VESION = 1;

	private static final String KEY_ID_STREETS = "id";
	private static final int COLUMN_ID_STREETS = 0;
	private static final String KEY_NAME_STREETS = "name";
	private static final int COLUMN_NAME_STREETS = 1;
	private static final String KEY_PARENT_ID_STREETS = "parent_id";
	private static final int COLUMN_PARENT_ID_STREETS = 2;
	private static final String KEY_ID_DISCOUNT_TYPE = "id";
	private static final String KEY_NAME_DISCOUNT_TYPE = "name";
	/*
	 * private static final String KEY_BUILDINGS_STREETS = "buildings"; private
	 * static final int COLUMN_BUILDINGS_STREETS = 3;
	 */

	private Cursor cursor;

	private SQLiteDatabase database;
	private DbOpenHelper dbOpenHelper;
	private Context context;

	public SqlAdapterCategories(Context context) {
		super();
		this.context = context;
		init();
	}

	@Override
	public long getItemId(int position) {
		Categories nameOnPosition = getItem(position);
		return nameOnPosition.getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parrent) {
		TextView textView;
		textView = (TextView) convertView;

		textView.setText(getItem(position).getName());
		return textView;
	}

	@Override
	public int getCount() {
		Log.e(this.toString(), "Cursor Count");
		return cursor.getCount();

	}

	@Override
	public Categories getItem(int position) {
		if (cursor.moveToPosition(position)) {
			long id = cursor.getLong(COLUMN_ID_STREETS);
			String name = cursor.getString(COLUMN_NAME_STREETS);
			String district_id = cursor.getString(COLUMN_PARENT_ID_STREETS);
			/*
			 * String buildings = cursor.getString(COLUMN_BUILDINGS_STREETS);
			 */

			Categories nameOnPositon = new Categories(id, name, district_id);// ,
																				// district_id,buildings);

			/*
			 * Streets nameOnPositon = new Streets(id, imageName, gpsX, gpsY,
			 * sending);
			 */
			Log.e(this.toString(), "Cursor Move");
			return nameOnPositon;
		} else {
			throw new CursorIndexOutOfBoundsException(
					"Cant move cursor on postion");
		}
	}

	public Cursor getAllEntries() {
		String[] columnsToTakeStreets = { KEY_ID_STREETS, KEY_NAME_STREETS,
				KEY_PARENT_ID_STREETS };/*
										 * , KEY_BUILDINGS_STREETS }; ;
										 */

		// return database.query(CATEGORIES_TABLE, columnsToTakeClient, null
		// ,null, null, null, null);
		return database.query(CATEGORIES_TABLE, columnsToTakeStreets, null,
				null, null, null, null);
		// KEY_SENDING_IMAGE + "= 'false'", null, null, null, null);
		/*
		 * return database.query(IMAGES_TABLE, columnsToTakeImage,
		 * KEY_SENDING_IMAGE + "=?", new String[] {"false"}, null, null, null,
		 * KEY_ID_IMAGE);
		 */
	}

	public long addItem(Categories clients) {
		ContentValues values = new ContentValues();
		/*
		 * values.put(KEY_IMAGE_NAME, images.getImageName());
		 * values.put(KEY_GPSX_IMAGE, images.getGpsX());
		 * values.put(KEY_GPSY_IMAGE, images.getGpsY());
		 * values.put(KEY_SENDING_IMAGE, images.getSending());
		 */
		// values.put(KEY_ID_STREETS,clients.getId());
		values.put(KEY_NAME_STREETS, clients.getName());
		values.put(KEY_PARENT_ID_STREETS, clients.getDistrictId());
		// values.put(KEY_BUILDINGS_STREETS,clients.getBuildings());

		long id = database.insert(CATEGORIES_TABLE, null, values);
		refresh();
		return id;
	}

	public boolean removeItem(Categories nameToRemove) {
		boolean isDeleted = (database.delete(CATEGORIES_TABLE, KEY_ID_STREETS
				+ "=?", new String[] { nameToRemove.getId() + "" })) > 0;
		refresh();
		return isDeleted;
	}

	public boolean updateItem(long id, String newId, String newName,
			String newDistrictId, String newBuildings) {
		ContentValues values = new ContentValues();
		/*
		 * values.put(KEY_IMAGE_NAME, newImageName); values.put(KEY_GPSX_IMAGE,
		 * newGpsX); values.put(KEY_GPSY_IMAGE, newGpsY);
		 * values.put(KEY_SENDING_IMAGE, newSending);
		 */

		values.put(KEY_ID_STREETS, newId);
		values.put(KEY_NAME_STREETS, newName);
		values.put(KEY_PARENT_ID_STREETS, newDistrictId);
		/*
		 * values.put(KEY_BUILDINGS_STREETS,newBuildings);
		 */

		boolean isUpdated = (database.update(CATEGORIES_TABLE, values,
				KEY_ID_STREETS + "=?", new String[] { id + "" })) > 0;
		return isUpdated;
	}

	public boolean updateChecked(long id, String newId, String newChecked) {
		ContentValues values = new ContentValues();

		values.put(KEY_ID_STREETS, newId);

		boolean isUpdated = (database.update(CATEGORIES_TABLE, values,
				KEY_ID_STREETS + "=?", new String[] { id + "" })) > 0;
		return isUpdated;
	}

	public void onDestroy() {
		cursor.close();
		dbOpenHelper.close();
		Log.e(this.toString(), "Cursor Destroyed");
		Log.e(this.toString(), "Helper Destroyed");
	}

	private void refresh() {
		cursor = getAllEntries();
		Log.e(this.toString(), "Cursor created refresh");
		notifyDataSetChanged();

	}

	private void init() {
		dbOpenHelper = new DbOpenHelper(context, DB_NAME, null, DB_VESION);
		try {
			database = dbOpenHelper.getWritableDatabase();
		} catch (SQLException e) {
			Log.e(this.toString(), "Error while getting database");
			throw new Error("The end");
		}

		cursor = getAllEntries();
		Log.e(this.toString(), "Cursor createde Helper");
	}

	private static class DbOpenHelper extends SQLiteOpenHelper {

		public DbOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			final String CREATE_CATEGORIES_TABLE = "CREATE TABLE "
					+ CATEGORIES_TABLE + " ("
					/*
					 * + KEY_ID_IMAGE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					 * KEY_IMAGE_NAME + " TEXT NOT NULL, " + KEY_GPSX_IMAGE +
					 * " TEXT NOT NULL, " + KEY_GPSY_IMAGE + " TEXT NOT NULL, "
					 * + KEY_SENDING_IMAGE + " TEXT NOT NULL "
					 */
					+ KEY_ID_STREETS + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_NAME_STREETS + " TEXT NOT NULL, "
					+ KEY_PARENT_ID_STREETS + " TEXT NOT NULL "
					/*
					 * + KEY_BUILDINGS_STREETS + " TEXT NOT NULL "
					 */

					+ " );";

			db.execSQL(CREATE_CATEGORIES_TABLE);

			final String CREATE_DISCOUNT_TABLE = "CREATE TABLE "
					+ DISCOUNT_TYPE_TABLE + " ("
					/*
					 * + KEY_ID_IMAGE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					 * KEY_IMAGE_NAME + " TEXT NOT NULL, " + KEY_GPSX_IMAGE +
					 * " TEXT NOT NULL, " + KEY_GPSY_IMAGE + " TEXT NOT NULL, "
					 * + KEY_SENDING_IMAGE + " TEXT NOT NULL "
					 */
					+ KEY_ID_DISCOUNT_TYPE
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_NAME_DISCOUNT_TYPE + " TEXT NOT NULL "
					// + KEY_PARENT_ID_STREETS + " TEXT NOT NULL "
					/*
					 * + KEY_BUILDINGS_STREETS + " TEXT NOT NULL "
					 */

					+ " );";

			db.execSQL(CREATE_DISCOUNT_TABLE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
			onCreate(db);
		}
	}

}
