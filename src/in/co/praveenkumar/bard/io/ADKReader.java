package in.co.praveenkumar.bard.io;

import in.co.praveenkumar.bard.activities.MainActivity.UIUpdater;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.future.usb.UsbAccessory;

public class ADKReader {
	final String DEBUG_TAG = "BARD.IO.ADKReader";
	FileInputStream mFin = null;
	UIUpdater uu = null;
	Context context = null;
	UsbAccessory mAccessory = null;
	FileDescriptor fd = null;

	public ADKReader(FileInputStream mFin, UIUpdater uu, Context context,
			UsbAccessory mAccessory) {
		this.mFin = mFin;
		this.uu = uu;
		this.context = context;
		this.mAccessory = mAccessory;
	}

	public void start() {
		new dataListener().execute(0);
	}

	public void setFinputstream(FileInputStream mFin) {
		this.mFin = mFin;
	}

	private class dataListener extends AsyncTask<Integer, Integer, Long> {
		String read = "";

		@Override
		protected void onProgressUpdate(Integer... progress) {
			uu.setRead(read);
		}

		@Override
		protected Long doInBackground(Integer... params) {
			int ret = 0;
			int i;
			File file = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"bard.txt");
			FileOutputStream f = null;

			Log.d(DEBUG_TAG, "ADKReader doInbackground called");

			while (true) { // read data
				byte[] buffer = new byte[16384];
				try {
					try {
						f = new FileOutputStream(file, true);
						
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					Log.d(DEBUG_TAG, "Trying to buffer read");
					ret = mFin.read(buffer);
					Log.d(DEBUG_TAG, "Buffer read");

					if (f != null) {
						f.write(buffer);
						f.flush();
						f.close();
					}
				} catch (IOException e) {
					Log.d(DEBUG_TAG, "Caught a Reader exception");
					e.printStackTrace();
					break;
				} catch (Exception e) {
					Log.d(DEBUG_TAG,
							"Unknow exception while getting inputstream");
					e.printStackTrace();
				}
				read = Arrays.toString(buffer);

				publishProgress(0);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			// We reached here means our read loop exited.
			// Most common reason is BAD File Descriptor.
			// So, open accessory again with updated FD.
			Log.d(DEBUG_TAG, "ADKReader Post execute called");
			uu.reInitAccessory();
		}

	}

}
