package com.znt.vodbox.fragment.first;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.ShopLoadMoreAdapter;
import com.znt.vodbox.model.Shopinfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends Fragment {


    public static final String ARGS_PAGE = "args_page";
    private TextView tv;

    private List<Shopinfo> dataList;
    private ShopLoadMoreAdapter adapter;
    private RecyclerView rv;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static PageFragment newInstance() {
        PageFragment fragment = new PageFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_page, container, false);
        initView();
        return view;
    }

    public void initView() {
        dataList = new ArrayList<Shopinfo>();
        adapter = new ShopLoadMoreAdapter(getContext(), dataList);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setRefreshing(true);

        seeRefresh();
    }

    //设置刷新
    public void seeRefresh() {
        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        //监听刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


            }
        });
    }



}
