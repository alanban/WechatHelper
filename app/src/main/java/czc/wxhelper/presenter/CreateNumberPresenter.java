package czc.wxhelper.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import czc.wxhelper.MyApplication;
import czc.wxhelper.constant.Const;
import czc.wxhelper.manager.DBManager;
import czc.wxhelper.model.PhoneModel;
import czc.wxhelper.util.ContactUtil;
import czc.wxhelper.view.CreatePhoneView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by alan on 2017/6/25.
 */

public class CreateNumberPresenter implements PhonePresenter {

    private CreatePhoneView mView;
    private Context context;
    private List<String> mCreNumList = new ArrayList<>();

    private String mQhNumber;
    private String mCenterNumber;
    private String mNumberFlag;
    private String mNumberFileUri;
    private int mNumber;

    public CreateNumberPresenter(CreatePhoneView view,Context context) {
        mView = view;
        this.context = context;
    }

    public void initData(Bundle bundle) {
        if (bundle != null) {
            mQhNumber = bundle.getString(Const.KEY_HD);
            mNumberFlag = bundle.getString(Const.KEY_CREATE_PHONE_NUMBER_FLAG);
            mCenterNumber = bundle.getString(Const.KEY_CENTER_NUMBER);
            mNumber = bundle.getInt(Const.KEY_CREATE_PHONE_NUMBER);
            mNumberFileUri = bundle.getString(Const.KEY_IMPORT_NUMBER_FILE);
        }
    }

    @Override
    public void createNumber() {
        mCreNumList.clear();
        if (mNumberFileUri.isEmpty()) {
            createPhoneNumber(mNumber);
        } else {
            createPhoneNumber(mNumberFileUri);
        }
        mView.showResult(mCreNumList);
    }


    @Override
    public void save() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                Log.i("czc", mCreNumList.size() + "个");

                List<PhoneModel> recordList = new ArrayList<>();
                for (String number : mCreNumList) {
                    String name = mNumberFlag + number;
                    PhoneModel record = new PhoneModel();
                    record.setName(name);
                    record.setNumber(number);
                    recordList.add(record);
//                    ContactUtil.insert(MyApplication.getAppContext(), name, number);
                }
                ContactUtil.batchAddContact(MyApplication.getAppContext(), recordList);
                DBManager.getInstance(MyApplication.getAppContext())
                        .getSession()
                        .getPhoneModelDao()
                        .insertInTx(recordList);
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgressDialog();
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        mView.hideProgressDialog();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        mView.success();
                    }
                });

    }


    private void createPhoneNumber(int number) {
        for (int i = 0; i < number; i++) {
            String phoneNumber = mQhNumber + mCenterNumber + getEnd4Number();
//            Log.i("czc", phoneNumber);
            if (!mCreNumList.contains(phoneNumber)) {
                mCreNumList.add(phoneNumber);
            }
        }
        if (mCreNumList.size() < mNumber) {
            Log.i("czc", "agin");
            createPhoneNumber(mNumber - mCreNumList.size());
        }
    }

    private void createPhoneNumber(String stringUri) {
        Uri uri = Uri.parse(stringUri);

        if (uri!=null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                InputStreamReader inputreader = new InputStreamReader(inputStream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    if (!line.isEmpty() && !mCreNumList.contains(line.trim())) {
                        mCreNumList.add(line.trim());
                    }
                }
                inputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private String getEnd4Number() {
        String endNumber = "";
        Random random = new Random();
        int nextInt = random.nextInt(9998) % (9998 - 1 + 1) + 1;
        if (nextInt < 10) {
            endNumber = "000" + nextInt;
        } else if (nextInt < 100) {
            endNumber = "00" + nextInt;
        } else if (nextInt < 1000) {
            endNumber = "0" + nextInt;
        } else {
            endNumber = "" + nextInt;
        }
        return endNumber;
    }
}
