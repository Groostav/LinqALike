package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.DefaultedQueryable;
import com.empowerops.linqalike.Factories;
import com.empowerops.linqalike.Queryable;
import com.empowerops.linqalike.common.Formatting;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.Iterator;

import static com.empowerops.linqalike.CommonDelegates.nullSafeEquals;
import static com.empowerops.linqalike.common.Formatting.otherwiseThrow;

public class ObservableLinkedLinqingList<TElement>
        extends ObservableLinqingList<TElement>
        implements DefaultedQueryable<TElement> {

    public interface Nodable<TSelf>{
        public void setLinkedListNode(ElementNode<TSelf> node);
    }

    public static class ElementNode<TElement>{

        private static boolean isNull(ElementNode node){
            return node == null || node.isNull();
        }

        @SuppressWarnings("unchecked") //correct usage is Nodable<TThis>.
        public ElementNode(){
            element.addListener((source, oldVal, newVal) -> {
                if (oldVal instanceof Nodable) {
                    ((Nodable) oldVal).setLinkedListNode(null);
                }
                if (newVal instanceof Nodable) {
                    ((Nodable) newVal).setLinkedListNode(this);
                }
            });
        }

        public ElementNode(TElement initialValue){
            this();
            element.setValue(initialValue);
        }

        final ObjectProperty<TElement> element =                                    new SimpleObjectProperty<>();
        public ObjectProperty<TElement> elementProperty()                           { return element; }
        public TElement getElement()                                                { return element.getValue(); }
        public void setElement(TElement newValue)                                   { element.setValue(newValue);}

        public final ObjectProperty<ElementNode<TElement>> leftElement =            new SimpleObjectProperty<>();
        public ReadOnlyObjectProperty<ElementNode<TElement>> leftElementProperty()  { return leftElement; }
        public boolean hasLeftElement()                                             { return leftElement.get() != null; }
        public ElementNode<TElement> getLeftElement()                               { return leftElement.getValue(); }
        protected void setLeftElement(ElementNode<TElement> newLeftElement) {
            leftElement.setValue(isNull(newLeftElement) ? null : newLeftElement);
        }

        public final ObjectProperty<ElementNode<TElement>> rightElement =           new SimpleObjectProperty<>();
        public ReadOnlyObjectProperty<ElementNode<TElement>> rightElementProperty() { return rightElement; }
        public boolean hasRightNeighbour()                                          { return rightElement.get() != null; }
        public ElementNode<TElement> getRightElement()                              { return rightElement.getValue(); }

        protected void setRightElement(ElementNode<TElement> newRightNeighbour) {
            rightElement.setValue(isNull(newRightNeighbour) ? null : newRightNeighbour);
        }
        protected boolean isNull() { return false; }

        @Override
        public String toString() {
            return "ObservableNode[" +
                    "left=" + (Formatting.nullSafeToString(
                    hasLeftElement() ? leftElement.get().getElement() : null)) + ", " +
                    "this=" + Formatting.nullSafeToString(getElement()) + ", " +
                    "right=" + (Formatting.nullSafeToString(
                    hasRightNeighbour() ? rightElement.get().getElement() : null)) + "]";
        }
    }

    private final ElementNode<TElement> nullNode = new ElementNode<TElement>() {
        @Override
        protected boolean isNull() { return true; }
        @Override
        public void setElement(Object newValue) {}
        @Override
        public String toString() { return "Null Node"; }

        @Override
        public boolean hasLeftElement() { return false; }
        @Override
        public ElementNode<TElement> getLeftElement() { return this; }
        @Override
        protected void setLeftElement(ElementNode newLeftElement) { /*do nothing*/ }

        @Override
        public boolean hasRightNeighbour() { return false; }
        @Override
        public ElementNode<TElement> getRightElement() { return this; }
        @Override
        protected void setRightElement(ElementNode newRightNeighbour) { /*do nothing*/ }
    };

    private ElementNode<TElement> head = nullNode;

    public ObservableLinkedLinqingList() {
    }

    @SafeVarargs
    public ObservableLinkedLinqingList(TElement... initialElements) {
        this(Factories.from(initialElements));
    }

    public ObservableLinkedLinqingList(Iterable<TElement> initialElements) {
        addAll(Factories.asList(initialElements));
    }

    @Override
    public TElement get(int index) {
        return nodeFor(index).getElement();
    }

    private int lastKnownModCount = modCount;
    private int cachedSize        = 0;
    @Override
    public int size() {

        if (lastKnownModCount == modCount) {
            return cachedSize;
        }

        if (head.isNull()) {
            cachedSize = 0;
            return cachedSize;
        }

        ElementNode<TElement> current = head;
        int count = 1;
        while (!current.isNull() && current.hasRightNeighbour()) {
            count += 1;
            current = current.getRightElement();
        }

        cachedSize = count;
        return cachedSize;
    }

    @Override
    protected synchronized void doAdd(int index, TElement newElement) {
        assertIsValidInsertionIndex(index);
        boolean isAppending = index == size();
        boolean isFirst = index == 0;


        ElementNode<TElement> newOccupant = new ElementNode<>(newElement);
        ElementNode<TElement> currentOccupant = isEmpty() || isAppending ? nullNode : nodeFor(index);
        ElementNode<TElement> leftNeighbour = isEmpty() ? nullNode :
                                              isAppending ? nodeFor(index - 1) :
                                              isFirst ? nullNode :
                                              any() ? currentOccupant.getLeftElement() :
                                              otherwiseThrow(new IllegalStateException());

        setAsNeighbourPair(leftNeighbour, newOccupant);
        setAsNeighbourPair(newOccupant, currentOccupant);

        updateHead(newOccupant);
        nodes.add(newOccupant);
    }

    @Override
    protected synchronized TElement doSet(int index, TElement replacementElement) {
        assertContainsIndex(index);

        ElementNode<TElement> node = nodeFor(index);
        TElement evicted = node.getElement();
        node.setElement(replacementElement);

        return evicted;
    }

    @Override
    protected synchronized TElement doRemove(int index) {
        assertContainsIndex(index);

        ElementNode<TElement> evicted = nodeFor(index);
        ElementNode<TElement> leftNeighbour = evicted.hasLeftElement() ? evicted.getLeftElement() : nullNode;
        ElementNode<TElement> rightNeighbour = evicted.hasRightNeighbour() ? evicted.getRightElement() : nullNode;

        setAsNeighbourPair(leftNeighbour, rightNeighbour);
        dissociate(evicted);
        updateHead(
                !head.isNull() && head != evicted ? head :
                leftNeighbour.isNull() ? rightNeighbour :
                rightNeighbour.isNull() ? leftNeighbour :
                otherwiseThrow(new IllegalStateException("removing from an empty list?"))
        );

        return evicted.getElement();
    }

    public ElementNode<TElement> nodeFor(int index) {
        assertContainsIndex(index);

        ElementNode<TElement> current = head;
        for (int ignored : Factories.range(0, index)) {
            current = current.getRightElement();
        }

        return current;
    }

    public ElementNode<TElement> nodeFor(TElement element) {
        ElementNode<TElement> current = head;

        while (!current.isNull()) {

            if (nullSafeEquals(current.getElement(), element)) {
                return current;
            }

            current = current.hasRightNeighbour() ? current.getRightElement() : nullNode;
        }

        return null;
    }

    public Queryable<ElementNode<TElement>> nodes(){
        return (DefaultedQueryable<ElementNode<TElement>>) () -> new Iterator<ElementNode<TElement>>() {

            private ElementNode<TElement> current = head;

            @Override
            public boolean hasNext() {
                return current.hasRightNeighbour();
            }

            @Override
            public ElementNode<TElement> next() {
                return current.getRightElement();
            }
        };
    }

    private void assertContainsIndex(int index) {
        if (index >= size()) {
            throw new IndexOutOfBoundsException("asked to get index:" + index + " but list contains only " + size() + " elements.");
        }
    }

    private void assertIsValidInsertionIndex(int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("asked to insert at index:" + index + " but list contains only " + size() + " elements.");
        }

    }

    private void updateHead(ElementNode<TElement> nodeStillOnList) {

        ElementNode<TElement> current = nodeStillOnList;

        while (current.hasLeftElement()) {
            current = current.getLeftElement();
        }

        if (current != head) {
            head = current;
        }

    }

    private void setAsNeighbourPair(ElementNode<TElement> leftNeighbour, ElementNode<TElement> rightNeighbour) {

        leftNeighbour.setRightElement(rightNeighbour);
        rightNeighbour.setLeftElement(leftNeighbour);
    }

    private void updateNeighboursFor(ElementNode<TElement> nodeToInsert,
                                     ElementNode<TElement> leftNeighbour,
                                     ElementNode<TElement> rightNeighbour) {

        nodeToInsert.setLeftElement(leftNeighbour);
        leftNeighbour.setRightElement(nodeToInsert);
        nodeToInsert.setRightElement(rightNeighbour);
        rightNeighbour.setLeftElement(nodeToInsert);
    }

    private void dissociate(ElementNode<TElement> currentOccupant) {
        updateNeighboursFor(currentOccupant, nullNode, nullNode);
    }

    private final ArrayList<ElementNode<TElement>> nodes = new ArrayList<>();
}

