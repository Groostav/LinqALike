package LinqALike;

import LinqALike.Common.Tuple;
import LinqALike.Delegate.Condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class LinqingList<TElement> extends ArrayList<TElement> implements Queryable<TElement> {

    // Constructors

    public LinqingList(){
        super();
    }
    public LinqingList(TElement... elements) {
        this(Arrays.asList(elements));
    }
    public LinqingList(Iterator<? extends TElement> elements){
        while (elements.hasNext()){
            TElement next = elements.next();
            add(next);
        }
    }
    public LinqingList(Iterable<? extends TElement> elements){
        this();
        for(TElement element : elements){
            add(element);
        }
    }

    @Override
    public boolean add(TElement tElement) {
        return super.add(tElement);
    }

    @Override @SafeVarargs
    public final Queryable<TElement> except(TElement... toExclude) {
        return Linq.except(this, toExclude);
    }

    public LinqingList(Class<TElement> elementClass, Object[] initialValues){
        this();
        for(Object object : initialValues){
            if(object == null){
                add(null);
                continue;
            }
            if( ! object.getClass().isAssignableFrom(elementClass)){
                throw new IllegalArgumentException("initialValues contains an element of type '" + object.getClass().getSimpleName() + "'" +
                                                   "but the list to be constructed is for elements of type '" + elementClass.getSimpleName() + "'.");
            }

            add((TElement) object);
        }
    }

    /*
     * List-based Mutators
     */

    /**
     * <p>adds all elements in the supplied ellipses set to this list, starting from the last current index, and adding them
     * left-to-right.</p>
     *
     * @param   valuesToBeAdded the elements to be added to this linqing list.
     * @return  true if the list changed as a result of this call.
     */
    public boolean addAll(TElement... valuesToBeAdded){
        boolean modified = false;
        for(TElement element : valuesToBeAdded){
            modified |= add(element);
        }
        return modified;
    }

    public boolean addAll(Iterable<? extends TElement> valuesToBeAdded) {
        boolean modified = false;
        for(TElement element : valuesToBeAdded){
            modified |= add(element);
        }
        return modified;
    }
    public void removeAll(Iterable<TElement> values) {
        super.removeAll(new LinqingList<>(values));
    }

    public void removeSingle(Condition<? super TElement> condition){
        TElement element = this.single(condition);
        this.remove(element);
    }


    public void addIfNotNull(TElement element) {
        if(element != null){
            add(element);
        }
    }

    public void addAllNew(Iterable<TElement> setContainingNewAndExistingElements) {
        Queryable<TElement> intersection = Factories.asList(setContainingNewAndExistingElements).except(this.intersect(setContainingNewAndExistingElements));
        this.addAll(intersection);
    }

    public void replaceAll(Queryable<Tuple<TElement, TElement>> changedItems) {
        for(Tuple<TElement, TElement> pair : changedItems){
            this.replace(pair.left, pair.right);
        }
    }

    public void clearAndAddAll(Iterable<? extends TElement> newItems){
        clear();
        addAll(newItems);
    }

    public void replace(TElement oldItem, TElement newItem) {
        int index = indexOf(oldItem);
        assert index != -1 : oldItem + " is not contained in " + this;
        this.add(index, newItem);
        this.remove(oldItem);
    }
}


