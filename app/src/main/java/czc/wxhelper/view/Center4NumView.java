package czc.wxhelper.view;

import java.util.List;

import czc.wxhelper.base.BaseView;

/**
 * 中间4位
 * Created by alan on 2018/8/1.
 */

public interface Center4NumView extends BaseView{
    void begin();
    void success(List<String> data);
    void end();
    void error();
}
