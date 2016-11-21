/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 16, 2015
 */

package solvo.jwatch.web.html;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import solvo.jwatch.SystemManager;
import solvo.jwatch.bo.ProcessContext;
import solvo.jwatch.bo.ProcessInfo;
import solvo.jwatch.bo.ProcessState;
import solvo.jwatch.bo.SystemModel;
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.config.WatchProcessDescription;
import solvo.jwatch.SystemManagerInstance;
import solvo.jwatch.bo.ProcessSession;
import solvo.jwatch.web.html.pojo.ProcessHTML;
import solvo.jwatch.web.html.pojo.SystemHTML;

/**
 *
 * @author user_akrikheli
 */
public class JwatchPageHTMLModel
{
    private final SystemState   systemState;
    private final String        environment;
    private final Date          lastUpdate;

    private final List<ProcessHTMLHolder> prHolderList = new ArrayList<>();
    private SystemHTMLHolder systemHolder;

    public JwatchPageHTMLModel(String errorMessage)
    {
        SystemManager systemManager = SystemManagerInstance.get();
        SystemModel system = systemManager.getSystem();

        systemState = system.getSystemState();
        environment = system.getEnvironment();

        initHtmlProcesses(system);
        initHtmlSystem(errorMessage);

        lastUpdate = new Date();
    }

    public StringBuffer getProcessesHTML()
    {
        StringBuffer sb = new StringBuffer();
        for (ProcessHTMLHolder phm : prHolderList)
        {
            sb.append( phm.getHTML() );
        }
        return sb;
    }

    public SystemHTMLHolder getSystemHTMLHolder()
    {
        return systemHolder;
    }

    public String getLastUpdate()
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        return dateFormat.format( lastUpdate );
    }

    private void initHtmlProcesses(SystemModel system)
    {
        Map<String, ProcessContext> incidentalProcesses = system.getIncidentalProcesses();
        Map<String, ProcessContext> processes = system.getProcesses();

        initHtmlProcessesBySyncMap(incidentalProcesses);
        initHtmlProcessesBySyncMap(processes);
    }

    private void initHtmlProcessesBySyncMap(Map<String, ProcessContext> processes)
    {
        synchronized (processes)
        {
            Iterator<ProcessContext> it = processes.values().iterator();
            while (it.hasNext())
            {
                ProcessContext pc   = it.next();
                addProcess(pc);
            }
        }
    }

    private void initHtmlSystem(String errorMessage)
    {
        SystemHTML sh = new SystemHTML();

        sh.setErrorMessage(errorMessage);
        sh.setSystemState(systemState);
        sh.setEnvironment(environment);

        /**
         * Wrap in holder
         */
        systemHolder = SystemHTMLHolder.createByPojo(sh);
    }

    private void addProcess(ProcessContext processContext)
    {
        ProcessInfo pi = processContext.getProcessInfo();
        WatchProcessDescription wpd = processContext.getProcessDescription();
        ProcessState processState = pi.getProcessState();
        ProcessSession session = processContext.getController().getCurrentSession();

        ProcessHTML ph = new ProcessHTML();

        //Configuration properties
        ph.setProcessName(wpd.getProcessName());
        ph.setMaxStartRetries(wpd.getStartRetries());
        ph.setEnabled(wpd.isEnabled());
        ph.setPingable(wpd.isPingable());
        ph.setMaxAlarms(wpd.getPingAlarms());

        //Runtime properties
        ph.setProcessState(processState);
        ph.setSystemState(systemState);
        ph.setDead(pi.isDead());
        ph.setStartRetries(pi.getRetriesNum());
        ph.setProcessSession(session);
        ph.setIncidental(processContext.isIncidental());

        /**
         * Wrap in holder
         */
        ProcessHTMLHolder phHolder = ProcessHTMLHolder.createByPojo(ph);

        prHolderList.add(phHolder);
    }
}
