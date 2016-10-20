package com.owen.photopicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class BeanFolder implements Parcelable {
	
	private String name;
	private String firstImagePath;
	private int imageNum;
	private List<BeanImage> images;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstImagePath() {
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}

	public int getImageNum() {
		return imageNum;
	}

	public void setImageNum(int imageNum) {
		this.imageNum = imageNum;
	}

	public List<BeanImage> getImages() {
		return images;
	}

	public void setImages(List<BeanImage> images) {
		this.images = images;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.firstImagePath);
		dest.writeInt(this.imageNum);
		dest.writeTypedList(this.images);
	}

	public BeanFolder() {
	}

	protected BeanFolder(Parcel in) {
		this.name = in.readString();
		this.firstImagePath = in.readString();
		this.imageNum = in.readInt();
		this.images = in.createTypedArrayList(BeanImage.CREATOR);
	}

	public static final Creator<BeanFolder> CREATOR = new Creator<BeanFolder>() {
		@Override
		public BeanFolder createFromParcel(Parcel source) {
			return new BeanFolder(source);
		}

		@Override
		public BeanFolder[] newArray(int size) {
			return new BeanFolder[size];
		}
	};
}
