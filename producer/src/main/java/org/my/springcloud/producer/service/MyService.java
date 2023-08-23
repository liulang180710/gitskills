package org.my.springcloud.producer.service;

public class MyService {
    public void A() {
        System.out.println("This is A().");
    }

    public String B(int i) {
        return "This is B()." + i;
    }
}
