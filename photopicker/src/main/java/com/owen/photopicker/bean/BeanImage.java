package com.owen.photopicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class  BeanImage implements Parcelable {

	private String filePath;

	public BeanImage(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.filePath);
	}

	protected BeanImage(Parcel in) {
		this.filePath = in.readString();
	}

	public static final Creator<BeanImage> CREATOR = new Creator<BeanImage>() {
		@Override
		public BeanImage createFromParcel(Parcel source) {
			return new BeanImage(source);
		}

		@Override
		public BeanImage[] newArray(int size) {
			return new BeanImage[size];
		}
	};
}
