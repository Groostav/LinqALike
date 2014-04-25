package UsageExamples;

public class ArchetectureExamples {

    //first, lets say you have some service
    public static class ServiceComponent{

        public void startGenerationService(){
            new Thead(this::doCoolWebQueries);
        }

        public void doCoolWebQueries(){
            while(true){
                Thread.sleep(20000);

            }
        }
    }

    public static class UserFacingComponent{

    }

}
