public class Main {
    public static void main(String[] args) {
//        Properties props=new Properties();
//        try {
//            props.load(new FileReader("bd.config"));
//        } catch (IOException e) {
//            System.out.println("Cannot find bd.config "+e);
//        }
//        System.out.println("Succes");
//
//        UtilizatorRepository utilizatorRepository = new UtilizatorDBRepository(props);
//        for(Object inscriere:utilizatorRepository.findAll())
//            System.out.println(inscriere);
//
//        ParticipantRepository participantRepository = new ParticipantDBRepository(props);
//        for(Object participant:participantRepository.filterByName("Kelu"))
//            System.out.println(participant);
//
//        ProbaRepository probaRepository = new ProbaDBRepository(props);
//        for(Object proba:probaRepository.findAll())
//            System.out.println(proba);
        MainApp.main(args);
    }
}
