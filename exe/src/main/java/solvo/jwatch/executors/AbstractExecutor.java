/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 17, 2015
 */

package solvo.jwatch.executors;

import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.SystemManager;
import solvo.jwatch.SystemManagerInstance;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.bo.SystemState;
import solvo.jwatch.exceptions.InaccessibleOperationException;

/**
 *
 * @author user_akrikheli
 */
public abstract class AbstractExecutor
{
    protected final SystemManager   systemManager;
    protected SystemState           originalState;
    protected OperationResult       result;

    protected AbstractExecutor()
    {
        systemManager = SystemManagerInstance.get();
    }

    /**
     * Validates system state on the ability of performing this operation.
     *
     * @throws InaccessibleOperationException if this operation is
     * inaccessible with the current system state
     */
    public void validateSystemState() throws InaccessibleOperationException
    {
        if ( systemManager.getSystem().isHalting() )
        {
            throw new InaccessibleOperationException("System is halting!");
        }
    }

    protected void beginOperation()
    {
        originalState = systemManager.getSystem().getSystemState();
        systemManager.assignExecutor(this);

        /**
         * Set intermediate state if it's possible
         */
        SystemState intermediateState = getIntermediateState();
        if (intermediateState != null)
        {
            systemManager.getSystem().setSystemState( intermediateState );
        }
    }

    protected void finishOperation(AbstractOperationEvent event, OperationResult result)
    {
        event.setOperationResult(result);
        systemManager.registerSystemChanges(event);
        systemManager.unassignExecutor();
    }

    public abstract void execute(AbstractOperationEvent se);
    public abstract String getOperationName();
    public abstract AbstractExecutor newInstance();
    public abstract SystemState getIntermediateState();
}
