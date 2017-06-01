package com.sven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MainRunner implements CommandLineRunner
{

    @Autowired
    private IndexerService serivce;
    @Override
    public void run(final String... args) throws Exception
    {
        serivce.index("/BP/ROWAN/en-gb");
        //serivce.index("/BP/ROWAN/zh-cn");
    }
}
