package com.empowerops.linqalike.common;

import com.empowerops.common.exceptions.RuntimeIOException;
import com.empowerops.linqalike.Factories;
import com.google.common.base.Strings;
import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.empowerops.common.BootstrappingUtilities.getEnvInt;
import static com.empowerops.linqalike.Linq.any;
import static java.lang.Math.round;

//------------------------------------------------------------
// Copy-pasted code from com.empowerops.common
// please do not modify this file, instead modify the version in common,
// and copy-paste it over here
// (and maybe update the build system to allow us to reference common from linq-a-like
// without bundling the whole damn package?)
//------------------------------------------------------------
public class Formatting {

    public static final String NullStringRepresentation = "<null>";

    public static final int ProgressBarCharColumns = getEnvInt(Formatting.class, "ProgressBarCharColumns")
            .orElse(18);

    /**
     * the number of character-coulmns the elipsis character "…" is assumed to take up.
     *
     * It is one character, but for spacing its assumed to be 2.
     */
    private static final int    PresumedElipsisColumns = 2;
    /**
     * a sequence of 80 space charaters
     *
     * <p>80 being the IBM standard for a punchcard
     * http://programmers.stackexchange.com/questions/148677/why-is-80-characters-the-standard-limit-for-code-width
     */
    public static final  String ClearLine              = Strings.repeat(" ", 80);

    private Formatting() {
    }

    @SafeVarargs
    public static <TElement> String verticallyPrintMembers(TElement... problemMembers) {
        return verticallyPrintMembers(Factories.from(problemMembers));
    }

    public static <TElement> String verticallyPrintMembers(Iterable<TElement> problemMembers) {
        String newlineIndent = "\n\t";
        List<String> toStringedMembers = new ArrayList<>();
        for (TElement member : problemMembers) {
            String toStringdMember = nullSafeToString(member);
            toStringedMembers.add(toStringdMember);
        }
        return join(toStringedMembers, newlineIndent) + "\n";
    }

    public static String nullSafeToString(Object objectToStringify) {
        return objectToStringify == null ? NullStringRepresentation : objectToStringify.toString();
    }

    public static String csv(Iterable<? extends Object> values) {
        return join(values, ", ");
    }

    public static String getDebugString(Object value){
        if(value == null){
            return NullStringRepresentation;
        }
        try{
            return "'" + value.toString() + "' " + getHumanType(value);
        }
        catch(Exception e){
            return "'[toString() threw " + e.getClass().getSimpleName() + "]' " + getHumanType(value);
        }
    }

    private static String getHumanType(Object value) {
        return "(type " + value.getClass().getSimpleName() + ")";
    }

    public static String join(Iterable<? extends Object> members, String separator){
        StringBuilder builder = new StringBuilder();

        for(Object member : members){
            String memberString = nullSafeToString(member);
            builder.append(memberString);
            builder.append(separator);
        }

        if(any(members)) {
            builder.replace(builder.length() - separator.length(), builder.length(), "");
        }

        return builder.toString();
    }

    public static String limitCharacters(int maxCharacters, Path pathToFormat) {
        return preLimitCharacters(maxCharacters, pathToFormat.toAbsolutePath().toString());
    }

    /**
     * Returns a string no longer than maxCharacters,
     * inserting an Elipsis at the <b>beginning</b> of the string if need be.
     */
    public static String preLimitCharacters(int maxCharacters, String textToFormat) {
        if(textToFormat.length() <= maxCharacters){
            return textToFormat;
        }
        assert maxCharacters > PresumedElipsisColumns;
        int beginIndex = textToFormat.length() - maxCharacters + PresumedElipsisColumns;
        beginIndex = Math.max(beginIndex, 0);
        return "…" + textToFormat.substring(beginIndex, textToFormat.length());
    }

    /**
     * Returns a string no longer than maxCharacters,
     * inserting an Elipsis at the <b>end</b> of the string if need be.
     */
    public static String postLimitCharacters(int maxCharacters, String textToFormat) {
        if(textToFormat.length() <= maxCharacters){
            return textToFormat;
        }
        assert maxCharacters > PresumedElipsisColumns;
        int endIndex = Math.min(maxCharacters - PresumedElipsisColumns, textToFormat.length());
        return textToFormat.substring(0, endIndex) + "…";
    }

