/**
 * 
 */
package edu.fudan.se.getbook.fragment.message;

import jade.core.MicroRuntime;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.fudan.se.agent.AgentMessage;
import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.getbook.R;
import edu.fudan.se.getbook.data.userTask.UserConfirmTask;
import edu.fudan.se.getbook.data.userTask.UserInputTask;
import edu.fudan.se.getbook.data.userTask.UserShowContentTask;
import edu.fudan.se.getbook.data.userTask.UserTask;
import edu.fudan.se.getbook.initial.GBApplication;

/**
 * @author whh
 * 
 */
public class UnreadFragment extends ListFragment {

	private GBApplication application; // 获取应用程序，以得到里面的全局变量
	private UnreadUserTaskAdapter adapter;

	private AideAgentInterface aideAgentInterface; // agent interface

	private Handler handler;
	private Runnable runnable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (GBApplication) getActivity().getApplication();

		try {
			aideAgentInterface = MicroRuntime.getAgent(
					application.getAgentNickname()).getO2AInterface(
					AideAgentInterface.class);
		} catch (StaleProxyException e) {
			Log.e("MessageFragment", "StaleProxyException");
			e.printStackTrace();
		} catch (ControllerException e) {
			Log.e("MessageFragment", "ControllerException");
			e.printStackTrace();
		}

		adapter = new UnreadUserTaskAdapter(getActivity(),
				R.layout.listview_unreadmes,
				application.getUserUnreadTaskList(),
				application.getUserReadTaskList(), aideAgentInterface);

		setListAdapter(adapter);

		// 用于定时刷新
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				handler.postDelayed(this, 500);
			}
		};
		handler.postDelayed(runnable, 500); // 0.5s刷新一次

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setUserVisibleHint(true);
		super.onActivityCreated(savedInstanceState);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(runnable);
		super.onDestroy();
	}
}

class UnreadUserTaskAdapter extends ArrayAdapter<UserTask> {

	private int mResource;
	private Context mContext;
	private LayoutInflater mInflater;
	private List<UserTask> mObjects;
	private List<UserTask> doneTaskList;
	private AideAgentInterface aideAgentInterface; // agent interface

	public UnreadUserTaskAdapter(Context context, int resource,
			List<UserTask> objects, List<UserTask> doneTaskList,
			AideAgentInterface aideAgentInterface) {
		super(context, resource, objects);
		init(context, resource, objects, doneTaskList, aideAgentInterface);
	}

	private void init(Context context, int resource, List<UserTask> objects,
			List<UserTask> doneTaskList, AideAgentInterface aideAgentInterface) {
		this.mContext = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mResource = resource;
		this.mObjects = objects;
		this.doneTaskList = doneTaskList;
		this.aideAgentInterface = aideAgentInterface;
		// this.progressDialog = progressDialog;
	}

	@Override
	public int getCount() {
		return this.mObjects.size();
	}

	@Override
	public UserTask getItem(int position) {
		return this.mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mResource);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(resource, parent, false);

			holder.taskLayout = (RelativeLayout) convertView
					.findViewById(R.id.ll_tasklayout);
			holder.time = (TextView) convertView.findViewById(R.id.tv_taskTime);
			holder.fromAgent = (TextView) convertView
					.findViewById(R.id.tv_taskFromName);
			holder.description = (TextView) convertView
					.findViewById(R.id.tv_taskDescription);
			holder.done = (Button) convertView.findViewById(R.id.bt_taskDone);
			holder.quit = (Button) convertView.findViewById(R.id.bt_taskQuit);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 下面部分不可缺少，是设置每个item具体显示的地方！
		UserTask usertask = getItem(position);
		holder.time.setText(usertask.getTime());
		holder.fromAgent.setText("From: " + usertask.getFromAgent());

		holder.done.setOnClickListener(new UserTaskDoneListener(usertask));
		holder.quit.setOnClickListener(new UserTaskQuitListener(usertask));

		if (usertask instanceof UserShowContentTask) {// 展示内容的user task
			holder.done.setText("Show");
		} else if (usertask instanceof UserInputTask) {// 让用户输入一段文本的task
			holder.done.setText("Input");
		} else if (usertask instanceof UserConfirmTask) {// 让用户确认的task
			holder.done.setText("Yes");
			holder.quit.setText("No");
		} else {// 普通的user task
			holder.done.setText("Done");
		}

