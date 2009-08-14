package org.abrantes.filex;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class FancyBackground{
	int width;
	int height;
	
	Bitmap currentAlbumBitmap;
	
	Paint paintAlbumReflection;
	Paint paintBg;
	Paint paintGlossOverlay;
	Paint paintDarkDivider;
	Paint paintLightDivider;
	
	int[] 	gradientBg = {Color.argb(175, 25, 25, 25), Color.argb(210, 25, 25, 25), Color.argb(255, 25, 25, 25)};
	float[]	gradientBgPositions = {0.f, 0.25f, 1.0f};
	
	int[] 	gradientGlossOverlay = {Color.argb(75, 200, 200, 200), Color.argb(35, 200, 200, 200), Color.argb(0, 200, 200, 200)};
	float[]	gradientGlossOverLayPositions = {0.f, 0.5f, 1.0f};
	
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
			currentAlbumBitmap = Bitmap.createBitmap(
					currentAlbumDrawable.getBitmap(),
					0, currentAlbumDrawable.getBitmap().getHeight() - height,
					width, height,
					flipMatrix,
					false);
		}
		else
			currentAlbumBitmap = null;
		
		/* Bg dimension */
		this.width = width;
		this.height = height;
		
		/* Album Reflection Paint */
		if(currentAlbumBitmap != null){
			paintAlbumReflection = new Paint();
			paintAlbumReflection.setShader(
					new BitmapShader(
							currentAlbumBitmap,
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
		paintGlossOverlay = new Paint();
		//paintGlossOverlay.setColor(Color.argb(100, 200, 200, 200));
		paintGlossOverlay.setShader(
				new LinearGradient(
						0,0,0,height/2,
						gradientGlossOverlay,
						gradientGlossOverLayPositions,
						TileMode.CLAMP));
		
		/* Dark Divider */
		paintDarkDivider = new Paint();
		paintDarkDivider.setColor(Color.argb(255, 70, 70, 70));
		
		/* Light Divider */
		paintLightDivider = new Paint();
		paintLightDivider.setColor(Color.argb(255, 135, 135, 135));
		
	}
	
	public Drawable getBackgroundDrawableBottom(){
		Bitmap			bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		BitmapDrawable 	bDrawable;
		
		/* Get ready to draw */
		Canvas canvas = new Canvas(bitmap);
		
		/* draw album reflection */
		if(currentAlbumBitmap != null){
			canvas.rotate(180, width/2, height/2);
			canvas.drawRect(new Rect(0,0,width,height),	paintAlbumReflection);
			canvas.rotate(180, width/2, height/2);
		}
			
		
		/* draw Bg */
		canvas.drawRect(new Rect(0,0,width,height), paintBg);
		
		/* draw gloss overlay */
		canvas.drawRect(new Rect(0,0,width,height/2), paintGlossOverlay);
		
		/* draw first border on top */
		canvas.drawRect(new Rect(0,0,width,1), paintDarkDivider);
		/* draw second border on top */
		canvas.drawRect(new Rect(0,1,width,2), paintLightDivider);
		
		bDrawable = new BitmapDrawable(bitmap);
		return bDrawable;
	}
}