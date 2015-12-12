package com.appdsn.xrecyclerview;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadMoreRecyclerView extends RecyclerView {

	/**
	 * 下拉刷新和加载更多的监听器
	 */
	private OnLoadListener mLoadListener;
	/**
	 * 用于滑到底部自动加载的Footer
	 */
	private View mAutoLoadingLayout;
	private boolean hasMoreData = true;
	private ProgressBar mProgressBar;
	private TextView mHintView;
	/**
	 * 当前的状态
	 */
	private State mCurState = State.NORMAL;
	/**
	 * 前一个状态
	 */
	private State mPreState = State.NORMAL;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public LoadMoreRecyclerView(Context context) {
		this(context, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 * @param defStyle
	 *            defStyle
	 */
	public LoadMoreRecyclerView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);// 调用基类构造方法，初始化一些配置方法
		// 设置Footer
		mAutoLoadingLayout = LayoutInflater.from(context).inflate(
				R.layout.layout_load_more_footer, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mAutoLoadingLayout.setLayoutParams(params);
		mProgressBar = (ProgressBar) mAutoLoadingLayout
				.findViewById(R.id.loadProgress);
		mHintView = (TextView) mAutoLoadingLayout.findViewById(R.id.loadHint);
		onStateChanged(State.UNAVAILABLE);// 首次进入一个界面是刷新，加载更多设置为不可用，需要隐藏footer
		setLayoutManager(new LinearLayoutManager(context));//默认值
		//设置Item增加、移除动画
		setItemAnimator(new DefaultItemAnimator());
		//添加分割线
		addItemDecoration(new DividerItemDecoration(
				context, DividerItemDecoration.VERTICAL_LIST));
		setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				// TODO Auto-generated method stub
				super.onScrolled(recyclerView, dx, dy);
				
				if (isLastItemVisible()) {
					if (hasMoreData && mCurState != State.LOADING
							&& mCurState != State.FAILED
							&& mCurState != State.UNAVAILABLE) {
						startLoading();// 开始加载
					}
				}

			}
			
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				// TODO Auto-generated method stub
				super.onScrollStateChanged(recyclerView, newState);
//				if (isLastItemVisible()) {
//					LogUtils.i("123", "isLastItemVisible");
//					if (hasMoreData && mCurState != State.LOADING
//							&& mCurState != State.FAILED
//							&& mCurState != State.UNAVAILABLE) {
//						startLoading();// 开始加载
//					}
//				}
				
			}
		});

	}

	
	@Override
	public void setAdapter(Adapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
		if (adapter instanceof XRecyclerAdapter) {
			XRecyclerAdapter commonRecyclerAdapter = (XRecyclerAdapter) adapter;
			commonRecyclerAdapter.addFooterView(mAutoLoadingLayout);
		}

		
	}

	protected void startLoading() {

		if (null != mAutoLoadingLayout && mLoadListener != null) {
			onStateChanged(State.LOADING);// 设置底部状态，正在加载
			mLoadListener.onLoad(this);
		}
	}

	// 加载成功后，调用改变footer状态
	public void onLoadSucess(boolean hasMoreData) {

		this.hasMoreData = hasMoreData;
		if (hasMoreData) {
			onStateChanged(State.NORMAL);// 设置底部状态，完成后隐藏
			postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (isLastItemVisible()) {
						
						if (mCurState != State.LOADING
								&& mCurState != State.FAILED
								&& mCurState != State.UNAVAILABLE) {
							startLoading();// 开始加载
						}
					}
				}
			}, 0);
			
		} else {
			onStateChanged(State.NO_MORE_DATA);// 设置底部状态，完成后隐藏
		}
	}

	// 加载失败后，调用改变footer状态
	public void onLoadFailed() {

		this.hasMoreData = true;
		onStateChanged(State.FAILED);// 设置底部状态，完成后隐藏
	}

	// 没有数据时，隐藏footer，并设置为不可用
	public void onLoadUnavailable() {

		this.hasMoreData = true;
		onStateChanged(State.UNAVAILABLE);// 设置底部状态，完成后隐藏
	}

	// 设置刷新的监听器
	public void setOnLoadListener(OnLoadListener loadListener) {
		mLoadListener = loadListener;
	}

	/**
	 * 判断最后一个child是否完全显示出来 判断listview是否滑动到底部
	 * 
	 * @return true完全显示出来，否则false
	 */
	private boolean isLastItemVisible() {

		RecyclerView.LayoutManager layoutManager = getLayoutManager();
		int lastVisibleItemPosition = getLastVisibleItemPosition(layoutManager);
		int visibleItemCount = layoutManager.getChildCount();
		int totalItemCount = layoutManager.getItemCount();
		if (totalItemCount == 0) {// 這裡主要判断数据还没有加载进来时，不可以上拉，只能下拉刷新
			return false;
		}
	
		if (lastVisibleItemPosition == totalItemCount-1) {// 当最后一项可见时
			int index = visibleItemCount - 1;
			View lastVisibleChild = this.getChildAt(index);
//			LogUtils.i("123", lastVisibleChild.getBottom()+"-"+this.getBottom());
			if (lastVisibleChild != null) {
				return lastVisibleChild.getBottom() <= this.getBottom();// 仅试验有可能小于一个像素值，就是下线条所占据的。每个item都有一个下线条，没有上线条
			}
		}

		return false;
	}

	private int getLastVisibleItemPosition(
			RecyclerView.LayoutManager layoutManager) {
		int lastVisibleItemPosition = -1;
		if (layoutManagerType == null) {
			if (layoutManager instanceof GridLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
			} else if (layoutManager instanceof LinearLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
			} else if (layoutManager instanceof StaggeredGridLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
			} else {
				throw new RuntimeException(
						"Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
			}
		}

		switch (layoutManagerType) {
		case LINEAR:
			lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
					.findLastVisibleItemPosition();
			break;
		case GRID:
			lastVisibleItemPosition = ((GridLayoutManager) layoutManager)
					.findLastVisibleItemPosition();
			break;
		case STAGGERED_GRID:
			StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
			int[] lastScrollPositions = new int[staggeredGridLayoutManager
					.getSpanCount()];
			staggeredGridLayoutManager
					.findLastVisibleItemPositions(lastScrollPositions);
			int max = Integer.MIN_VALUE;
			for (int value : lastScrollPositions) {
				if (value > max)
					max = value;
			}

			lastVisibleItemPosition = max;
			break;
		}
		return lastVisibleItemPosition;
	}

	protected LAYOUT_MANAGER_TYPE layoutManagerType;

	public enum LAYOUT_MANAGER_TYPE {
		LINEAR, GRID, STAGGERED_GRID
	}

	/**
	 * 当状态改变时调用
	 * 
	 * 当前状态 老的状态
	 */
	protected void onStateChanged(State newState) {
		mAutoLoadingLayout.setVisibility(View.VISIBLE);
		mAutoLoadingLayout.setOnClickListener(null);
		mCurState = newState;
		switch (newState) {
		case NORMAL:
			mProgressBar.setVisibility(View.GONE);
			mHintView.setText("上拉可以加载更多");
			break;
		case LOADING:
			mProgressBar.setVisibility(View.VISIBLE);
			mHintView.setText("正在加载更多...");
			break;
		case NO_MORE_DATA:
			mProgressBar.setVisibility(View.GONE);
			mHintView.setText("没有更多了");
			break;
		case UNAVAILABLE:
			mAutoLoadingLayout.setVisibility(View.INVISIBLE);
			break;
		case FAILED:
			mProgressBar.setVisibility(View.GONE);
			mHintView.setText("加载更多失败，点击重新加载");
			mAutoLoadingLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoading();
				}
			});
			break;
		default:
			break;
		}
	}

	/**
	 * 当前的状态
	 */
	private enum State {

		/**
		 * No more data
		 */
		NO_MORE_DATA,

		/**
		 * 正常状态，或者初始化的状态
		 */
		NORMAL,

		/**
		 * 加载中
		 */
		LOADING,
		/**
		 * 失败后的状态
		 */
		FAILED,
		/**
		 * 不可用的状态
		 */
		UNAVAILABLE
	}

	public interface OnLoadListener {

		/**
		 * 加载更多时会被调用或上拉时调用，子类实现具体的业务逻
		 */
		void onLoad(LoadMoreRecyclerView listView);
	}
	
}
