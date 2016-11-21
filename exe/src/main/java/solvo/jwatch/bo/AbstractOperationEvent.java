/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 14, 2015
 */

package solvo.jwatch.bo;

/**
 *
 * @author user_akrikheli
 */
public abstract class AbstractOperationEvent extends AbstractEvent
{
    /**
     * Result of operation execution. Used as "callback object".
     */
    protected OperationResult operationResult;    
        
    protected AbstractOperationEvent()
    {
        super();        
    }

    /**
     * Returns operation result
     * @return
     */
    public synchronized OperationResult getOperationResult()
    {
        return operationResult;
    }

    /**
     * Sets operation result
     * @param operationResult operation result
     */
    public synchronized void setOperationResult(OperationResult operationResult)
    {
        this.operationResult = operationResult;
    }
}
