package com.example;

import com.example.service.StudentServiceImpl;

import javax.xml.ws.Endpoint;

public class Main {
    public static void main(String[] args) {
        String url = "http://localhost:8080/student";
        Endpoint.publish(url, new StudentServiceImpl());
        System.out.println("SOAP service running at: " + url + "?wsdl");
    }
}
