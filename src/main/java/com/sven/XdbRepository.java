package com.sven;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xhive.XhiveDriverFactory;
import com.xhive.core.interfaces.XhiveDriverIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.dom.interfaces.XhiveNodeIf;
import com.xhive.query.interfaces.XhiveXQueryQueryIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;
import com.xhive.util.interfaces.IterableIterator;

@Component
public class XdbRepository
{

    @Value("${xdb.user}")
    protected String xDBUser;

    @Value("${xdb.password}")
    protected String xDBpassword;

    @Value("${xdb.database}")
    protected String xDBdatabase;

    @Value("${xdb.url}")
    protected String xDBUrl;

    protected XhiveDriverIf xhiveDriver;

    @PostConstruct
    public void postContruct()
    {
        xhiveDriver = XhiveDriverFactory.getDriver(xDBUrl);
    }
    
    public List<String> getMonographs(String folder)
    {
        
        String query = String.format(
                "for $i in collection(\"%s\") return $i",
                //"let $docu := collection(\"%s\") return <monograph id=\"{$docu/*/@dx-id}\" type=\"{$docu/*/name()}\" document-uri=\"{document-uri($docu)}\">{$docu}</monograph>",
                folder);
        
        List<String> monographList = new LinkedList<>();

        if (!xhiveDriver.isInitialized())
        {
            xhiveDriver.init(1024);
        }
        XhiveSessionIf session = xhiveDriver.createSession();
        session.setReadOnlyMode(true);

        try
        {
            session.connect(xDBUser, xDBpassword, xDBdatabase);
            session.begin();

            XhiveLibraryIf rootLibrary = session.getDatabase().getRoot();

            XhiveXQueryQueryIf theParsedQuery = rootLibrary.createXQuery(query);

            IterableIterator<? extends XhiveXQueryValueIf> result = theParsedQuery.execute();
            
int i = 0;
            for (XhiveXQueryValueIf value : result)
            {
                XhiveNodeIf xml = value.asNode().getFirstChild();
                
                monographList.add(xml.toString());
                System.out.println("reading xml  [" + i++ + "]");
//                if (i > 2)
//                    break;
            }
        }
        catch (Exception e)
        {
            session.rollback();
            System.out.println(e.getMessage());
        }
        finally
        {
            if (session.isOpen())
            {
                session.rollback();
            }
            session.disconnect();
            session.terminate();
        }

        return monographList;
    }
}
