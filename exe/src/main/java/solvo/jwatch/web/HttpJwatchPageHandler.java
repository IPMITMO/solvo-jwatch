/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Aug 27, 2015
 */

package solvo.jwatch.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import solvo.jwatch.web.html.JwatchPageHTMLModel;
import solvo.jwatch.web.html.SystemHTMLHolder;

/**
 *
 * @author user_akrikheli
 */
public class HttpJwatchPageHandler extends org.glassfish.grizzly.http.server.HttpHandler
{
    @Override
    public void service(Request request, Response response) throws Exception
    {
        response.addHeader("Content-Type",  "text/html; charset=UTF-8");
        response.addHeader("Cache-Control", "no-cache");

        String errorInfo = getErrorInfo(request);
        Writer writer = response.getWriter();
        new TemplateProcessor().write(writer, errorInfo);
    }

    private String getErrorInfo(Request request)
    {
        return request.getParameter(RestServer.ERROR_PARAMETER);
    }


    private static class TemplateProcessor
    {
        private final static Pattern TOKEN_PATTERN = Pattern.compile("\\{\\$[A-Z_]+\\}");

        /**
         * Tokens
         */
        public final static String PROCESSES_LIST   = "{$PROCESSES_LIST}";
        public final static String SYSTEM_STATE     = "{$SYSTEM_STATE}";
        public final static String START_SYSTEM_CMD = "{$START_SYSTEM_CMD}";
        public final static String STOP_SYSTEM_CMD  = "{$STOP_SYSTEM_CMD}";
        public final static String SAVE_SYSTEM_CMD  = "{$SAVE_SYSTEM_CMD}";
        public final static String RELOAD_SYSTEM_CMD    = "{$RELOAD_SYSTEM_CMD}";
        public final static String ERROR_INFO           = "{$ERROR_INFO}";
        public final static String LAST_UPDATE          = "{$LAST_UPDATE}";
        public final static String ENVIRONMENT_NAME     = "{$ENVIRONMENT_NAME}";
        public final static String SHUT_DOWN_SYSTEM_CMD = "{$SHUT_DOWN_SYSTEM_CMD}";
        public final static String JS_SHUTDOWN_LISTENER = "{$JS_SHUTDOWN_LISTENER}";

        private JwatchPageHTMLModel jwatchPage;

        public void write(Writer writer, String errorInfo) throws Exception
        {
            if (StringUtils.isEmpty(errorInfo))
            {
                errorInfo = null;
            }
            this.jwatchPage    = new JwatchPageHTMLModel(errorInfo);

            InputStream is = TemplateProcessor.class.getClassLoader().getResourceAsStream("index.html");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is)))
            {
                String line = br.readLine();
                while (line != null)
                {
                    writer.write( processLine(line) );
                    line = br.readLine();
                }
            }
        }

        private String processLine(String line)
        {
            Matcher matcher = TOKEN_PATTERN.matcher(line);
            SystemHTMLHolder systemHtml = jwatchPage.getSystemHTMLHolder();

            while ( matcher.find() )
            {
                String token = matcher.group();

                switch (token)
                {
                    case PROCESSES_LIST:
                        line = jwatchPage.getProcessesHTML().toString();
                        break;
                    case SYSTEM_STATE:
                        String systemState = systemHtml.getSystemState();
                        line = line.replace(TemplateProcessor.SYSTEM_STATE, systemState);
                        break;
                    case START_SYSTEM_CMD:
                        String startCmdHtml = systemHtml.getStartCmd();
                        line = line.replace(TemplateProcessor.START_SYSTEM_CMD, startCmdHtml);
                        break;
                    case STOP_SYSTEM_CMD:
                        String stopCmdHtml = systemHtml.getStopCmd();
                        line = line.replace(TemplateProcessor.STOP_SYSTEM_CMD, stopCmdHtml);
                        break;
                    case RELOAD_SYSTEM_CMD:
                        String reloadCmdHtml = systemHtml.getReloadCmd();
                        line = line.replace(TemplateProcessor.RELOAD_SYSTEM_CMD, reloadCmdHtml);
                        break;
                    case SAVE_SYSTEM_CMD:
                        String saveCmdHtml  = systemHtml.getSaveBtn();
                        line = line.replace(TemplateProcessor.SAVE_SYSTEM_CMD, saveCmdHtml);
                        break;
                    case ERROR_INFO:
                        String errorMsg     = systemHtml.getErrorMessage();
                        line = line.replace(TemplateProcessor.ERROR_INFO, errorMsg);
                        break;
                    case LAST_UPDATE:
                        String lastUpdate = jwatchPage.getLastUpdate();
                        line = line.replace(TemplateProcessor.LAST_UPDATE, lastUpdate);
                        break;
                    case ENVIRONMENT_NAME:
                        String envName = systemHtml.getEnvironmentName();
                        line = line.replace(TemplateProcessor.ENVIRONMENT_NAME, envName);
                        break;
                    case SHUT_DOWN_SYSTEM_CMD:
                        String shutDownCmdHtml = systemHtml.getShutDownCmd();
                        line = line.replace(TemplateProcessor.SHUT_DOWN_SYSTEM_CMD, shutDownCmdHtml);
                        break;
                    case JS_SHUTDOWN_LISTENER:
                        line = line.replace(TemplateProcessor.JS_SHUTDOWN_LISTENER,
                                            systemHtml.getJsShutdownListener());
                        break;
                }
            }
            return line;
        }
    }
}

