package org.abrantes.filex;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.provider.MediaStore;
import android.view.Display;
import android.view.WindowManager;

public class AlbumArtUtils{
	
	/*
	 * Definitions (most should be defined in Constants class - to avoid redefinition in several classes)
	 */
    int		BITMAP_SIZE_SMALL = 0;
    int		BITMAP_SIZE_NORMAL = 1;
    int		BITMAP_SIZE_FULLSCREEN = 2;
    int		BITMAP_SIZE_XSMALL = 3;
    String	FILEX_FILENAME_EXTENSION = "";
    String	FILEX_ALBUM_ART_PATH = "/sdcard/albumthumbs/RockOn/";
    String	FILEX_SMALL_ALBUM_ART_PATH = "/sdcard/albumthumbs/RockOn/small/";
    double 	CURRENT_PLAY_SCREEN_FRACTION = 0.66;
    double 	CURRENT_PLAY_SCREEN_FRACTION_LANDSCAPE = 0.75;
    double 	NAVIGATOR_SCREEN_FRACTION = 1 - CURRENT_PLAY_SCREEN_FRACTION;
    double	NAVIGATOR_SCREEN_FRACTION_LANDSCAPE = 1 - CURRENT_PLAY_SCREEN_FRACTION_LANDSCAPE;

	
	/*
	 * Temp vars
	 */
	int 	dim = 1;
	String 	albumCoverPath;
	String 	path;
	File	albumCoverFilePath;
	Bitmap	albumCoverBitmap;
	
	/*
	 * Global Vars
	 */
	boolean showFrame;
    int		viewWidth;
    int		viewWidthNormal;
    int		viewWidthBig;
	int		viewHeightBig;
	
	/*********************************
	 * 
	 * Constructor
	 * @param showFrame
	 * 
	 *********************************/
    AlbumArtUtils(Display display, boolean showFrame){
    	this.showFrame = showFrame;
    	calcViewWidths(display);
    }
	
    /*********************************
     * 
     * Calculate Dimensions
     * 
     *********************************/
    private void calcViewWidths(Display display){
		
		if(display.getOrientation() == 0){
			viewWidth = (int) Math.floor(
				display.getWidth() * 
				NAVIGATOR_SCREEN_FRACTION
				);
		
			viewWidthNormal = (int) Math.floor(
				display.getWidth() * 
				(1-NAVIGATOR_SCREEN_FRACTION)
				);
			viewWidthBig = (int) Math.min(
				display.getWidth(),
				display.getHeight()
				);
		}else{    
			viewWidth = (int) Math.floor(
				display.getWidth() * 
				NAVIGATOR_SCREEN_FRACTION_LANDSCAPE
				);
			viewWidthNormal = (int) Math.floor(
				display.getWidth() * 
				(1-NAVIGATOR_SCREEN_FRACTION_LANDSCAPE) / 2
				);
			viewWidthBig = (int) Math.min(
				display.getWidth(),
				display.getHeight()
				);
		}
		viewWidthBig = display.getWidth() - 30;
		viewHeightBig = display.getHeight() - 60;
		
		viewWidthBig = 320;
		viewHeightBig = 320;

    }
	
