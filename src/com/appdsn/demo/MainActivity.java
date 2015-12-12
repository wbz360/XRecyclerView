package com.appdsn.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.appdsn.xrecyclerview.LoadMoreRecyclerView;
import com.appdsn.xrecyclerview.LoadMoreRecyclerView.OnLoadListener;
import com.appdsn.xrecyclerview.R;
import com.appdsn.xrecyclerview.XRecyclerAdapter;

public class MainActivity extends Activity {

	private ArrayList<String> filmListDatas;
	private XRecyclerAdapter<String> filmListAdapter;
	private LoadMoreRecyclerView recyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		filmListDatas = new ArrayList<String>();
		recyclerView = (LoadMoreRecyclerView) findViewById(R.id.recyclerView);
		loadData();
	}

	protected void loadData() {
		for (int i = 0; i < 20; i++) {
			filmListDatas.add("1");
		}
		/*如果只有一种类型的item，可以使用CommonRecyclerAdapter，更加的方便*/
		filmListAdapter = new XRecyclerAdapter<String>(this,
				filmListDatas) {

			@Override
			public void convert(BaseViewHolder holder, String itemData,
					int realPosition) {
				// TODO Auto-generated method stub

			}

			@Override
			public int getItemType(int realPosition) {
				// TODO Auto-generated method stub

				return realPosition % 5;
			}

			@Override
			public XRecyclerAdapter.BaseViewHolder onCreateHolder(
					ViewGroup parent, int itemType) {
				// TODO Auto-generated method stub
				BaseViewHolder holder = null;
				if (itemType == 0) {
					holder = new BaseViewHolder(mInflater.inflate(
							R.layout.list_item_group, parent, false));
				} else {
					holder = new BaseViewHolder(mInflater.inflate(
							R.layout.list_item_child, parent, false));
				}

				return holder;
			}
		};

		recyclerView.setAdapter(filmListAdapter);
		
		/*添加头部view*/
		ImageView headerView = new ImageView(this);
		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		headerView.setScaleType(ScaleType.CENTER_CROP);
		headerView.setLayoutParams(params);
		headerView.setImageResource(R.drawable.banner);
		filmListAdapter.addHeaderView(headerView);

		/*设置可以加载更多*/
		recyclerView.onLoadSucess(true);
		recyclerView.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad(LoadMoreRecyclerView listView) {
				// TODO Auto-generated method stub
				recyclerView.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						for (int i = 0; i < 5; i++) {
							filmListDatas.add("1");
						}
						filmListAdapter.notifyDataSetChanged();
						if (filmListDatas.size() > 30) {
							recyclerView.onLoadSucess(false);
						} else {
							recyclerView.onLoadSucess(true);
						}
					}
				}, 2000);
			}
		});
	}
}
