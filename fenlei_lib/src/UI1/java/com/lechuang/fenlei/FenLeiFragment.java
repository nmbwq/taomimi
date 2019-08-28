package java.com.lechuang.fenlei;


import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.LazyBaseFragment;
import com.lechuang.fenlei.R;

/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:
 */

@Route(path = ARouters.PATH_FEN_LEI)
public class FenLeiFragment extends LazyBaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fenlei;
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
