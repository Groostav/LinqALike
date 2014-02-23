package LinqALike.Common;

import LinqALike.CommonDelegates;
import LinqALike.Factories;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Geoff
 * Date: 19/02/14
 * Time: 5:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Formatting {
    private Formatting(){}

    public static String getClassNameOrPrintableNull(Object objectToGetTypeOf){
        return objectToGetTypeOf == null ? "<null>" : objectToGetTypeOf.getClass().getCanonicalName();
    }

    public static <TElement> String verticallyPrintMembers(Iterable<TElement> problemMembers) {
        String newlineIndent = "\n\t";
        return StringUtils.join(Factories.asList(problemMembers).select(CommonDelegates.NullSafeToString).iterator(), newlineIndent) + "\n";
    }
}
