package org.tunnel.serverapi.rest;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.tunnel.serverapi.config.SocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MassagerResource {

    @Autowired
    SocketServer socketServer;

    @GetMapping("/{clientId}/**")
    public Object sendMessage(@PathVariable("clientId")Integer clientPort, HttpServletRequest request) throws IOException {
        String url = request.getRequestURL().toString();
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        Socket clientSocket = socketServer.getClients().get(clientPort);
        int port = clientSocket.getPort();
        System.out.println("port "+port);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        Thread.ofVirtual().start(() -> {
            if(!clientSocket.isClosed()){
                System.out.println("Server message at " + requestUri);
                out.println(requestUri.replace("/"+clientPort , ""));
            }
        });

        String inputLine;
        JsonNode jsonNode = null;
        ObjectMapper objectMapper = new ObjectMapper();
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received from client: " + inputLine);
             jsonNode = objectMapper.readTree(inputLine);
            break;
        }

        return jsonNode;
    }
}
