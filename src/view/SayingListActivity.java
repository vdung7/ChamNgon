package view;

import cyber.app.chamngon.R;

import model.ChamNgonListAdapter;
import model.SQLiteHelper;
import android.app.*;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

public class SayingListActivity extends ListActivity {

	private int cid = 0;
	private TextView textTitle;
	private SQLiteHelper sqliteHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sayings);

		cid = getIntent().getExtras().getInt("cid");

		// connect activity with database and adapter
		sqliteHelper = new SQLiteHelper(this);
		if (cid != 9)
			setListAdapter(new ChamNgonListAdapter(getApplicationContext(),
					R.layout.list_row_saying,
					sqliteHelper.getSayingByContent(cid),
					SayingListActivity.this, sqliteHelper));
		else {
			// lay danh sach cac danh ngon yeu thich
			setListAdapter(new ChamNgonListAdapter(getApplicationContext(),
					R.layout.list_row_saying,
					sqliteHelper.getFavoriteSayings(),
					SayingListActivity.this, sqliteHelper));
			System.out.println("set list yeu thich thanh cong");
		}
		System.out.println("set list adapter thanh cong ");

		// set title for list
		textTitle = (TextView) findViewById(R.id.title);
		textTitle.setText(sqliteHelper.getContent(cid).getName());
	}

	@Override
	protected void onResume() {
		super.onResume();
		// this needn't
		sqliteHelper = new SQLiteHelper(this);
		if (cid != 9)
			setListAdapter(new ChamNgonListAdapter(getApplicationContext(),
					R.layout.list_row_saying,
					sqliteHelper.getSayingByContent(cid),
					SayingListActivity.this, sqliteHelper));
		else {
			// lay danh sach cac danh ngon yeu thich
			setListAdapter(new ChamNgonListAdapter(getApplicationContext(),
					R.layout.list_row_saying,
					sqliteHelper.getFavoriteSayings(),
					SayingListActivity.this, sqliteHelper));
			System.out.println("set list yeu thich thanh cong");
		}
		resumeState(cid);
	}

	// ///////////////

	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveState();
	}

	@Override
	protected void onStop() {
		super.onStop();
		saveState();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	private void resumeState(int cid) {
		SharedPreferences state = getSharedPreferences("data_state",
				MODE_PRIVATE);
		int nFirstVisiblePosition = state.getInt("firstVisiblePosition" + cid,
				0);
		int nTop = state.getInt("top" + cid, 0);
		getListView().setSelectionFromTop(nFirstVisiblePosition, nTop);

	}

	private void saveState() {
		// init sharepreferences
		SharedPreferences state = getSharedPreferences("data_state",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = state.edit();

		// Put data
		editor.putInt("firstVisiblePosition" + cid, getListView()
				.getFirstVisiblePosition());
		View view = getListView().getChildAt(0);
		editor.putInt("top" + cid, (view == null) ? 0 : view.getTop());
		editor.putInt("cid" + cid, cid);

		// commit
		editor.commit();
	}
}
