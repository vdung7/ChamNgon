package model;

import java.util.ArrayList;

import view.SayingListActivity;
import view.ContentActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContentListAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflater;
	private int mViewResId;
	private ArrayList<Content> contentList;
	private ContentActivity mainAct;
	private SQLiteHelper sh;

	public ContentListAdapter(Context context, int viewResourceId,
			ArrayList<Content> contentList, ContentActivity mainAct) {
		super(context, viewResourceId);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.contentList = contentList;
		this.mainAct = mainAct;
		this.mViewResId = viewResourceId;
		this.sh = new SQLiteHelper(context);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return contentList.size();
	}

	@Override
	public String getItem(int position) {
		return contentList.get(position).getName();
	}

	@Override
	public long getItemId(int position) {
		return contentList.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(mViewResId, null);
		TextView ten = (TextView) convertView
				.findViewById(cyber.app.chamngon.R.id.ten);
		if (contentList.get(position).getId() != 9) {
			int numberChamNgon = sh.getNumberChamNgonByContent(contentList.get(
					position).getId());
			ten.setText("    " + contentList.get(position).getName() + "("
					+ numberChamNgon + ")");
		} else {
			int numberChamNgon = sh.getNumOfFavouriteSaying();
			ten.setText("    " + contentList.get(position).getName() + "("
					+ numberChamNgon + ")");
		}
		// chuyen acti
		ten.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent(mainAct, SayingListActivity.class);
				mIntent.putExtra("cid", contentList.get(position).getId());
				mainAct.startActivity(mIntent);

			}
		});
		return convertView;
	}

}
