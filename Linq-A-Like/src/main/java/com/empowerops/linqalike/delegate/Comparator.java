package com.empowerops.linqalike.delegate;

import com.empowerops.linqalike.CommonDelegates;

import java.io.Serializable;

import static com.empowerops.common.ReflectionUtilities.getSimpleHumanReadableName;

//class exists so Xstream will flag the comparators as serializable
@FunctionalInterface
public interface Comparator<TCompared> extends java.util.Comparator<TCompared>, Serializable {


    @Override int compare(TCompared left, TCompared right);


    public static <TCompared> Comparator<TCompared> of(Class<? super TCompared> minimumType, Comparator<TCompared> baseComparator){
        return new WithTypeBounds<>(minimumType, baseComparator);
    }

    public static class WithTypeBounds<TInspected> extends WithDescription<TInspected> {

        private static final long serialVersionUID = 920463194546763050L;
        private final Class<? super TInspected> minimumType;

        public WithTypeBounds(Class<? super TInspected> minimumType, Comparator<TInspected> delegate) {
            super(makeComparatorTypeMessageFor(minimumType, delegate), delegate);
            this.minimumType = minimumType;
        }

        private static String makeComparatorTypeMessageFor(Class type, Comparator delegate) {
            String humanReadableType = getSimpleHumanReadableName(type);
            return "Type bounded Comparator: " +
                    "(left, right) -> " + humanReadableType + ".isInstance(left) && " + humanReadableType + ".isInstance(right) " +
                    "? {" + delegate + "}.compare(left, right) " +
                    ": CommonDelegates.HashCodeComparator.compare(left, right) ";
        }

        @Override
        public int compare(TInspected left, TInspected right) {
            return minimumType.isInstance(left) && minimumType.isInstance(right)
                    ? super.compare(left, right)
                    : CommonDelegates.HashCodeComparator.compare(left, right);
        }
    }

    public static class WithDescription<TInspected> implements Comparator<TInspected>{

        private static final long serialVersionUID = - 6485231615470764737L;

        public final String                 description;
        public final Comparator<TInspected> delegate;

        public WithDescription(String description, Comparator<TInspected> delegate) {
            this.description = description;
            this.delegate = delegate;
        }

        @Override
        public String toString() {
            return description;
        }

        @Override public int compare(TInspected left, TInspected right) {
            return delegate.compare(left, right);
        }
    }

}
