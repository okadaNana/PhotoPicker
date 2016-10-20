package com.owen.photopicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.owen.photopicker.R;
import com.owen.photopicker.bean.BeanImage;
import com.owen.photopicker.config.ImageSelectorConfig;
import com.owen.photopicker.util.DensityUtil;
import com.owen.photopicker.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;


public class ImageAdapter extends BaseAdapter {

    private static final int ITEM_TYPE_CAMERA = 1;
    private static final int ITEM_TYPE_IMAGE = 2;

    private final Context mContext;
    private final LayoutInflater mInflater;

    private ImageSelectorConfig mConfig;
    private List<BeanImage> mBeanImageList;
    private List<BeanImage> mBeanImageSelectedList;
    private OnImageSelectedListener mOnImageSelectedListener;

    private final int mScreenWidth;
    private final int mItemSpan;

    public ImageAdapter(Context context, ImageSelectorConfig imageSelectorConfig) {
        mContext = context;
        mConfig = imageSelectorConfig;
        mInflater = LayoutInflater.from(mContext);

        mBeanImageSelectedList = new ArrayList<>(mConfig.maxImageCount);

        mScreenWidth = ScreenUtil.getScreenWidth(mContext);
        mItemSpan = DensityUtil.dp2px(mContext, 2);
    }

    public void setOnImageSelectedListener(OnImageSelectedListener onImageSelectedListener) {
        mOnImageSelectedListener = onImageSelectedListener;
    }

    private int getExactlyPosition(int position) {
        return mConfig.showCamera ? position - 1 : position;
    }

    private void setLayoutParams(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = mScreenWidth / 3 - mItemSpan;
        layoutParams.height = mScreenWidth / 3 - mItemSpan;
        view.setLayoutParams(layoutParams);
    }

    private void setIvCheckedStyle(boolean isSelected, ImageView ivChecked, View hover) {
        ivChecked.setImageResource(isSelected ? R.drawable.ic_checked : R.drawable.ic_un_checked);
        hover.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
    }

    public void setDataSource(List<BeanImage> imgFileList) {
        mBeanImageList = imgFileList;
        notifyDataSetChanged();
    }

    public List<BeanImage> getDataSource() {
        return mBeanImageList;
    }

    public void updateBeanImageHasSelected(List<BeanImage> imgHasSelected) {
        mBeanImageSelectedList = imgHasSelected;
        notifyDataSetChanged();
    }

    public interface OnImageSelectedListener {
        void onImageSelected(BeanImage beanImage);
        void onImageUnSelected(BeanImage beanImage);
        void onPreviewImage(BeanImage beanImage, int position);
    }

	@Override
	public int getCount() {
		if (mConfig.showCamera) {
            return mBeanImageList == null ? 1 : mBeanImageList.size() + 1;
        } else {
            return mBeanImageList == null ? 0 : mBeanImageList.size();
        }
	}
	
	@Override
	public int getViewTypeCount() {
		return mConfig.showCamera ? 2 : 1;
	}
	
	@Override
    public int getItemViewType(int position) {
        if (mConfig.showCamera) {
            return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_IMAGE;
        } else {
            return ITEM_TYPE_IMAGE;
        }
    }

	@Override
	public Object getItem(int position) {
		return mBeanImageList == null ? null : mBeanImageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (ITEM_TYPE_CAMERA == getItemViewType(position)) {
			CameraViewHolder cameraViewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_camera, parent, false);
				cameraViewHolder = new CameraViewHolder(convertView);
				convertView.setTag(cameraViewHolder);
			}
			cameraViewHolder = (CameraViewHolder) convertView.getTag();
			bindCameraViewHolder(cameraViewHolder);
		} else {
			ImageViewHolder imageViewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_image, parent, false);
				imageViewHolder = new ImageViewHolder(convertView);
				convertView.setTag(imageViewHolder);
			}
			imageViewHolder = (ImageViewHolder) convertView.getTag();
			bindImageViewHolder(imageViewHolder, getExactlyPosition(position));
		}
		return convertView;
	}
	
	private boolean imageIsSelected(BeanImage beanImage) {
    	for (BeanImage beanImageHasSelected : mBeanImageSelectedList) {
    		if (beanImageHasSelected.getFilePath().equals(beanImage.getFilePath())) {
    			return true;
    		}
    	}
    	return false;
    }
    
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
	
	private void bindImageViewHolder(final ImageViewHolder holder, final int position) {
        setLayoutParams(holder.mItemView);

        final BeanImage beanImage = mBeanImageList.get(position);

        Glide.with(mContext)
                .load(beanImage.getFilePath())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .dontAnimate()
                .centerCrop()
                .into(holder.mIvImage);

        setIvCheckedStyle(imageIsSelected(beanImage), holder.mIvChecked, holder.mHoverView);

        holder.mIvChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageIsSelected(beanImage)) {
                    // 已选中变为未选中
                	removeBeanImageSelected(beanImage);
                    setIvCheckedStyle(false, holder.mIvChecked, holder.mHoverView);
                    if (mOnImageSelectedListener != null) {
                        mOnImageSelectedListener.onImageUnSelected(beanImage);
                    }
                } else {
                    // 未选中变为已选中
                    if (mBeanImageSelectedList.size() < mConfig.maxImageCount) {
                    	addBeanImageSelected(beanImage);
                        setIvCheckedStyle(true, holder.mIvChecked, holder.mHoverView);
                        if (mOnImageSelectedListener != null) {
                            mOnImageSelectedListener.onImageSelected(beanImage);
                        }
                    }
                }
            }
        });
        holder.mIvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnImageSelectedListener != null) {
                    mOnImageSelectedListener.onPreviewImage(beanImage, position);
                }
            }
        });
    }
	
	private void bindCameraViewHolder(CameraViewHolder holder) {
        setLayoutParams(holder.mItemView);

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Capture photo
            }
        });
    }
	
	private static class ImageViewHolder {

        View mItemView;
        ImageView mIvImage;
        View mHoverView;
        ImageView mIvChecked;

        public ImageViewHolder(View itemView) {
            mItemView = itemView;
            mIvImage = (ImageView) itemView.findViewById(R.id.ivImage);
            mHoverView = itemView.findViewById(R.id.hover_checked);
            mIvChecked = (ImageView) itemView.findViewById(R.id.iv_check);
        }
    }

    private static class CameraViewHolder {

        View mItemView;
        ImageView mIvCamera;

        public CameraViewHolder(View itemView) {
            mItemView = itemView;
            mIvCamera = (ImageView) itemView.findViewById(R.id.iv_camera);
        }
    }
}
