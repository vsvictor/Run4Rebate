package mk.run4rebate;

import android.net.Uri;

public class News {

	String date;
	String news;
	Uri image;
	int size;

	News(String date_s, String news_s, Uri file, int size_s) {
		date = date_s;
		news = news_s;
		image = file;
		size = size_s;
	}
}
