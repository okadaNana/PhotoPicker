package com.owen.demo.photopicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.owen.photopicker.activity.ImageSelectorActivity;
import com.owen.photopicker.config.ImageSelectorConfig;

import java.util.ArrayList;

public class DemoActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_PHOTO = 1;

    private TextView mTvImageFilePathResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvImageFilePathResultList = (TextView) findViewById(R.id.tv_image_filepath_result_list);
        findViewById(R.id.btnPickPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelectorConfig config = new ImageSelectorConfig();
                config.showCamera = false;
                config.maxImageCount = 9;
                config.requestCode = REQUEST_PICK_PHOTO;
                Intent intent = ImageSelectorActivity.newIntent(DemoActivity.this, config);
                startActivityForResult(intent, config.requestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PICK_PHOTO) {
            ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < stringArrayListExtra.size(); i++) {
                String filePath = stringArrayListExtra.get(i);
                sb.append(i).append(") ").append(filePath).append("\n\n");
            }
            mTvImageFilePathResultList.setText(sb.toString());
        }
    }
}
