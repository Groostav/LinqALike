package com.empowerops.linqalike.assists;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
* Created with IntelliJ IDEA.
* User: Geoff
* Date: 12/04/14
* Time: 23:27
* To change this template use File | Settings | File Templates.
*/
public abstract class CountingDelegate {

    protected List<Object> inspectedElements = new ArrayList<>();

    public void shouldHaveBeenInvoked(int numberOfTimes){
        assertThat(inspectedElements.size()).describedAs("the elements for which the delegate " + this + " was invoked.").isEqualTo(numberOfTimes);
    }

    public int getNumberOfInvocations(){
        return inspectedElements.size();
    }
}
