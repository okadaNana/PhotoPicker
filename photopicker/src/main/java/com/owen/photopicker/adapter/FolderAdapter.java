package com.owen.photopicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.owen.photopicker.R;
import com.owen.photopicker.bean.BeanFolder;

import java.io.File;
import java.util.List;

public class FolderAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<BeanFolder> mBeanFolderList;
    private OnItemClickListener mOnItemClickListener;
    private int checkedIndex;

    public FolderAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setBeanFolderList(List<BeanFolder> beanFolderList) {
        mBeanFolderList = beanFolderList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(BeanFolder beanFolder);
    }

	@Override
	public int getCount() {
		return mBeanFolderList == null ? 0 : mBeanFolderList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBeanFolderList == null ? null : mBeanFolderList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		FolderViewHolder folderViewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_folder, parent, false);
			folderViewHolder = new FolderViewHolder(convertView);
			convertView.setTag(folderViewHolder);
		}
		folderViewHolder = (FolderViewHolder) convertView.getTag();
		
		final BeanFolder beanFolder = mBeanFolderList.get(position);
        Glide.with(mContext)
                .load(new File(beanFolder.getFirstImagePath()))
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .centerCrop()
                .into(folderViewHolder.firstImage);
        folderViewHolder.folderName.setText(beanFolder.getName());
        folderViewHolder.imageNum.setText(mContext.getString(R.string.num_postfix, beanFolder.getImageNum()));
        folderViewHolder.isSelected.setVisibility(checkedIndex == position ? View.VISIBLE : View.INVISIBLE);
        folderViewHolder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    checkedIndex = position;
                    notifyDataSetChanged();
                    mOnItemClickListener.onItemClick(beanFolder);
                }
            }
        });
		
		return convertView;
	}
	
	private static class FolderViewHolder {

        View contentView;
        ImageView firstImage;
        TextView folderName;
        TextView imageNum;
        ImageView isSelected;

        public FolderViewHolder(View itemView) {
            contentView = itemView;
            firstImage = (ImageView) itemView.findViewById(R.id.first_image);
            folderName = (TextView) itemView.findViewById(R.id.folder_name);
            imageNum = (TextView) itemView.findViewById(R.id.image_num);
            isSelected = (ImageView) itemView.findViewById(R.id.is_selected);
        }
    }
}
