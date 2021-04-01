package server;

import networking.utils.AbstractServer;
import networking.utils.ChatRpcConcurrentServer;
import networking.utils.ServerException;
import repository.InscriereRepository;
import repository.ParticipantRepository;
import repository.ProbaRepository;
import repository.UtilizatorRepository;
import repository.databases.InscriereDBRepository;
import repository.databases.ParticipantDBRepository;
import repository.databases.ProbaDBRepository;
import repository.databases.UtilizatorDBRepository;
import service.AppException;
import service.Service;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort=55555;
    public static void main(String[] args) {
        // UserRepository userRepo=new UserRepositoryMock();
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/chatserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
            return;
        }
        UtilizatorRepository utilizatorRepository = new UtilizatorDBRepository(serverProps);
        ParticipantRepository participantRepository = new ParticipantDBRepository(serverProps);
        ProbaRepository probaRepository = new ProbaDBRepository(serverProps);
        InscriereRepository inscriereRepository = new InscriereDBRepository(serverProps,probaRepository,participantRepository);

        Service service = new Service(inscriereRepository, participantRepository, probaRepository, utilizatorRepository);
        int chatServerPort=defaultPort;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("chat.server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+chatServerPort);
        AbstractServer server = new ChatRpcConcurrentServer(chatServerPort, service);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                System.err.println("Error stopping server "+e.getMessage());
            }
        }
    }
}
