/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 10, 2015
 */

package solvo.jwatch.bo;

/**
 * This class describes an operation result.
 * @author user_akrikheli
 */
public class OperationResult
{
    public static enum ResultEnum
    {
        INACCESSIBLE
        {
            @Override
            public String toString()
            {
                return "Inaccessible";
            }
        },
        FAILING
        {
            @Override
            public String toString()
            {
                return "Failing";
            }
        },
        ABORTED
        {
            @Override
            public String toString()
            {
                return "Aborted";
            }
        },
        COMPLETED
        {
            @Override
            public String toString()
            {
                return "Completed";
            }
        }
    }

    private Exception   ex;
    private ResultEnum  result;
    private SystemState newSystemState;

    /**
     * Default constructor
     */
    protected OperationResult()
    {

    }

    public Exception getEx()
    {
        return ex;
    }

    public ResultEnum getResult()
    {
        return result;
    }

    public SystemState getNewSystemState()
    {
        return newSystemState;
    }

    public static OperationResult createCompleted(SystemState newState)
    {
        OperationResult or = new OperationResult();
        or.result = ResultEnum.COMPLETED;
        or.newSystemState = newState;

        return or;
    }

    public static OperationResult createAborted(SystemState newState)
    {
        OperationResult or = new OperationResult();
        or.result = ResultEnum.ABORTED;
        or.newSystemState = newState;

        return or;
    }

    public static OperationResult createFailing(Exception ex, SystemState newState)
    {
        OperationResult or = new OperationResult();
        or.result = ResultEnum.FAILING;
        or.newSystemState = newState;

        or.ex = ex;

        return or;
    }

    public static OperationResult createInaccessible()
    {
        OperationResult or = new OperationResult();
        or.result = ResultEnum.INACCESSIBLE;

        return or;
    }
}
