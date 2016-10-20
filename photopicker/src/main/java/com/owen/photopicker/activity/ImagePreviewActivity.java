package com.owen.photopicker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.owen.photopicker.R;
import com.owen.photopicker.bean.BeanImage;
import com.owen.photopicker.fragment.ImagePreviewFragment;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends FragmentActivity {

    public static final String EXTRA_IMAGE_HAS_SELECTED = "com.owen.imageselector.img_has_selected";
    public static final String EXTRA_EXIT_IMAGE_SELECTOR = "com.owen.imageselector.exit_img_selector";
    private static final String EXTRA_IMAGE_LIST = "com.owen.imageselector.img_list";
    private static final String EXTRA_CURRENT_IMAGE_POSITION = "com.owen.imageselector.current_img_position";
    private static final String EXTRA_MAX_IMAGE_COUNT = "com.owen.imageselector.max_img_count";

    private List<BeanImage> mBeanImageSelectedList;
    private List<BeanImage> mBeanImageList;
    private int mCurrentImgPosition;
    private int mMaxImgCount;

    private LinearLayout mToolbarLayout;
    private TextView mTvTitleText;
    private Button mBtnOK;

    private ViewPager mViewPager;

    private RelativeLayout mSelectBarLayout;
    private CheckBox mChkBoxSelect;

    private boolean mIsShowBar = true;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_image_preview);

        Intent intent = getIntent();
        mBeanImageSelectedList = (List<BeanImage>) intent.getSerializableExtra(EXTRA_IMAGE_HAS_SELECTED);
        mBeanImageList = (List<BeanImage>) intent.getSerializableExtra(EXTRA_IMAGE_LIST);
        mCurrentImgPosition = intent.getIntExtra(EXTRA_CURRENT_IMAGE_POSITION, 0);
        mMaxImgCount = intent.getIntExtra(EXTRA_MAX_IMAGE_COUNT, 1);

        mToolbarLayout = (LinearLayout) findViewById(R.id.layout_toolbar);
        findViewById(R.id.ivBtnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnData(false);
            }
        });
        mTvTitleText = (TextView) findViewById(R.id.tv_title_text);
        setTitleText();
        mBtnOK = (Button) findViewById(R.id.btnOk);
        setButtonOkText();
        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnData(true);
            }
        });
        
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new PreviewFragmentAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(mCurrentImgPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
            	mCurrentImgPosition = position;
                mChkBoxSelect.setChecked(currentImageIsChecked());
                setTitleText();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        mSelectBarLayout = (RelativeLayout) findViewById(R.id.select_bar_layout);
        mChkBoxSelect = (CheckBox) findViewById(R.id.checkbox_select);
        mChkBoxSelect.setChecked(currentImageIsChecked());
        mChkBoxSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChkBoxSelect.isChecked()) {
                    if (mBeanImageSelectedList.size() < mMaxImgCount) {
                        // add new
                    	addBeanImageSelected(mBeanImageList.get(mCurrentImgPosition));
                        setButtonOkText();
                    }
                } else {
                    // remove old
                	removeBeanImageSelected(mBeanImageList.get(mCurrentImgPosition));
                    setButtonOkText();
                }
            }
        });
    }
    
    private void setTitleText() {
    	mTvTitleText.setText(getString(R.string.current_position, mCurrentImgPosition + 1, mBeanImageList.size()));
    }
    
    private void addBeanImageSelected(BeanImage beanImage) {
    	for (BeanImage beanImageSelected : mBeanImageSelectedList) {
    		if (beanImageSelected.getFilePath().equals(beanImage.getFilePath())) {
    			return;
    		}
    	}
    	mBeanImageSelectedList.add(beanImage);
    }
    
    private void removeBeanImageSelected(BeanImage beanImage) {
    	for (BeanImage beanImageSelected : mBeanImageSelectedList) {
    		if (beanImageSelected.getFilePath().equals(beanImage.getFilePath())) {
    			mBeanImageSelectedList.remove(beanImageSelected);
    			return;
    		}
    	}
    }

	private void returnData(boolean exitImgSelector) {
        Intent data = new Intent();
        data.putExtra(EXTRA_IMAGE_HAS_SELECTED, (ArrayList<BeanImage>) mBeanImageSelectedList);
        data.putExtra(EXTRA_EXIT_IMAGE_SELECTOR, exitImgSelector);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode ==  KeyEvent.KEYCODE_BACK) {
            returnData(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean currentImageIsChecked() {
    	if (mBeanImageSelectedList.isEmpty()) {
    		return false;
    	}
    	
    	String filePath = mBeanImageList.get(mCurrentImgPosition).getFilePath();
    	for (BeanImage beanImageSelected : mBeanImageSelectedList) {
    		if (beanImageSelected.getFilePath().equals(filePath)) {
    			return true;
    		}
    	}
    	return false;
    }

    private void setButtonOkText() {
        if (mBeanImageSelectedList.isEmpty()) {
        	mBtnOK.setEnabled(false);
            mBtnOK.setText(getString(R.string.btn_ok));
        } else {
        	mBtnOK.setEnabled(true);
            mBtnOK.setText(getString(R.string.btn_ok_with_num, mBeanImageSelectedList.size(), mMaxImgCount));
        }
    }

    public void switchBarVisibility() {
        mToolbarLayout.setVisibility(mIsShowBar ? View.GONE : View.VISIBLE);
        mSelectBarLayout.setVisibility(mIsShowBar ? View.GONE : View.VISIBLE);
        if (mIsShowBar) {
            hideStatusBar();
        } else {
            showStatusBar();
        }
        mIsShowBar = !mIsShowBar;
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private class PreviewFragmentAdapter extends FragmentStatePagerAdapter {

        public PreviewFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImagePreviewFragment.newInstance(mBeanImageList.get(position).getFilePath());
        }

        @Override
        public int getCount() {
            return mBeanImageList == null ? 0 : mBeanImageList.size();
        }
    }

    public static Intent newIntent(Context context,
                                   List<BeanImage> imgHasSelected,
                                   List<BeanImage> imgList,
                                   int currentPosition,
                                   int maxImgCount) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(EXTRA_IMAGE_HAS_SELECTED, (ArrayList<BeanImage>) imgHasSelected);
        intent.putExtra(EXTRA_IMAGE_LIST, (ArrayList<BeanImage>) imgList);
        intent.putExtra(EXTRA_CURRENT_IMAGE_POSITION, currentPosition);
        intent.putExtra(EXTRA_MAX_IMAGE_COUNT, maxImgCount);
       return intent;
    }
}
