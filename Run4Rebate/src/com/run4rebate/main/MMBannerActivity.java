package mk.run4rebate;

import java.util.HashMap;
import java.util.Map;

import com.millennialmedia.android.MMRequest;

import android.app.Activity;

public abstract class MMBannerActivity extends Activity {
	/** Below are some of the meta data values you can use */
	protected Map<String, String> createMetaData()
	{
		Map<String, String> metaData = new HashMap<String, String>();
		metaData.put(MMRequest.KEY_AGE, "14");
		/*metaData.put(MMRequest.KEY_GENDER, MMRequest.GENDER_MALE);
		metaData.put(MMRequest.KEY_ZIP_CODE, "21224");
		metaData.put(MMRequest.KEY_MARITAL_STATUS, MMRequest.MARITAL_SINGLE);
		metaData.put(MMRequest.KEY_ORIENTATION, MMRequest.ORIENTATION_STRAIGHT);
		metaData.put(MMRequest.KEY_ETHNICITY, MMRequest.ETHNICITY_HISPANIC);
		metaData.put(MMRequest.KEY_INCOME, "50000");
		metaData.put(MMRequest.KEY_CHILDREN, "yes");
		metaData.put(MMRequest.KEY_POLITICS, "other");
		metaData.put(MMRequest.KEY_KEYWORDS, "soccer");*/
		return metaData;
	}
}
