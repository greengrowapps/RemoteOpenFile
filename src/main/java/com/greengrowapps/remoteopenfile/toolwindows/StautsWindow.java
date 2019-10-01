package com.greengrowapps.remoteopenfile.toolwindows;

import com.greengrowapps.remoteopenfile.server.RemoteOpenFileServerServer;
import com.greengrowapps.remoteopenfile.services.RofAppService;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StautsWindow implements RofAppService.RofServiceListener {
    private static int runCount=0;
    private JButton hideToolWindowButton;
    private JPanel myToolWindowContent;
    private JLabel statusText;
    private JTextArea itemsText;
    private JButton startButton;
    private JButton stopButton;

    public StautsWindow(ToolWindow toolWindow) {

        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        itemsText.setText("");
        RofAppService service = ServiceManager.getService(RofAppService.class);
        service.registerListener(this);
        if(service.isRunning()){
            onConnected(service.getPort());
        }else{
            onDisconnected();
        }
        this.refreshInterface();
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RofAppService service = ServiceManager.getService(RofAppService.class);
                service.start();
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RofAppService service = ServiceManager.getService(RofAppService.class);
                service.stop();
            }
        });
    }

    public void refreshInterface(){

       StringBuilder sb=new StringBuilder();

       sb.append("Hello world "+runCount+"\n");

        runCount++;
    }



    public JPanel getContent() {
        return myToolWindowContent;
    }

    @Override
    public void onConnected(int port) {
        statusText.setText("Service running on port: "+port);
        startButton.setVisible(false);
        stopButton.setVisible(true);
    }

    @Override
    public void onDisconnected() {
        statusText.setText("Not running");
        startButton.setVisible(true);
        stopButton.setVisible(false);
    }

    @Override
    public void clientsListChange(List<RemoteOpenFileServerServer.ClientInfo> clients) {
        this.itemsText.setText("");
        StringBuilder sb=new StringBuilder();
        int i=1;
        for (RemoteOpenFileServerServer.ClientInfo client: clients ) {
            sb.append("------------------Client "+i+ " ("+client.getUniqId()+")------------\n");
            sb.append("Origin: "+client.getOrigin()+"\n");
            sb.append("Path: "+client.getPath()+"\n");
            sb.append("Search: "+client.getSearch()+"\n");
            if(client.hasLastRequest()){
                sb.append("Last request: "+client.getLastResquetedFile()+" Line:"+client.getLastRequestedLine()+"\n");
            }
            i++;
        }
        this.itemsText.setText(sb.toString());
    }
}
