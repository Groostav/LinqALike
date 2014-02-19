package LanguageSmokeTests;


public class when_two_unrelated_interfaces_provide_the_same_signature_default_impl {

    interface FirstInterface{
        default public String DoSomething(){
            return "first";
        }
    }
    interface SecondInterface{
        default public String DoSomething(){
            return "second";
        }
    }

//    class FirstAndSecondImpl implements FirstInterface, SecondInterface{
//        compile-time exception. Ok.
//    }

}
