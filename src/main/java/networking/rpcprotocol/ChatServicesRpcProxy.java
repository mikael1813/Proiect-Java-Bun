package networking.rpcprotocol;

import domain.Participant;
import domain.Proba;
import domain.Utilizator;
import javafx.application.Platform;
import networking.dto.*;
import service.AppException;
import service.IAppObserver;
import service.IService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ChatServicesRpcProxy implements IService {
    private String host;
    private int port;

    private IAppObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public ChatServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
    }

    public void login(Utilizator user, IAppObserver client) throws AppException {
        initializeConnection();
        UserDTO udto= DTOUtils.getDTO(user);
        Request req=new Request.Builder().type(RequestType.LOGIN).data(udto).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            this.client=client;
            return;
        }
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            throw new AppException(err);
        }
    }

    public Proba[] getProbe() throws AppException {
        //ParticipantDTO udto= DTOUtils.getDTO(user);
        Request req=new Request.Builder().type(RequestType.GET_PROBE).data().build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
        ProbaDTO[] probaDTO = (ProbaDTO[]) response.data();
        Proba[] probe = DTOUtils.getFromDTO(probaDTO);
        return probe;
    }

    @Override
    public Participant[] getParticipantiDupaProba(Proba p) throws AppException {
        ProbaDTO udto= DTOUtils.getDTO(p);
        Request req=new Request.Builder().type(RequestType.GET_PARTICIPANTI_DUPA_PROBE).data(udto).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
        ParticipantDTO[] participantDTO = (ParticipantDTO[]) response.data();
        Participant[] probe = DTOUtils.getFromDTO(participantDTO);
        return probe;
    }

    @Override
    public Proba[] getProbeDupaParticipanti(Participant p) throws AppException{
        ParticipantDTO udto= DTOUtils.getDTO(p);
        Request req=new Request.Builder().type(RequestType.GET_PROBE_DUPA_PARTICIPANT).data(udto).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
        ProbaDTO[] probaDTO = (ProbaDTO[]) response.data();
        Proba[] probe = DTOUtils.getFromDTO(probaDTO);
        return probe;
    }

    @Override
    public void Inscrie(Participant participant, Proba[] probe) throws AppException {
        InscriereDTO idto = new InscriereDTO(participant,probe);
        Request req=new Request.Builder().type(RequestType.INSCRIE).data(idto).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            this.client=client;
            return;
        }
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            throw new AppException(err);
        }
    }

    @Override
    public Integer getNrParticipanti(Proba p) throws AppException {
        ProbaDTO udto= DTOUtils.getDTO(p);
        Request req=new Request.Builder().type(RequestType.GET_NR_PARTICIPANTI).data(udto).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
        Integer nr = (Integer) response.data();
        return nr;
    }


    public void logout(Utilizator user, IAppObserver client) throws AppException {
        UserDTO udto= DTOUtils.getDTO(user);
        Request req=new Request.Builder().type(RequestType.LOGOUT).data(udto).build();
        sendRequest(req);
        Response response=readResponse();
        closeConnection();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new AppException(err);
        }
    }


    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request)throws AppException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new AppException("Error sending object "+e);
        }

    }

    private Response readResponse() throws AppException {
        Response response=null;
        try{

            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
    private void initializeConnection() throws AppException {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response){
        if (response.type()== ResponseType.NEW_INSCRIERE){

            //Participant participant= DTOUtils.getFromDTO((ParticipantDTO) response.data());
            //System.out.println("Inscriere noua: "+participant);
            try {
                client.newInscriere();
            } catch (AppException e) {
                e.printStackTrace();
            }
        }
//        if (response.type()== ResponseType.FRIEND_LOGGED_IN){
//
//            User friend= DTOUtils.getFromDTO((UserDTO) response.data());
//            System.out.println("Friend logged in "+friend);
//            try {
//                client.friendLoggedIn(friend);
//            } catch (ChatException e) {
//                e.printStackTrace();
//            }
//        }
//        if (response.type()== ResponseType.FRIEND_LOGGED_OUT){
//            User friend= DTOUtils.getFromDTO((UserDTO)response.data());
//            System.out.println("Friend logged out "+friend);
//            try {
//                client.friendLoggedOut(friend);
//            } catch (ChatException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (response.type()== ResponseType.NEW_MESSAGE){
//            Message message= DTOUtils.getFromDTO((MessageDTO)response.data());
//            try {
//                client.messageReceived(message);
//            } catch (ChatException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private boolean isUpdate(Response response){
        return response.type()== ResponseType.NEW_INSCRIERE || response.type()== ResponseType.FRIEND_LOGGED_IN || response.type()== ResponseType.NEW_MESSAGE;
    }
    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{

                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
