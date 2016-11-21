/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Oct 20, 2015
 */

package solvo.jwatch.executors;

/**
 *
 * @author user_akrikheli
 */
public class ShutdownSystemForciblyExecutor extends ShutdownSystemExecutor
{
    @Override
    public String getOperationName()
    {
        return "Shutdown system forcibly";
    }

    @Override
    public void validateSystemState()
    {
        //Cancel all validations
    }

     @Override
    public AbstractExecutor newInstance()
    {
        return new ShutdownSystemForciblyExecutor();
    }
}
