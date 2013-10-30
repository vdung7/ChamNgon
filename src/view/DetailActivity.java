package view;

import java.util.Arrays;
import java.util.List;

import model.Saying;
import model.SQLiteHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import cyber.app.chamngon.R;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public class DetailActivity extends Activity {
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");

	private int chid;
	private Saying chamngon;
	private SQLiteHelper sqliteHelper;

	private TextView textEnglish;
	private TextView textVietnamese;
	private TextView textAuthor;
	private ImageButton buttonLove;
	private ImageButton buttonShare;
	private ImageButton buttonOtherShare;

	// private boolean canPresentShareDialog;
	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	// private UiLifecycleHelper uiHelper;
	private PendingAction pendingAction = PendingAction.NONE;

	/*
	 * private Session.StatusCallback callback = new Session.StatusCallback() {
	 * 
	 * @Override public void call(Session session, SessionState state, Exception
	 * exception) {
	 * 
	 * } };
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// uiHelper = new UiLifecycleHelper(this, callback);
		// uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		chamngon = new Saying();
		chid = getIntent().getExtras().getInt("chid");
		sqliteHelper = new SQLiteHelper(this);

		chamngon = sqliteHelper.getSaying(chid);

		// reference to GUI xml
		textVietnamese = (TextView) findViewById(R.id.viet);
		textEnglish = (TextView) findViewById(R.id.anh1);
		textAuthor = (TextView) findViewById(R.id.tacgia);
		buttonLove = (ImageButton) findViewById(R.id.buttonLove);
		buttonShare = (ImageButton) findViewById(R.id.buttonShare);
		buttonOtherShare = (ImageButton) findViewById(R.id.buttonOtherShare);

		textVietnamese.setText(chamngon.getVietnamese());
		textEnglish.setText(chamngon.getEnglish());
		textAuthor.setText(chamngon.getAuthor());

		if (chamngon.getFavourite() == 0) {
			buttonLove.setBackgroundResource(R.drawable.favorites_add);
		} else {
			buttonLove.setBackgroundResource(R.drawable.gnome_favorites);
		}

		buttonLove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (chamngon.getFavourite() == 0) {
					chamngon.setFavourite(1);
					buttonLove
							.setBackgroundResource(R.drawable.gnome_favorites);
				} else {
					chamngon.setFavourite(0);
					buttonLove.setBackgroundResource(R.drawable.favorites_add);
				}

				sqliteHelper.updateSaying(chamngon);
			}
		});

		buttonShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickShare();
			}
		});

		Session session = Session.getActiveSession();
		boolean available = session != null && session.isOpened();
		buttonShare.setEnabled(available);
		if (!available) {
			buttonShare.setVisibility(View.INVISIBLE);
		} else {
			buttonShare.setVisibility(View.VISIBLE);
		}

		buttonOtherShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				System.out.println("share intent");
				// shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT,
						textVietnamese.getText() + "\n" + textEnglish.getText()
								+ "\n" + textAuthor.getText());
				startActivity(Intent.createChooser(shareIntent,
						"Select an application to share: "));
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		chid = getIntent().getExtras().getInt("chid");
	}

	@SuppressWarnings("unused")
	private void onClickShare() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		Session session = Session.getActiveSession();
		System.out.println("Session in detail: " + session);
		System.out.println("Session Permission in detail: "
				+ session.getPermissions());

		if (session != null) {
			builder.setTitle(R.string.alert_confirm_title)
					.setMessage(R.string.alert_comfirm_message)
					.setPositiveButton(R.string.alert_comfirm_positiveButton,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									performPublish(PendingAction.POST_STATUS_UPDATE);
								}
							})
					.setNegativeButton(R.string.alert_confirm_negative, null)
					.show();

		} else {
			builder.setTitle(R.string.alert_error_title)
					.setMessage(R.string.alert_error_message)
					.setPositiveButton(R.string.alert_error_positiveButton,
							null);
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		// These actions may re-set pendingAction if they are still pending, but
		// we assume they
		// will succeed.
		pendingAction = PendingAction.NONE;

		switch (previouslyPendingAction) {
		case POST_STATUS_UPDATE:
			postStatusUpdate();
			break;
		}
	}

	private interface GraphObjectWithId extends GraphObject {
		String getId();
	}

	private void showPublishResult(String message, GraphObject result,
			FacebookRequestError error) {
		String title = null;
		String alertMessage = null;
		if (error == null) {
			title = getString(R.string.success);
			String id = result.cast(GraphObjectWithId.class).getId();
			alertMessage = getString(R.string.successfully_posted_post,
					message, id);
		} else {
			title = getString(R.string.error);
			alertMessage = error.getErrorMessage();
		}

		new AlertDialog.Builder(this).setTitle(title).setMessage(alertMessage)
				.setPositiveButton(R.string.ok, null).show();
	}

	private void postStatusUpdate() {
		if (hasPublishPermission()) {
			final String message = textVietnamese.getText() + "\n\n"
					+ textEnglish.getText() + "\n\n" + "- "
					+ textAuthor.getText() + " -";
			Request request = Request.newStatusUpdateRequest(
					Session.getActiveSession(), message,
					new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							showPublishResult(message,
									response.getGraphObject(),
									response.getError());
						}
					});
			request.executeAsync();
		} else {
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}

	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null
				&& session.getPermissions().contains("publish_actions");
	}

	private void performPublish(PendingAction action) {
		Session session = Session.getActiveSession();
		if (session != null) {
			pendingAction = action;
			if (hasPublishPermission()) {
				// We can do the action right away.
				handlePendingAction();
			} else {
				// We need to get new permissions, then complete the action when
				// we get called back.
				session.openForPublish(new Session.OpenRequest(this)
						.setCallback(null));
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
						this, PERMISSIONS));
			}
		}
	}
}
