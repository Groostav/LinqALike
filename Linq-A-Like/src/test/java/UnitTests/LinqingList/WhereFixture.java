package UnitTests.LinqingList;

import UnitTests.Queries.WhereQueryFixture;
import com.EmpowerOperations.LinqALike.LinqingList;
import com.EmpowerOperations.LinqALike.Queryable;

/**
 * Created by Geoff on 2014-05-23.
 */
public class WhereFixture extends WhereQueryFixture{

    @Override
    public <TElement> Queryable<TElement> makeSetFor(TElement... initialMembers) {
        return new LinqingList<>(initialMembers);
    }
}
