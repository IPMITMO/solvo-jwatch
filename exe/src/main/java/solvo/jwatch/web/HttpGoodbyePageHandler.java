/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 14, 2015
 */

package solvo.jwatch.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

/**
 *
 * @author user_akrikheli
 */
public class HttpGoodbyePageHandler extends org.glassfish.grizzly.http.server.HttpHandler
{
    @Override
    public void service(Request request, Response response) throws Exception
    {
        response.addHeader("Content-Type",  "text/html; charset=UTF-8");
        response.addHeader("Cache-Control", "no-cache");

        Writer writer = response.getWriter();
        new TemplateProcessor().write(writer);
    }


    private static class TemplateProcessor
    {
        public void write(Writer writer) throws Exception
        {
            InputStream is = TemplateProcessor.class.getClassLoader().getResourceAsStream("goodbye.html");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is)))
            {
                String line = br.readLine();
                while (line != null)
                {
                    writer.write( line );
                    line = br.readLine();
                }
                writer.flush();
            }
        }
    }
}
