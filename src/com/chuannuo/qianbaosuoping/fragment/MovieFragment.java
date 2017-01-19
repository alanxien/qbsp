package com.chuannuo.qianbaosuoping.fragment;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.hScollView.ChannelItem;
import com.chuannuo.qianbaosuoping.hScollView.ColumnHorizontalScrollView;
import com.chuannuo.qianbaosuoping.hScollView.NewsFragment;
import com.chuannuo.qianbaosuoping.hScollView.NewsFragmentPagerAdapter;
import com.chuannuo.qianbaosuoping.movie.SearchMovieActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author alan.xie
 * @date 2014-10-14 ����12:19:21
 * @Description: ����
 */
public class MovieFragment extends Fragment implements OnClickListener{
	
	private SharedPreferences pref;
	private Editor editor;
	
	protected EditText query;
    protected ImageButton clearSearch;
    protected InputMethodManager inputMethodManager;
    
    /*
     * ���򻬶�tab
     */
    private HorizontalScrollView mColumnHorizontalScrollView;
    private ViewPager mViewPager;
	/** �û�ѡ������ŷ����б�*/
	private ArrayList<ChannelItem> userChannelList=new ArrayList<ChannelItem>();
	/** ��ǰѡ�е���Ŀ*/
	private int columnSelectIndex = 0;
	/** ��Ļ��� */
	private int mScreenWidth = 0;
	/** Item��� */
	private int mItemWidth = 0;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	LinearLayout mRadioGroup_content;
	private int  touch_flag  = 0;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_movie, container, false);
		
		/*
	     * ���򻬶�tab
	     */
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mItemWidth = mScreenWidth / 7;
		
		initView(view);
		setChangelView();
		return view;
	}
	
	private void initView(View view){
		query = (EditText) view.findViewById(R.id.query);
        clearSearch = (ImageButton) view.findViewById(R.id.search_clear);
        setHint(query, "����");
        
        mColumnHorizontalScrollView =  (HorizontalScrollView)view.findViewById(R.id.mColumnHorizontalScrollView);
        mViewPager = (ViewPager) view.findViewById(R.id.mViewPager);
        mRadioGroup_content = (LinearLayout) view.findViewById(R.id.mRadioGroup_content);
        
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        query.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				touch_flag++;
				if(touch_flag==1){
					Intent intent = new Intent(getActivity(),SearchMovieActivity.class);
					startActivity(intent);
					return true;
				}else{
					return false;
				}
				
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		touch_flag=0;
		super.onResume();
	}
	
	protected void setHint(EditText editText, String hint) {
		// �½�һ������������Ե��ı�����
		SpannableString ss = new SpannableString(hint);
		// �½�һ�����Զ���,�������ֵĴ�С
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
		// �������Ե��ı�
		ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// ����hint
		editText.setHint(new SpannedString(ss));
	}
	
	/** 
	 *  ����Ŀ����仯ʱ�����
	 * */
	private void setChangelView() {
		initColumnData();
		initTabColumn();
		initFragment();
	}
	/** ��ȡColumn��Ŀ ����*/
	private void initColumnData() {
		ChannelItem channelItem1 = new ChannelItem(1, "��ѡ", 1, 1);
		ChannelItem channelItem2 = new ChannelItem(2, "����", 2, 0);
		ChannelItem channelItem3 = new ChannelItem(3, "����", 3, 0);
		ChannelItem channelItem4 = new ChannelItem(4, "ϲ��", 4, 0);
		ChannelItem channelItem5 = new ChannelItem(5, "����", 5, 0);
		ChannelItem channelItem6 = new ChannelItem(6, "ս��", 6, 0);
		ChannelItem channelItem7 = new ChannelItem(7, "�ƻ�", 7, 0);
		ChannelItem channelItem8 = new ChannelItem(8, "�ֲ�", 8, 0);
		ChannelItem channelItem9 = new ChannelItem(9, "��ʷ", 9, 0);
		ChannelItem channelItem10 = new ChannelItem(10, "����", 10, 0);
		ChannelItem channelItem11 = new ChannelItem(11, "����", 11, 0);
		ChannelItem channelItem12 = new ChannelItem(12, "Ӣ��", 12, 0);
		ChannelItem channelItem13 = new ChannelItem(13, "��½", 13, 0);
		ChannelItem channelItem14 = new ChannelItem(14, "���", 14, 0);
		ChannelItem channelItem15 = new ChannelItem(15, "����", 15, 0);
		ChannelItem channelItem16 = new ChannelItem(16, "�ձ�", 16, 0);
		ChannelItem channelItem17 = new ChannelItem(17, "���Ӿ�", 17, 0);
		
		userChannelList.add(channelItem1);
		userChannelList.add(channelItem2);
		userChannelList.add(channelItem3);
		userChannelList.add(channelItem4);
		userChannelList.add(channelItem5);
		userChannelList.add(channelItem6);
		userChannelList.add(channelItem7);
		userChannelList.add(channelItem8);
		userChannelList.add(channelItem9);
		userChannelList.add(channelItem10);
		userChannelList.add(channelItem11);
		userChannelList.add(channelItem12);
		userChannelList.add(channelItem13);
		userChannelList.add(channelItem14);
		userChannelList.add(channelItem15);
		userChannelList.add(channelItem16);
		userChannelList.add(channelItem17);
	}
	
	/** 
	 *  ��ʼ��Column��Ŀ��
	 * */
	@SuppressWarnings("deprecation")
	private void initTabColumn() {
		mRadioGroup_content.removeAllViews();
		int count =  userChannelList.size();
		//mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left, shade_right, ll_more_columns, rl_column);
		for(int i = 0; i< count; i++){
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth , LayoutParams.WRAP_CONTENT);
			params.leftMargin = 5;
			params.rightMargin = 5;
			//TextView localTextView = (TextView) mInflater.inflate(R.layout.column_radio_item, null);
			TextView columnTextView = new TextView(getActivity());
			columnTextView.setTextAppearance(getActivity(),R.style.top_category_scroll_view_item_text);
			//localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
			//columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
			columnTextView.setGravity(Gravity.CENTER);
			columnTextView.setPadding(5, 5, 5, 5);
			columnTextView.setId(i);
			columnTextView.setText(userChannelList.get(i).getName());
			columnTextView.setTextColor(getResources().getColorStateList(R.drawable.top_category_scroll_text_color_day));
			if(columnSelectIndex == i){
				columnTextView.setSelected(true);
			}
			columnTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
			          for(int i = 0;i < mRadioGroup_content.getChildCount();i++){
				          View localView = mRadioGroup_content.getChildAt(i);
				          if (localView != v)
				        	  localView.setSelected(false);
				          else{
				        	  localView.setSelected(true);
				        	  mViewPager.setCurrentItem(i);
				          }
			          }
				}
			});
			mRadioGroup_content.addView(columnTextView, i ,params);
		}
	}
	
	/** 
	 *  ��ʼ��Fragment
	 * */
	private void initFragment() {
		fragments.clear();//���
		int count =  userChannelList.size();
		for(int i = 0; i< count;i++){
			Bundle data = new Bundle();
    		data.putString("title", userChannelList.get(i).getName());
    		data.putInt("id", userChannelList.get(i).getId());
			NewsFragment newfragment = new NewsFragment();
			newfragment.setArguments(data);
			fragments.add(newfragment);
		}
		//getActivity().getSupportFragmentManager()
		NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(getChildFragmentManager(), fragments);
//		mViewPager.setOffscreenPageLimit(0);
		mViewPager.setAdapter(mAdapetr);
		mViewPager.setOnPageChangeListener(pageListener);
	}
	
	public OnPageChangeListener pageListener= new OnPageChangeListener(){

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};
	
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			// rg_nav_content.getParent()).smoothScrollTo(i2, 0);
			mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
			// mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
			// mItemWidth , 0);
		}
		//�ж��Ƿ�ѡ��
		for (int j = 0; j <  mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		
	}
}











