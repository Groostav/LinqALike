package Assists;

import LinqALike.Delegate.Action;

/**
 * Created with IntelliJ IDEA.
 * User: Geoff
 * Date: 02/02/14
 * Time: 04:00
 * To change this template use File | Settings | File Templates.
 */
public class Exceptions {
    private Exceptions(){}

    public static void assertThrows(Class<? extends Exception> expectedException, Action runnable){
        try{
            runnable.run();
        }
        catch(Exception e){
            if ( ! expectedException.isAssignableFrom(e.getClass())) {
                throw e;
            }
        }
    }
}
