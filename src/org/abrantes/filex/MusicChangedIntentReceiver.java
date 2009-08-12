package org.abrantes.filex;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.drawable.TransitionDrawable;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;


public class MusicChangedIntentReceiver	extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("MUSICINTENT", "musicChanged");
		Filex filex =((Filex) context);
		
		
		try {
			/*
			 * Destroy song Progress Dialog if on screen
			 */
			try{
				if(filex.songProgressAlertDialog != null){
					/*
					 * dismiss (And remove) the song progress dialog
					 */
					filex.dismissDialog(R.layout.song_progress_dialog);
					filex.removeDialog(R.layout.song_progress_dialog);
					filex.songProgressAlertDialog = null;
					filex.songProgressView = null;
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
			/* 
			 * move (album)cursor to the new album 
			 */
			filex.albumCursor.moveToPosition(
					filex.playerServiceIface.getAlbumCursorPosition());
			
			/*
			 * move (song)cursor to the new song
			 */
			filex.songCursor = filex.initializeSongCursor(filex.albumCursor.getString(
												filex.albumCursor.getColumnIndexOrThrow(
														MediaStore.Audio.Albums.ALBUM)));
			filex.songCursor.moveToPosition(
					filex.playerServiceIface.getSongCursorPosition());

			/*
			 * Update status variables 
			 */
			filex.albumCursorPositionPlaying = filex.playerServiceIface.getAlbumCursorPosition();
			filex.currentAlbumPosition = filex.playerServiceIface.getAlbumCursorPosition();
			filex.currentSongPosition = filex.playerServiceIface.getSongCursorPosition();
			
			/*
			 * Update the song progress bar
			 */
			filex.songProgressBar.setProgress(0);
			filex.songProgressBar.setMax((int) filex.songCursor.getDouble(
												filex.songCursor.getColumnIndexOrThrow(
														MediaStore.Audio.Media.DURATION)));
			/*
			 * Update the song name (and artist too)
			 */
			filex.updateSongTextUI();
			/*
			 * Trigger song progress
			 */
			filex.triggerSongProgress();
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (CursorIndexOutOfBoundsException e){
			e.printStackTrace();
			
			// TODO: Maybe resync service and frontend cursors....
			/*
			 * Probably the service and front end cursors are not synchronized
			 * 	- try to resynch
			 */
			try{
				filex.initializeAlbumCursor();
				filex.albumCursor.moveToNext();
				filex.initializeSongCursor(
						filex.albumCursor.getString(
								filex.albumCursor.getColumnIndexOrThrow(
										MediaStore.Audio.Albums.ALBUM)));
			}catch(Exception ee){
				ee.printStackTrace();
			}
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	
}