package mcore.edu.petDr.samples.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import m.client.android.library.core.common.DataHandler;
import m.client.android.library.core.common.LibDefinitions;
import m.client.android.library.core.common.Parameters;
import m.client.android.library.core.model.NetReqOptions;
import m.client.android.library.core.utils.FileIoUtil;
import m.client.android.library.core.utils.IOUtils;
import m.client.android.library.core.utils.ImageLoader;
import m.client.android.library.core.utils.Logger;
import m.client.android.library.core.view.AbstractFragmentActivity;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import mcore.edu.petDr.R;

public class ImageList1Activity extends AbstractFragmentActivity implements OnItemClickListener {
	public boolean singleMode = true;
	public boolean imageMode = true;
	public boolean zoomMode = false;
	public boolean detailMode = false; 
	public int numColumn = 3;
	private int maxCount = 0;
	ArrayAdapter<Dir> mAdapter = null;
	boolean[] mChecked = null;
	ArrayList<Dir> mImageList = null;
	ImageLoader mImageLoader = null;
	private int LAYOUT_IMAGELIST = 0;
	private int ID_LIST = 0;
	private int ID_BACK = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initID(this);
		setContentView(LAYOUT_IMAGELIST);
		
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.getStringExtra("columns") != null) {
				numColumn = Integer.parseInt(intent.getStringExtra("columns"));
			}
			if (intent.getStringExtra("detailMode") != null) {
				detailMode = (intent.getStringExtra("detailMode").equals("Y"))? true : false;
			}
			if (intent.getStringExtra("zoomMode") != null) {
				zoomMode = (intent.getStringExtra("zoomMode").equals("Y"))? true : false;
			}
			
			if(intent.getStringExtra("maxCount") != null){
				String _maxcount  = intent.getStringExtra("maxCount");
				try {
					maxCount = Integer.parseInt(_maxcount);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			
			String mediaType = intent.getStringExtra("mediaType");
			if (mediaType != null) {
				if (mediaType.equals("single_image")) {
					singleMode = true;
					imageMode = true;
				}
				else if (mediaType.equals("multi_image")) {
					singleMode = false;
					imageMode = true;
				}
				else if (mediaType.equals("single_video")) {
					singleMode = true;
					imageMode = false;
				}
				else {
					singleMode = false;
					imageMode = false;
				}
			}
			
		}
		
		if (imageMode) {
			mImageList = getImage();
		}
		else {
			mImageList = getVideo();
		}
		
		findViewById(ID_BACK).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				JSONArray images = new JSONArray();
				Intent intent = new Intent();
				intent.putExtra("images", images.toString());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		System.out.println(mImageList.size());
		mImageLoader = new ImageLoader(this);

		GridView gridView = (GridView) findViewById(ID_LIST);
		//gridView.setNumColumns(3);
		gridView.setAdapter(new MyAdapter(this));
		gridView.setOnItemClickListener(this);
	}

	private class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mImageList.size();
		}

		@Override
		public Object getItem(int i) {
			return mImageList.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {
			View v = view;
			ViewHolder holder;

			if (v == null) {
				v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
				holder = new ViewHolder();
				holder.picture = (ImageView) v.findViewById(R.id.picture);
				holder.name = (TextView) v.findViewById(R.id.text);
			}
			else {
				holder = (ViewHolder) v.getTag();
			}
			
			Dir item = (Dir) getItem(position);
			holder.arrays = item.mArray;
			v.setTag(holder);

			if (imageMode) {
				new ImageDownloaderTask(holder.picture).execute(item.mArray.get(0)[Dir.PATH]);
			}
			else {
				Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(item.mArray.get(0)[Dir.PATH], Thumbnails.MINI_KIND);
				holder.picture.setImageBitmap(bitmap);
			}

			holder.name.setText(String.format("%s \n%d item(s)", item.mDirName, item.mArray.size()));

			return v;
		}
	}
	
	static class ViewHolder {
		ImageView picture;
		TextView name;
		ArrayList<String[]> arrays;
    }
	
	class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;

	    public ImageDownloaderTask(ImageView imageView) {
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    @Override
	    protected Bitmap doInBackground(String... params) {
	    	BitmapFactory.Options option = new BitmapFactory.Options();
	    	option.inJustDecodeBounds = true;
			//option.inSampleSize = 3;
	    	BitmapFactory.decodeFile(params[0], option);
	    	option.inSampleSize = ImageList2Activity.calculateInSampleSize(option, 200, 200);
	    	option.inJustDecodeBounds = false;
	    	Bitmap bitmap = mImageLoader.GetRotatedBitmap(BitmapFactory.decodeFile(params[0], option), mImageLoader.GetExifOrientation(params[0]));
			return bitmap;
	    }

	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (isCancelled()) {
	            bitmap = null;
	        }

	        if (imageViewReference != null) {
	            ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                if (bitmap != null) {
	                    imageView.setImageBitmap(bitmap);
	                }
	            }
	        }
	    }
	}

	@Override
	public void handlingError(String arg0, String arg1, String arg2,
			String arg3, NetReqOptions arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestData(String arg0, String arg1, DataHandler arg2,
			NetReqOptions arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void responseData(int arg0, String arg1, String arg2, String arg3,
			NetReqOptions arg4) {
		// TODO Auto-generated method stub

	}

	/**
	 * ID초기화 
	 * @param context
	 */
	private void initID(Context context) {
		Resources res = context.getResources();
		LAYOUT_IMAGELIST = res.getIdentifier("activity_imagelist", "layout", context.getPackageName());
		ID_LIST = res.getIdentifier("gridview", "id", context.getPackageName());
		ID_BACK = res.getIdentifier("cancelBtn", "id", context.getPackageName());
	}
	
	/**
	 * 모든 이미지를 검색 하여 이미지가 포함된 경로별로 이미지를 구분하여 리스트 형태로 만들어 준다.
	 * @return
	 */
	private ArrayList<Dir> getImage() {
		ArrayList<Dir> array = new ArrayList<Dir>();
		String[] proj = {
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.ORIENTATION,
		};
		
		Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				proj, null, null, null);
		
		if (imageCursor != null && imageCursor.moveToFirst()) {
			int id = 0;
			String path;
			String name;
			int orientation = 0;
			long duration = 0;
			
			do {
				id = imageCursor.getInt(0);
				path = imageCursor.getString(1);			// 파일의 경로가 나온다.
				name = imageCursor.getString(2);	// 파일 이름만 나온다.
				orientation = imageCursor.getInt(3);	// orientation

				try {
					File file = new File(path);
					if (name != null && file.exists()) {
						String dir = path.substring(0, path.lastIndexOf("/"));
						Dir folder = checkFolder(array, dir);
						folder.mArray.add(new String[]{path, name, orientation+"", id+"", duration+""});
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					Logger.i("이미지 url 호출 에러 ");
				}
			} while (imageCursor.moveToNext());
		}
		if (imageCursor != null) {
			imageCursor.close();
		}
		
		return array;
	}
	
	/**
	 * 모든 이미지를 검색 하여 이미지가 포함된 경로별로 이미지를 구분하여 리스트 형태로 만들어 준다.
	 * @return
	 */
	private ArrayList<Dir> getVideo() {
		ArrayList<Dir> array = new ArrayList<Dir>();
		String[] proj = {
			    MediaStore.Video.Media._ID,
			    MediaStore.Images.Media.DATA,
			    MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.DATE_MODIFIED,
				MediaStore.Video.Media.SIZE,
				MediaStore.Video.Media.DURATION };
		
		
		Cursor imageCursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				proj, null, null, null);
		
		if (imageCursor != null && imageCursor.moveToFirst()) {
			int id = 0;
			String path;
			String name;
			int orientation = 0;
			long duration = 0;
			
			do {
				id = imageCursor.getInt(0);
				path = imageCursor.getString(1);			// 파일의 경로가 나온다.
				name = imageCursor.getString(2);	// 파일 이름만 나온다.
				duration = imageCursor.getInt(5);	// orientation
				try {
					File file = new File(path);
					if (name != null && file.exists()) {
						String dir = path.substring(0, path.lastIndexOf("/"));
						Dir folder = checkFolder(array, dir);
						folder.mArray.add(new String[]{path, name, orientation+"", id+"", duration+""});
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					Logger.i("이미지 url 호출 에러 ");
				}
			} while (imageCursor.moveToNext());
		}
		if (imageCursor != null) {
			imageCursor.close();
		}
		
		return array;
	}
	
	/**
	 * 폴더별로 이미지를 리스트에 추가
	 * @param array
	 * @param path
	 * @return
	 */
	private Dir checkFolder(ArrayList<Dir> array, String path) {
		Dir dir = null;
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).mDirPath.contains(path)) {
				return dir = array.get(i);
			}
		}
		dir = new Dir();
		dir.mDirPath = path;
		dir.mDirName = path.substring(path.lastIndexOf("/")+1);
		array.add(dir);
		
		return dir;
	}
	
	/**
	 * Dir Class
	 * 
	 * @author 성시종(<a mailto="sijong@uracle.co.kr">sijong@uracle.co.kr</a>)
	 * @version v 1.0.0
	 * @since Android 2.1 <br>
	 *        <DT><B>Date: </B>
	 *        <DD>2011.07</DD>
	 *        <DT><B>Company: </B>
	 *        <DD>Uracle Co., Ltd.</DD>
	 *        
	 * 폴더별 이미지 리스트를 만든다.
	 * 
	 * Copyright (c) 2001-2011 Uracle Co., Ltd. 
	 * 166 Samseong-dong, Gangnam-gu, Seoul, 135-090, Korea All Rights Reserved.
	 */
	public class Dir {
		/** 파일의 PATH */
		static final int PATH = 0;
		/** 파일의 이름 */
		static final int NAME = 1;
		/** Orientation */
		static final int ORIENTATION = 2;
		static final int ID = 3;
		static final int DURATION = 4;
		String mDirName = new String();
		String mDirPath = new String();
		ArrayList<String[]> mArray = new ArrayList<String[]>();
	}

	@Override
	public void addClassId() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getClassId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNextClassId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Parameters getParameters() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	/*Parameters param = new Parameters();
    	param.putParam("dir", ((ViewHolder) view.getTag()).arrays);
    	param.putParam("detailMode", (detailMode)? "Y" : "N");
    	param.putParam("zoomMode", (zoomMode)? "Y" : "N");
    	System.out.println("onItemClick:: " + numColumn);
    	param.putParam("columns", ""+numColumn);
    	param.putParam("singleMode", (singleMode)? "Y" : "N");
    	param.putParam("imageMode", (imageMode)? "Y" : "N");*/
    	//param.putParam("PARAMETERS", param.toString());
    	/*CommonLibHandler.getInstance().getController().actionMoveActivity(LibDefinitions.libactivities.ACTY_LISTIMAGE2, 
							CommonLibUtil.getActionType(null), 
		    				ImageList1Activity.this, 
		    				null, 
		    				param);*/
    	
    	Intent intent = new Intent(ImageList1Activity.this, ImageList2Activity.class);
    	System.out.println("((ViewHolder) view.getTag()).arrays.size():: " + ((ViewHolder) view.getTag()).arrays.size());
    	intent.putExtra("dir", ((ViewHolder) view.getTag()).arrays);
    	intent.putExtra("detailMode", (detailMode)? "Y" : "N");
    	intent.putExtra("zoomMode", (zoomMode)? "Y" : "N");
    	intent.putExtra("columns", ""+numColumn);
    	intent.putExtra("singleMode", (singleMode)? "Y" : "N");
    	intent.putExtra("imageMode", (imageMode)? "Y" : "N");
    	intent.putExtra("maxCount", String.valueOf(maxCount));
    	
    	startActivityForResult(intent, LibDefinitions.libactivities.ACTY_LISTIMAGE2);
		
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// 이미지를 선택 하고 넘어 왓을때
		if (resultCode == RESULT_OK && 
				requestCode == LibDefinitions.libactivities.ACTY_LISTIMAGE2) {
			setResult(RESULT_OK, data);
			finish();
		}
		else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