	/*********************************
     * 
     * getAlbumBitmap
     *
     *********************************/
    public Bitmap getAlbumBitmap(String artistName, String albumName, String embeddedArtPath, int bitmapFuzzySize){
    	if(artistName == null && albumName == null){
//    		return albumUndefinedCoverBitmap;
    	}
    	
    	/*
    	 * Get the path to the album art
    	 */
    	albumCoverPath = null;
		if(bitmapFuzzySize == BITMAP_SIZE_SMALL){
			path = FILEX_SMALL_ALBUM_ART_PATH+
				validateFileName(artistName)+
				" - "+
				validateFileName(albumName)+
				FILEX_FILENAME_EXTENSION;
    	} else {
    		path = embeddedArtPath;
    		
        	/* check if the embedded mp3 is valid (or big enough)*/
    		if(path != null){
    			FileInputStream pathStream = null;
				try {
					pathStream = new FileInputStream(path);
					BitmapFactory.Options opts = new BitmapFactory.Options();
		    		opts.inJustDecodeBounds = true;
		    		Bitmap bmTmp = BitmapFactory.decodeStream(pathStream, null, opts);
		    		if(opts == null || opts.outHeight < 300 || opts.outWidth < 300)
		    			path = null;
		    		if(bmTmp != null)
		    			bmTmp.recycle();
	    			pathStream.close();
				} catch (Exception e) {
					e.printStackTrace();
					path = null;
				}
    		}
    		
    		if(path == null){
	    		path = FILEX_ALBUM_ART_PATH+
					validateFileName(artistName)+
					" - "+
					validateFileName(albumName)+
					FILEX_FILENAME_EXTENSION;
    		}
    	}
    
		albumCoverFilePath = new File(path);
		if(albumCoverFilePath.exists() && albumCoverFilePath.length() > 0){
			albumCoverPath = path;
		}
		
		try{
			/*
			 * If the album art exists put it in the preloaded array, otherwise
			 * just use the default image
			 */
			if(albumCoverPath != null){
				if(bitmapFuzzySize == BITMAP_SIZE_XSMALL){
					dim = viewWidth;
					albumCoverBitmap = createFancyAlbumCoverFromFilePath(albumCoverPath, dim, dim);
				}
				else if(bitmapFuzzySize == BITMAP_SIZE_SMALL)
					albumCoverBitmap = createFancyAlbumCoverFromFilePath(albumCoverPath, viewWidth, viewWidth);
				else if(bitmapFuzzySize == BITMAP_SIZE_NORMAL)
					albumCoverBitmap = createFancyAlbumCoverFromFilePath(albumCoverPath, viewWidthNormal, viewWidthNormal);
				else if(bitmapFuzzySize == BITMAP_SIZE_FULLSCREEN)
					albumCoverBitmap = createFancyAlbumCoverFromFilePath(albumCoverPath, viewWidthBig, viewHeightBig);
				return albumCoverBitmap;
			} else {
//				// TODO:
//				// adjust sample size dynamically
////				opts = new Options();
////				opts.inSampleSize = NO_COVER_SAMPLING_INTERVAL;
////				albumCoverBitmap = bitmapDecoder.decodeResource(this.context.getResources(),
////																R.drawable.albumart_mp_unknown, opts);
////				albumCoverBitmap = createFancyAlbumCoverFromResource(R.drawable.albumart_mp_unknown, viewWidth, viewWidth);
//				if(bitmapFuzzySize == BITMAP_SIZE_XSMALL)
//					albumCoverBitmap = createFancyAlbumCover(albumUndefinedCoverBitmap, viewWidth, viewWidth);
//				else if(bitmapFuzzySize == BITMAP_SIZE_SMALL)
//					albumCoverBitmap = createFancyAlbumCover(albumUndefinedCoverBitmap, viewWidth, viewWidth);
//				else if(bitmapFuzzySize == BITMAP_SIZE_NORMAL)
//					albumCoverBitmap = createFancyAlbumCover(albumUndefinedCoverBigBitmap, viewWidthNormal, viewWidthNormal);
//				else if(bitmapFuzzySize == BITMAP_SIZE_FULLSCREEN)
//					albumCoverBitmap = createFancyAlbumCover(albumUndefinedCoverBigBitmap, viewWidthBig, viewWidthBig);
//				return albumCoverBitmap;
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
//			if(bitmapFuzzySize == BITMAP_SIZE_XSMALL)
//				return albumUndefinedCoverBitmap;
//			else if(bitmapFuzzySize == BITMAP_SIZE_SMALL)
//				return albumUndefinedCoverBitmap;
//			else if(bitmapFuzzySize == BITMAP_SIZE_NORMAL)
//				return albumUndefinedCoverBigBitmap;
//			else if(bitmapFuzzySize == BITMAP_SIZE_FULLSCREEN)
//				return albumUndefinedCoverBigBitmap;
//			else
//				return albumUndefinedCoverBigBitmap;
			return null;
		}
    }
    
    
    
    
    
    
    Shader shader;
    RectF	rect = new RectF();
    Paint	paint= new Paint();

    
    /*********************************
     * 
     * createFancyAlbumCover
     * 
     *********************************/
//    Bitmap cBitmap = Bitmap.createBitmap(460, 460, Bitmap.Config.ARGB_8888);
    public Bitmap createFancyAlbumCover(Bitmap bitmap, int width, int height){

    	try{

    		/*
    		 * Adjust the aspect ratio of the incoming bitmap if needed
    		 */
    		float aspectRatio = (float)width/(float)height; 
    		if(Math.abs(aspectRatio - ((float)bitmap.getWidth()/(float)bitmap.getHeight())) > 0.1){
        		
    			if(aspectRatio > 1){ // width is larger
	    			bitmap = Bitmap.createBitmap(
		        				bitmap,
		        				0,
		        				(int)(bitmap.getHeight()-(bitmap.getHeight()/aspectRatio))/2,
		        				bitmap.getWidth(),
		        				(int)(bitmap.getHeight()/aspectRatio));
    			} else {
					bitmap = Bitmap.createBitmap(
	        				bitmap,
	        				(int)(bitmap.getWidth()-(bitmap.getWidth()*aspectRatio))/2,
	        				0,
	        				(int)(bitmap.getWidth()*aspectRatio),
	        				bitmap.getHeight());
    			}
    		}
    		
	//    	Bitmap tBitmap;
	    	paint.setAntiAlias(true);
	    	paint.setDither(true);
	    	//BlurMaskFilter blurFilter = new BlurMaskFilter(viewWidth/20.0f, BlurMaskFilter.Blur.INNER);
	    	
	        Bitmap cBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas();
	        canvas.setBitmap(cBitmap); 
	        
	        paint.setXfermode(null);
	        if(bitmap != null){
		        Shader bmpShader = new BitmapShader(
		        		Bitmap.createScaledBitmap(
		        				bitmap,
		        				width, 
		        				height, 
		        				false),
		        		TileMode.CLAMP,
		        		TileMode.CLAMP);
		        paint.setShader(bmpShader);
	        }
	        
	        rect.left = 1.0f;
	        rect.top = 1.0f;
	        rect.right = width - 1.0f;
	        rect.bottom = height - 1.0f; 
	        if(showFrame){
	        	canvas.drawRoundRect(rect, width/40.0f, height/40.0f, paint);
	        } else {
	        	canvas.drawRoundRect(rect, width/30.0f, height/30.0f, paint);
	        }	
	        return cBitmap;
    	} catch (Exception e) {
    		e.printStackTrace();
//    		return albumUndefinedCoverBitmap;
    		return null;
    	} catch (Error e) {
    		e.printStackTrace();
//    		return albumUndefinedCoverBigBitmap;
    		return null;
    	}
    	
    }
    
    BitmapFactory	bitmapDecoder = new BitmapFactory();
    BitmapFactory.Options bitOpts = new BitmapFactory.Options();
    byte[] tmpBitmapAlloc = new byte[1024*8];
    /*********************************
     * 
     * createFancyAlbumCoverFromFilePath
     * 
     *********************************/
    public Bitmap createFancyAlbumCoverFromFilePath(String path, int width, int height){
    	bitOpts.inTempStorage = tmpBitmapAlloc; 
    	
    	try{
    		FileInputStream pathStream = new FileInputStream(path);
    		Bitmap tmpBm = bitmapDecoder.decodeStream(pathStream, null, bitOpts);
    		Bitmap bm = createFancyAlbumCover(tmpBm, width,height);
    		tmpBm.recycle();
    		pathStream.close();
    		return bm;
    	} catch(Exception e) {
    		e.printStackTrace();
//    		return createFancyAlbumCover(albumUndefinedCoverBitmap, width,height);
    		return null;
    	}catch (Error err){
    		err.printStackTrace();
//    		return albumUndefinedCoverBitmap;
    		return null;
    	}
    	
    }
    
//    /*********************************
//     * 
//     * createFancyAlbumCoverFromResource
//     * 
//     *********************************/
//    public Bitmap createFancyAlbumCoverFromResource(int res, int width, int height){
//    	bitOpts.inTempStorage = tmpBitmapAlloc; 
//
//    	if (false)
//    		return bitmapDecoder.decodeResource(context.getResources(), res, bitOpts);
//    	try{
//	    	Bitmap bmpTmp = bitmapDecoder.decodeResource(context.getResources(), res, bitOpts);
//	    	Bitmap bm = createFancyAlbumCover(bmpTmp, width,height);
//	    	bmpTmp.recycle();
//	    	return bm;
//    	}catch(Error err){
//    		err.printStackTrace();
//    		return albumUndefinedCoverBitmap;
//    	}
//    }

    
    
    
    
	/*********************************
	 * 
	 * ValidateFilename
	 * 
	 *********************************/
	private String validateFileName(String fileName) {
		if(fileName == null)
			return "";
		fileName = fileName.replace('/', '_');
		fileName = fileName.replace('<', '_');
		fileName = fileName.replace('>', '_');
		fileName = fileName.replace(':', '_');
		fileName = fileName.replace('\'', '_');
		fileName = fileName.replace('?', '_');
		fileName = fileName.replace('"', '_');
		fileName = fileName.replace('|', '_');
		return fileName;
	}
	
}