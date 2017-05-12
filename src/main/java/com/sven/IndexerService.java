package com.sven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IndexerService
{
    @Autowired
    private XdbRepository xdb;

    
    public void index(String folder)
    {
        
        RestTemplate rt = new RestTemplate();
        rt.getInterceptors().add(new BasicAuthorizationInterceptor("user", "letmein"));
        for (String xml : xdb.getMonographs(folder)) {
            
            try {
                rt.put("http://127.0.0.1:8080/internal/indexer/topics", xml);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
