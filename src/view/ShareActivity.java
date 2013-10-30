package view;

import cyber.app.chamngon.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ShareActivity extends Activity{
	private String a;
	private String v;
	private String t;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_othershare);
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		System.out.println("share intent");
		//shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		a=shareIntent.getExtras().getString("a");
		v=shareIntent.getExtras().getString("v");
		t=shareIntent.getExtras().getString("t");
		shareIntent.putExtra(Intent.EXTRA_TEXT , v + "\n" + a +"\n"+t);
		startActivity(Intent.createChooser(shareIntent, "Select an application to share: " ));
	}

}
