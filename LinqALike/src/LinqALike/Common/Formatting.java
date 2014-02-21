package LinqALike.Common;

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
}
