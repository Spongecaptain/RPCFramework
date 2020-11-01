package cool.spongecaptain.api.impl;

import cool.spongecaptain.api.SayHelloInterface;

public class SayHelloImpl implements SayHelloInterface {
    @Override
    public String sayHello(String name) {
        return "Hello " + name + " !";
    }
}

