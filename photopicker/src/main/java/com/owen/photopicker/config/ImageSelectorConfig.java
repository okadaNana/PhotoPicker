package com.owen.photopicker.config;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageSelectorConfig implements Parcelable {

	public boolean showCamera;
	public int maxImageCount;
	public int requestCode;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(this.showCamera ? (byte) 1 : (byte) 0);
		dest.writeInt(this.maxImageCount);
		dest.writeInt(this.requestCode);
	}

	public ImageSelectorConfig() {
	}

	protected ImageSelectorConfig(Parcel in) {
		this.showCamera = in.readByte() != 0;
		this.maxImageCount = in.readInt();
		this.requestCode = in.readInt();
	}

	public static final Creator<ImageSelectorConfig> CREATOR = new Creator<ImageSelectorConfig>() {
		@Override
		public ImageSelectorConfig createFromParcel(Parcel source) {
			return new ImageSelectorConfig(source);
		}

		@Override
		public ImageSelectorConfig[] newArray(int size) {
			return new ImageSelectorConfig[size];
		}
	};
}
