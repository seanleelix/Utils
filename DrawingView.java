import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

	// drawing path
	private Path drawPath;
	// drawing and canvas paint
	private Paint drawPaint, canvasPaint;
	// initial color
	private int paintColor = 0xFFFF0000;
	// canvas
	private Canvas drawCanvas;
	// canvas bitmap
	private Bitmap canvasBitmap;

	private float brushSize, lastBrushSize;

	private boolean erase = false;

	public static final int STATUS_INIT = 1;

	public static final int STATUS_ZOOM_OUT = 2;

	public static final int STATUS_ZOOM_IN = 3;

	public static final int STATUS_MOVE = 4;

	private Matrix matrix = new Matrix();

	private Bitmap sourceBitmap;

	private int currentStatus;

	private int width;

	private int height;

	private float centerPointX;

	private float centerPointY;

	private float currentBitmapWidth;

	private float currentBitmapHeight;

	private float lastXMove = -1;

	private float lastYMove = -1;

	private float movedDistanceX;

	private float movedDistanceY;

	private float totalTranslateX;

	private float totalTranslateY;

	public float totalRatio;

	private float scaledRatio;

	private float initRatio;

	private double lastFingerDis;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		currentStatus = STATUS_INIT;
		setupDrawing();
	}

	private void setupDrawing() {
		// get drawing area setup for interaction

		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		brushSize = getResources().getInteger(R.integer.medium_size);
		lastBrushSize = brushSize;
		drawPaint.setStrokeWidth(brushSize);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// view given size
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		switch (currentStatus) {
		case STATUS_ZOOM_OUT:
		case STATUS_ZOOM_IN:
			zoom(canvas);
			break;
		case STATUS_MOVE:
			move(canvas);
			break;
		case STATUS_INIT:
			initBitmap(canvas);
		default:
			canvas.drawBitmap(sourceBitmap, matrix, null);
			break;
		}
		if (DrawingActivity.editFlag) {
			// draw view
			// canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
			canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
			canvas.drawPath(drawPath, drawPaint);
		} else {
			canvas.drawBitmap(canvasBitmap, matrix, canvasPaint);
		}

	}

	public void setImageBitmap(Bitmap bitmap) {
//		Log.i("sean", "setImageBitmap");
		sourceBitmap = bitmap;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			width = getWidth();
			height = getHeight();
			// sourceBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, width,
			// height);
			// Log.i("sean", "onLayout---> width:" + width + " height:" + height);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (DrawingActivity.editFlag) {
			float touchX = event.getX();
			float touchY = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				drawPath.moveTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_MOVE:
				drawPath.lineTo(touchX, touchY);
				if (erase) {
					drawCanvas.drawPath(drawPath, drawPaint);
					drawPath.reset();
					drawPath.moveTo(touchX, touchY);
				}
				break;
			case MotionEvent.ACTION_UP:

				drawCanvas.drawPath(drawPath, drawPaint);
				// finish drawing
				drawPath.reset();

				break;
			default:
				return false;
			}
			invalidate();
			return true;
		} else {
			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_POINTER_DOWN:
				if (event.getPointerCount() == 2) {
					lastFingerDis = distanceBetweenFingers(event);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (event.getPointerCount() == 1) {
					float xMove = event.getX();
					float yMove = event.getY();
					if (lastXMove == -1 && lastYMove == -1) {
						lastXMove = xMove;
						lastYMove = yMove;
					}
					currentStatus = STATUS_MOVE;
					movedDistanceX = xMove - lastXMove;
					movedDistanceY = yMove - lastYMove;
					if (totalTranslateX + movedDistanceX > 0) {
						movedDistanceX = 0;
					} else if (width - (totalTranslateX + movedDistanceX) > currentBitmapWidth) {
						movedDistanceX = 0;
					}
					if (totalTranslateY + movedDistanceY > 0) {
						movedDistanceY = 0;
					} else if (height - (totalTranslateY + movedDistanceY) > currentBitmapHeight) {
						movedDistanceY = 0;
					}
					invalidate();
					lastXMove = xMove;
					lastYMove = yMove;
				} else if (event.getPointerCount() == 2) {
					centerPointBetweenFingers(event);
					double fingerDis = distanceBetweenFingers(event);
					if (fingerDis > lastFingerDis) {
						currentStatus = STATUS_ZOOM_OUT;
					} else {
						currentStatus = STATUS_ZOOM_IN;
					}
					if ((currentStatus == STATUS_ZOOM_OUT && totalRatio < 4 * initRatio)
							|| (currentStatus == STATUS_ZOOM_IN && totalRatio > initRatio)) {
						scaledRatio = (float) (fingerDis / lastFingerDis);
						totalRatio = totalRatio * scaledRatio;
						if (totalRatio > 4 * initRatio) {
							totalRatio = 4 * initRatio;
						} else if (totalRatio < initRatio) {
							totalRatio = initRatio;
						}
						invalidate();
						lastFingerDis = fingerDis;
					}
				}
				break;
			case MotionEvent.ACTION_POINTER_UP:
				if (event.getPointerCount() == 2) {
					lastXMove = -1;
					lastYMove = -1;
				}
				break;
			case MotionEvent.ACTION_UP:
				lastXMove = -1;
				lastYMove = -1;
				break;
			default:
				break;
			}
			return true;

		}
	}

	// private void zoom(Canvas canvas) {
	// matrix.reset();
	// Log.i("sean", "totalRatio:" + totalRatio);
	// matrix.postScale(totalRatio, totalRatio);
	// float scaledWidth = sourceBitmap.getWidth() * totalRatio;
	// float scaledHeight = sourceBitmap.getHeight() * totalRatio;
	// float translateX = 0f;
	// float translateY = 0f;
	// if (currentBitmapWidth < width) {
	// // translateX = (width - scaledWidth) / 2f;
	// translateX = 0;
	// } else {
	// // translateX = totalTranslateX * scaledRatio + centerPointX
	// // * (1 - scaledRatio);
	// translateX = centerPointX * (1 - scaledRatio);
	// if (translateX > 0) {
	// translateX = 0;
	// } else if (width - translateX > scaledWidth) {
	// translateX = width - scaledWidth;
	// }
	// }
	// if (currentBitmapHeight < height) {
	// // translateY = (height - scaledHeight) / 2f;
	// translateY = 0;
	// } else {
	// // translateY = totalTranslateY * scaledRatio + centerPointY
	// // * (1 - scaledRatio);
	// translateY = centerPointY * (1 - scaledRatio);
	// if (translateY > 0) {
	// translateY = 0;
	// } else if (height - translateY > scaledHeight) {
	// translateY = height - scaledHeight;
	// }
	// }
	// matrix.postTranslate(translateX, translateY);
	// totalTranslateX = translateX;
	// totalTranslateY = translateY;
	// currentBitmapWidth = scaledWidth;
	// currentBitmapHeight = scaledHeight;
	// canvas.drawBitmap(sourceBitmap, matrix, null);
	// }

	private void zoom(Canvas canvas) {
		matrix.reset();
		matrix.postScale(totalRatio, totalRatio);
		float scaledWidth = sourceBitmap.getWidth() * totalRatio;
		float scaledHeight = sourceBitmap.getHeight() * totalRatio;
		float translateX = 0f;
		float translateY = 0f;
		if (currentBitmapWidth < width) {
			// translateX = (width - scaledWidth) / 2f;
			translateX = 0;
		} else {
			translateX = totalTranslateX * scaledRatio + centerPointX * (1 - scaledRatio);
			if (translateX > 0) {
				translateX = 0;
			} else if (width - translateX > scaledWidth) {
				translateX = width - scaledWidth;
			}
		}
		if (currentBitmapHeight < height) {
			// translateY = (height - scaledHeight) / 2f;
			translateY = 0;
		} else {
			translateY = totalTranslateY * scaledRatio + centerPointY * (1 - scaledRatio);
			if (translateY > 0) {
				translateY = 0;
			} else if (height - translateY > scaledHeight) {
				translateY = height - scaledHeight;
			}
		}
		matrix.postTranslate(translateX, translateY);
		totalTranslateX = translateX;
		totalTranslateY = translateY;
		currentBitmapWidth = scaledWidth;
		currentBitmapHeight = scaledHeight;
		canvas.drawBitmap(sourceBitmap, matrix, null);
	}

	private void move(Canvas canvas) {
		matrix.reset();
		float translateX = totalTranslateX + movedDistanceX;
		float translateY = totalTranslateY + movedDistanceY;
		matrix.postScale(totalRatio, totalRatio);
		matrix.postTranslate(translateX, translateY);
		totalTranslateX = translateX;
		totalTranslateY = translateY;
		canvas.drawBitmap(sourceBitmap, matrix, null);
	}

	// private void initBitmap(Canvas canvas) {
	// if (sourceBitmap != null) {
	// matrix.reset();
	// int bitmapWidth = sourceBitmap.getWidth();
	// int bitmapHeight = sourceBitmap.getHeight();
	// if (bitmapWidth > width || bitmapHeight > height) {
	// if (bitmapWidth - width > bitmapHeight - height) {
	// float ratio = width / (bitmapWidth * 1.0f);
	// matrix.postScale(ratio, ratio);
	// // float translateY = (height - (bitmapHeight * ratio)) /
	// // 2f;
	// // matrix.postTranslate(0, translateY);
	// // totalTranslateY = translateY;
	// totalRatio = initRatio = ratio;
	// } else {
	// float ratio = height / (bitmapHeight * 1.0f);
	// matrix.postScale(ratio, ratio);
	// // float translateX = (width - (bitmapWidth * ratio)) / 2f;
	// // matrix.postTranslate(translateX, 0);
	// // totalTranslateX = translateX;
	// totalRatio = initRatio = ratio;
	// }
	// Log.i("sean", "initRatio:" + initRatio);
	// currentBitmapWidth = bitmapWidth * initRatio;
	// currentBitmapHeight = bitmapHeight * initRatio;
	// } else {
	// // else {
	// //
	// // float translateX = (width - sourceBitmap.getWidth()) / 2f;
	// // float translateY = (height - sourceBitmap.getHeight()) / 2f;
	// // matrix.postTranslate(translateX, translateY);
	// // totalTranslateX = translateX;
	// // totalTranslateY = translateY;
	//
	// totalTranslateX = 0;
	// totalTranslateY = 0;
	// totalRatio = initRatio = 1f;
	// currentBitmapWidth = bitmapWidth;
	// currentBitmapHeight = bitmapHeight;
	// }
	// canvas.drawBitmap(sourceBitmap, matrix, null);
	// }
	// }

	private void initBitmap(Canvas canvas) {
		if (sourceBitmap != null) {
			matrix.reset();
			int bitmapWidth = sourceBitmap.getWidth();
			int bitmapHeight = sourceBitmap.getHeight();
			// if (bitmapWidth > width || bitmapHeight > height)
			// {
			sourceBitmap = Bitmap.createScaledBitmap(sourceBitmap, width, height, false);
			// if (bitmapWidth - width > bitmapHeight - height) {
			// float ratio = width / (bitmapWidth * 1.0f);
			// //matrix.postScale(ratio, ratio);
			// // float translateY = (height - (bitmapHeight * ratio)) /
			// // 2f;
			// // matrix.postTranslate(0, translateY);
			// // totalTranslateY = translateY;
			// totalTranslateY = 0;
			// totalRatio = initRatio = ratio;
			// } else {
			// float ratio = height / (bitmapHeight * 1.0f);
			// //matrix.postScale(ratio, ratio);
			// // float translateX = (width - (bitmapWidth * ratio)) / 2f;
			// // matrix.postTranslate(translateX, 0);
			// // totalTranslateX = translateX;
			// totalTranslateX = 0;
			// totalRatio = initRatio = ratio;
			// }
			// currentBitmapWidth = bitmapWidth * initRatio;
			// currentBitmapHeight = bitmapHeight * initRatio;
			// }

			// float translateX = (width - sourceBitmap.getWidth()) / 2f;
			// float translateY = (height - sourceBitmap.getHeight()) / 2f;
			// matrix.postTranslate(translateX, translateY);
			// totalTranslateX = translateX;
			// totalTranslateY = translateY;
			totalTranslateX = 0;
			totalTranslateY = 0;
			totalRatio = initRatio = 1f;
			currentBitmapWidth = bitmapWidth;
			currentBitmapHeight = bitmapHeight;
			// }
			canvas.drawBitmap(sourceBitmap, matrix, null);
		}
	}

	private double distanceBetweenFingers(MotionEvent event) {
		float disX = Math.abs(event.getX(0) - event.getX(1));
		float disY = Math.abs(event.getY(0) - event.getY(1));
		return Math.sqrt(disX * disX + disY * disY);
	}

	private void centerPointBetweenFingers(MotionEvent event) {
		float xPoint0 = event.getX(0);
		float yPoint0 = event.getY(0);
		float xPoint1 = event.getX(1);
		float yPoint1 = event.getY(1);
		centerPointX = (xPoint0 + xPoint1) / 2;
		centerPointY = (yPoint0 + yPoint1) / 2;
	}

	public void setColor(String newColor) {
		// set color
		invalidate();
		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
	}

	public void setBrushSize(float newSize) {
		// update size
		float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
		brushSize = pixelAmount;
		drawPaint.setStrokeWidth(brushSize);
	}

	public void setLastBrushSize(float lastSize) {
		lastBrushSize = lastSize;
	}

	public float getLastBrushSize() {
		return lastBrushSize;
	}

	public void setErase(boolean isErase) {
		// set erase true or false
		erase = isErase;
		if (erase)
			drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		else
			drawPaint.setXfermode(null);
	}

	public Bitmap getCanvasBitmap() {
		return canvasBitmap;
	}

	public void resetScale() {
		matrix = new Matrix();
		invalidate();
	}

}
