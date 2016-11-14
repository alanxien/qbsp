package com.chuannuo.qianbaosuoping.fragment;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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
import android.view.View;
import android.view.View.OnClickListener;
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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author alan.xie
 * @date 2014-10-14 下午12:19:21
 * @Description: 更多
 */
public class MovieFragment extends Fragment implements OnClickListener{
	
	private SharedPreferences pref;
	private Editor editor;
	
	protected EditText query;
    protected ImageButton clearSearch;
    protected InputMethodManager inputMethodManager;
    
    /*
     * 横向滑动tab
     */
    private HorizontalScrollView mColumnHorizontalScrollView;
    private ViewPager mViewPager;
	/** 用户选择的新闻分类列表*/
	private ArrayList<ChannelItem> userChannelList=new ArrayList<ChannelItem>();
	/** 当前选中的栏目*/
	private int columnSelectIndex = 0;
	/** 屏幕宽度 */
	private int mScreenWidth = 0;
	/** Item宽度 */
	private int mItemWidth = 0;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	LinearLayout mRadioGroup_content;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_movie, container, false);
		
		/*
	     * 横向滑动tab
	     */
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mItemWidth = mScreenWidth / 7;
		
		initView(view);
		setChangelView();
		initData();
		return view;
	}
	
	private void initView(View view){
		query = (EditText) view.findViewById(R.id.query);
        clearSearch = (ImageButton) view.findViewById(R.id.search_clear);
        setHint(query, "搜索");
        
        mColumnHorizontalScrollView =  (HorizontalScrollView)view.findViewById(R.id.mColumnHorizontalScrollView);
        mViewPager = (ViewPager) view.findViewById(R.id.mViewPager);
        mRadioGroup_content = (LinearLayout) view.findViewById(R.id.mRadioGroup_content);
        
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
      
	}
	
	protected void setHint(EditText editText, String hint) {
		// 新建一个可以添加属性的文本对象
		SpannableString ss = new SpannableString(hint);
		// 新建一个属性对象,设置文字的大小
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
		// 附加属性到文本
		ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置hint
		editText.setHint(new SpannedString(ss));
	}
	
	/** 
	 *  当栏目项发生变化时候调用
	 * */
	private void setChangelView() {
		initColumnData();
		initTabColumn();
		initFragment();
	}
	/** 获取Column栏目 数据*/
	private void initColumnData() {
		ChannelItem channelItem1 = new ChannelItem(1, "动作", 1, 1);
		ChannelItem channelItem2 = new ChannelItem(2, "喜剧", 2, 0);
		ChannelItem channelItem3 = new ChannelItem(3, "爱情", 3, 0);
		ChannelItem channelItem4 = new ChannelItem(4, "战争", 4, 0);
		ChannelItem channelItem5 = new ChannelItem(5, "科幻", 5, 0);
		ChannelItem channelItem6 = new ChannelItem(6, "恐怖", 6, 0);
		ChannelItem channelItem7 = new ChannelItem(7, "历史", 7, 0);
		ChannelItem channelItem8 = new ChannelItem(8, "武侠", 8, 0);
		ChannelItem channelItem9 = new ChannelItem(9, "美国", 9, 0);
		ChannelItem channelItem10 = new ChannelItem(10, "英国", 10, 0);
		ChannelItem channelItem11 = new ChannelItem(11, "大陆", 11, 0);
		ChannelItem channelItem12 = new ChannelItem(12, "香港", 12, 0);
		ChannelItem channelItem13 = new ChannelItem(13, "韩国", 13, 0);
		ChannelItem channelItem14 = new ChannelItem(14, "日本", 14, 0);
		
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
	}
	
	/** 
	 *  初始化Column栏目项
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
			          Toast.makeText(getActivity(), userChannelList.get(v.getId()).getName(), Toast.LENGTH_SHORT).show();
				}
			});
			mRadioGroup_content.addView(columnTextView, i ,params);
		}
	}
	
	/** 
	 *  初始化Fragment
	 * */
	private void initFragment() {
		fragments.clear();//清空
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
		//判断是否选中
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
	
	private void initData(){
		
		query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            	//conversationListView.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });
	}
	
	protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}











