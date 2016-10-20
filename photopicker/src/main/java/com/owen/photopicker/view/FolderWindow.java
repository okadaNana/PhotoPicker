package com.owen.photopicker.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.owen.photopicker.R;
import com.owen.photopicker.adapter.FolderAdapter;
import com.owen.photopicker.bean.BeanFolder;
import com.owen.photopicker.util.ScreenUtil;

import java.lang.reflect.Method;
import java.util.List;

public class FolderWindow extends PopupWindow {
    private Context context;
    private View window;
    private ListView recyclerView;
    private FolderAdapter adapter;

    private boolean isDismiss = false;
    
    public FolderWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		this(context);
	}

	public FolderWindow(Context context, AttributeSet attrs, int defStyle) {
		this(context);
	}

	public FolderWindow(Context context, AttributeSet attrs) {
		this(context);
	}

	public FolderWindow(Context context) {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.popwindow_folder, null);
        this.setContentView(window);
        this.setWidth(ScreenUtil.getScreenWidth(context));
        this.setHeight(ScreenUtil.getScreenHeight(context));
        this.setAnimationStyle(R.style.WindowStyle);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable(Color.argb(153, 0, 0, 0)));

        initView();
        registerListener();
        setPopupWindowTouchModal(this, false);
    }

    public void initView() {
        adapter = new FolderAdapter(context);

        recyclerView = (ListView) window.findViewById(R.id.folder_list);
        recyclerView.setAdapter(adapter);
    }

    public void registerListener() {

    }

    public void bindFolder(List<BeanFolder> folders) {
        adapter.setBeanFolderList(folders);
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_in);
        recyclerView.startAnimation(animation);
    }

    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        isDismiss = true;
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.down_out);
        recyclerView.startAnimation(animation);
        dismiss();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            	new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                    	 isDismiss = false;
                         FolderWindow.super.dismiss();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void setPopupWindowTouchModal(PopupWindow popupWindow, boolean touchModal) {
        if (null == popupWindow) {
            return;
        }
        Method method;
        try {
            method = PopupWindow.class.getDeclaredMethod("setTouchModal", boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, touchModal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnItemClickListener(FolderAdapter.OnItemClickListener onItemClickListener) {
        adapter.setOnItemClickListener(onItemClickListener);
    }
}

