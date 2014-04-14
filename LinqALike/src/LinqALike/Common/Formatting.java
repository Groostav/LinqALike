package LinqALike.Common;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Formatting {

    public static final String NullStringRepresentation = "<null>";

    private Formatting(){}

    public static <TElement> String verticallyPrintMembers(Iterable<TElement> problemMembers) {
        String newlineIndent = "\n\t";
        List<String> toStringedMembers = new ArrayList<>();
        for(TElement member : problemMembers){
            String toStringdMember = nullSafeToString(member);
            toStringedMembers.add(toStringdMember);
        }
        return StringUtils.join(toStringedMembers.iterator(), newlineIndent) + "\n";
    }

    public static String nullSafeToString(Object objectToStringify) {
        return objectToStringify == null ? NullStringRepresentation : objectToStringify.toString();
    }

    public static String csv(Iterable<String> values) {
        return StringUtils.join(values.iterator(), ", ");
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
}
