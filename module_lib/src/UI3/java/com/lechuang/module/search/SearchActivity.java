package java.com.lechuang.module.search;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.SPUtils;
import com.common.app.view.CommonPopupwind;
import com.lechuang.module.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.SearchHotBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SEARCH)
public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTvSearchChannel;
    private EditText mEtSearch;
    private CommonPopupwind mCommonPopupwind;
    private TextView mTvSearchTB;
    private TextView mTvSearchJD;
    private TextView mTvSearchPDD;
    private TextView mTvSearchApp;
    private ImageView mIvSearchClear;

    public String mStringExtra;
    public String mSearchTxt;
//    private TagFlowLayout mFlowSearchEveryBody;
    private RecyclerView mRvSearchEveryBody;
    private TagFlowLayout mFlowSearchLately;
    private BaseQuickAdapter<String, BaseViewHolder> mBaseQuickAdapter;

    //流式布局  https://github.com/hongyangAndroid/FlowLayout

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mStringExtra = intent.getStringExtra(Constant.CHANNEL);
        mSearchTxt = intent.getStringExtra(Constant.SEARCHTEXT);
        if (!TextUtils.isEmpty(mStringExtra)) {
            mTvSearchChannel.setText(mStringExtra);
        }
        if (!TextUtils.isEmpty(mSearchTxt)) {
            mEtSearch.setText(mSearchTxt);
        }
        String text = mEtSearch.getText().toString();
        if (text.length() > 0) {
            mEtSearch.setSelection(text.length());
        }
    }

    @Override
    protected void findViews() {
        $(R.id.tv_status_bar).setBackgroundColor(getResources().getColor(R.color.c_F2F2F2));

        mTvSearchChannel = findViewById(R.id.tv_search_channel);
        mEtSearch = findViewById(R.id.et_search);
//        mFlowSearchEveryBody = findViewById(R.id.flow_search_everybody);
        mRvSearchEveryBody = findViewById(R.id.rv_search_everybody);
        mFlowSearchLately = findViewById(R.id.flow_search_lately);
        mIvSearchClear = findViewById(R.id.iv_search_clear);

        mTvSearchChannel.setOnClickListener(this);
        findViewById(R.id.iv_common_back).setOnClickListener(this);
        findViewById(R.id.tv_search).setOnClickListener(this);
        findViewById(R.id.iv_search_delete).setOnClickListener(this);
        mIvSearchClear.setOnClickListener(this);
    }

    @Override
    protected void initView() {

        mStringExtra = getIntent().getStringExtra(Constant.CHANNEL);
        mSearchTxt = getIntent().getStringExtra(Constant.SEARCHTEXT);
        if (!TextUtils.isEmpty(mStringExtra)) {
            mTvSearchChannel.setText(mStringExtra);
        }
        if (!TextUtils.isEmpty(mSearchTxt)) {
            mEtSearch.setText(mSearchTxt);
        }
        String text = mEtSearch.getText().toString();
        if (text.length() > 0) {
            mEtSearch.setSelection(text.length());
        }

        mCommonPopupwind = new CommonPopupwind
                .Builder()
                .showAsDropDown(mTvSearchChannel)
                .build(SearchActivity.this, R.layout.popupwind_search_channel);

        mTvSearchApp = (TextView) mCommonPopupwind.findViewById(R.id.tv_search_app);
        mTvSearchTB = (TextView) mCommonPopupwind.findViewById(R.id.tv_search_taobao);
        mTvSearchJD = (TextView) mCommonPopupwind.findViewById(R.id.tv_search_jingdong);
        mTvSearchPDD = (TextView) mCommonPopupwind.findViewById(R.id.tv_search_pinduoduo);

        mTvSearchApp.setOnClickListener(this);
        mTvSearchTB.setOnClickListener(this);
        mTvSearchJD.setOnClickListener(this);
        mTvSearchPDD.setOnClickListener(this);
        stringTransInt(mStringExtra);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0){
                    mIvSearchClear.setVisibility(View.VISIBLE);
                }else {
                    mIvSearchClear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void getData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取热搜数据
        getSearchHotData();

        //历史搜索
        setHistory();

    }

    /**
     * 热搜
     */
    private void getSearchHotData() {
        NetWork.getInstance()
                .setTag(Qurl.programaShowAll)
                .getApiService(ModuleApi.class)
                .hotSearchWord()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<SearchHotBean>(this, true, false) {

                    @Override
                    public void onSuccess(SearchHotBean result) {
                        if (result == null) {
                            return;
                        }
                        //设置热搜数据
                        setSearchHot(result.hswList);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }


    /**
     * 设置热搜信息
     *
     * @param hswList
     */
    private List<String> mTagList;
//    TagAdapter<String> mTagSearchHotAdapter;

    private void setSearchHot(List<SearchHotBean.HswListBean> hswList) {

        if (hswList == null || hswList.size() <= 0) {
            return;
        }

        if (mTagList == null) {
            mTagList = new ArrayList<>();
        }
        mTagList.clear();
        for (SearchHotBean.HswListBean hswListBean : hswList) {
            mTagList.add(hswListBean.searchWord);
        }

        if (mBaseQuickAdapter == null){
            mBaseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_search_body,mTagList) {

                @Override
                protected void convert(BaseViewHolder helper, String item) {
                    helper.setText(R.id.tv_search,item);
                }
            };
            mRvSearchEveryBody.setHasFixedSize(true);
            mRvSearchEveryBody.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            mRvSearchEveryBody.setLayoutManager(gridLayoutManager);

            mRvSearchEveryBody.setAdapter(mBaseQuickAdapter);
            mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    startActivity(mTagList.get(position));
                }
            });
        }else {
            mBaseQuickAdapter.notifyDataSetChanged();
        }

        /*if (mTagSearchHotAdapter != null) {
            mTagSearchHotAdapter = null;
        }
        if (mTagSearchHotAdapter == null) {
            mTagSearchHotAdapter = new TagAdapter<String>(mTagList) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_search,
                            mFlowSearchEveryBody, false);
                    tv.setText(s);
                    return tv;
                }
            };
            mFlowSearchEveryBody.setAdapter(mTagSearchHotAdapter);
            mFlowSearchEveryBody.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                @Override
                public void onSelected(Set<Integer> selectPosSet) {
                    //搜索
                    Iterator<Integer> iterator = selectPosSet.iterator();
                    if (iterator.hasNext()){
                        startActivity(mTagList.get(iterator.next()));
                    }
                }
            });
        }*/
    }

    /**
     * 记录搜索历史
     */
    private List<String> mHistoryList = new ArrayList<>();
    private TagAdapter<String> mTagSearchHistoryAdapter;

    private void setHistory() {

        if (mHistoryList == null) {
            mHistoryList = new ArrayList<>();
        }
        mHistoryList.clear();

        for (int i = 0; i < 20; i++) {
            String string = SPUtils.getInstance().getString(BaseApplication.getApplication(), i + "", "");
            if (TextUtils.isEmpty(string)) {
                continue;
            }
            mHistoryList.add(string);
        }

        if (mTagSearchHistoryAdapter != null) {
            mTagSearchHistoryAdapter = null;
        }
        if (mTagSearchHistoryAdapter == null) {
            mTagSearchHistoryAdapter = new TagAdapter<String>(mHistoryList) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_search,
                            mFlowSearchLately, false);
                    tv.setText(s);
                    return tv;
                }
            };
            mFlowSearchLately.setAdapter(mTagSearchHistoryAdapter);
            mFlowSearchLately.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                @Override
                public void onSelected(Set<Integer> selectPosSet) {
                    //搜索
                    Iterator<Integer> iterator = selectPosSet.iterator();
                    if (iterator.hasNext()){
                        startActivity(mHistoryList.get(iterator.next()));
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {//返回
            finish();
        } else if (id == R.id.iv_search_delete) {
            for (int i = 0; i < 20; i++) {
                SPUtils.getInstance().putString(BaseApplication.getApplication(), i + "", "");
            }
            setHistory();
        } else if (id == R.id.tv_search) {//搜索
            startActivity(mEtSearch.getText().toString().trim());
        } else if (id == R.id.tv_search_channel) {//展示选框
            mCommonPopupwind.showPopupwind(mCommonPopupwind);
        } else if (id == R.id.tv_search_app) {//选择APP
            changePopupwindTvStyle(R.string.s_app);
        } else if (id == R.id.tv_search_taobao) {//选择淘宝
            changePopupwindTvStyle(R.string.s_tb);
        } else if (id == R.id.tv_search_jingdong) {//选择京东
            changePopupwindTvStyle(R.string.s_jd);
        } else if (id == R.id.tv_search_pinduoduo) {//选择拼多多
            changePopupwindTvStyle(R.string.s_pdd);
        }else if (id == R.id.iv_search_clear){
            mEtSearch.setText("");
        }
    }

    private void startActivity(String searchTxt) {
        if (TextUtils.isEmpty(searchTxt)){
            toast("搜索内容不能为空！");
            return;
        }

        //搜索
        ARouter.getInstance().build(ARouters.PATH_SEARCH_RESULT)
                .withString(Constant.CHANNEL, mTvSearchChannel.getText().toString())
                .withString(Constant.SEARCHTEXT, searchTxt)
                .navigation();
        setHistoryList(searchTxt);
    }

    private void setHistoryList(String searchTxt) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < mHistoryList.size(); i++) {
            if (TextUtils.equals(searchTxt, mHistoryList.get(i))) {
                continue;
            }
            list.add(mHistoryList.get(i));
        }

        list.add(0, searchTxt);
        mHistoryList = list;
        for (int i = 0; i < mHistoryList.size(); i++) {
            SPUtils.getInstance().putString(BaseApplication.getApplication(), i + "", mHistoryList.get(i));
        }


    }

    /**
     * 更改弹出框样式
     *
     * @param tvId
     */
    private void changePopupwindTvStyle(@StringRes int tvId) {
        mTvSearchChannel.setText(tvId);
        if (tvId == R.string.s_app) {
            mTvSearchApp.setTextColor(getResources().getColor(R.color.c_main));
            mTvSearchTB.setTextColor(getResources().getColor(R.color.c_343434));
            mTvSearchJD.setTextColor(getResources().getColor(R.color.c_343434));
            mTvSearchPDD.setTextColor(getResources().getColor(R.color.c_343434));
        } else if (tvId == R.string.s_tb) {
            mTvSearchApp.setTextColor(getResources().getColor(R.color.c_343434));
            mTvSearchTB.setTextColor(getResources().getColor(R.color.c_main));
            mTvSearchJD.setTextColor(getResources().getColor(R.color.c_343434));
            mTvSearchPDD.setTextColor(getResources().getColor(R.color.c_343434));

        } else if (tvId == R.string.s_jd) {
            mTvSearchApp.setTextColor(getResources().getColor(R.color.c_343434));
            mTvSearchTB.setTextColor(getResources().getColor(R.color.c_343434));
            mTvSearchJD.setTextColor(getResources().getColor(R.color.c_main));
            mTvSearchPDD.setTextColor(getResources().getColor(R.color.c_343434));
        } else if (tvId == R.string.s_pdd) {
            mTvSearchApp.setTextColor(getResources().getColor(R.color.c_343434));
            mTvSearchTB.setTextColor(getResources().getColor(R.color.c_343434));
            mTvSearchJD.setTextColor(getResources().getColor(R.color.c_343434));
            mTvSearchPDD.setTextColor(getResources().getColor(R.color.c_main));
        }
        if (mCommonPopupwind != null) {
            mCommonPopupwind.dismiss();
        }
    }

    private void stringTransInt(String channel) {
        if (TextUtils.isEmpty(channel)) {
            return;
        }
        int tvId = 0;
        if (channel.equalsIgnoreCase(getResources().getString(R.string.s_app))) {
            tvId = R.string.s_app;
        } else if (channel.equalsIgnoreCase(getResources().getString(R.string.s_tb))) {
            tvId = R.string.s_tb;
        } else if (channel.equalsIgnoreCase(getResources().getString(R.string.s_jd))) {
            tvId = R.string.s_jd;
        } else if (channel.equalsIgnoreCase(getResources().getString(R.string.s_pdd))) {
            tvId = R.string.s_pdd;
        }
        changePopupwindTvStyle(tvId);
    }
}
