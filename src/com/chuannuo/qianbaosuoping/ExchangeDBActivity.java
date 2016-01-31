package com.chuannuo.qianbaosuoping;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.view.CustomDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author alan.xie
 * @date 2014-10-14 ÏÂÎç12:17:57
 * @Description: ¶á±¦±Ò¶Ò»»
 */
public class ExchangeDBActivity extends BaseActivity {

	private int xbNum; // »ý·Ö
	private TextView tv_ex_title;
	private EditText et_ex_account;
	private LinearLayout ll_ex_confirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange_qb);
		tv_ex_title = (TextView) findViewById(R.id.tv_ex_title);
		et_ex_account = (EditText) findViewById(R.id.et_ex_account);
		ll_ex_confirm = (LinearLayout) findViewById(R.id.ll_ex_confirm);

		xbNum = pref.getInt(Constant.SCORE, 0);
		tv_ex_title.setText("µ±Ç°¿É¶Ò»»" + xbNum / 100000 + "¶á±¦±Ò");
		et_ex_account.setText(xbNum / 100000+"");
		et_ex_account.addTextChangedListener(textWatcher);

		if(xbNum / 100000 <= 0){
			ll_ex_confirm.setVisibility(View.GONE);
		}else{
			ll_ex_confirm.setVisibility(View.VISIBLE);
			ll_ex_confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					exchange();
				}
			});
		}
		

	}

	/**
	 * @Title: exchange
	 * @Description: TODO
	 * @author xie.xin
	 * @param
	 * @return void
	 * @throws
	 */
	protected void exchange() {
		RequestParams params = new RequestParams();
		params.put("appid", pref.getString(Constant.APPID, "0"));
		params.put("code", pref.getString(Constant.CODE, ""));
		params.put("count", et_ex_account.getText());
		params.put("integral",
				Integer.parseInt(et_ex_account.getText().toString()) * 100000);
		params.put("type", "1");

		dialog = new CustomDialog(this, R.style.CustomDialog,
				new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						dialog.cancel();
						ExchangeDBActivity.this.finish();
					}
				}, 1);
		dialog.setTitle(getResources().getString(R.string.dg_remind_title));
		dialog.setBtnStr(getResources().getString(R.string.dg_iknow));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		HttpUtil.get(Constant.EXCHANGE_QB_URL, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							if (response.getInt("code") == 1) {
								dialog.setContent(ExchangeDBActivity.this
										.getResources().getString(
												R.string.dg_exchange_success));
							} else if (response.getInt("code") == 0) {
								dialog.setContent("ºÜ±§Ç¸£¬"
										+ response.getString("info") + "!");
							}
							dialog.show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Toast.makeText(ExchangeDBActivity.this,
								getResources().getString(R.string.sys_remind2),
								Toast.LENGTH_SHORT).show();
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			int len = s.toString().length();
			String t = s.toString();
			if (len == 1 && t.equals("0")) {
				s.clear();
				s.append("1");
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}
	};

}
