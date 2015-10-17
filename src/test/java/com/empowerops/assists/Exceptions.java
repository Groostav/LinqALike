package com.empowerops.assists;

import com.empowerops.linqalike.delegate.Action;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: Geoff
 * Date: 02/02/14
 * Time: 04:00
 * To change this template use File | Settings | File Templates.
 */
public class Exceptions {
    private Exceptions(){}

    public static <TException extends Throwable> TException assertThrows(Class<TException> expectedException, Action runnable){
        assert ! expectedException.equals(AssertionError.class) : "cant assert on assertion errors since thats how we fail a test!";
        try{
            runnable.run();
            fail("expected action to throw " + expectedException.getSimpleName() + " but it did not.");
            return null;
        }
        catch(Throwable exception){
            if ( ! expectedException.isInstance(exception)) {
                throw exception;
            }
            return expectedException.cast(exception);
        }
    }

    public static void assertDoesNotThrow(Action runnable){
        try{
            runnable.run();
        }
        catch(Exception instigatingException){
            throw new AssertionError("expected clause not to throw, but it did.", instigatingException);
        }
    }
}
