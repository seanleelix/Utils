/**
 * Created by Sean Lee on 25/2/16.
 */
public abstract class PermissionUtils {

    // Permission name
    public static final String PERMISSION_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    // Permission request
    public final static int PERMISSION_REQ_FINE_LOCATION = 100;
    public final static int PERMISSION_REQ_PHONE = 101;
    public final static int PERMISSION_REQ_WRITE_EXTERNAL_STORAGE = 102;

    private static final String PERMISSION_REQUEST = "PERMISSION_REQUEST";
    private static final String PERMISSION_REQUEST_CODE = "PERMISSION_REQUEST_CODE";

    public static boolean checkPermission(Context mContext, String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(AppCompatActivity activity, String permission, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            showRationaleDialog(activity, permission, requestCode);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    public static void requestPermission(Fragment fragment, String permission, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), permission)) {
            showRationaleDialog(fragment, permission, requestCode);
        } else {
            fragment.requestPermissions(new String[]{permission}, requestCode);
        }
    }

    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                              String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    /**
     * *************************** Rationale Dialog *********************************
     */

    /**
     * Used in activity
     *
     * @param activity          Should be AppCompatActivity
     * @param requestPermission
     * @param requestCode
     */
    public static void showRationaleDialog(final AppCompatActivity activity, final String requestPermission, final int requestCode) {

        int rationaleStringResource;

        switch (requestPermission) {
            case PERMISSION_PHONE: {
                rationaleStringResource = R.string.permission_rationale_phone;
                break;
            }
            case PERMISSION_LOCATION: {
                rationaleStringResource = R.string.permission_rationale_location;
                break;
            }
            default: {
                rationaleStringResource = R.string.permission_rationale_default;
                break;
            }
        }

        new AlertDialog.Builder(activity)
                .setMessage(rationaleStringResource)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{requestPermission},
                                requestCode);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }


    /**
     * Used in fragment
     *
     * @param fragment
     * @param requestPermission
     * @param requestCode
     */
    public static void showRationaleDialog(final Fragment fragment, final String requestPermission, final int requestCode) {

        int rationaleStringResource;

        switch (requestPermission) {
            case PERMISSION_PHONE: {
                rationaleStringResource = R.string.permission_rationale_phone;
                break;
            }
            case PERMISSION_LOCATION: {
                rationaleStringResource = R.string.permission_rationale_location;
                break;
            }
            case PERMISSION_WRITE_EXTERNAL_STORAGE: {
                rationaleStringResource = R.string.permission_rationale_external_storage;
                break;
            }
            default: {
                rationaleStringResource = R.string.permission_rationale_default;
                break;
            }
        }

        new AlertDialog.Builder(fragment.getActivity())
                .setMessage(rationaleStringResource)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fragment.requestPermissions(
                                new String[]{requestPermission},
                                requestCode);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    /**
     * *************************** Permission Denied Dialog *********************************
     */

    public static void showPermissionDeniedDialog(Context context, String requestPermission) {

        int deniedStringResource;
        switch (requestPermission) {
            case PERMISSION_PHONE: {
                deniedStringResource = R.string.permission_denied_phone;
                break;
            }
            case PERMISSION_LOCATION: {
                deniedStringResource = R.string.permission_denied_location;
                break;
            }
            case PERMISSION_WRITE_EXTERNAL_STORAGE: {
                deniedStringResource = R.string.permission_denied_external_storage;
                break;
            }
            default: {
                deniedStringResource = R.string.permission_denied_default;
                break;
            }
        }

        new AlertDialog.Builder(context)
                .setMessage(deniedStringResource)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();

    }
}
