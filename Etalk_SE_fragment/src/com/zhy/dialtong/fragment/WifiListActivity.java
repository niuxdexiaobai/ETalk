package com.zhy.dialtong.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.zhy.dialtong.R;
import com.zhy.dialtong.fragment.more.MyListView;
import com.zhy.dialtong.fragment.more.MyListView.OnRefreshListener;
import com.zhy.dialtong.fragment.more.MyListViewAdapter;
import com.zhy.dialtong.fragment.more.OnNetworkChangeListener;
import com.zhy.dialtong.fragment.more.WifiAdmin;
import com.zhy.dialtong.fragment.more.WifiConnDialog;
import com.zhy.dialtong.fragment.more.WifiStatusDialog;

public class WifiListActivity extends Activity{
	
	protected static final String TAG = WifiListActivity.class.getSimpleName();

	private static final int REFRESH_CONN = 100;

	private static final int REQ_SET_WIFI = 200;

	// Wifi������
	private WifiAdmin mWifiAdmin;
	// ɨ�����б�
	private List<ScanResult> list = new ArrayList<ScanResult>();
	// ��ʾ�б�
	private MyListView listView;
	private ToggleButton tgbWifiSwitch;

	private MyListViewAdapter mAdapter;

	private OnNetworkChangeListener mOnNetworkChangeListener = new OnNetworkChangeListener() {

		@Override
		public void onNetWorkDisConnect() {
			getWifiListInfo();
			mAdapter.setDatas(list);
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onNetWorkConnect() {
			getWifiListInfo();
			mAdapter.setDatas(list);
			mAdapter.notifyDataSetChanged();
		}
	};

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wifi_list);

		initData();
		initView();
		setListener();

		refreshWifiStatusOnTime();

	}

	private void initData() {
		mWifiAdmin = new WifiAdmin(WifiListActivity.this);
		// ���Wifi�б���Ϣ
		getWifiListInfo();
	}

	private void initView() {

		tgbWifiSwitch = (ToggleButton) findViewById(R.id.tgb_wifi_switch);
		listView = (MyListView) findViewById(R.id.freelook_listview);
		mAdapter = new MyListViewAdapter(this, list);
		listView.setAdapter(mAdapter);
		//
		int wifiState = mWifiAdmin.checkState();
		if (wifiState == WifiManager.WIFI_STATE_DISABLED
				|| wifiState == WifiManager.WIFI_STATE_DISABLING
				|| wifiState == WifiManager.WIFI_STATE_UNKNOWN) {
			tgbWifiSwitch.setChecked(false);
		} else {
			tgbWifiSwitch.setChecked(true);
		}
	}

	private void setListener() {

		tgbWifiSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Log.w(TAG, "======== open wifi ========");
					// ��Wifi
					mWifiAdmin.openWifi();
				} else {
					Log.w(TAG, "======== close wifi ========");
					// �ر�Wifi
					boolean res = mWifiAdmin.closeWifi();
					if (!res) {
						gotoSysCloseWifi();
					}
				}
			}
		});

		// ����ˢ�¼���
		listView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {

				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						getWifiListInfo();
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mAdapter.setDatas(list);
						mAdapter.notifyDataSetChanged();
						listView.onRefreshComplete();
					}

				}.execute();
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				int position = pos - 1;
				// String wifiName = list.get(position).SSID;
				// String singlStrength = "" + list.get(position).frequency;
				// String secuString = list.get(position).capabilities;

				ScanResult scanResult = list.get(position);

				if (mWifiAdmin.isConnect(scanResult)) {
					// �����ӣ���ʾ����״̬�Ի���
					WifiStatusDialog mStatusDialog = new WifiStatusDialog(
							WifiListActivity.this, R.style.PopDialog,
							scanResult, mOnNetworkChangeListener);
					mStatusDialog.show();
				} else {
					// δ������ʾ��������Ի���
					WifiConnDialog mDialog = new WifiConnDialog(
							WifiListActivity.this, R.style.PopDialog,
							scanResult, mOnNetworkChangeListener);
					// WifiConnDialog mDialog = new WifiConnDialog(
					// WifiListActivity.this, R.style.PopDialog, wifiName,
					// singlStrength, secuString);
					mDialog.show();
				}
			}
		});
	}

	private void getWifiListInfo() {
		System.out.println("WifiListActivity#getWifiListInfo");
		mWifiAdmin.startScan();
		List<ScanResult> tmpList = mWifiAdmin.getWifiList();
		if (tmpList == null) {
			list.clear();
		} else {
			list = tmpList;
		}
	}

	private Handler mHandler = new MyHandler(this);

	protected boolean isUpdate = true;

	private static class MyHandler extends Handler {

		private WeakReference<WifiListActivity> reference;

		public MyHandler(WifiListActivity activity) {
			this.reference = new WeakReference<WifiListActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			WifiListActivity activity = reference.get();

			switch (msg.what) {
			case REFRESH_CONN:
				activity.getWifiListInfo();
				activity.mAdapter.setDatas(activity.list);
				activity.mAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}
	}

	/**
	 * Function:��ʱˢ��Wifi�б���Ϣ<br>
	 * 
	 * @author ZYT DateTime 2014-5-15 ����9:14:34<br>
	 * <br>
	 */
	private void refreshWifiStatusOnTime() {
		new Thread() {
			public void run() {
				while (isUpdate) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mHandler.sendEmptyMessage(REFRESH_CONN);
				}
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isUpdate = false;
	}

	/**
	 * Function:��ϵͳ������wifi������û��ֶ��ر�ʧ�ܣ���ת��ϵͳ�н��йر�Wifi<br>
	 * 
	 * @author ZYT DateTime 2014-5-15 ����10:03:15<br>
	 * <br>
	 */
	private void gotoSysCloseWifi() {
		// 05-15 09:57:56.351: I/ActivityManager(397): START
		// {act=android.settings.WIFI_SETTINGS flg=0x14000000
		// cmp=com.android.settings/.Settings$WifiSettingsActivity} from pid 572

		Intent intent = new Intent("android.settings.WIFI_SETTINGS");
		intent.setComponent(new ComponentName("com.android.settings",
				"com.android.settings.Settings$WifiSettingsActivity"));
		startActivityForResult(intent, REQ_SET_WIFI);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQ_SET_WIFI:
			// ����ı�wifi״̬���
			//
			int wifiState = mWifiAdmin.checkState();
			if (wifiState == WifiManager.WIFI_STATE_DISABLED
					|| wifiState == WifiManager.WIFI_STATE_DISABLING
					|| wifiState == WifiManager.WIFI_STATE_UNKNOWN) {
				tgbWifiSwitch.setChecked(false);
			} else {
				tgbWifiSwitch.setChecked(true);
			}
			break;

		default:
			break;
		}
	}

}
