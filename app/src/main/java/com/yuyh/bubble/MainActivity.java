package com.yuyh.bubble;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.yuyh.library.BubblePopupWindow;
import com.yuyh.library.BubbleRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BubblePopupWindow leftTopWindow;
    private BubblePopupWindow rightTopWindow;
    private BubblePopupWindow leftBottomWindow;
    private BubblePopupWindow rightBottomWindow;
    private BubblePopupWindow centerWindow;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftTopWindow = new BubblePopupWindow(MainActivity.this);
        rightTopWindow = new BubblePopupWindow(MainActivity.this);
        leftBottomWindow = new BubblePopupWindow(MainActivity.this);
        rightBottomWindow = new BubblePopupWindow(MainActivity.this);
        centerWindow = new BubblePopupWindow(MainActivity.this);

        inflater = LayoutInflater.from(this);
    }

    public void leftTop(View view) {

        BubbleRelativeLayout contentView = (BubbleRelativeLayout) LayoutInflater.from(this).inflate(R.layout.rounded_rectangle_layout, null);
        contentView.setBackgroundColor(Color.TRANSPARENT);
        handleListView(contentView);

        //设置popWindow要显示的内容
        leftTopWindow.setContentView(contentView);
        contentView.setBubbleParams(BubbleRelativeLayout.BubbleLegOrientation.LEFT,
                leftTopWindow.getMeasuredWidth() - (view.getWidth() / 2)); // 设置气泡布局方向及尖角偏移
        //显示popWindow

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        setBacgroundAlphaDark(leftTopWindow, view);
        leftTopWindow.showAtLocation(view, Gravity.NO_GRAVITY,
                ViewUtils.getScreenWidth(MainActivity.this) - leftTopWindow.getMeasuredWidth(), location[1] + view.getHeight());
    }

    public void leftTop2(View view) {
        Toast.makeText(MainActivity.this, "测试popupwidnow", Toast.LENGTH_SHORT).show();
    }


    public void rightTop(View view) {

        BubbleRelativeLayout contentView = (BubbleRelativeLayout) LayoutInflater.from(this).inflate(R.layout.rounded_rectangle_layout, null);
        contentView.setBackgroundColor(Color.TRANSPARENT);
        handleListView(contentView);

        //设置popWindow要显示的内容
        rightTopWindow.setContentView(contentView);
        contentView.setBubbleParams(BubbleRelativeLayout.BubbleLegOrientation.TOP,
                rightTopWindow.getMeasuredWidth() - (view.getWidth() / 2)); // 设置气泡布局方向及尖角偏移
        //显示popWindow

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        //setBacgroundAlphaDark(rightTopWindow,view);
        //  rightTopWindow.show(view, Gravity.LEFT, 0);
        rightTopWindow.showAtLocation(view, Gravity.NO_GRAVITY,
                ViewUtils.getScreenWidth(MainActivity.this) - rightTopWindow.getMeasuredWidth(), location[1] + view.getHeight());
    }


    public void leftBottom(View view) {
        View bubbleView = inflater.inflate(R.layout.layout_popup_view, null);
        leftBottomWindow.setBubbleView(bubbleView);
        leftBottomWindow.show(view);
    }

    public void rightBottom(View view) {
        View bubbleView = inflater.inflate(R.layout.layout_popup_view, null);
        rightBottomWindow.setBubbleView(bubbleView);
        rightBottomWindow.show(view, Gravity.RIGHT, 0);
    }

    public void center(View view) {
        View bubbleView = inflater.inflate(R.layout.layout_popup_view, null);
        centerWindow.setBubbleView(bubbleView);
        centerWindow.show(view, Gravity.BOTTOM, 0);
    }


    private void handleListView(View contentView) {
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        DatePopupWindowAdapter adapter = new DatePopupWindowAdapter();
        List<String> mockData = mockData();
        adapter.setData(mockData());

        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new RecycleViewDivider(
//                MainActivity.this, LinearLayoutManager.VERTICAL, 20, getResources().getColor(R.color.colorPrimaryDark)));
        adapter.setOnItemClickListener(new DatePopupWindowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, mockData().get(position) + "toast", Toast.LENGTH_SHORT).show();
                Log.i("seachal", mockData().get(position));
            }
        });
        adapter.notifyDataSetChanged();

    }

    private List<String> mockData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            data.add("2017年11月:" + i);
        }
        return data;
    }


    /**
     * 设置透明度
     *
     * @param popupWindow
     */
    private void setBacgroundAlphaDark(BubblePopupWindow popupWindow, View view) {


        popupWindow.setDarkColor(Color.parseColor("#a0000000"));
        popupWindow.resetDarkPosition();
        popupWindow.darkBelow(view);
    }


}
