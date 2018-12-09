package com.znt.vodbox.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.OnlineMusicActivity;
import com.znt.vodbox.activity.SearchMusicActivity;
import com.znt.vodbox.adapter.SheetAdapter;
import com.znt.vodbox.application.AppCache;
import com.znt.vodbox.constants.Extras;
import com.znt.vodbox.model.SheetInfo;
import com.znt.vodbox.utils.binding.Bind;
import com.znt.vodbox.view.xlistview.LJListView;

import java.util.ArrayList;
import java.util.List;


/**
 * 在线音乐
 * Created by wcy on 2015/11/26.
 */
public class SheetListFragment extends BaseFragment implements LJListView.IXListViewListener, AdapterView.OnItemClickListener {
    @Bind(R.id.lv_sheet)
    private LJListView listView = null;
    private SheetAdapter adapter = null;
    private List<SheetInfo> mSongLists = new ArrayList<>();

    public static SheetListFragment INSTANCE = null;

    public static SheetListFragment newInstance()
    {
        if(INSTANCE == null)
            INSTANCE = new SheetListFragment();
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sheet_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
        listView.getListView().setDividerHeight(1);
        listView.setPullLoadEnable(true,"");
        listView.setPullRefreshEnable(true);
        listView.setIsAnimation(true);
        listView.setXListViewListener(this);
        listView.showFootView(false);
        listView.setRefreshTime();
        listView.setOnItemClickListener(this);
        adapter = new SheetAdapter(mSongLists);
        listView.setAdapter(adapter);

        loadData();
    }

    private void loadData()
    {
        mSongLists = AppCache.get().getSheetList();
        if (mSongLists.isEmpty()) {
            String[] titles = getResources().getStringArray(R.array.online_music_list_title);
            String[] types = getResources().getStringArray(R.array.online_music_list_type);
            for (int i = 0; i < titles.length; i++) {
                SheetInfo info = new SheetInfo();
                info.setTitle(titles[i]);
                info.setType(types[i]);
                mSongLists.add(info);
            }
        }
        adapter.setDataList(mSongLists);
        listView.stopRefresh();
    }

    public void goSearchMusicActivity(Context activity)
    {
        startActivity(new Intent(activity, SearchMusicActivity.class));
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SheetInfo sheetInfo = mSongLists.get(position);
        Intent intent = new Intent(getContext(), OnlineMusicActivity.class);
        intent.putExtra(Extras.MUSIC_LIST_TYPE, sheetInfo);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*int position = lvPlaylist.getFirstVisiblePosition();
        int offset = (lvPlaylist.getChildAt(0) == null) ? 0 : lvPlaylist.getChildAt(0).getTop();
        outState.putInt(Keys.PLAYLIST_POSITION, position);
        outState.putInt(Keys.PLAYLIST_OFFSET, offset);*/
    }

   /* public void onRestoreInstanceState(final Bundle savedInstanceState) {
        lvPlaylist.post(() -> {
            int position = savedInstanceState.getInt(Keys.PLAYLIST_POSITION);
            int offset = savedInstanceState.getInt(Keys.PLAYLIST_OFFSET);
            lvPlaylist.setSelectionFromTop(position, offset);
        });
    }*/

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onLoadMore() {

    }
}
