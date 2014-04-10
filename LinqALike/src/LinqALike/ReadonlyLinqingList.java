package LinqALike;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static LinqALike.CommonDelegates.NullSafeToString;

public class ReadonlyLinqingList<TElement> extends LinqingList<TElement> {

	/*
	FindBugs flagged this as:
	Field isn't final but should be
	This static field public but not final, and could be changed by malicious code or by accident from another package. The field could be made final to avoid this vulnerability.

	Bug kind and pattern: MS - MS_SHOULD_BE_FINAL*/
    private final Exception origin;
    private final Because reasonGiven;

    public enum Because{
        DerivedFromAnotherSet,
        InstantiatedAsReadonly
        ;

        public String getDescription(){
            switch(this){
                case DerivedFromAnotherSet: return " as the set was transformed from another set, and modifying this one would not modify the source-set.";
                case InstantiatedAsReadonly: return " as the set was declared to be read-only at construction.";
                default: throw new RuntimeException("no switch for enum");
            }
        }
    }

    public static Exception noOriginGiven = null;

    private ReadonlyLinqingList(Because reasonGiven, Exception origin){
        super();
        this.reasonGiven = reasonGiven;
        this.origin = origin;
    }

    public ReadonlyLinqingList(){
        this(Because.InstantiatedAsReadonly, noOriginGiven);
    }
    public ReadonlyLinqingList(Exception origin){
        this(Because.InstantiatedAsReadonly, origin);
    }

    public ReadonlyLinqingList(Class<TElement> elementsBaseClass, Object... elements){
        this(Because.InstantiatedAsReadonly, noOriginGiven);
        for(Object element : elements){
            assert element == null || elementsBaseClass.isAssignableFrom(element.getClass());
            super.add((TElement)element);
        }
    }
    public ReadonlyLinqingList(TElement... elements) {
        this(Arrays.asList(elements), Because.InstantiatedAsReadonly);
    }
    public ReadonlyLinqingList(Iterable<? extends TElement> elements){
        this(elements, Because.InstantiatedAsReadonly);
    }
    protected ReadonlyLinqingList(Iterable<? extends TElement> elements,
                                  ReadonlyLinqingList.Because reasonGiven){
        this(reasonGiven, noOriginGiven);
        for(TElement element : elements){
            super.add(element);
        }
    }

    @Override
    public boolean add(TElement value){
        throwReadonly("add");
        return false;
    }
    @Override
    public void add(int index, TElement value){
        throwReadonly("add");
    }
    @Override
    public boolean addAll(Collection<? extends TElement> collection){
        throwReadonly("addAll");
        return false;
    }
    @Override
    public boolean addAll(int targetIndex, Collection<? extends TElement> collection){
        throwReadonly("addAll");
        return false;
    }
    @Override
    public TElement set(int index, TElement value){
        throwReadonly("set");
        return null;
    }
    @Override
    public void clear(){
        throwReadonly("clear");
    }
    @Override
    public boolean remove(Object element){
        throwReadonly("remove");
        return false;
    }
    @Override
    public TElement remove(int index){
        throwReadonly("remove");
        return null;
    }
    @Override
    public boolean removeAll(Collection<?> collection){
        throwReadonly("removeAll");
        return false;
    }
    @Override
    public void removeRange(int left, int right){
        throwReadonly("removeRange");
    }
    @Override
    public boolean retainAll(Collection<?> collection){
        throwReadonly("retainAll");
        return false;
    }
    @Override
    public Iterator<TElement> iterator(){
        return super.listIterator();
    }

    private void throwReadonly(String methodName) {
        String message = "cannot '" + methodName + "' a " + getClass().getSimpleName() + reasonGiven.getDescription() + "\n"
                + "currently the set is: \n\t"
                + StringUtils.join(this.select(NullSafeToString).iterator(), "\n\t");
        throw new UnsupportedOperationException(message, origin);
    }
}
