package org.example.model.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

/**
 * Represents a dimensional model for storing date and time information.
 * This class is used in a data warehouse environment to store various dimensions related to date and time.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatetimeDim {

    /**
     * The unique identifier for the datetime dimension.
     */
    public int id;

    /**
     * The year component of the datetime.
     */
    public int year;

    /**
     * The quarter component of the datetime.
     */
    public int quarter;

    /**
     * The month component of the datetime.
     */
    public int month;

    /**
     * The day of the month component of the datetime.
     */
    public int dayOfMonth;

    /**
     * The name of the day in the week.
     */
    public String dayName;

    /**
     * The week of the year component of the datetime.
     */
    public int weekOfYear;

    /**
     * The hour component of the datetime.
     */
    public int hour;

    /**
     * The minute component of the datetime.
     */
    public int minute;

    /**
     * The second component of the datetime.
     */
    public int second;

    /**
     * The period of the day (e.g., AM, PM).
     */
    public String period;

    /**
     * The time of day (e.g., morning, afternoon).
     */
    public String timeOfDay;

    /**
     * The day in the quarter component of the datetime.
     */
    public int dayInQuarter;

    /**
     * The day in the month component of the datetime.
     */
    public int dayInMonth;

    /**
     * The day in the year component of the datetime.
     */
    public int dayInYear;

    /**
     * The week in the month component of the datetime.
     */
    public int weekInMonth;

    /**
     * The week in the year component of the datetime.
     */
    public int weekInYear;

    /**
     * The name of the month.
     */
    public String monthName;

    /**
     * The name of the quarter.
     */
    public String quarterName;

    /**
     * The name of the year.
     */
    public String yearName;

    /**
     * Indicates whether the datetime falls on a weekend.
     */
    public boolean isWeekend;

    /**
     * Indicates whether the datetime is a holiday.
     */
    public boolean isHoliday;

    /**
     * The date represented as MM-DD.
     */
    public Date mmDd;

    /**
     * The date represented as MM-DD-YYYY.
     */
    public String mmDdYyyy;

    /**
     * The time represented as HH:MM:SS.
     */
    public Time hhMmSs;

    /**
     * The time represented in military format.
     */
    public String militaryTime;

    /**
     * The ISO-formatted time.
     */
    public String isoTime;

    /**
     * The full date component of the datetime.
     */
    public Date date;

    /**
     * The full time component of the datetime.
     */
    public Time time;
}
