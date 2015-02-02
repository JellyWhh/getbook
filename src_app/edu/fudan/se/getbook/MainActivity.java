/**
 * 
 */
package edu.fudan.se.getbook;

import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.getbook.fragment.MainFragment;
import edu.fudan.se.getbook.initial.GBApplication;
import jade.core.MicroRuntime;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * @author whh
 * 
 */
public class MainActivity extends FragmentActivity {
	private MyReceiver myReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			// 休眠5s是为了让agent能够启动起来，不然在MessageFragment里得不到agent的引用

			try {
				MicroRuntime.getAgent(
						((GBApplication) getApplication()).getAgentNickname())
						.getO2AInterface(AideAgentInterface.class);
			} catch (StaleProxyException e) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (ControllerException e) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.add(R.id.container, new MainFragment()).commit();
		}

		// 处理agent弹窗相关
		myReceiver = new MyReceiver();
		IntentFilter refreshChatFilter = new IntentFilter();
		refreshChatFilter.addAction("jade.task.NOTIFICATION");
		registerReceiver(myReceiver, refreshChatFilter);

	}

	@Override
	public void onBackPressed() {
		// 点击返回键后不会退出程序，也就是再次进来的时候还是原来的运行状态
		this.moveTaskToBack(true);
		return;
	}

	/**
	 * 用来监听agent发来的弹窗UI的消息
	 * 
	 * @author whh
	 * 
	 */
	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			NotificationUtil mNotificationUtil = new NotificationUtil(context);
			String action = intent.getAction();
			if (action.equalsIgnoreCase("jade.task.NOTIFICATION")) {
				mNotificationUtil.showNotification("New Task", intent
						.getExtras().getString("Content"),
						"New Task From SGM!", 100);
			}
		}
	}

}