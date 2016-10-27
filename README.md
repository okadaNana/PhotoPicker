## PhotoPicker

## Screenshot

![](./images/all_image.png)
![](./images/preview_image.png)
![](./images/show_folder.png)

## Usage

## Pick Photo

```java
ImageSelectorConfig config = new ImageSelectorConfig();
config.showCamera = false;
config.maxImageCount = 9;
config.requestCode = REQUEST_PICK_PHOTO;
Intent intent = ImageSelectorActivity.newIntent(DemoActivity.this, config);
startActivityForResult(intent, config.requestCode);
```

### onActivityResult

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);

	if (resultCode != Activity.RESULT_OK) {
		return;
	}

	if (requestCode == REQUEST_PICK_PHOTO) {
		ArrayList<String> photoPathList = 
			data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
	}
}
```

### manifest

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	<uses-permission android:name="android.permission.CAMERA" />

    <application
        ...>
		
		...
		
        <activity android:name="com.owen.photopicker.activity.ImageSelectorActivity"/>
        <activity android:name="com.owen.photopicker.activity.ImagePreviewActivity"/>
		
    </application>

</manifest>
```

## TODO

* ~~pick photo~~
* capture photo~~

## License

```
The MIT License (MIT)

Copyright (c) 2016 omike

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```