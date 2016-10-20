package com.owen.photopicker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.owen.photopicker.R;
import com.owen.photopicker.adapter.FolderAdapter;
import com.owen.photopicker.adapter.ImageAdapter;
import com.owen.photopicker.bean.BeanFolder;
import com.owen.photopicker.bean.BeanImage;
import com.owen.photopicker.config.ImageSelectorConfig;
import com.owen.photopicker.view.FolderWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageSelectorActivity extends FragmentActivity {

    public static final String EXTRA_RESULT = "com.owen.imageselector.imgs_selected_result";
    private static final String EXTRA_IMG_SELECTOR_CONFIG = "com.owen.imageselector.config";

    private static final int LOADER_ID_IMAGE = 1;
    private static final int REQUEST_PREVIEW_IMAGE = 1;

    private GridView mImageListView;
    private ImageAdapter mImageAdapter;

    private Button mBtnOk;
    private TextView mTvFolderName;
    private TextView mTvPreview;
    private FolderWindow mFolderWindow;

    private ImageSelectorConfig mConfig;
    private List<BeanImage> mBeanImageSelectedList = new ArrayList<>();

    private void addBeanImageSelected(BeanImage beanImage) {
    	for (BeanImage beanImageHasSelected : mBeanImageSelectedList) {
    		if (beanImageHasSelected.getFilePath().equals(beanImage.getFilePath())) {
    			return;
    		}
    	}
    	mBeanImageSelectedList.add(beanImage);
    }
    
    private void removeBeanImageSelected(BeanImage beanImage) {
    	for (BeanImage beanImageHasSelected : mBeanImageSelectedList) {
    		if (beanImageHasSelected.getFilePath().equals(beanImage.getFilePath())) {
    			mBeanImageSelectedList.remove(beanImageHasSelected);
    			return;
    		}
    	}
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_seletor);

        findViewById(R.id.ivBtnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        mConfig = intent.getParcelableExtra(EXTRA_IMG_SELECTOR_CONFIG);
        mImageAdapter = new ImageAdapter(this, mConfig);
        mImageAdapter.setOnImageSelectedListener(new ImageAdapter.OnImageSelectedListener() {

            @Override
            public void onImageSelected(BeanImage beanImage) {
            	addBeanImageSelected(beanImage);
                setTvPreviewText();
                setButtonOkText();
            }

            @Override
            public void onImageUnSelected(BeanImage beanImage) {
            	removeBeanImageSelected(beanImage);
                setTvPreviewText();
                setButtonOkText();
            }

            @Override
            public void onPreviewImage(BeanImage beanImage, int position) {
            	Intent intentPreviewImg = ImagePreviewActivity.newIntent(
              		  ImageSelectorActivity.this,
              		  mBeanImageSelectedList,
              		  mImageAdapter.getDataSource(),
              		  position,
              		  mConfig.maxImageCount);
    			startActivityForResult(intentPreviewImg, REQUEST_PREVIEW_IMAGE);
            }
        });

        mImageListView = (GridView) findViewById(R.id.recyclerView);
        mImageListView.setAdapter(mImageAdapter);

        mBtnOk = (Button) findViewById(R.id.btnOk);
        mBtnOk.setEnabled(false);
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnData();
            }
        });

        mFolderWindow = new FolderWindow(this);
        mTvFolderName = (TextView) findViewById(R.id.tv_folder_name);
        mFolderWindow.setOnItemClickListener(new FolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BeanFolder beanFolder) {
                mFolderWindow.dismiss();
                mImageAdapter.setDataSource(beanFolder.getImages());
                mImageAdapter.updateBeanImageHasSelected(mBeanImageSelectedList);
                mTvFolderName.setText(beanFolder.getName());
            }
        });
        findViewById(R.id.folder_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFolderWindow.isShowing()) {
                    mFolderWindow.dismiss();
                } else {
                    mFolderWindow.showAsDropDown(findViewById(R.id.bottom_layout));
                }
            }
        });

        mTvPreview = (TextView) findViewById(R.id.tv_preview);
        setTvPreviewText();
        mTvPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	Intent intentPreviewImg = ImagePreviewActivity.newIntent(
            		  ImageSelectorActivity.this,
            		  mBeanImageSelectedList,
            		  mBeanImageSelectedList,
            		  0,
            		  mConfig.maxImageCount);
      			startActivityForResult(intentPreviewImg, REQUEST_PREVIEW_IMAGE);
            }
        });

        loadData();
    }

    private void returnData() {
        Intent data = new Intent();

        ArrayList<String> resultList = new ArrayList<>(mBeanImageSelectedList.size());
        for (BeanImage beanImageSelected : mBeanImageSelectedList) {
            resultList.add(beanImageSelected.getFilePath());
        }
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);

        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void setTvPreviewText() {
        if (mBeanImageSelectedList.isEmpty()) {
        	mTvPreview.setEnabled(false);
            mTvPreview.setText(getString(R.string.preview));
        } else {
        	mTvPreview.setEnabled(true);
            mTvPreview.setText(getString(R.string.preview_with_num, mBeanImageSelectedList.size()));
        }
    }

    private void setButtonOkText() {
        if (mBeanImageSelectedList.isEmpty()) {
        	mBtnOk.setEnabled(false);
            mBtnOk.setText(getString(R.string.btn_ok));
        } else {
        	mBtnOk.setEnabled(true);
            mBtnOk.setText(getString(R.string.btn_ok_with_num, mBeanImageSelectedList.size(),
                    mConfig.maxImageCount));
        }
    }

    private void loadData() {
        getSupportLoaderManager().initLoader(LOADER_ID_IMAGE, null, new LoaderManager.LoaderCallbacks<Cursor>() {

            private final String[] IMAGE_PROJECTION = {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED
            };

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(
                        ImageSelectorActivity.this,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" },
                        MediaStore.Images.Media.DATE_ADDED + " DESC"
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data == null) {
                    return;
                }

                Map<String, List<BeanImage>> folderImgMap = new HashMap<>();
                List<BeanImage> filePathListOfAllImg = new ArrayList<>();

                int columnIndexOfDATA = data.getColumnIndex(MediaStore.Images.Media.DATA);
                while (data.moveToNext()) {
                    String imgFilePath = data.getString(columnIndexOfDATA);
                    BeanImage beanImage = new BeanImage(imgFilePath);
                   
                    // Image
                    filePathListOfAllImg.add(beanImage);

                    // Folder
                    File parentFile = new File(imgFilePath).getParentFile();
                    if (parentFile != null && parentFile.exists()) {
                        String parentFilePath = parentFile.getAbsolutePath();
                        if (folderImgMap.containsKey(parentFilePath)) {
                            folderImgMap.get(parentFilePath).add(beanImage);
                        } else {
                            List<BeanImage> filePathList = new ArrayList<>();
                            filePathList.add(beanImage);
                            folderImgMap.put(parentFilePath, filePathList);
                        }
                    }
                }

                bindImageFileListData(filePathListOfAllImg);
                bindFolderListData(folderImgMap, filePathListOfAllImg);

                data.close();
            }


            private void bindImageFileListData(List<BeanImage> imgFilePathList) {
                mImageAdapter.setDataSource(imgFilePathList);
            }

            private void bindFolderListData(Map<String, List<BeanImage>> folderFilesMap, List<BeanImage> filePathListOfAllImg) {
                // a folder has all Image
                BeanFolder folderOfAllImage = new BeanFolder();
                folderOfAllImage.setImageNum(filePathListOfAllImg.size());
                folderOfAllImage.setFirstImagePath(filePathListOfAllImg.get(0).getFilePath());
                folderOfAllImage.setImages(filePathListOfAllImg);
                folderOfAllImage.setName(getString(R.string.all_image));

                List<BeanFolder> beanFolderList = new ArrayList<>();

                beanFolderList.add(folderOfAllImage);
                for (String folderPath : folderFilesMap.keySet()) {
                    // each folder
                    List<BeanImage> filePathList = folderFilesMap.get(folderPath);

                    BeanFolder beanFolder = new BeanFolder();
                    beanFolder.setImageNum(filePathList.size());
                    beanFolder.setFirstImagePath(filePathList.get(0).getFilePath());
                    beanFolder.setImages(filePathList);
                    beanFolder.setName(new File(folderPath).getName());

                    beanFolderList.add(beanFolder);
                }

                mFolderWindow.bindFolder(beanFolderList);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }
        });
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PREVIEW_IMAGE) {
            List<BeanImage> beanImageSelected = (List<BeanImage>) data.getSerializableExtra(ImagePreviewActivity.EXTRA_IMAGE_HAS_SELECTED);
            boolean exitImgSelector = data.getBooleanExtra(ImagePreviewActivity.EXTRA_EXIT_IMAGE_SELECTOR, false);
            
            if (exitImgSelector) {
                mBeanImageSelectedList = beanImageSelected;
                returnData();
            } else {
            	mBeanImageSelectedList = beanImageSelected;
                mImageAdapter.updateBeanImageHasSelected(mBeanImageSelectedList);
            }
        }
    }

    public static Intent newIntent(Context context, ImageSelectorConfig config) {
        Intent intent = new Intent(context, ImageSelectorActivity.class);
        intent.putExtra(EXTRA_IMG_SELECTOR_CONFIG, config);
        return intent;
    }
}
