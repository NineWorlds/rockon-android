package org.abrantes.filex;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class FancyBackground{
	int width;
	int height;
	
	Bitmap currentAlbumBitmapTop;
	Bitmap currentAlbumBitmapBottom;
	
	Paint paintAlbumReflectionTop;
	Paint paintAlbumReflectionBottom;
	Paint paintBg;
	Paint paintGlossOverlay;
	Paint paintDarkDivider;
	Paint paintLightDivider;
	
	int[] 	gradientBg = {Color.argb(150, 50,50,50), Color.argb(175, 50,50,50), Color.argb(200, 50,50,50)};
	float[]	gradientBgPositions = {0.f, 0.5f, 1.0f};
	
//	int[] 	gradientGlossOverlay = {Color.argb(75, 200,200,200), Color.argb(50, 200,200,200), Color.argb(25, 200,200,200)};
//	float[]	gradientGlossOverLayPositions = {0.f, 0.5f, 1.f};
	
	LinearGradient lG;
	
	static final String TAG = "FANCYBG";
	
	/*
	 * Constructor
	 */
	FancyBackground(int width, int height, BitmapDrawable currentAlbumDrawable){
		Log.i(TAG, "width: "+width+" height: "+height);
		
		/* current album Drawable */
		if(currentAlbumDrawable != null){
			/* need to flip the bitmap to have the reflection effect */
			Matrix flipMatrix = new Matrix();
			flipMatrix.preScale(-1.f, 1.f);
			currentAlbumBitmapBottom = Bitmap.createBitmap(
					currentAlbumDrawable.getBitmap(),
					0, currentAlbumDrawable.getBitmap().getHeight() - height,
					width, height,
					flipMatrix,
					false);
			currentAlbumBitmapTop = Bitmap.createBitmap(
					currentAlbumDrawable.getBitmap(),
					0, 0,
					width, height,
					flipMatrix,
					false);
		}
		else{
			currentAlbumBitmapTop = null;
			currentAlbumBitmapBottom = null;
		}
		
		/* Bg dimension */
		this.width = width;
		this.height = height;
		
		/* Album Reflection Paint */
		if(currentAlbumDrawable != null){
			paintAlbumReflectionTop = new Paint();
			paintAlbumReflectionTop.setShader(
					new BitmapShader(
							currentAlbumBitmapTop,
							TileMode.CLAMP,
							TileMode.CLAMP));
			paintAlbumReflectionBottom = new Paint();
			paintAlbumReflectionBottom.setShader(
					new BitmapShader(
							currentAlbumBitmapBottom,
							TileMode.CLAMP,
							TileMode.CLAMP));
		}
		
		/* Bg Paint */
		paintBg = new Paint();
		//paintBg.setColor(Color.argb(200, 25, 25, 25));
		paintBg.setShader(
				new LinearGradient(
						0,0,0,height,
						gradientBg,
						gradientBgPositions,
						TileMode.CLAMP));
		
		/* Gloss Overlay Paint */
//		paintGlossOverlay = new Paint();
//		paintGlossOverlay.setShader(
//				new LinearGradient(
//						0,0,0,height/2,
//						gradientGlossOverlay,
//						gradientGlossOverLayPositions,
//						TileMode.CLAMP));
////		paintGlossOverlay.setXfermode(new PorterDuffXfermode(Mode.LIGHTEN));
		
		/* Dark Divider */
		paintDarkDivider = new Paint();
		paintDarkDivider.setColor(Color.argb(175, 50, 50, 50));
		
		/* Light Divider */
		paintLightDivider = new Paint();
		paintLightDivider.setColor(Color.argb(175, 155, 155, 155));
		
	}
	
	public Drawable getBackgroundDrawableBottom(){
		Bitmap			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		BitmapDrawable 	bDrawable;
		
		/* Get ready to draw */
		Canvas canvas = new Canvas(bitmap);
		
		/* draw album reflection */
		if(currentAlbumBitmapBottom != null){
			canvas.rotate(180, width/2, height/2);
			canvas.drawRect(new Rect(0,0,width,height),	paintAlbumReflectionBottom);
			canvas.rotate(180, width/2, height/2);
		}
			
		
		/* draw Bg */
		canvas.drawRect(new Rect(0,0,width,height), paintBg);
		
		/* draw gloss overlay */
//		canvas.drawRect(new Rect(0,0,width,height/2), paintGlossOverlay);
		
		/* draw first border on top */
		canvas.drawRect(new Rect(0,0,width,1), paintDarkDivider);
		/* draw second border on top */
		canvas.drawRect(new Rect(0,1,width,2), paintLightDivider);
		
		bDrawable = new BitmapDrawable(bitmap);
		return bDrawable;
	}
	
	public Drawable getBackgroundDrawableTop(){
		Bitmap			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		BitmapDrawable 	bDrawable;
		
		/* Get ready to draw */
		Canvas canvas = new Canvas(bitmap);
		
		canvas.rotate(180, width/2, height/2);
		/* draw album reflection */
		if(currentAlbumBitmapTop != null){
			canvas.drawRect(new Rect(0,0,width,height),	paintAlbumReflectionTop);
		}
			
		
		/* draw Bg */
		canvas.drawRect(new Rect(0,0,width,height), paintBg);
		canvas.rotate(180, width/2, height/2);

		
		/* draw gloss overlay */
//		canvas.drawRect(new Rect(0,0,width,height/2), paintGlossOverlay);
		
		/* draw first border on top */
		canvas.drawRect(new Rect(0,height-2,width,height-1), paintLightDivider);
		/* draw second border on top */
		canvas.drawRect(new Rect(0,height-1,width,height), paintDarkDivider);
		
		bDrawable = new BitmapDrawable(bitmap);
		return bDrawable;
	}
}