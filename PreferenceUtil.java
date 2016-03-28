public class PreferenceUtil {

    // Preference Tag
    private static final String PreferenceTag = "PREFERENCE_TAG";

    // Preference Key
    public static final String SOME_KEY = "KEY";

    
    //********************* Set SharedPreference ***********************
    //String
    public static void setPref(Context mContext, String tag, String value) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        Editor editor = myPreference.edit();
        editor.putString(tag, value);
        editor.commit();
    }

    //Int
    public static void setPref(Context mContext, String tag, int value) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        Editor editor = myPreference.edit();
        editor.putInt(tag, value);
        editor.commit();
    }

    //Long
    public static void setPref(Context mContext, String tag, long value) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        Editor editor = myPreference.edit();
        editor.putLong(tag, value);
        editor.commit();
    }

    //Boolean
    public static void setPref(Context mContext, String tag, boolean value) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        Editor editor = myPreference.edit();
        editor.putBoolean(tag, value);
        editor.commit();
    }

    //Float
    public static void setPref(Context mContext, String tag, float value) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        Editor editor = myPreference.edit();
        editor.putFloat(tag, value);
        editor.commit();
    }

    //********************* Get SharedPreference ***********************
    //String
    public static String getString(Context mContext, String tag, String defValue) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        return myPreference.getString(tag, defValue);
    }

    //Int
    public static int getInt(Context mContext, String tag, int defValue) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        return myPreference.getInt(tag, defValue);
    }

    //Long
    public static long getLong(Context mContext, String tag, long defValue) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        return myPreference.getLong(tag, defValue);
    }

    //Boolean
    public static boolean getBoolean(Context mContext, String tag, boolean defValue) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        return myPreference.getBoolean(tag, defValue);
    }

    //Float
    public static float getFloat(Context mContext, String tag, float defValue) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        return myPreference.getFloat(tag, defValue);
    }


    //********************* Clear SharedPreference ***********************
    public static void clearPref(Context mContext, String tag) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        Editor editor = myPreference.edit();
        editor.remove(tag);
        editor.commit();
    }

    public static void clearAllPref(Context mContext) {
        SharedPreferences myPreference = mContext.getSharedPreferences(PreferenceTag, Context.MODE_PRIVATE);
        Editor editor = myPreference.edit();
        editor.clear();
        editor.commit();
    }
}
