PackageManager pm = getPackageManager();
    Intent intent = pm.getLaunchIntentForPackage("com.grantec.filhorapido");
    startActivity(intent);





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
