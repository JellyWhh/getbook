/**
 * 
 */
package edu.fudan.se.getbook.fragment;

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
import android.widget.Toast;

/**
 * @author whh
 * 
 */
public class SettingFragment extends Fragment {

	private GBApplication application; // 获取应用程序，以得到里面的全局变量
	private EditText et_set_library, et_set_shop, et_set_campusShop,
			et_set_secondHand;
	private Button bt_set_edit, bt_set_save;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (GBApplication) getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_setting, container,
				false);

		et_set_library = (EditText) rootView.findViewById(R.id.et_set_library);
		et_set_shop = (EditText) rootView.findViewById(R.id.et_set_shop);
		et_set_campusShop = (EditText) rootView
				.findViewById(R.id.et_set_campusShop);
		et_set_secondHand = (EditText) rootView
				.findViewById(R.id.et_set_secondHand);

		bt_set_edit = (Button) rootView.findViewById(R.id.bt_set_edit);
		bt_set_save = (Button) rootView.findViewById(R.id.bt_set_save);

		et_set_library.setVisibility(View.INVISIBLE);
		et_set_shop.setVisibility(View.INVISIBLE);
		et_set_campusShop.setVisibility(View.INVISIBLE);
		et_set_secondHand.setVisibility(View.INVISIBLE);

		bt_set_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_set_library.setVisibility(View.VISIBLE);
				et_set_shop.setVisibility(View.VISIBLE);
				et_set_campusShop.setVisibility(View.VISIBLE);
				et_set_secondHand.setVisibility(View.VISIBLE);
			}
		});

		bt_set_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String et_library = et_set_library.getText().toString();
				String et_shop = et_set_shop.getText().toString();
				String et_campusShop = et_set_campusShop.getText().toString();
				String et_secondHand = et_set_secondHand.getText().toString();
				
				try {
//					Integer.parseInt(et_library);
//					Integer.parseInt(et_shop);
//					Integer.parseInt(et_campusShop);
//					Integer.parseInt(et_secondHand);
					
					application.setPriorityLibrary(Integer.parseInt(et_library));
					application.setPriorityShop(Integer.parseInt(et_shop));
					application.setPriorityCBS(Integer.parseInt(et_campusShop));
					application.setPrioritySHS(Integer.parseInt(et_secondHand));

					et_set_library.setVisibility(View.INVISIBLE);
					et_set_shop.setVisibility(View.INVISIBLE);
					et_set_campusShop.setVisibility(View.INVISIBLE);
					et_set_secondHand.setVisibility(View.INVISIBLE);
				} catch (Exception e) {
					Toast.makeText(getActivity(), "input error!", Toast.LENGTH_LONG).show();
				}

				

			}
		});

		return rootView;
	}
}
