public void open() {
    openApplication(getActivity(), "com.grantec.filhorapido");
}

public void openApplication(Context context, String packageN) {
    Intent i = context.getPackageManager().getLaunchIntentForPackage(packageN);
    if (i == null) {
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
    } else {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageN)));
        }
        catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageN)));
        }
    }
}





public class TransitionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String error = intent.getStringExtra("error");

        if (error != null) {
            //handle error
            Log.println(Log.ERROR, "YourAppTAG", error);
        } else {
			
       open();
	  
        }
    }
}
