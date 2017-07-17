/** Open another app.
 * @param context current Context, like Activity, App, or Service
 * @param packageName the full package name of the app to open
 * @return true if likely successful, false if unsuccessful
 */
public static boolean openApp(Context context, String packageName) {
    PackageManager manager = context.getPackageManager();
    try {
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    } catch (PackageManager.NameNotFoundException e) {
        return false;
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
			
       openApp(this, "com.grantec.filhorapido");
	  
        }
    }
}