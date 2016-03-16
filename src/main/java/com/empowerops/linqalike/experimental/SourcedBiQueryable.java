package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.DefaultedBiQueryable;
import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.Linq;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.Condition;

import javax.xml.transform.Source;
import java.util.Iterator;
import java.util.Optional;

/**
 * Idea is to support something like this:
 *
 * <pre>{@code
 * Queryable<VariableSymbol> vars = sourceVars
 *       .pushTransform(VariableSymbol::getCanonicalName)
 *       .where(targetName::equals)
 *       .popTransform()
 *       .singleOrDefault()
 * }</pre>
 *
 * <p>* Created by Geoff on 1/27/2016.</p>
 */
public interface SourcedBiQueryable<TSource, TTransformed> extends DefaultedBiQueryable<TSource, TTransformed> {

    class Wrapper<TSource, TTransformed> implements SourcedBiQueryable<TSource, TTransformed>{

        private final Iterable<Tuple<TSource, TTransformed>> source;

        public Wrapper(Iterable<Tuple<TSource, TTransformed>>  source){
            this.source = source;
        }

        @Override public Iterator<Tuple<TSource, TTransformed>> iterator() {
            return source.iterator();
        }
    }

    default SourcedBiQueryable<TSource, TTransformed> where(Condition<TTransformed> condition){
        return new Wrapper<>(this.where((left, right) -> condition.passesFor(right)));
    }

    //hmm,
    // and: first, second, last, single

    default Queryable<TSource> popTransform(){
        return this.select((left, right) -> left);
    }
}
