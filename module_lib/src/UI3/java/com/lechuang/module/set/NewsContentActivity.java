package java.com.lechuang.module.set;

import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.lechuang.module.R;

import retrofit2.http.PUT;

@Route(path = ARouters.PATH_NEWS_CONTENT)
public class NewsContentActivity extends BaseActivity {

    @Autowired(name = "title")
    public String title;

    @Autowired(name = "timeStr")
    public String timeStr;

    @Autowired(name = "content")
    public String content;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_content;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);

        ((TextView) $(R.id.iv_common_title)).setText("消息详情");
        ((TextView) $(R.id.tv_news_content_title)).setText(title);
        ((TextView) $(R.id.tv_news_content_time)).setText(timeStr);
        ((TextView) $(R.id.tv_news_content)).setText(content);
        $(R.id.iv_common_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void getData() {

    }
}
