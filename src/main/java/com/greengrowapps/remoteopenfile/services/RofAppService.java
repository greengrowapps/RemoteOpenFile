package com.greengrowapps.remoteopenfile.services;

import com.greengrowapps.remoteopenfile.server.RemoteOpenFileServerServer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.java_websocket.WebSocketImpl;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;

public class RofAppService implements RemoteOpenFileServerServer.RofListener {
    private RemoteOpenFileServerServer server = null;
    private int currentPort;
    private LogService logger = ServiceManager.getService(LogService.class);
    private List<RofServiceListener> listeners=new ArrayList<>();


    private void onDisconnected(){
        synchronized (listeners){
            for (RofServiceListener l: listeners) {
                l.onDisconnected();
            }
        }
    }

    private void onConnected(int port){
        synchronized (listeners){
            for (RofServiceListener l: listeners) {
                l.onConnected(port);
            }
        }
    }

    private void onClientListChange(){
        List<RemoteOpenFileServerServer.ClientInfo> clients = this.server.getClients();
        synchronized (listeners){
            for (RofServiceListener l: listeners) {
                l.clientsListChange(clients);
            }
        }
    }

    public void  start(){
        if (server == null) {
            startServer();
        } else {
            log("Server is running");
        }
    }
    public void stop() {
        if(server != null){
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            server=null;
        }
        onDisconnected();
    }

    private void startServer() {

        for (int port = 8880; port <= 8889; port++) {
            try {
                if (!availablePort(port)) {
                    log("Port " + port + " not available");
                    continue;
                }
                RemoteOpenFileServerServer server = new RemoteOpenFileServerServer(port, RofAppService.this);
                server.start();
                RofAppService.this.currentPort = server.getPort();
                RofAppService.this.server = server;
                onConnected(server.getPort());
                log("Rof started on port: " + server.getPort());
                break;
            } catch (Exception e) {
                log("Port " + port + " in use");
                e.printStackTrace();
            }
        }

    }

    private boolean availablePort(int port) {
        boolean result = false;
        try {
            ServerSocketChannel s = ServerSocketChannel.open();
            s.configureBlocking( false );
            ServerSocket socket = s.socket();
            socket.setReceiveBufferSize( WebSocketImpl.RCVBUF );
            socket.setReuseAddress( false );
            socket.bind( new InetSocketAddress(port) );
            s.close();
            result = true;
        }
        catch(Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onOpenFileRequest(String filename, int lineNumber) {
        ProjectManager PM = ProjectManager.getInstance();
        Project[] AllProjects = PM.getOpenProjects();

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                for (Project p : AllProjects ) {
                    openFileAndLineNumber(p,filename,lineNumber);
                }
            }
        }, ModalityState.NON_MODAL);
    }

    @Override
    public void clientListChange() {
        onClientListChange();
    }

    @Override
    public void onServerError() {
       onDisconnected();
    }


    public void openFileAndLineNumber(Project project, String filename, int lineNumber) {
        PsiFile[] files = FilenameIndex.getFilesByName(project, filename, GlobalSearchScope.allScope(project));

        boolean found = false;
        for (PsiFile f : files) {
            found = true;

            FileEditor[] a = FileEditorManager.getInstance(project).openFile(f.getVirtualFile(), true);
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            goToLineInEditor(lineNumber, editor, project);
        }
        if (!found) {
            log("File " + filename + " not found in project "+project.getName());
        }
    }


    private void goToLineInEditor(int lineNumber, Editor editor,Project project){
        if( editor == null ){
            log("No editor opened");
            return;
        }

        CaretModel caretModel = editor.getCaretModel();
        int totalLineCount = editor.getDocument().getLineCount();

        if( lineNumber > totalLineCount ){
            log("Line number biger than line count");
            return;
        }

        //Moving caret to line number
        caretModel.moveToLogicalPosition(new LogicalPosition(lineNumber-1,0));

        //Scroll to the caret
        ScrollingModel scrollingModel = editor.getScrollingModel();
        scrollingModel.scrollToCaret(ScrollType.CENTER);
    }


    private void log(String line){
        logger.writeLine(line);
    }

    public void registerListener(RofServiceListener listener) {
        synchronized (listeners){
            listeners.add(listener);
        }
    }

    public int getPort() {
        return this.currentPort;
    }

    public boolean isRunning() {
       return this.server != null;
    }

    public interface RofServiceListener {
        void onConnected(int port);
        void onDisconnected();
        void clientsListChange(List<RemoteOpenFileServerServer.ClientInfo> clients);
    }
}
