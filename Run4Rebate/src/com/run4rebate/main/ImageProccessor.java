package mk.run4rebate;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageProccessor {
	private ImageProccessor() {
	};

	public static Bitmap getRoundedCornersImage(Bitmap source, int radiusPixels) {

		if (source == null) {
			// we cant proccess null image, go out
			return null;
		}
		final int sourceWidth = source.getWidth();
		final int sourceHeight = source.getHeight();
		final Bitmap output = Bitmap.createBitmap(sourceWidth, sourceHeight,
				Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(output);

		final int color = 0xFF000000;
		final Paint paint = new Paint();
		paint.setColor(color);

		final Rect rect = new Rect(0, 0, sourceWidth, sourceHeight);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, radiusPixels, radiusPixels, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);

		return output;
	}

	public static Bitmap getCroppedBitmap(Bitmap source, int left, int top,
			int bottom, int right) {

		if (source == null) {
			return null;
		}

		final int sourceWidth = source.getWidth();
		final int sourceHeight = source.getHeight();
		final int outputWidth = right - left;
		final int outputHeight = bottom - top;

		if (sourceWidth < outputWidth || sourceHeight < outputHeight) {
			throw new IllegalArgumentException(
					"Destination size larget than source size. Cant crop that way.");
		}

		final Bitmap output = Bitmap.createBitmap(outputWidth, outputHeight,
				Bitmap.Config.ARGB_8888);

		final Rect dest = new Rect(0, 0, outputWidth, outputHeight);
		final Rect src = new Rect(left, top, right, bottom);

		Canvas canvas = new Canvas(output);
		canvas.drawBitmap(source, src, dest, new Paint());

		return output;
	}

}
