package LinqALike.Common;

/**
 * This class is effectively a double pointer, allowing you to ether modify
 * something by reference, or escape javas 'members in a closure must be final'
 * semantics.
 *
 * @author Geoff on 24/07/13
 */
public class Ref<TValue>{

	/*
	FindBugs flagged this as:
	Unread public/protected field
	This field is never read.  The field is public or protected, so perhaps it is intended to be used with classes not seen as part of the analysis. If not, consider removing it from the class.

	Bug kind and pattern: UrF - URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD*/
    public TValue target;

    public Ref() {
    }
    public Ref(TValue value){
        this.target = value;
    }
}
