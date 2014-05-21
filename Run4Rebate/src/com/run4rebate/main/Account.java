package mk.run4rebate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Account {
	public String access_token_VK;
	public String access_token_FB;
	public String access_token_TW;
	public String access_secret_TW;
	public String access_token_OK;
	public String access_token;

	public String user_name;
	public String user_first_name;
	public String user_last_name;
	public String user_photo;
	public String user_skype;
	public String user_email;
	public String user_phone;
	public String user_address;
	public String user_city;
	public String user_country;
	public String user_login;
	public String user_bonuses;
	public String user_notification;
	public String last_object_name;
	public long user_id_VK;
	public long user_id_GP;
	public long access_expires_FB;
	public long user_id_TW;
	public boolean checked;
	public long last_object_id;
	public boolean isFirst;

	public void save(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString("user_name", user_name);
		editor.putString("user_first_name", user_first_name);
		editor.putString("user_last_name", user_last_name);
		editor.putString("user_photo", user_photo);
		editor.putString("user_skype", user_skype);
		editor.putString("user_email", user_email);
		editor.putString("user_phone", user_phone);
		editor.putString("user_address", user_address);
		editor.putString("user_city", user_city);
		editor.putString("user_country", user_country);
		editor.putString("user_login", user_login);
		editor.putString("user_bonuses", user_bonuses);
		editor.putString("user_notification", user_notification);

		editor.putString("last_object_name", last_object_name);

		editor.putString("access_token", access_token);
		editor.putString("access_token_VK", access_token_VK);
		editor.putString("access_token_FB", access_token_FB);
		editor.putString("access_token_TW", access_token_TW);
		editor.putString("access_secret_TW", access_secret_TW);
		editor.putString("access_token_OK", access_token_OK);

		editor.putLong("user_id_VK", user_id_VK);
		editor.putLong("user_id_TW", user_id_TW);
		editor.putLong("user_id_GP", user_id_GP);
		editor.putLong("access_expires_FB", access_expires_FB);

		editor.putLong("last_object_id", last_object_id);
		editor.putBoolean("checked", checked);
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	public void restore(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		user_name = prefs.getString("user_name", null);
		user_first_name = prefs.getString("user_first_name", null);
		user_last_name = prefs.getString("user_last_name", null);

		user_photo = prefs.getString("user_photo", null);
		user_skype = prefs.getString("user_skype", null);
		user_email = prefs.getString("user_email", null);
		user_phone = prefs.getString("user_phone", null);
		user_address = prefs.getString("user_address", null);
		user_city = prefs.getString("user_city", null);
		user_country = prefs.getString("user_country", null);
		user_login = prefs.getString("user_login", null);
		user_bonuses = prefs.getString("user_bonuses", null);
		user_notification = prefs.getString("user_notification", null);

		last_object_name = prefs.getString("last_object_name", null);

		access_token = prefs.getString("access_token", null);
		access_token_VK = prefs.getString("access_token_VK", null);
		access_token_FB = prefs.getString("access_token_FB", null);
		access_token_TW = prefs.getString("access_token_TW", null);
		access_secret_TW = prefs.getString("access_secret_TW", null);
		access_token_OK = prefs.getString("access_token_OK", null);

		user_id_VK = prefs.getLong("user_id_VK", 0);
		user_id_TW = prefs.getLong("user_id_TW", 0);
		user_id_GP = prefs.getLong("user_id_GP", 0);
		access_expires_FB = prefs.getLong("access_expires_FB", 0);

		last_object_id = prefs.getLong("last_object_id", 0);

		checked = prefs.getBoolean("checked", false);
		isFirst = prefs.getBoolean("isFirst", true);
	}
}
