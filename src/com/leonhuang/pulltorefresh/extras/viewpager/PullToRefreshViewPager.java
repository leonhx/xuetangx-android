package com.leonhuang.pulltorefresh.extras.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.leonhuang.pulltorefresh.library.PullToRefreshBase;
import com.leonhuang.xuetangx.R;

public class PullToRefreshViewPager extends PullToRefreshBase<ViewPager> {

	public PullToRefreshViewPager(Context context) {
		super(context);
	}

	public PullToRefreshViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.HORIZONTAL;
	}

	@Override
	protected ViewPager createRefreshableView(Context context, AttributeSet attrs) {
		ViewPager viewPager = new ViewPager(context, attrs);
		viewPager.setId(R.id.viewpager);
		return viewPager;
	}

	@Override
	protected boolean isReadyForPullStart() {
		ViewPager refreshableView = getRefreshableView();

		PagerAdapter adapter = refreshableView.getAdapter();
		if (null != adapter) {
			return refreshableView.getCurrentItem() == 0;
		}

		return false;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		ViewPager refreshableView = getRefreshableView();

		PagerAdapter adapter = refreshableView.getAdapter();
		if (null != adapter) {
			return refreshableView.getCurrentItem() == adapter.getCount() - 1;
		}

		return false;
	}
}
