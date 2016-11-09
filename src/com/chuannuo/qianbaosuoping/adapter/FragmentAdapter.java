package com.chuannuo.qianbaosuoping.adapter;

import com.chuannuo.qianbaosuoping.MainActivity;
import com.chuannuo.qianbaosuoping.duobao.SnatchFragment;
import com.chuannuo.qianbaosuoping.fragment.EarnFragment;
import com.chuannuo.qianbaosuoping.fragment.ExchangeFragment;
import com.chuannuo.qianbaosuoping.fragment.HomeFragment;
import com.chuannuo.qianbaosuoping.fragment.MeFragment;
import com.chuannuo.qianbaosuoping.fragment.MovieFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:23:04
 * @Description: 底部菜单导航
 */
public class FragmentAdapter extends FragmentPagerAdapter{
	public final static int TAB_COUNT = 6;
	public FragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int id) {
		switch (id) {
		case MainActivity.TAB_HOME:
			HomeFragment homeFragment = new HomeFragment();
			return homeFragment;
		case MainActivity.TAB_EXCHANGE:
			ExchangeFragment exchangeFragment = new ExchangeFragment();
			return exchangeFragment;
		case MainActivity.TAB_EARN:
			EarnFragment earnFragment = new EarnFragment();
			return earnFragment;
		case MainActivity.TAB_SNATCH:
			SnatchFragment snatchFragment = new SnatchFragment();
			return snatchFragment;
		case MainActivity.TAB_ME:
			MeFragment meFragment = new MeFragment();
			return meFragment;
		case MainActivity.TAB_MORE:
			MovieFragment moreeFragment = new MovieFragment();
			return moreeFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		return TAB_COUNT;
	}
	
}
