/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 3, 2015
 */

package solvo.jwatch.bo;

/**
 * Immutable object to describe process information.
 *
 * @author user_akrikheli
 */
public class ProcessInfo
{
    private ProcessState processState = ProcessState.INACTIVE;
    private int retriesNum = 0;
    private boolean isDead = false;

    private ProcessInfo()
    {

    }

    public ProcessState getProcessState()
    {
        return processState;
    }

    public int getRetriesNum()
    {
        return retriesNum;
    }

    public boolean isDead()
    {
        return isDead;
    }

    public static Builder newBuilder()
    {
        return new ProcessInfo().new Builder();
    }

    /**
     * Builder for this entity
     */
    public class Builder
    {
        private Builder()
        {

        }

        public Builder setProcessState(ProcessState state)
        {
            ProcessInfo.this.processState = state;
            return this;
        }

        public Builder setRetriesNum(int retriesNum)
        {
            ProcessInfo.this.retriesNum = retriesNum;
            return this;
        }

        public Builder setDead(boolean isDead)
        {
            ProcessInfo.this.isDead = isDead;
            return this;
        }

        public ProcessInfo build()
        {
            return ProcessInfo.this;
        }
    }

}
