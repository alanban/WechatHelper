package czc.wxhelper.presenter;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import czc.wxhelper.view.TaskView;

/**
 * Created by alan on 2017/7/3.
 */

public interface TaskStrategy {

	/**
	 * 绑定
	 *
	 * @param view
	 */
	void bindTask(TaskView view);

	/**
	 * 执行自动化事件
	 *
	 * @param event
	 */
	void doTask(AccessibilityEvent event, AccessibilityNodeInfo nodeInfo);

}