    public static <TElement> TElement otherwiseThrow(RuntimeException exception) {
        throw exception;
    }

    public static String getOrdinalSuffix(int number) {
        switch(number % 100){
            case 11: case 12: case 13:
                return "th";
            default: switch (number % 10){
                case 1: return "st";
                case 2: return "nd";
                case 3: return "rd";
                case 4:case 5:case 6:case 7:
                case 8:case 9:case 0:
                    return "th";
                default:
                    assert false : "Space time occlusion error";
                    return "th";
            }
        }
    }

    /**
     * get a human readable version of a Calendar object. Thanks to
     * <a href="http://r3dux.org/2011/10/how-to-get-human-friendly-dates-in-java-using-the-calendar-class/">r3dux.org</a>
     * for this!
     */
    public static String getFriendlyDate(Calendar theDate) {
        int year       = theDate.get(Calendar.YEAR);
        int month      = theDate.get(Calendar.MONTH);
        int dayOfMonth = theDate.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek  = theDate.get(Calendar.DAY_OF_WEEK);

        // Get the day of the week as a String.
        String friendly = "";
        friendly += getWeekdayFor(dayOfWeek);
        friendly += ", ";
        friendly += dayOfMonth + getOrdinalSuffix(dayOfMonth);
        friendly += " ";
        friendly += getMonthFor(month);
        friendly += " " + year;

        return friendly;
    }

    /**
     * gets the month name corresponding to the supplied month-index
     * starting from 0, which is January.
     */
    private static String getMonthFor(int month) {
        switch (month) {
            case 0: return"January";
            case 1: return"February";
            case 2: return"March";
            case 3: return"April";
            case 4: return"May";
            case 5: return"June";
            case 6: return"July";
            case 7: return"August";
            case 8: return"September";
            case 9: return"October";
            case 10: return"November";
            case 11: return"December";
            default: return"BadMonthValue";
        }
    }

    /**
     * gets the weekday corresponding to the supplied weekday-index
     * starting from 1, which is Sunday
     */
    private static String getWeekdayFor(int dayOfWeek) {
        switch (dayOfWeek){
            case 1: return"Sunday";
            case 2: return"Monday";
            case 3: return"Tuesday";
            case 4: return"Wednesday";
            case 5: return"Thursday";
            case 6: return"Friday";
            case 7: return"Saturday";
            default: return"BadDayValue";
        }
    }

    public static String getHourOfDayTimestamp(DateTime dateTime) {
        StringBuilder builder = new StringBuilder();

        append(builder,
                twoDigits(dateTime.getHourOfDay()),
                ".",
                twoDigits(dateTime.getMinuteOfHour()),
                ".",
                twoDigits(dateTime.getSecondOfMinute()));

        return builder.toString();
    }

    private static StringBuilder append(StringBuilder builder, Object... args){
        for(Object object : args){
            builder.append(object);
        }
        return builder;
    }

    private static String twoDigits(int number) {
        return String.format("%02d", number);
    }

    public static String safeToString(Object source) {
        try{
            return source == null ? "<null>" : source.toString();
        }
        catch(Exception exception){
            return "toString() on " + source.getClass().getSimpleName() + " hashcode '" + source.hashCode() + "' threw:\n " + exception;
        }
    }


    public static void appendProgressBar(Appendable buiilder, double percentageCompleted) {
        double epsilon = 0.00000001;
        try {
            buiilder.append('[');
            buiilder.append(Strings.repeat("=", (int) round((percentageCompleted + epsilon) * ProgressBarCharColumns)));
            buiilder.append('>');
            buiilder.append(Strings.repeat(" ", (int) round((1.0 - percentageCompleted - epsilon) * ProgressBarCharColumns)));
            buiilder.append(']');
        }
        catch (IOException e) { throw new RuntimeIOException(e); }
    }

}