		// 必须把setClickable放在setOnClickListener后面，否则不起作用
		if (usertask.isDone()) { // 用户已经完成的
			holder.taskLayout.setBackgroundColor(mContext.getResources()
					.getColor(R.color.done_grey));
			holder.done.setClickable(false);
			holder.quit.setClickable(false);
			holder.done.setTextColor(mContext.getResources().getColor(
					R.color.unclickable_grey));
			holder.quit.setTextColor(mContext.getResources().getColor(
					R.color.unclickable_grey));
		} else {
			// if (usertask instanceof UserDelegateInTask) {
			// holder.taskLayout.setBackgroundColor(mContext.getResources()
			// .getColor(R.color.nodone_green));
			// } else
			if (usertask instanceof UserShowContentTask) {
				holder.taskLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.nodone_pink));
			} else if (usertask instanceof UserInputTask) {
				holder.taskLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.nodone_yellow));
			} else {
				holder.taskLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.nodone_white));
			}
			holder.done.setClickable(true);
			holder.quit.setClickable(true);
			holder.done.setTextColor(mContext.getResources().getColor(
					R.color.clickable_black));
			holder.quit.setTextColor(mContext.getResources().getColor(
					R.color.clickable_black));

		}

		holder.description.setText(usertask.getDescription());

		return convertView;
	}

	/**
	 * 用户点击done按钮时的监听器，根据不同的UserTask类型有不同的响应
	 * 
	 * @author whh
	 * 
	 */
	private class UserTaskDoneListener implements OnClickListener {
		private UserTask userTask;

		public UserTaskDoneListener(UserTask userTask) {
			this.userTask = userTask;
		}

		@Override
		public void onClick(View v) {
			// 让用户输入文本的task
			if (userTask instanceof UserInputTask) {
				showInputTextDialog(userTask);
			}
			// 展示内容的user task
			else if (userTask instanceof UserShowContentTask) {
				showContentDialog(userTask);
			}
			// 普通的user task 或者让用户确认结果的task
			else {
				AgentMessage agentMessage = new AgentMessage(
						AgentMessage.AMHeader.TaskRet,
						AgentMessage.AMBody.TaskRet);

				agentMessage.setContent("YES");
				agentMessage.setToAgent(userTask.getFromAgent());

				aideAgentInterface.sendTaskRetToExternalAgent(agentMessage);

				userTask.setDone(true);
				mObjects.remove(userTask);
				doneTaskList.add(0, userTask);
			}
		}

	}

	/**
	 * 用户点击quit按钮时的监听器，只有委托出去的任务是结束一个goal machine，其余都是结束一个task machine
	 * 
	 * @author whh
	 * 
	 */
	private class UserTaskQuitListener implements OnClickListener {
		private UserTask userTask;

		public UserTaskQuitListener(UserTask userTask) {
			this.userTask = userTask;
		}

		@Override
		public void onClick(View v) {

			AgentMessage agentMessage = new AgentMessage(
					AgentMessage.AMHeader.TaskRet, AgentMessage.AMBody.TaskRet);

			agentMessage.setContent("NO");
			agentMessage.setToAgent(userTask.getFromAgent());

			aideAgentInterface.sendTaskRetToExternalAgent(agentMessage);

			userTask.setDone(true);
			mObjects.remove(userTask);
			doneTaskList.add(0, userTask);
		}

	}

	private class ViewHolder {
		RelativeLayout taskLayout;
		TextView time;
		TextView fromAgent;
		TextView description;
		Button done;
		Button quit;
	}

	/**
	 * 让用户输入文本的task
	 * 
	 * @param userTask
	 *            UserInputTextTask
	 */
	private void showInputTextDialog(final UserTask userTask) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Input:");
		builder.setIcon(android.R.drawable.ic_dialog_info);

		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_userinput, null);
		final EditText editText = (EditText) view
				.findViewById(R.id.et_userinput);

		builder.setView(view);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String userInput = editText.getText().toString();

				AgentMessage agentMessage = new AgentMessage(
						AgentMessage.AMHeader.TaskRet,
						AgentMessage.AMBody.TaskRet);

				agentMessage.setContent(userInput);
				agentMessage.setToAgent(userTask.getFromAgent());

				aideAgentInterface.sendTaskRetToExternalAgent(agentMessage);

				userTask.setDone(true);
				mObjects.remove(userTask);
				doneTaskList.add(0, userTask);

				dialog.cancel();
			}

		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 创建一个显示RequestData的对话框，显示的可以是Text或者Image
	 * 
	 * @param requestData
	 *            要显示的requestData
	 */
	private void showContentDialog(final UserTask userTask) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Select:");

		// seller:tom;price:20;addr:room10###seller:
		String content = ((UserShowContentTask) userTask).getContent();

		final String[] sellerInfos = content.split("###");

		final int[] selectIndex = new int[1];
		builder.setSingleChoiceItems(sellerInfos, 0,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						selectIndex[0] = which;

					}
				});

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String select = sellerInfos[selectIndex[0]];
				// 将选中的结果发回去
				AgentMessage agentMessage = new AgentMessage(
						AgentMessage.AMHeader.TaskRet,
						AgentMessage.AMBody.TaskRet);

				agentMessage.setContent(select);
				agentMessage.setToAgent(userTask.getFromAgent());

				aideAgentInterface.sendTaskRetToExternalAgent(agentMessage);

				userTask.setDone(true);
				mObjects.remove(userTask);
				doneTaskList.add(0, userTask);
				dialog.cancel();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

}
