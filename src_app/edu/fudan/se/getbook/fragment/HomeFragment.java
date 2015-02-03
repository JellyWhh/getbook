/**
 * 
 */
package edu.fudan.se.getbook.fragment;

import jade.core.MicroRuntime;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.getbook.GetBookRunnable;
import edu.fudan.se.getbook.R;
import edu.fudan.se.getbook.initial.GBApplication;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author whh
 * 
 */
public class HomeFragment extends Fragment {

	private GBApplication application; // 获取应用程序，以得到里面的全局变量

	private Button bt_start, bt_stop;
	private EditText et_inputbookname;

	private AideAgentInterface aideAgentInterface;
	
	private Thread getBookThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (GBApplication) getActivity().getApplication();

		try {
			aideAgentInterface = MicroRuntime.getAgent(
					application.getAgentNickname()).getO2AInterface(
					AideAgentInterface.class);
		} catch (StaleProxyException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		et_inputbookname = (EditText) rootView
				.findViewById(R.id.et_inputbookname);

		bt_start = (Button) rootView.findViewById(R.id.bt_start);
		bt_stop = (Button) rootView.findViewById(R.id.bt_stop);

		bt_start.setOnClickListener(startOnClickListener);
		bt_stop.setOnClickListener(stopOnClickListener);
		
		bt_stop.setClickable(false);

		return rootView;
	}

	private OnClickListener startOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			String bookname = et_inputbookname.getText().toString();

			GetBookRunnable gbRunnable = new GetBookRunnable(bookname,
					application.getPriorityLibrary(),
					application.getPriorityShop(),
					application.getPriorityCBS(), application.getPrioritySHS(),
					application.getProcedureLogList(), aideAgentInterface);
			
			getBookThread = new Thread(gbRunnable);
			getBookThread.start();

			bt_stop.setClickable(true);
			bt_stop.setOnClickListener(stopOnClickListener);
			
			bt_start.setClickable(false);
			
			et_inputbookname.setText("");
			bt_start.setTextColor(getResources().getColor(R.color.unclickable_grey));
			bt_stop.setTextColor(getResources().getColor(R.color.clickable_black));
		}
	};

	private OnClickListener stopOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			getBookThread.stop();
			bt_start.setClickable(true);
			bt_start.setOnClickListener(startOnClickListener);
			
			bt_stop.setClickable(false);
			bt_stop.setTextColor(getResources().getColor(R.color.unclickable_grey));
			bt_start.setTextColor(getResources().getColor(R.color.clickable_black));
		}
	};
}
