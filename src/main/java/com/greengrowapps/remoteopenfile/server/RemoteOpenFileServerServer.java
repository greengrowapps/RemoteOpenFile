package com.greengrowapps.remoteopenfile.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greengrowapps.remoteopenfile.server.Messages.*;
import com.greengrowapps.remoteopenfile.services.LogService;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class RemoteOpenFileServerServer extends WebSocketServer {


    private LogService logger = ServiceManager.getService(LogService.class);
    private HashMap<WebSocket,ClientInfo> currentClients=new HashMap<>();
    private RofListener listener;


    public RemoteOpenFileServerServer(int port, RofListener listener) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
        this.listener=listener;
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        String content=serializeHello(getProjectNames());
        conn.send(content);
        log(  conn.hashCode() + " connected with address: "+conn.getRemoteSocketAddress()+" "+conn.getLocalSocketAddress() );
    }


    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        if(currentClients.containsKey(conn)){
            currentClients.remove(conn);
        }
        listener.clientListChange();
        log( conn.hashCode() + " disconnected" );
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        log( conn.hashCode() + ": " + message );

        BaseMsg r = parseMessage(message);
        if(r instanceof  HelloFromClientMsg){
            HelloFromClientMsg hfc= (HelloFromClientMsg) r;
            if(!currentClients.containsKey(conn)){
                currentClients.put(conn,new ClientInfo(hfc.origin,hfc.path,hfc.search,conn.hashCode()));
            }else{
                log("Already exist "+conn.hashCode());
            }
            listener.clientListChange();

        }else if (r instanceof RemoteOpenFileMsg){
            RemoteOpenFileMsg rof = (RemoteOpenFileMsg) r;
            if(currentClients.containsKey(conn)){
                currentClients.get(conn).setLastResquest(rof.filePath,rof.line);
            }
            listener.clientListChange();
            listener.onOpenFileRequest(rof.filePath,rof.line);
        }else{
            log("Message "+r.getClass()+" not implemented");
        }
        conn.send(serializeEcho(message));
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        log( conn.hashCode() + "buffer message : " + message );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        log("onError "+ex);
        if( conn != null ) {
            log(conn.hashCode()+" onError"+ex);            
        }else{
            listener.onServerError();
        }
    }

    @Override
    public void onStart() {
        log("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public List<String> getProjectNames() {
        ProjectManager PM = ProjectManager.getInstance();
        List<String> ret = new ArrayList<>();
        for (Project p : PM.getOpenProjects()){
            ret.add(p.getName());
        }
        return ret;
    }

    public List<ClientInfo> getClients() {
        List<ClientInfo> ret=new ArrayList<>();
        for (ClientInfo info:currentClients.values()) {
            ret.add(info);
        }
        return ret;
    }

    private  void log(String text){
        logger.writeLine(text);
    }

    public ObjectMapper getObjectMaper(){
        ObjectMapper mapper = new ObjectMapper();
        return mapper;
    }

    private BaseMsg parseMessage(String message) {
        try {
            ObjectMapper mapper = getObjectMaper();
            BaseMsg msg =mapper.readValue(message, BaseMsg.class);
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
           log("Error parsing "+message+" "+e);
           return new BaseMsg();
        }
    }

    private String serializeEcho(String message) {
        ObjectMapper maper = getObjectMaper();
        EchoMsg content=new EchoMsg();
        content.echo=message;
        try {
            return maper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log("Error in echo serialization "+e);
            return "error";
        }
    }

    private String serializeHello(List<String> projectNames) {
        ObjectMapper maper = getObjectMaper();
        HelloFromServerMsg content=new HelloFromServerMsg();
        content.projects=projectNames;
        try {
            return maper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log("Error in hello serialization "+e);
            return "error";
        }
    }



    public interface RofListener {
        void onOpenFileRequest(String filename, int lineNumber);
        void clientListChange();

        void onServerError();
    }

    public class ClientInfo{

        private final String origin;
        private final String path;
        private final String search;
        private final int uniqId;

        private String lastResquetedFile;
        private int lastRequestedLine;

        public ClientInfo(String origin, String path, String search,int uniqId) {
            this.origin=origin;
            this.path=path;
            this.search=search;
            this.uniqId=uniqId;
        }

        public boolean hasLastRequest(){
            return lastResquetedFile != null;
        }

        public int getLastRequestedLine() {
            return lastRequestedLine;
        }

        public String getLastResquetedFile() {
            return lastResquetedFile;
        }

        public void setLastResquest(String file, int line) {
            this.lastResquetedFile=file;
            this.lastRequestedLine=line;
        }

        public String getOrigin() {
            return origin;
        }

        public String getPath() {
            return path;
        }

        public String getSearch() {
            return search;
        }

        public int getUniqId() {
            return uniqId;
        }
    }

}