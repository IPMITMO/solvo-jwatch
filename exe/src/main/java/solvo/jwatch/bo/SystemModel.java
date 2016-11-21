/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 11, 2015
 */

package solvo.jwatch.bo;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author user_akrikheli
 */
public class SystemModel
{    
    /**
     * Spool directory
     */
    private File spoolDirectory;

    /**
     * Processes (contains halting processing too)
     */
    private final Map<String, ProcessContext> processes = Collections.synchronizedMap(
                    new LinkedHashMap<String, ProcessContext>());

    /**
     * Map for processes that was shut down with fail
     */
    private final Map<String, ProcessContext> haltingProcesses = Collections.synchronizedMap(
                    new LinkedHashMap<String, ProcessContext>());

    /**
     * Map for incidental processes
     */
    private final Map<String, ProcessContext> incidentalProcesses = Collections.synchronizedMap(
                    new LinkedHashMap<String, ProcessContext>());

    /**
     * System state
     */
    private volatile SystemState systemState = SystemState.STOPPED;

    /**
     * Project environment
     */
    private volatile String environment;

    /**
     * System constructor     
     */
    public SystemModel()
    {
        
    }

    
    
    /**
     * Returns current project environment name
     * @return project environment name
     */
    public String getEnvironment()
    {
        return environment;
    }

    /**
     * Sets project environment name
     * @param environment project environment name
     */
    public void setEnvironment(String environment)
    {
        this.environment = environment;
    }

    /**
     * Returns system state
     * @return system state
     */
    public SystemState getSystemState()
    {
        return systemState;
    }

    /**
     * Sets system state
     * @param state system state
     */
    public void setSystemState(SystemState state)
    {
        this.systemState = state;
    }

    /**
     * Return spool directory as File object
     * @return spool directory
     */
    public File getSpoolDirectory()
    {
        return spoolDirectory;
    }

    /**
     * Sets spool directory
     * @param spoolDirectory spool directory 
     */
    public void setSpoolDirectory(File spoolDirectory) 
    {
        this.spoolDirectory = spoolDirectory;
    }
        

    /**
     * Returns processes map
     * @return processes map
     */
    public Map<String, ProcessContext> getProcesses()
    {
        return processes;
    }

    /**
     * Returns processes that was shut down with fail map
     * @return processes map
     */
    public Map<String, ProcessContext> getHaltingProcesses()
    {
        return haltingProcesses;
    }

    /**
     * Returns incidental processes map
     * @return processes map
     */
    public Map<String, ProcessContext> getIncidentalProcesses()
    {
        return incidentalProcesses;
    }

    /**
     * Sets running status.
     */
    public void setRunning()
    {
        systemState = SystemState.RUNNING;
    }

    /**
     * Sets starting status.
     */
    public void setStarting()
    {
        systemState = SystemState.STARTING;
    }

    /**
     * Sets stopped status.
     */
    public void setStopped()
    {
        systemState = SystemState.STOPPED;
    }

    /**
     * Sets stopping status.
     */
    public void setStopping()
    {
        systemState = SystemState.STOPPING;
    }

    /**
     * Sets reloading status.
     */
    public void setReloading()
    {
        systemState = SystemState.RELOADING;
    }

    /**
     * Sets halting status.
     */
    public void setHalting()
    {
        systemState = SystemState.HALTING;
    }

    /**
     * Returns true if system state is <i>Stopping</i>,
     * otherwise returns false.
     * @return is stopping
     */
    public boolean isStopping()
    {
        return systemState == SystemState.STOPPING;
    }

    /**
     * Returns true if system state is <i>Stopped</i>,
     * otherwise returns false.
     * @return is stopped
     */
    public boolean isStopped()
    {
        return systemState == SystemState.STOPPED;
    }

    /**
     * Returns true if system state is <i>Starting</i>,
     * otherwise returns false.
     * @return is starting
     */
    public boolean isStarting()
    {
        return systemState == SystemState.STARTING;
    }

    /**
     * Returns true if system state is <i>Running</i>,
     * otherwise returns false.
     * @return is running
     */
    public boolean isRunning()
    {
        return systemState == SystemState.RUNNING;
    }

    /**
     * Returns true if system state is <i>Reloading</i>,
     * otherwise returns false.
     * @return is reloading
     */
    public boolean isReloading()
    {
        return systemState == SystemState.RELOADING;
    }

    /**
     * Returns true if system state is <i>Halting</i>,
     * otherwise returns false.
     * @return is halting
     */
    public boolean isHalting()
    {
        return systemState == SystemState.HALTING;
    }
}
