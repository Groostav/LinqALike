package LinqALike.Common;

/**
 * This class is effectively a double pointer, allowing you to ether modify
 * something by reference, or escape javas 'members in a closure must be final'
 * semantics.
 *
 * @author Geoff on 24/07/13
 */
public class Ref<TValue>{

    public TValue target;

    public Ref() {
    }
    public Ref(TValue value){
        this.target = value;
    }
}
