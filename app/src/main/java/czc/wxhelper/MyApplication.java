package czc.wxhelper;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import czc.wxhelper.constant.Const;
import czc.wxhelper.manager.DBManager;
import czc.wxhelper.util.AbnormalHandler;

/**
 * Created by alan on 2017/6/18.
 */

public class MyApplication extends Application {

    /**
     * SDK初始化也可以放到Application中
     */
    public static String APPID = "b5bd5e26a4593b6d0a61cd52f80a9c55";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Bmob.initialize(this, APPID, "Bmob");
        BmobInstallation installation = BmobInstallation.getCurrentInstallation();
        installation.setDeviceType(android.os.Build.MODEL);
        installation.save();

        DBManager.init(this);


//        Pandora.init(this).enableShakeOpen();
//        Pandora.init(this).open();
        if (Const.DEBUG) {
            AbnormalHandler.getInstance().init(this);
        }
    }

    public static Context getAppContext() {
        return context;
    }
}
