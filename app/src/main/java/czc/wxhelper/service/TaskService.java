package czc.wxhelper.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import czc.wxhelper.constant.Const;
import czc.wxhelper.event.EasyEventBuilder;
import czc.wxhelper.event.EventType;
import czc.wxhelper.manager.TaskManager;
import czc.wxhelper.presenter.TaskStrategy;
import czc.wxhelper.util.PreferenceHelper;
import czc.wxhelper.view.TaskView;

/**
 * 核心服务：执行自动化任务
 * Created by alan on 2017/6/13.
 */
public class TaskService extends AccessibilityService implements TaskView {

	private static final String TAG = "TaskService";

	/**
	 * 服务没有连接
	 */
	public static final int STATUS_UNCONNECT = 0;
	/**
	 * 服务连接
	 */
	public static final int STATUS_CONNECT = 1;
	/**
	 * 服务状态
	 */
	public static int SERVICE_STATUS = STATUS_UNCONNECT;


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onServiceConnected() {
		SERVICE_STATUS = STATUS_CONNECT;
		Log.i(TAG, "onServiceConnected");

		EventBus.getDefault()
				.post(new EasyEventBuilder<Boolean>(EventType.TYPE_SERVICE_HAS_CONNECTED)
						.setValue(true)
						.build());
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		SERVICE_STATUS = STATUS_CONNECT;
		boolean canAutoAddPeople = PreferenceHelper.getBoolean(Const.PREF_KEY_STOP_AUTO_ADD_PEOPLE_FLAG, false);
		if (!canAutoAddPeople) {
            try {
                TaskStrategy strategy = TaskManager.getInstance().getTaskStrategy();
                strategy.bindTask(this);
                strategy.doTask(event, getRoot());
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showShort("出现异常，返回重试···");
                performBack();
            }
        }
	}

	public AccessibilityNodeInfo getRoot() {
		return getRootInActiveWindow();
	}

	@Override
	public void onInterrupt() {
		SERVICE_STATUS = STATUS_UNCONNECT;
		Log.e(TAG, "onInterrupt");
	}

	@Override
	public void onDestroy() {
		SERVICE_STATUS = STATUS_UNCONNECT;
		Log.e(TAG, "onDestroy");
		getRootInActiveWindow().recycle();
	}

	@Override
	public void performBack() {
		performGlobalAction(GLOBAL_ACTION_BACK);
	}

	@Override
	public void performAction(int action) {
		performGlobalAction(action);
	}

	@Override
	public AccessibilityNodeInfo getActiveRoot() {
		return getRootInActiveWindow();
	}

	@Override
	public Context getContext() {
		return TaskService.this;
	}

}
