package boot.common.base;

import net.dreamlu.mica.common.support.IController;
import net.dreamlu.mica.core.result.R;
import net.dreamlu.mica.core.result.SystemCode;

/**
 抽象控制器
 */
public class BaseController implements IController {
    /**
     * 根据状态返回成功和失败
     *
     * @param status 状态
     * @param <T>    枚举
     * @return 返回信息
     */
    protected <T> R<T> status(boolean status) {
        return status ? success() : fail();
    }

    /**
     * 状态返回失败
     *
     * @param <T> 枚举
     * @return 返回信息
     */
    protected <T> R<T> fail() {
        return R.fail(SystemCode.FAILURE, "操作失败");
    }
}
