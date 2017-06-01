package com.sven;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IndexerService
{
    @Autowired
    private XdbRepository xdb;

    
    public void index(final String folder)
    {
        
        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        rt.getInterceptors().add(new BasicAuthorizationInterceptor("user", "letmein"));
        for (String xml : xdb.getMonographs(folder)) {
            
            try {
                rt.put("http://127.0.0.1:8080/indexer/topics", xml);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
