package com.appdsn.xrecyclerview;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;

public  abstract class CommonRecyclerAdapter<Model> extends XRecyclerAdapter<Model> {
	protected int mItemLayoutId=-1;
	
	public CommonRecyclerAdapter(Context context, List<Model> mDatas, int itemLayoutId) {
		super(context, mDatas);
		this.mItemLayoutId = itemLayoutId;
	}
	
	@Override
	public abstract void convert(BaseViewHolder holder,Model itemData, int realPosition);


	@Override
	public int getItemType(int realPosition) {
		// TODO Auto-generated method stub
		return XRecyclerAdapter.TYPE_NORMAL;//0
	}

	@Override
	public BaseViewHolder onCreateHolder(
			ViewGroup parent, int itemType) {
	
		return new BaseViewHolder(mInflater.inflate(
				mItemLayoutId, parent, false));
	}

}
