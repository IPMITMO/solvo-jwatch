/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 17, 2015
 */

package solvo.jwatch.executors;

import solvo.gadgets.Lib;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.bo.ProcessContext;
import solvo.jwatch.bo.ProcessOperationEvent;
import solvo.jwatch.exceptions.InaccessibleOperationException;

/**
 *
 * @author user_akrikheli
 */
public abstract class AbstractProcessExecutor extends AbstractExecutor
{
    protected AbstractProcessExecutor()
    {
        super();
    }

    public abstract void validateProcessState(ProcessContext pc)
                            throws InaccessibleOperationException;

    protected abstract OperationResult perform(ProcessContext pc);

    @Override
    public void execute(AbstractOperationEvent poEvent)
    {
        ProcessContext processContext;
        String processName = ((ProcessOperationEvent)poEvent).getProcessName();

        /**
        * Validation section
        */
        try
        {
            validateSystemState();
            processContext = getProcessContext( processName );
            validateProcessEnabled(processContext);
            validateProcessState(processContext);
        }
        catch (InaccessibleOperationException ex)
        {
            Lib.log.error("Process operation '" + getOperationName() +
                    "' is not available now", ex);
            poEvent.setOperationResult( OperationResult.createInaccessible() );
            return;
        }

        beginOperation();
        result = perform(processContext);
        finishOperation(poEvent, result);
    }

    protected ProcessContext getProcessContext(String processName)
                                        throws InaccessibleOperationException
    {
        ProcessContext processContext = systemManager.getProcessByName( processName );

        if (processContext == null)
        {
            throw new InaccessibleOperationException("Can not find process '" +
                    processName + "'");
        }
        else
        {
            return processContext;
        }
    }

    protected void validateProcessEnabled(ProcessContext processContext)
                                                        throws InaccessibleOperationException
    {
        String processName = processContext.getProcessDescription().getProcessName();
        if ( !processContext.isProcessEnabled() )
        {
            throw new InaccessibleOperationException("Process '" + processName + "' is disabled");
        }
    }
}
