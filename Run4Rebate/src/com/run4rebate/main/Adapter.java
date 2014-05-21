package mk.run4rebate;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan.LeadingMarginSpan2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Adapter extends BaseAdapter {
	Context ctx;
	LayoutInflater lInflater;
	ArrayList<News> objects;

	Adapter(Context context, ArrayList<News> products) {
		ctx = context;
		objects = products;
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// кол-во элементов
	@Override
	public int getCount() {
		return objects.size();
	}

	// элемент по позиции
	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	// id по позиции
	@Override
	public long getItemId(int position) {
		return position;
	}

	// пункт списка
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// используем созданные, но не используемые view
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.list, parent, false);
		}

		News p = getProduct(position);

		// заполняем View в пункте списка данными из товаров: наименование, цена
		// и картинка

		int leftMargin = p.size;
		SpannableString ss = new SpannableString(p.news);
		ss.setSpan(new MyLeadingMarginSpan2(4, leftMargin), 0, ss.length(), 0);

		((TextView) view.findViewById(R.id.textDate)).setText(p.date);
		((TextView) view.findViewById(R.id.textContent)).setText(ss);
		((ImageView) view.findViewById(R.id.img)).setImageURI(p.image);

		// присваиваем чекбоксу обработчик
		// пишем позицию
		// заполняем данными из товаров: в корзине или нет
		return view;
	}

	// товар по позиции
	News getProduct(int position) {
		return ((News) getItem(position));
	}

	// содержимое корзины

	class MyLeadingMarginSpan2 implements LeadingMarginSpan2 {
		private int margin;
		private int lines;

		MyLeadingMarginSpan2(int lines, int margin) {
			this.margin = margin;
			this.lines = lines;
		}

		@Override
		public int getLeadingMargin(boolean first) {
			if (first) {
				return margin;
			} else {
				return 0;
			}
		}

		@Override
		public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
				int top, int baseline, int bottom, CharSequence text,
				int start, int end, boolean first, Layout layout) {
		}

		@Override
		public int getLeadingMarginLineCount() {
			return lines;
		}
	};

}