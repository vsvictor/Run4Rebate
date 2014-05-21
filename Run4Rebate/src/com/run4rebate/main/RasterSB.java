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
		// �������������
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
		// �������� ������� ������ � ������ ������������
		int w = this.getWidth();
		int h = this.getHeight();
		// �������� �������� ��� ��������������� ��������
		double sfx = (double) w / top.getWidth();
		double sfy = (double) h / top.getHeight();
		// ������������� �����������
		// Bitmap TMP = Bitmap.createScaledBitmap(filler,
		// (int)(filler.getWidth()*sfx), (int)(filler.getHeight()*sfy), true);
		// ��������� ��� � ��� ������ ���������� �����
		// ����������� ������� <img
		// src="http://davidmd.ru/wp-includes/images/smilies/icon_smile.gif"
		// alt=":)" class="wp-smiley">
		int pos = (w / this.getMax()) * this.getProgress();
		// � ������ ���� �������
		/*
		 * for (int i =(int)(5*sfx); i<pos; i+=TMP.getWidth() ) {
		 * canvas.drawBitmap(TMP, i,0, bmpPaint); }
		 */
		// ������������ �����
		Bitmap TMP = Bitmap.createScaledBitmap(top,
				(int) (top.getWidth() * sfx), (int) (top.getHeight() * sfy),
				true);
		// ������ ������� ������ �����
		canvas.drawBitmap(TMP, 0, 0, bmpPaint);
		// ������������ ��������
		TMP = Bitmap.createScaledBitmap(slider,
				(int) (slider.getWidth() * sfx),
				(int) (slider.getHeight() * sfy), true);
		// ������� ���� ����� �� ��������
		Canvas c = new Canvas(TMP);
		// ��������� ������ ������ � ������� ����������
		Rect r = new Rect(0, 0, 0, 0);
		if (!this.isInEditMode()) {
			textPaint.getTextBounds("" + this.getProgress(), 0,
					("" + this.getProgress()).length(), r);
		}
		// ������ ����� �� ����� ��������
		c.drawText("" + this.getProgress(), c.getWidth() / 2 - r.width() / 2,
				c.getHeight() / 2 + r.height() / 2, textPaint);
		// � ����� ������ ��� ��������
		canvas.drawBitmap(TMP, pos - TMP.getWidth() / 2, 0, bmpPaint);
	}
}
