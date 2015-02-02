package edu.fudan.se.getbook.initial;

import java.util.logging.Level;

import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import edu.fudan.se.agent.AideAgent;
import edu.fudan.se.getbook.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 应用启动后第一个加载的页面，是连接agent的界面
 * 
 * @author whh
 * 
 */
public class LoginActivity extends Activity {

	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	private MicroRuntimeServiceBinder microRuntimeServiceBinder;
	private ServiceConnection serviceConnection;

	static final int CHAT_REQUEST = 0;

	private String nickname;

	private EditText et_login_input;
	private Button bt_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		et_login_input = (EditText) findViewById(R.id.et_login_input);

		bt_login = (Button) findViewById(R.id.bt_login);
		bt_login.setOnClickListener(loginClickListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindService(serviceConnection);

		logger.log(Level.INFO, "Destroy activity!");
	}

	private OnClickListener loginClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			nickname = et_login_input.getText().toString();
			if (nickname == null || nickname.trim().equals("")) {
				logger.log(Level.INFO, "Invalid nickname!");
			} else {
				((GBApplication) getApplication()).setAgentNickname(nickname);

				SharedPreferences settings = getSharedPreferences(
						"jadeChatPrefsFile", 0);
				String host = settings.getString("defaultHost", "");
				String port = settings.getString("defaultPort", "");
				startChat(nickname, host, port, agentStartupCallback);
			}
		}
	};

	public void startChat(final String nickname, final String host,
			final String port,
			final RuntimeCallback<AgentController> agentStartupCallback) {

		final Properties profile = new Properties();
		profile.setProperty(Profile.MAIN_HOST, host);
		profile.setProperty(Profile.MAIN_PORT, port);
		profile.setProperty(Profile.MAIN, Boolean.FALSE.toString());
		profile.setProperty(Profile.JVM, Profile.ANDROID);

		if (AndroidHelper.isEmulator()) {
			profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
		} else {
			profile.setProperty(Profile.LOCAL_HOST,
					AndroidHelper.getLocalIPAddress());
		}
		profile.setProperty(Profile.LOCAL_PORT, "2000");

		if (microRuntimeServiceBinder == null) {
			serviceConnection = new ServiceConnection() {
				public void onServiceConnected(ComponentName className,
						IBinder service) {
					microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;
					logger.log(Level.INFO,
							"Gateway successfully bound to MicroRuntimeService");
					startContainer(nickname, profile, agentStartupCallback);
				};

				public void onServiceDisconnected(ComponentName className) {
					microRuntimeServiceBinder = null;
					logger.log(Level.INFO,
							"Gateway unbound from MicroRuntimeService");
				}
			};
			logger.log(Level.INFO, "Binding Gateway to MicroRuntimeService...");

			bindService(new Intent(getApplicationContext(),
					MicroRuntimeService.class), serviceConnection,
					Context.BIND_AUTO_CREATE);

		} else {
			logger.log(Level.INFO,
					"MicroRumtimeGateway already binded to service");
			startContainer(nickname, profile, agentStartupCallback);
		}
	}

	private RuntimeCallback<AgentController> agentStartupCallback = new RuntimeCallback<AgentController>() {
		@Override
		public void onSuccess(AgentController agent) {
		}

		@Override
		public void onFailure(Throwable throwable) {
			logger.log(Level.INFO, "Nickname already in use!");
		}
	};

	private void startContainer(final String nickname, Properties profile,
			final RuntimeCallback<AgentController> agentStartupCallback) {
		if (!MicroRuntime.isRunning()) {

			RuntimeCallback<Void> rc = new RuntimeCallback<Void>() {
				@Override
				public void onSuccess(Void thisIsNull) {
					logger.log(Level.INFO,
							"Successfully start of the container...");
					startAgent(nickname, agentStartupCallback);
				}

				@Override
				public void onFailure(Throwable throwable) {
					logger.log(Level.SEVERE, "Failed to start the container...");
				}
			};

			microRuntimeServiceBinder.startAgentContainer(profile, rc);
		} else {
			startAgent(nickname, agentStartupCallback);
		}
	}

	private void startAgent(final String nickname,
			final RuntimeCallback<AgentController> agentStartupCallback) {

		RuntimeCallback<Void> rc = new RuntimeCallback<Void>() {
			@Override
			public void onSuccess(Void thisIsNull) {
				logger.log(Level.INFO, "Successfully start of the "
						+ AideAgent.class.getName() + "...");
				try {
					agentStartupCallback.onSuccess(MicroRuntime
							.getAgent(nickname));

				} catch (ControllerException e) {
					// Should never happen
					agentStartupCallback.onFailure(e);
				}
			}

			@Override
			public void onFailure(Throwable throwable) {
				logger.log(Level.SEVERE, "Failed to start the "
						+ AideAgent.class.getName() + "...");
				agentStartupCallback.onFailure(throwable);
			}
		};
		microRuntimeServiceBinder.startAgent(nickname,
				AideAgent.class.getName(), new Object[] {
						getApplicationContext(),
						(GBApplication) getApplication() }, rc);
	}

}
