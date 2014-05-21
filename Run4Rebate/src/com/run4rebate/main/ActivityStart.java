package mk.run4rebate;

import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

public class ActivityStart extends Activity {
	// Account account = new Account();
	Intent intent;
	MyTaskStart mtS;

	private static final class Size {
		int width;
		int height;
	}

	private static Size getBitmapSize(String path) {
		Size size = new Size();
		try {
			int separator = path.indexOf('_');
			if (separator >= 0) {
				int lastSeparator = path.indexOf('_', separator + 1);
				if (lastSeparator >= 0) {
					String resolution = path.substring(separator + 1, lastSeparator);
					int resSeparator = resolution.indexOf('x');
					size.width = Integer.parseInt(resolution.substring(0, resSeparator));
					size.height = Integer.parseInt(resolution.substring(resSeparator + 1));
				}
			}
		} catch (Exception e) {
			size.width = size.height = 0;
			Log.d("", e.toString());
		}
		return size;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_screen);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth(); // deprecated
		int height = display.getHeight(); // deprecated

		if (width > height) {
			int temp = width;
			width = height;
			height = temp;
		}

		String folderName = Locale.getDefault().getLanguage().equalsIgnoreCase("ru") ? "splash_ru"
				: "splash_en";

		int bestDiffWidth = -1;
		int bestWidth = 0;
		try {
			String[] paths = getResources().getAssets().list(folderName);
			for (String path : paths) {
				Size size = getBitmapSize(path);
				if (size.width > 0) {
					if (bestDiffWidth < 0 || Math.abs(size.width - width) < bestDiffWidth) {
						bestDiffWidth = Math.abs(size.width - width);
						bestWidth = size.width;
					}
				}
			}
		} catch (Exception e) {
			Log.d("", e.toString());
		}

		String bestPath = null;
		int bestDiffHeight = -1;

		try {
			String[] paths = getResources().getAssets().list(folderName);
			for (String path : paths) {
				Size size = getBitmapSize(path);
				if (size.width == bestWidth && size.height > 0) {
					if (bestDiffHeight < 0 || Math.abs(size.height - height) < bestDiffHeight) {
						bestDiffHeight = Math.abs(size.height - height);
						bestPath = path;
					}
				}
			}
		} catch (Exception e) {
			Log.d("", e.toString());
		}
		Log.d("", "best path: " + bestPath);

		if (!TextUtils.isEmpty(bestPath)) {
			try {
				InputStream is = getAssets().open(folderName + "/" + bestPath);
				Bitmap bitmap = BitmapFactory.decodeStream(is);

				((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);
			} catch (Exception e) {
				Log.d("", e.toString());
			}
			// account.restore(this);
			startAddSearchActivity(false);
		} else {
			startAddSearchActivity(true);
		}
	}

	private void startAddSearchActivity(boolean startImmediately) {
		intent = new Intent();
		// intent.setClass(this, ActivityAdd.class);
		intent.setClass(this, ActivityAddSearch.class);
		if (startImmediately) {
			startActivity(intent);
			finish();
		} else {
			mtS = new MyTaskStart();
			mtS.execute("");
		}
	}

	class MyTaskStart extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... arg0) {

			try {
				TimeUnit.SECONDS.sleep(4);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			startActivity(intent);
			finish();
		}
	}
}
