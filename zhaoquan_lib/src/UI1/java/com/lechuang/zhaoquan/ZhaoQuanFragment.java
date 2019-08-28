package java.com.lechuang.zhaoquan;


import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.LazyBaseFragment;
import com.lechuang.zhaoquan.R;

/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:
 */

@Route(path = ARouters.PATH_ZHAO_QUAN)
public class ZhaoQuanFragment extends LazyBaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_zhaoquan;
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
