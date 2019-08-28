package java.com.lechuang.zhibo;


import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.LazyBaseFragment;
import com.lechuang.zhibo.R;

/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:
 */

@Route(path = ARouters.PATH_ZHI_BO)
public class ZhiBoFragment extends LazyBaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_zhibo;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {

    }
}
