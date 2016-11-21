/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 11, 2015
 */

package solvo.jwatch.executors;

import solvo.gadgets.Lib;
import solvo.jwatch.bo.AbstractOperationEvent;
import solvo.jwatch.bo.OperationResult;
import solvo.jwatch.exceptions.InaccessibleOperationException;
import solvo.jwatch.exceptions.JwatchConfigurationException;

/**
 *
 * @author user_akrikheli
 */
public abstract class AbstractSystemExecutor extends AbstractExecutor
{
    protected AbstractSystemExecutor()
    {
        super();
    }

    @Override
    public void execute(AbstractOperationEvent event)
    {
        try
        {
            validateSystemState();
        }
        catch (InaccessibleOperationException ex)
        {
            Lib.log.error("System operation '" + getOperationName()
                    + "' is not available now", ex);
            event.setOperationResult( OperationResult.createInaccessible() );
            return;
        }

        beginOperation();
        try
        {
            result = perform(event);
        }
        catch (JwatchConfigurationException ex)
        {
            Lib.log.error("System operation '" + getOperationName() + "' failed", ex);
            result = OperationResult.createFailing(ex, originalState);
        }
        finishOperation(event, result);
    }

    protected abstract OperationResult perform(AbstractOperationEvent se)
                            throws JwatchConfigurationException;

}
