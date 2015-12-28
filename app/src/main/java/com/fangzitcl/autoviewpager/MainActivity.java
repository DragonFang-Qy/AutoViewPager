package com.fangzitcl.autoviewpager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fangzitcl.autoviewpager.model.BasePagerModel;
import com.fangzitcl.autoviewpager.model.Gravity;
import com.fangzitcl.autoviewpager.view.AutoViewFlipper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    AutoViewFlipper mAutoViewFlipper;
    ArrayList<BasePagerModel> mArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAutoViewFlipper = (AutoViewFlipper) findViewById(R.id.main_id);

        mArrayList = new ArrayList<>();

        BasePagerModel model = new BasePagerModel();
        model.setImagePath("http://img4.duitang.com/uploads/item/201407/10/20140710203824_kNSui.jpeg");
        model.setTitle("http://img4.duitang.com/uploads/item/201407/10/20140710203824_kNSui.jpeg");
        mArrayList.add(model);

        model = new BasePagerModel();
        model.setImagePath("http://d.3987.com/hbxg_131011/002.jpg");
        model.setTitle("http://d.3987.com/hbxg_131011/002.jpg");
        mArrayList.add(model);

        model = new BasePagerModel();
        model.setImagePath("http://tupian.qqjay.com/u/2012/0915/1_17439_15.jpg");
        model.setTitle("http://tupian.qqjay.com/u/2012/0915/1_17439_15.jpg");
        mArrayList.add(model);

        mAutoViewFlipper.setLoop(true);
        mAutoViewFlipper.setShowTitle(false);
        mAutoViewFlipper.setShowIndicator(true);
        mAutoViewFlipper.setIndicatorGravity(Gravity.center);
//        必须在以上设置之后
        mAutoViewFlipper.setShowData(mArrayList);


    }
}
