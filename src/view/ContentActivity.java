package view;

import java.io.IOException;
import java.sql.SQLDataException;
import java.util.Arrays;
import java.util.List;

import model.AboutBox;
import model.ContentListAdapter;
import model.SQLiteHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;
import cyber.app.chamngon.R;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.searchboxsdk.android.StartAppSearch;
import com.startapp.android.publish.StartAppAd;

public class ContentActivity extends Activity {

	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

	private ListView listContent;
	private LoginButton loginButton;
	private ProfilePictureView profilePic;

	private GraphUser user;
	private UiLifecycleHelper uiHelper;

	// advertisement part
	private StartAppAd startAppAd = new StartAppAd(this);

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actvity_contents);

		// for advertisements
		StartAppSearch.init(this);
		StartAppSearch.showSearchBox(this);
/*		
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.example.chamngon", 
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
				System.out.println("KeyHash: " + (Base64.encodeToString(md.digest(), Base64.DEFAULT)));
			}
		} catch (NameNotFoundException e) {
			System.out.println("NameNotFoundException");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException");
		}*/

		SQLiteHelper sh= new SQLiteHelper(this);
		System.out.println("ok 1....................");
		try {
			sh.createDataBase();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			sh.openDatabase();
		} catch (SQLDataException e) {
			e.printStackTrace();
		}
		listContent = (ListView) findViewById(R.id.contentListView);
		listContent.setAdapter(new ContentListAdapter(getApplicationContext(),R.layout.list_row_content,
				sh.getAllContent(),ContentActivity.this));
		System.out.println("get all content thanh cong");

		profilePic = (ProfilePictureView)findViewById(R.id.profilePic);
		loginButton = (LoginButton)findViewById(R.id.loginButton);
		loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {

			@Override
			public void onUserInfoFetched(GraphUser user) {
				ContentActivity.this.user = user;
				updateUI();
			}
		});
		loginButton.setPublishPermissions(PERMISSIONS);
		loginButton.setDefaultAudience(SessionDefaultAudience.EVERYONE);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// for advertisement 
		startAppAd.onResume();
		((BaseAdapter)listContent.getAdapter()).notifyDataSetChanged();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onBackPressed() {
		startAppAd.onBackPressed();
		super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.content_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuAbout:
			AboutBox.show(ContentActivity.this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if ((exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(ContentActivity.this)
			.setTitle(R.string.cancelled)
			.setMessage(R.string.permission_not_granted)
			.setPositiveButton(R.string.ok, null)
			.show();
		}
		updateUI();
	}   

	private void updateUI() {
		System.out.println("User: " + user);
		if (user != null) {
			profilePic.setProfileId(user.getId());
		} else {
			profilePic.setProfileId(null);
		}
	}
}
