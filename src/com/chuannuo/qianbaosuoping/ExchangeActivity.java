package com.chuannuo.qianbaosuoping;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.chuannuo.qianbaosuoping.R;
import com.chuannuo.qianbaosuoping.common.Constant;
import com.chuannuo.qianbaosuoping.common.HttpUtil;
import com.chuannuo.qianbaosuoping.view.CustomDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author alan.xie
 * @date 2014-10-14 ����12:17:57
 * @Description: �һ�
 */
public class ExchangeActivity extends BaseActivity {

	private CustomDialog mDialog; // �Ի���
	private String type; // �һ�����
	private int xbNum; // ��������
	private String title; // �һ�����

	private TextView tv_ex_title; // �һ�����
	private TextView tv_ex_type; // �һ�����
	private TextView tv_ex_account; // �һ��˺ţ�Q�ң�QQ,֧���������ѣ��Ƹ�ͨ
	private TextView tv_ex_xb; // �һ���Ҫ������
	private LinearLayout ll_ex_confirm; // ȷ�϶һ�

	private CustomDialog dialog; // �һ��ɹ���ʾ��

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange);

		initView();
		initData();

	}

	public void initView() {
		tv_ex_title = (TextView) findViewById(R.id.tv_ex_title);
		tv_ex_type = (TextView) findViewById(R.id.tv_ex_type);
		tv_ex_account = (TextView) findViewById(R.id.tv_ex_account);
		tv_ex_xb = (TextView) findViewById(R.id.tv_ex_xb);
		ll_ex_confirm = (LinearLayout) findViewById(R.id.ll_ex_confirm);

	}

	/**
	 * @author alan.xie
	 * @date 2014-10-28 ����4:37:23
	 * @Description: ��ʼ������
	 * @param
	 * @return void
	 */
	public void initData() {
		Intent intent = getIntent();
		type = intent.getStringExtra(Constant.TYPE);
		xbNum = intent.getIntExtra(Constant.XB_NUM, 0);
		title = intent.getStringExtra(Constant.TITLE);
		tv_ex_title.setText(title);
		// ���û�а��ֻ���
		if (!isBindingPhone()) {
			initDialog(Constant.PHONE);
		} else if (type.equals(Constant.ZFB)) {

			// ���û�а�֧����
			if (!isBindingZFB()) {
				initDialog(Constant.ZFB);
			}

			tv_ex_type.setText(getResources().getString(R.string.alipay));
			tv_ex_xb.setText(xbNum / 10000 + "��");
			tv_ex_account.setText(pref.getString(Constant.ZFB, ""));
		} else if (type.equals(Constant.PHONE)) {

			// ���û�а��ֻ���
			if (!isBindingPhone()) {
				initDialog(Constant.PHONE);
			}

			tv_ex_type.setText(getResources().getString(R.string.phone_number));
			tv_ex_xb.setText(xbNum / 10000 + "��");
			tv_ex_account.setText(pref.getString(Constant.PHONE, ""));
		} else if (type.equals(Constant.CFT)) {

			// ���û�а󶨲Ƹ�ͨ
			if (!isBindingCFT()) {
				initDialog(Constant.CFT);
			}

			tv_ex_type.setText(getResources().getString(R.string.caifutong));
			tv_ex_xb.setText(xbNum / 10000 + "��");
			tv_ex_account.setText(pref.getString(Constant.CFT, ""));
		}

		ll_ex_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exchange(type);
			}
		});
	}

	/**
	 * @author alan.xie
	 * @date 2014-10-23 ����3:05:17
	 * @Description: ��ʼ��������
	 * @param
	 * @return void
	 */
	public void initDialog(final String code) {
		mDialog = new CustomDialog(this, R.style.CustomDialog,
				new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						mDialog.cancel();
						if (code.equals(Constant.PHONE)) {
							Intent intent = new Intent();
							intent.setClass(ExchangeActivity.this,
									BindingPhoneActivity.class);
							ExchangeActivity.this.finish();
							startActivity(intent);
						} else {
							Intent intent = new Intent();
							intent.setClass(ExchangeActivity.this,
									PerfectInfoActivity.class);
							ExchangeActivity.this.finish();
							startActivity(intent);
						}

					}
				}, 1);

		mDialog.setTitle(getResources().getString(R.string.dg_remind_title));

		if (code.equals(Constant.ZFB)) {

			mDialog.setContent(getResources().getString(
					R.string.dg_improve_zfbinfo));
		} else if (code.equals(Constant.CFT)) {

			mDialog.setContent(getResources().getString(
					R.string.dg_improve_cftinfo));
		} else if (code.equals(Constant.PHONE)) {

			mDialog.setContent("���Ȱ��ֻ����ڶһ�");
		}

		mDialog.setBtnStr(getResources().getString(R.string.dg_iknow));
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
	}

	/**
	 * @author alan.xie
	 * @date 2014-10-31 ����3:01:09
	 * @Description: �һ�
	 * @param
	 * @return void
	 */
	public void exchange(String type) {
		RequestParams params = new RequestParams();

		int ex_num = Integer.parseInt(title.replaceAll("[^0-9]", "")); // �һ�����
		String url = "";

		params.put("appid", pref.getString(Constant.APPID, "0"));
		params.put("code", pref.getString(Constant.CODE, ""));
		params.put("count", ex_num);
		params.put("integral", xbNum);

		dialog = new CustomDialog(this, R.style.CustomDialog,
				new CustomDialog.CustomDialogListener() {

					@Override
					public void onClick(View view) {
						dialog.cancel();
						ExchangeActivity.this.finish();
					}
				}, 1);
		dialog.setTitle(getResources().getString(R.string.dg_remind_title));
		dialog.setBtnStr(getResources().getString(R.string.dg_iknow));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		if (type.equals(Constant.QB)) {

			params.put("type", "1");
			url = Constant.EXCHANGE_QB_URL;
		} else if (type.equals(Constant.PHONE)) {

			params.put("type", "2");
			url = Constant.EXCHANGE_PHONE_URL;
		} else if (type.equals(Constant.ZFB)) {

			params.put("type", "3");
			url = Constant.EXCHANGE_ZFB_URL;
		} else if (type.equals(Constant.CFT)) {

			params.put("type", "4");
			url = Constant.EXCHANGE_CFT_URL;
		} else {
			params.put("type", "0");

		}

		HttpUtil.get(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if (response.getInt("code") == 1) {
						dialog.setContent(ExchangeActivity.this.getResources()
								.getString(R.string.dg_exchange_success));
					} else if (response.getInt("code") == 0) {
						dialog.setContent("�ܱ�Ǹ��" + response.getString("info")
								+ "!");
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
				Toast.makeText(ExchangeActivity.this,
						getResources().getString(R.string.sys_remind2),
						Toast.LENGTH_SHORT).show();
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});

	}

}
