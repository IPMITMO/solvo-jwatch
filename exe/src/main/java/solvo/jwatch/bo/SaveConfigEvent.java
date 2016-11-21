/*
 * Copyright 2015 by Solvo, LTD
 *
 * This program is commercial software.
 * Any unauthorized use is prohibited.
 *
 * Created Sep 7, 2015
 */

package solvo.jwatch.bo;

import solvo.jwatch.IConfigBuilder;

/**
 *
 * @author user_akrikheli
 */
public class SaveConfigEvent extends SystemOperationEvent
{
    protected SaveConfigEvent()
    {
        super();
        setEventName(SAVE_SYSTEM);
    }

    private IConfigBuilder builder;

    public IConfigBuilder getConfigBuilder()
    {
        return builder;
    }

    public void setBuilder(IConfigBuilder builder)
    {
        this.builder = builder;
    }

    public static SaveConfigEvent createEvent(IConfigBuilder builder)
    {
        SaveConfigEvent sce = new SaveConfigEvent();
        sce.setBuilder(builder);
        return sce;
    }
}
