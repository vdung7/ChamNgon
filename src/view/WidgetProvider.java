package view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import cyber.app.chamngon.R;
import model.SQLiteHelper;
import model.Saying;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;



public class WidgetProvider extends AppWidgetProvider {

	private static enum Language {
		NONE, 
		ENGLISH, 
		VIETNAMESE
	};

	private static final String TAG = "widget";

	private static final int ALARM_TIME = 1000 * 60 * 3;
	private static final String ACTION_EV_LANGUAGE = "engvie";
	private static final String ACTION_RANDOM = "random";
	private static final String ACTION_SAYING = "saying";
	private static final String ACTION_FAVORITE = "favorite";
	private static final String ACTION_ALARM_RANDOM = "alarmRandom";

	private static Saying currentSaying = null;
	private static Language currentLanguage = Language.NONE;
	private static SQLiteHelper sqLiteHelper = null;
	private static ArrayList<Saying> listSayings = null;
//	private static AlarmManager alarmManager = null;
	private static boolean isAlarmRunning = false;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i("widget", "---- App widget update ----");
		
		// Get the component name of widget
		ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);

		// Get all IDs of current widgets in Home screen
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		
		for (int appWidgetId : allWidgetIds) {
			// Get the layout for the App Widget and attach an on-click listener
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			
			Calendar now = new GregorianCalendar(TimeZone.getTimeZone(Time.getCurrentTimezone()));
			String dayInWeek = now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
			String month = now.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
			remoteViews.setTextViewText(R.id.textDayInWeek, dayInWeek);
			remoteViews.setTextViewText(R.id.textDayAndMonth, month + " " + now.get(Calendar.DAY_OF_MONTH));
			switch (currentLanguage) {
			case ENGLISH:
				remoteViews.setTextViewText(R.id.textSaying, "     " + currentSaying.getEnglish());
				remoteViews.setTextViewText(R.id.buttonEV, context.getString(R.string.widget_text_english));
				break;
			case VIETNAMESE:
				remoteViews.setTextViewText(R.id.textSaying, "     " + currentSaying.getVietnamese());
				remoteViews.setTextViewText(R.id.buttonEV, context.getString(R.string.widget_text_vietnamese));
				break;
			case NONE:
				Log.i(TAG, "---- Language none !!! ----");
				break;
			}
			remoteViews.setTextViewText(R.id.textAuthor, currentSaying.getAuthor());
			remoteViews.setImageViewResource(R.id.buttonFavourite, (currentSaying.getFavourite() == 0) ? R.drawable.widget_not_rating : R.drawable.widget_rating);
			
			remoteViews.setOnClickPendingIntent(R.id.buttonEV, getPendingSelfIntent(context, ACTION_EV_LANGUAGE));
			remoteViews.setOnClickPendingIntent(R.id.buttonRandom, getPendingSelfIntent(context, ACTION_RANDOM));
			remoteViews.setOnClickPendingIntent(R.id.textSaying, getPendingSelfIntent(context, ACTION_SAYING));
			remoteViews.setOnClickPendingIntent(R.id.buttonFavourite, getPendingSelfIntent(context, ACTION_FAVORITE));
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
		
		if (!isAlarmRunning) {
			startAlarm(context, ALARM_TIME);
			isAlarmRunning = true;
		}
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);


	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.i(TAG, "---- App widget deleted ----");
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		Log.i(TAG, "---- App widget enabled ----");
		super.onEnabled(context);
	}
	
	

	@Override
	public void onDisabled(Context context) {
		Log.i(TAG, "---- App widget disabled ----");
		if(isAlarmRunning) {
			stopAlarm(context);
			isAlarmRunning = false;
		}
		if (sqLiteHelper != null) {
			sqLiteHelper.close();
			sqLiteHelper = null;
		}
		if (listSayings != null) {
			listSayings = null;
		}
		if (currentSaying != null) {
			currentSaying = null;
		}
		if (currentLanguage != Language.NONE) {
			currentLanguage = Language.NONE;
		}
		super.onDisabled(context);
		

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "---- App widget receive ----");
		super.onReceive(context, intent);
		init(context);
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		ComponentName watchWidget = new ComponentName(context, WidgetProvider.class);


		String pendingAction = intent.getAction();
		if(pendingAction.equalsIgnoreCase(ACTION_EV_LANGUAGE)) {
			Log.i(TAG, "---- Actions ev ----");
			if(currentLanguage == Language.ENGLISH) {
				remoteViews.setTextViewText(R.id.textSaying, "     " + currentSaying.getVietnamese());
				remoteViews.setTextViewText(R.id.buttonEV, context.getString(R.string.widget_text_vietnamese));
				currentLanguage = Language.VIETNAMESE;
			} else if (currentLanguage == Language.VIETNAMESE) {
				remoteViews.setTextViewText(R.id.textSaying, "     " + currentSaying.getEnglish());
				remoteViews.setTextViewText(R.id.buttonEV, context.getString(R.string.widget_text_english));
				currentLanguage = Language.ENGLISH;
			}

		} else if(pendingAction.equalsIgnoreCase(ACTION_RANDOM)) {
			Log.i(TAG, "---- Actions random ----");
			currentSaying = randomSaying();
			Log.i("widget", "Current language: " + currentLanguage);
			switch (currentLanguage) {
			case ENGLISH:
				remoteViews.setTextViewText(R.id.textSaying, "     " + currentSaying.getEnglish());
				break;
			case VIETNAMESE:
				remoteViews.setTextViewText(R.id.textSaying, "     " + currentSaying.getVietnamese());
				break;
			case NONE:
				Log.e(TAG, "---- Language none !!!----");
				break;
			}
			remoteViews.setTextViewText(R.id.textAuthor, currentSaying.getAuthor());
			remoteViews.setImageViewResource(R.id.buttonFavourite, (currentSaying.getFavourite() == 1) ? R.drawable.widget_rating : R.drawable.widget_not_rating);
		} else if(pendingAction.equalsIgnoreCase(ACTION_SAYING)) {
			Log.i(TAG, "---- Actions saying click ----");
			Intent deltailIntent = new Intent(context.getApplicationContext(), ContentActivity.class);
			deltailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(deltailIntent);
		} else if(pendingAction.equalsIgnoreCase(ACTION_FAVORITE)) {
			Log.i(TAG, "---- Actions favorite ----");
			currentSaying.setFavourite(currentSaying.getFavourite() == 0 ? 1 : 0);
			sqLiteHelper.replaceSaying(currentSaying);
			remoteViews.setImageViewResource(R.id.buttonFavourite, 
					(currentSaying.getFavourite() == 1) ? R.drawable.widget_rating : R.drawable.widget_not_rating);
		} else if(pendingAction.equalsIgnoreCase(ACTION_ALARM_RANDOM)) {
			Log.i(TAG, "---- Actions alarm random ----");
			currentSaying = randomSaying();
			Log.i("widget", "Current language: " + currentLanguage);
			switch (currentLanguage) {
			case ENGLISH:
				remoteViews.setTextViewText(R.id.textSaying, "     " + currentSaying.getEnglish());
				break;
			case VIETNAMESE:
				remoteViews.setTextViewText(R.id.textSaying, "     " + currentSaying.getVietnamese());
				break;
			case NONE:
				Log.e(TAG, "---- Language none !!!----");
				break;
			}
			remoteViews.setTextViewText(R.id.textAuthor, currentSaying.getAuthor());
			remoteViews.setImageViewResource(R.id.buttonFavourite, (currentSaying.getFavourite() == 1) ? R.drawable.widget_rating : R.drawable.widget_not_rating);
		}
		appWidgetManager.updateAppWidget(watchWidget, remoteViews);

	}

	private Saying randomSaying() {
		Log.i(TAG, "---- get randoom Saying from list ----");
		if (listSayings == null) {
			Log.e(TAG, "---- list saying is null ----");
			return null;
		}
		Random random = new Random();
		int index= random.nextInt(listSayings.size()-1);
		return listSayings.get(index);
	}

	private void startAlarm(Context context, long intervalMillis) {
		Log.i(TAG, "---- Start alarm ----");
		Intent intent = new Intent(context, WidgetProvider.class);
		intent.setAction(ACTION_ALARM_RANDOM);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC,
				System.currentTimeMillis(), intervalMillis, pendingIntent);
	}
	
	private void stopAlarm(Context context) {
		Log.i(TAG, "---- Stop alarm ----");
		Intent intent = new Intent(context, WidgetProvider.class);
		intent.setAction(ACTION_ALARM_RANDOM);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	protected PendingIntent getPendingSelfIntent(Context context, String action) {
		Intent intent = new Intent(context, WidgetProvider.class);
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	private void init(Context context) {
		if (sqLiteHelper == null) {
			sqLiteHelper = new SQLiteHelper(context);
		}
		if (listSayings == null) {
			listSayings = new ArrayList<Saying>();
			//listSayings = sqLiteHelper.getAllSaying();
			listSayings = sqLiteHelper.getSayingByLength(100);
		}
		if (currentSaying == null) {
			currentSaying = randomSaying();
		}
		if (currentLanguage == Language.NONE) {
			currentLanguage = Language.ENGLISH;
		}
	}
}
