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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * @author whh
 *
 */
public class MainActivity extends FragmentActivity {


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
			transaction.add(R.id.container, new MainFragment())
					.commit();
		}

	}

	@Override
	public void onBackPressed() {
		// 点击返回键后不会退出程序，也就是再次进来的时候还是原来的运行状态
		this.moveTaskToBack(true);
		return;
	}

}