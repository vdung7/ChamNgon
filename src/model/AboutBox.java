package model;

import cyber.app.chamngon.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutBox {
	static String versionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			return "Unknow";
		}
	}
	public static void show(Activity callingActivity) {
		//Use a Spannable to allow for links highlighting
		SpannableString aboutText = new SpannableString(
				callingActivity.getString(R.string.about_text_version) + " " + versionName(callingActivity)+ "\n\n" + 
				callingActivity.getString(R.string.about_text1) + " " + callingActivity.getString(R.string.about_app_author) + "\n\n" +
				callingActivity.getString(R.string.about_text2) + callingActivity.getString(R.string.about_text_website) + "\n\n" +
				callingActivity.getString(R.string.about_text3) + callingActivity.getString(R.string.about_text_email) + "\n");
		//Generate views to pass to AlertDialog.Builder and to set the text
		View about;
		TextView tvAbout;
		try {
			//Inflate the custom view
			LayoutInflater inflater = callingActivity.getLayoutInflater();
			about = inflater.inflate(R.layout.aboutbox, 
					(ViewGroup) callingActivity.findViewById(R.id.aboutView));
			tvAbout = (TextView) about.findViewById(R.id.aboutText);
		}
		catch(InflateException e) {
			//Inflater can throw exception, unlikely but default to TextView if it occ
			about = tvAbout = new TextView(callingActivity);
		}
		//Set the about text 
		tvAbout.setText(aboutText);
		// Now Linkify the text
		Linkify.addLinks(tvAbout, Linkify.ALL); 
		//Build and show the dialog 
		new AlertDialog.Builder(callingActivity)
		.setTitle(callingActivity.getString(R.string.about) + callingActivity.getString(R.string.app_name))
		.setCancelable(true)
		.setIcon(R.drawable.icon)
		.setPositiveButton(R.string.about_button_ok, null)
		.setView(about)
		.show(); //Builder method returns allow for method chaining 
	} 
}

