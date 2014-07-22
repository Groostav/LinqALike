package UnitTests;

import com.EmpowerOperations.LinqALike.LinqingList;
import org.junit.Test;

import static Assists.Exceptions.assertThrows;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Justin on 7/18/2014.
 */
public class LinqingListFixture {
    @Test
    public void when_moving_an_element_in_the_list(){
        //Setup
        LinqingList<Integer> favoritePlayers = new LinqingList<>(8,23,13,99,1,21);

        //Act
        favoritePlayers.move(23, newLocation -> newLocation.left == 1 && newLocation.right == 21, () -> Integer.MAX_VALUE);

        //Assert
        assertThat(favoritePlayers).containsExactly(8,13,99,1,23,21);
    }

    @Test
    public void when_moving_to_yourself_move_call_should_do_nothing() {
        //Setup
        LinqingList<Double> shoeSizes = new LinqingList<>(5d, 13d, 10.5, 11.5, 25d);

        //Act
        shoeSizes.move(10.5, element -> element.left == 13d && element.right == 11.5, () -> Double.NaN);

        //assert
        assertThat(shoeSizes).containsExactly(5d, 13d, 10.5, 11.5, 25d);
    }

    @Test
    public void when_moving_through_bag_element_should_move_appropriately(){
        //Setup
        LinqingList<String> names = new LinqingList<>("Justin", "Geoff", "Vincent", "Gary", "Geoff", "George");

        //Act
        names.move("Vincent", position -> position.left.equals("Geoff") && position.right.equals("George"), () -> "");

        //Assert
        assertThat(names).containsExactly("Justin", "Geoff", "Gary", "Geoff", "Vincent", "George");
    }

    @Test
    public void when_moving_between_duplicates_in_a_bag_move_should_act_appropriately(){
        //Setup
        LinqingList<String> names = new LinqingList<>("Justin", "Vincent", "Gary", "Josh", "Geoff", "Geoff", "George");

        //Act
        names.move("Vincent", element -> "Geoff".equals(element.left)&& "George".equals(element.right), () -> null);

        //Assert
        assertThat(names).containsExactly("Justin", "Gary", "Josh", "Geoff", "Geoff", "Vincent", "George");
    }

//    @Test
//    public void (){
//
//    }
}
