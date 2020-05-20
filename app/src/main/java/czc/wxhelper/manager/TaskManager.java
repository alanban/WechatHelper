package czc.wxhelper.manager;

import czc.wxhelper.presenter.NearHumanStrategy2;
import czc.wxhelper.presenter.TaskStrategy;

/**
 * 任务配置
 * Created by alan on 2018/8/2.
 */

public class TaskManager {
    private static final TaskManager ourInstance = new TaskManager();
    private TaskMode mMode = TaskMode.NEAR_PEOPLE;
    private TaskStatus mStatus = TaskStatus.IDLE;
    private TaskStrategy mStrategy = new NearHumanStrategy2();

   public static TaskManager getInstance() {
        return ourInstance;
    }

    private TaskManager() {
    }

    public void setCurrentTaskMode(TaskMode mode) {
        mMode = mode;
    }

    public TaskMode getCurrentTaskMode() {
        return mMode;
    }

    public void setTaskStatus(TaskStatus status) {
        mStatus = status;
    }

    public TaskStatus getTaskStatus() {
        return mStatus;
    }

    public void setTaskStrategy(TaskStrategy strategy){
    	mStrategy = strategy;
	}

	public TaskStrategy getTaskStrategy(){
    	return mStrategy;
	}
}
