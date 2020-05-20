package czc.wxhelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;
import czc.wxhelper.R;
import czc.wxhelper.base.BaseActivity;
import czc.wxhelper.constant.Const;
import czc.wxhelper.ui.SingleSelectDialogView;
import czc.wxhelper.util.ContactUtil;

/**
 * 填写生成号码信息
 */
public class ProduceActivity extends BaseActivity {
    private List<String> mNumList = new ArrayList<>();
    private SingleSelectDialogView mSdv;
    private int mCurrentIndex;//0,1,2
    private final int READ_REQUEST_CODE = 0x123;

    @BindView(R.id.et_center_num)
    EditText mCenterNumEt;
    @BindView(R.id.et_create_num)
    EditText mCreatePhoneNumberEt;
    @BindView(R.id.et_create_flag)
    EditText mCreateFlagEt;

    @BindArray(R.array.city)
    String[] mCityArr;

    @BindArray(R.array.city_pinyin)
    String[] mCityPinYinArr;

    @BindView(R.id.btn_createNum)
    Button mCreateNumBtn;
    @BindView(R.id.btn_choose_center_number)
    Button mChooseNumBtn;
    @BindView(R.id.btn_record)
    Button mRecordBtn;
    @BindView(R.id.btn_import)
    Button mImportBtn;

    @BindView(R.id.ll_area)
    LinearLayout mAreaLl;
    @BindView(R.id.ll_yys)
    LinearLayout mYysLl;
    @BindView(R.id.ll_hd)
    LinearLayout mHdLl;

