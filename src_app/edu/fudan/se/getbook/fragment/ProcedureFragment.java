/**
 * 
 */
package edu.fudan.se.getbook.fragment;

import java.util.List;

import edu.fudan.se.getbook.R;
import edu.fudan.se.getbook.data.ProcedureLog;
import edu.fudan.se.getbook.initial.GBApplication;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author whh
 *
 */
public class ProcedureFragment extends ListFragment {

	private GBApplication application; // 获取应用程序，以得到里面的全局变量
	private ProcedureLogAdapter adapter;

	private Handler handler;
	private Runnable runnable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (GBApplication) getActivity().getApplication();
		adapter = new ProcedureLogAdapter(getActivity(),
				R.layout.listview_procedure, application.getProcedureLogList());

		setListAdapter(adapter);

		// 用于定时刷新
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				handler.postDelayed(this, 1 * 1000);
			}
		};
		handler.postDelayed(runnable, 1 * 1000); // 1s刷新一次
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}


	@Override
	public void onDestroy() {
		handler.removeCallbacks(runnable);
		super.onDestroy();
	}
}


class ProcedureLogAdapter extends ArrayAdapter<ProcedureLog>{
	
	private List<ProcedureLog> procedureLogList;
	private int mResource;
	private Context mContext;
	private LayoutInflater mInflater;
	
	public ProcedureLogAdapter(Context context, int resource,
			List<ProcedureLog> procedureLogList) {
		super(context, resource, procedureLogList);
		init(context, resource, procedureLogList);
	}

	private void init(Context context, int resource, List<ProcedureLog> procedureLogList) {
		this.mContext = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mResource = resource;
		this.procedureLogList = procedureLogList;
	}

	@Override
	public int getCount() {
		return this.procedureLogList.size();
	}

	@Override
	public ProcedureLog getItem(int position) {
		return this.procedureLogList.get(position);
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

			holder.time = (TextView) convertView.findViewById(R.id.tv_procedure_time);
			holder.content = (TextView) convertView
					.findViewById(R.id.tv_procedure_content);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 下面部分不可缺少，是设置每个item具体显示的地方！
		ProcedureLog procedureLog = getItem(position);
		holder.time.setText(procedureLog.getTime());
		holder.content.setText(procedureLog.getContent());

		return convertView;
	}

	class ViewHolder {
		TextView time;
		TextView content;
	}
}

