package com.layer.xdk.ui.util;

import java.text.DateFormat;
import java.util.Date;

public interface DateFormatter {
    String formatTimeDay(Date date);
    String formatTime (Date date);
    @SuppressWarnings("unused")
    String formatTime(Date date, DateFormat timeFormat, DateFormat dateFormat);
}
