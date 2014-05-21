package mk.run4rebate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class RasterSB extends SeekBar {

	Bitmap top, filler, slider;
	Paint bmpPaint = new Paint(), textPaint = new Paint();

	private void init(Context c) {
		// Инициализация
		top = BitmapFactory.decodeResource(c.getResources(),
				R.drawable.track_scroll_meter);
		// filler = BitmapFactory.decodeResource(c.getResources(),
		// R.drawable.thumb_scroll_meters);
		slider = BitmapFactory.decodeResource(c.getResources(),
				R.drawable.thumb_scroll_meters);
		if (!this.isInEditMode()) {
			textPaint.setShadowLayer(2, 0, 0, Color.WHITE);
		}
		textPaint.setColor(Color.BLUE);
		textPaint.setTextSize(10);
	}

	public RasterSB(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init(context);
	}

	public RasterSB(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	public RasterSB(Context context) {
		super(context);
		this.init(context);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// Получаем текущую ширину и высоту прогрессбара
		int w = this.getWidth();
		int h = this.getHeight();
		// Получаем значения для масштабирования битмапов
		double sfx = (double) w / top.getWidth();
		double sfy = (double) h / top.getHeight();
		// Масштабаируем заполнитель
		// Bitmap TMP = Bitmap.createScaledBitmap(filler,
		// (int)(filler.getWidth()*sfx), (int)(filler.getHeight()*sfy), true);
		// Вычисляем где у нас должен находиться конец
		// заполняющей колбасы <img
		// src="http://davidmd.ru/wp-includes/images/smilies/icon_smile.gif"
		// alt=":)" class="wp-smiley">
		int pos = (w / this.getMax()) * this.getProgress();
		// И рисуем саму колбасу
		/*
		 * for (int i =(int)(5*sfx); i<pos; i+=TMP.getWidth() ) {
		 * canvas.drawBitmap(TMP, i,0, bmpPaint); }
		 */
		// Масштабируем рамку
		Bitmap TMP = Bitmap.createScaledBitmap(top,
				(int) (top.getWidth() * sfx), (int) (top.getHeight() * sfy),
				true);
		// Поверх колбасы рисуем рамку
		canvas.drawBitmap(TMP, 0, 0, bmpPaint);
		// масштабируем ползунок
		TMP = Bitmap.createScaledBitmap(slider,
				(int) (slider.getWidth() * sfx),
				(int) (slider.getHeight() * sfy), true);
		// Создаем ноую канву из ползунка
		Canvas c = new Canvas(TMP);
		// Вычисляем размер текста с текущим прогрессом
		Rect r = new Rect(0, 0, 0, 0);
		if (!this.isInEditMode()) {
			textPaint.getTextBounds("" + this.getProgress(), 0,
					("" + this.getProgress()).length(), r);
		}
		// Рисуем текст на канве ползунка
		c.drawText("" + this.getProgress(), c.getWidth() / 2 - r.width() / 2,
				c.getHeight() / 2 + r.height() / 2, textPaint);
		// А затем рисуем сам ползунок
		canvas.drawBitmap(TMP, pos - TMP.getWidth() / 2, 0, bmpPaint);
	}
}
