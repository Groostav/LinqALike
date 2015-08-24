package com.empowerops.linqalike;

/**
 * Represents a service or controller whose functionality is derrived by adding and removing things from a group.
 *
 * <p>As of writing, the only consumer of this interface is the {@link com.empowerops.linqalike.GroupsChangeAnalyzer}.
 * The idea is that classes that implement this interface 'control' some number of elements.
 * But those elements are modelled as members of (other) immutable (or otherwise annoying, eg non-notifying)
 * collections. The purpose of this class as the <code>GroupsChangeAnalyzer</code> is vicariously bootstrap
 * a notification system onto these immutable sets.
 *
 * Created by Geoff on 2015-02-20.
 */
public interface MutableGroupController<TImmutableGroup, TMember>{

    /**
     * Used to determine if the this mutable group has the same members as an immutable group.
     *
     * <p>If this method returns <tt>true</tt>, the groups change analyzer will do a further inspection on the
     */
    public boolean hasSameMembersAs(TImmutableGroup groupToCompareTo);

    /**
     * Getter for the members of the group.
     */
    public WritableCollection<TMember> memberPoints();

    public interface Factory<TImmutableGroup, TMember, TMutableGroup extends MutableGroupController<TImmutableGroup, TMember>>{
        TMutableGroup create(TImmutableGroup targetGroup);
    }

    public interface NeedsMeta<TImmutableGroup, TMember> extends MutableGroupController<TImmutableGroup, TMember> {
        void setImmutableSource(TImmutableGroup source);
    }
}