    @BindView(R.id.tv_area)
    TextView mAreaTv;
    @BindView(R.id.tv_yys)
    TextView mYysTv;
    @BindView(R.id.tv_hd)
    TextView mHdTV;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_number);
        mSdv = new SingleSelectDialogView(this, "请选择", new String[]{}, -1);
    }

    @Override
    protected void initData() {
        String[] ydQh = getResources().getStringArray(R.array.yd_qh);
        String[] ltQh = getResources().getStringArray(R.array.lt_qh);
        String[] dxQh = getResources().getStringArray(R.array.dx_qh);

        mNumList.addAll(Arrays.asList(ydQh));
        mNumList.addAll(Arrays.asList(ltQh));
        mNumList.addAll(Arrays.asList(dxQh));
    }

    @Override
    protected void setListener() {
        mSdv.setOnOKListener(new SingleSelectDialogView.OnOKListener() {
            @Override
            public void onOK(int postion, String text) {
                if (mCurrentIndex == 0) {
                    mAreaTv.setText(text);
                } else if (mCurrentIndex == 1) {
                    mYysTv.setText(text);
                } else if (mCurrentIndex == 2) {
                    mHdTV.setText(text);
                }
            }
        });

    }

    @OnClick(R.id.btn_createNum)
    void clickCreateNum() {
        String area = mAreaTv.getText().toString().trim();
        if (TextUtils.isEmpty(area)) {
            toast("请选择地区");
            return;
        }

        String hd = mHdTV.getText().toString().trim();
        if (TextUtils.isEmpty(hd)) {
            toast("请选择号段");
            return;
        }
        String centerNum = mCenterNumEt.getText().toString().trim();
        if (TextUtils.isEmpty(centerNum)) {
            toast("请填写精确号段");
            return;
        }
        if (centerNum.length() < 4) {
            toast("中间4位数不能为空，也不能少于4位哦");
            return;
        }
        String num = mCreatePhoneNumberEt.getText().toString().trim();
        if (TextUtils.isEmpty(num)) {
            toast("请填写生成个数");
            return;
        }

        if (Integer.parseInt(num) < 0) {
            num = "0";
        }

        if (Integer.parseInt(num) > 500) {
            toast("一次最多只能生成500个");
            return;
        }
        hideSystemKeyBoard(mCenterNumEt);
        hideSystemKeyBoard(mCreatePhoneNumberEt);
        Bundle bundle = new Bundle();
        bundle.putString(Const.KEY_HD, hd);
        bundle.putString(Const.KEY_CENTER_NUMBER, centerNum);
        bundle.putInt(Const.KEY_CREATE_PHONE_NUMBER, Integer.parseInt(num));
        String flag = mCreateFlagEt.getText().toString().trim().equals("") ? ContactUtil.SK : mCreateFlagEt.getText().toString().trim();
        bundle.putString(Const.KEY_CREATE_PHONE_NUMBER_FLAG, flag);
        Intent intent = new Intent(this, SavePhoneActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @OnClick(R.id.ll_area)
    void clickArea() {
        mCurrentIndex = 0;
        mSdv.setList(mCityArr, findIndex(mCityArr, mAreaTv.getText().toString()));
        mSdv.setTitle("选择地区");
        mSdv.show();
    }

    @OnClick(R.id.ll_yys)
    void clickYys() {
        mCurrentIndex = 1;
        String[] yysArr = getResources().getStringArray(R.array.yys);
        mSdv.setList(yysArr, findIndex(yysArr, mYysTv.getText().toString().trim()));
        mSdv.setTitle("选择运营商");
        mSdv.show();
    }

    @OnClick(R.id.ll_hd)
    void clickHd() {
        mCurrentIndex = 2;
        String yys = mYysTv.getText().toString().trim();
        mSdv.setTitle(yys);
        if ("中国移动".equals(yys)) {
            String[] ydArr = getResources().getStringArray(R.array.yd_qh);
            mSdv.setList(ydArr, findIndex(ydArr, mHdTV.getText().toString()));
        } else if ("中国电信".equals(yys)) {
            String[] dxArr = getResources().getStringArray(R.array.dx_qh);
            mSdv.setList(dxArr, findIndex(dxArr, mHdTV.getText().toString()));
        } else if ("中国联通".equals(yys)) {
            String[] ltArr = getResources().getStringArray(R.array.lt_qh);
            mSdv.setList(ltArr, findIndex(ltArr, mHdTV.getText().toString()));
        } else {
            mSdv.setTitle("选择号段");
            String[] allArr = new String[mNumList.size()];
            mSdv.setList(mNumList.toArray(allArr), findIndex(allArr, mHdTV.getText().toString()));
        }
        mSdv.show();
    }

    @OnClick(R.id.btn_record)
    void clickRecord() {
        startActivity(new Intent(this, RecordActivity.class));
    }

    @OnClick(R.id.btn_choose_center_number)
    void clickChoose() {
        String area = mAreaTv.getText().toString().trim();
        if (TextUtils.isEmpty(area)) {
            toast("请选择地区");
            return;
        }
        String hd = mHdTV.getText().toString().trim();
        if (TextUtils.isEmpty(hd)) {
            toast("请选择号段");
            return;
        }
        int index = -1;
        int i = 0;
        for (String city : mCityArr) {
            i++;
            if (city.equals(area)) {
                index = i;
                break;
            }
        }
        if (index != -1 && index >= 0) {
            String cityPinYin = mCityPinYinArr[index];
            log(cityPinYin);
            Bundle bundle = new Bundle();
            bundle.putString(Const.KEY_CITY_PIN_YIN, cityPinYin);
            bundle.putString(Const.KEY_HD, hd);
            Intent intent = new Intent(this, CenterNumActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 101);
        }
    }

    @OnClick(R.id.btn_import)
    void onImport(){
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mCenterNumEt.setText(data.getStringExtra(Const.KEY_CHOOSE_CENTER_NUMBER));
                }
            }
        }else if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().

            if (data != null && data.getData()!=null) {
                importNumbers(data.getData().toString());
            }else {
                toast("文件获取失败");
            }
        }
    }

    private void importNumbers(String uri) {
        Bundle bundle = new Bundle();
        bundle.putString(Const.KEY_IMPORT_NUMBER_FILE, uri);
        String flag = mCreateFlagEt.getText().toString().trim().equals("") ? ContactUtil.SK : mCreateFlagEt.getText().toString().trim();
        bundle.putString(Const.KEY_CREATE_PHONE_NUMBER_FLAG, flag);
        Intent intent = new Intent(this, SavePhoneActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private int findIndex(String[] arr, String key) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }
}
