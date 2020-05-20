package czc.wxhelper.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import butterknife.ButterKnife;
import czc.wxhelper.R;
import czc.wxhelper.util.ToastUtil;

/**
 * Created by alan on 2017/6/21.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        ButterKnife.bind(this);
        initToolBar();
        initView();
        initData();
        setListener();
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    protected abstract void setContentView();
    protected abstract void initData();

    protected void initView() {

    }

    protected void setListener(){

    };

    protected void toast(String msg){
        ToastUtil.showToast(this,msg);
    }

    protected void log(String log){
        Log.i("czc",log);
    }

    protected void redirect(Class targetClass){
        startActivity(new Intent(this,targetClass));
    }

    protected void back(){
        finish();
    }

    protected void hideSystemKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
