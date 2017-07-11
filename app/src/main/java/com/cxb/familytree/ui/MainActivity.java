package com.cxb.familytree.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cxb.familytree.R;

public class MainActivity extends BaseActivity {

    private TextView tvNot;
    private TextView tvHave;
    private TextView tvNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setData();

    }

    private void initView() {
        tvNot = (TextView) findViewById(R.id.tv_not_foster_parent);
        tvHave = (TextView) findViewById(R.id.tv_have_foster_parent);
        tvNew = (TextView) findViewById(R.id.tv_new_familytree);
    }

    private void setData() {
        tvNot.setOnClickListener(click);
        tvHave.setOnClickListener(click);
        tvNew.setOnClickListener(click);
    }

    //点击监听
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_not_foster_parent:
                    startActivity(new Intent(MainActivity.this, FamilyTreeActivity.class));
                    break;
                case R.id.tv_have_foster_parent:
                    startActivity(new Intent(MainActivity.this, FamilyTreeActivity.class).putExtra(FamilyTreeActivity.HAVE_FOSTER_PARENT, true));
                    break;
                case R.id.tv_new_familytree:
                    startActivity(new Intent(MainActivity.this, FamilyTreeActivity3.class));
                    break;
            }
        }
    };

}
