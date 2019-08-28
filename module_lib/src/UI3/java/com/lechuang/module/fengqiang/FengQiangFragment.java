package java.com.lechuang.module.fengqiang;

import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseFragment;
import com.lechuang.module.R;

/**
 * @author: zhengjr
 * @since: 2018/8/24
 * @describe:
 */

@Route(path = ARouters.PATH_FENGQIANG)
public class FengQiangFragment extends BaseFragment {

    private TextView mTvFengQiangTime;
    private TextView mTvFengQiangDoday;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fengqiang;
    }

    @Override
    protected void findViews() {

        mTvFengQiangTime = $(R.id.tv_fengqiang_time);
        mTvFengQiangDoday = $(R.id.tv_fengqiang_today);
        mTvFengQiangTime.setSelected(true);
        mTvFengQiangDoday.setSelected(false);

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {

    }
}
