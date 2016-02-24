   /**
     * String ==> GregorianCalendar
     *
     * @param strTime
     * @param formatType the format should be same with strTime
     * @return
     * @throws ParseException
     */
    public static GregorianCalendar stringToGregorianCalendar(String strTime, String formatType) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatType);
        Date date = simpleDateFormat.parse(strTime);
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return gregorianCalendar;
    }
    
    /**
     * Transfer Date ==> String
     *
     * @param date
     * @param formatType format in yyyy-MM-dd HH:mm:ss or something like that
     * @return datetime in string
     */
    public static String dateToString(Date date, String formatType) {
        return new SimpleDateFormat(formatType).format(date);
    }

    /**
     * datetime long ==> String
     *
     * @param currentTime
     * @param formatType  The String format you want
     * @return
     * @throws ParseException
     */
    public static String longToString(long currentTime, String formatType) throws ParseException {
        Date date = longToDate(currentTime, formatType);
        String strTime = dateToString(date, formatType);
        return strTime;
    }

    /**
     * String ==> Date
     *
     * @param strTime
     * @param formatType the format should be same with strTime
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String strTime, String formatType) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);

        return date;
    }

    public static Date longToDate(long currentTime, String formatType) throws ParseException {
        Date dateOld = new Date(currentTime);
        String sDateTime = dateToString(dateOld, formatType);
        Date date = stringToDate(sDateTime, formatType);
        return date;
    }

    public static long stringToLong(String strTime, String formatType) throws ParseException {
        Date date = stringToDate(strTime, formatType);
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date);
            return currentTime;
        }
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }
