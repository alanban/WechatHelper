package czc.wxhelper.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;
import czc.wxhelper.R;
import czc.wxhelper.base.BaseActivity;
import czc.wxhelper.constant.Const;
import czc.wxhelper.presenter.CenterNumPresenter;
import czc.wxhelper.view.Center4NumView;

/**
 * 选择中间4位区段号
 */
public class CenterNumActivity extends BaseActivity implements Center4NumView {

    @BindView(R.id.lv_center_number)
    ListView mCenterNumListView;

    @BindView(R.id.tv_empty)
    TextView mEmptyView;

    private ArrayAdapter<String> mListAdapter;
    private ProgressDialog mDialog;
    private CenterNumPresenter mController;
    private List<String> mPhoneList = new ArrayList<>();

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_center_num_list);
    }

    @Override
    protected void initView() {
        mEmptyView.setText("查询结果为空");
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("加载中，请稍等···");
        mDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initData() {
        mController = new CenterNumPresenter(this);
        mController.getDateFromNet(getIntent().getExtras());
        mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPhoneList);
        mCenterNumListView.setAdapter(mListAdapter);
        mCenterNumListView.setEmptyView(mEmptyView);
    }

    @Override
    public void begin() {
        mDialog.show();
    }

    @Override
    public void end() {
        mDialog.dismiss();
    }

    @Override
    public void success(List<String> data) {
        toast("共"+data.size()+"条记录");
        if (data != null && data.size() > 0) {
            mPhoneList.addAll(data);
        }
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void error() {
        mDialog.dismiss();
        toast("哎呀，请求失败了，请重试~");
    }

    @OnItemClick(R.id.lv_center_number)
    void onItemClick(int position) {
        Intent intent = new Intent();
        intent.putExtra(Const.KEY_CHOOSE_CENTER_NUMBER, mPhoneList.get(position));
        setResult(RESULT_OK, intent);
        back();
    }
}
