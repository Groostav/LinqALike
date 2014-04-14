package Assists;

import org.fest.assertions.GenericAssert;

import static java.lang.String.format;

public class DelegateAssert<TDelegate extends CountingDelegate>
       extends GenericAssert<DelegateAssert, TDelegate> {

    protected DelegateAssert(TDelegate actual) {
        super(DelegateAssert.class, actual);
    }

    public static <TDelegate extends CountingDelegate> DelegateAssert<TDelegate> assertThat(TDelegate delegate){
        return new DelegateAssert<>(delegate);
    }

    public DelegateAssert<TDelegate> wasInvoked(int expectedInvocationCount){
        if (this.actual.getNumberOfInvocations() == expectedInvocationCount){
            return this;
        }

        fail(format("expecting\n\t%s\n to be invoked %s times, but it was invoked %s times",
                actual.toString(), expectedInvocationCount, actual.getNumberOfInvocations()));
        return null;
    }
}
