/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Nov 19, 2015
 */

package solvo.jwatch.executors;


import java.util.Objects;
import solvo.jwatch.config.WatchProcessDescription;

/**
 *
 * @author user_akrikheli
 */
public class RestartResolver
{
    private WatchProcessDescription currentProcessDescription;
    private WatchProcessDescription newProcessDescription;

    public RestartResolver(WatchProcessDescription currentProcessDescription,
                            WatchProcessDescription newProcessDescription)
    {
        if (currentProcessDescription == null || newProcessDescription == null)
        {
            throw new NullPointerException();
        }

        this.currentProcessDescription  = currentProcessDescription;
        this.newProcessDescription      = newProcessDescription;
    }

    public boolean isStartCmdParameterUpdated()
    {
        return !Objects.equals( newProcessDescription.getStartCmd(),
                                    currentProcessDescription.getStartCmd() );
    }

    public boolean isEnabledParameterUpdated()
    {
        return currentProcessDescription.isEnabled() != newProcessDescription.isEnabled();
    }

    public boolean isCoreDirParameterUpdated()
    {
        return !Objects.equals( newProcessDescription.getCoreDir(),
                                    currentProcessDescription.getCoreDir() );
    }

    public boolean resolveRestart()
    {
        if (isStartCmdParameterUpdated())
        {
            return true;
        }

        if (isEnabledParameterUpdated())
        {
            return true;
        }

        if (isCoreDirParameterUpdated())
        {
            return true;
        }

        return false;
    }
}
