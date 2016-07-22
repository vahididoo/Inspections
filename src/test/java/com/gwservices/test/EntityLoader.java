package com.gwservices.test;

import com.guidewire.commons.metadata.schema.dm2.EntityNodeBuilder;
import org.junit.Test;

/**
 * Created by vmansoori on 7/17/2016.
 */
public class EntityLoader {

    @Test
    public void testLoadEntity() throws Exception {
        EntityNodeBuilder builder = new EntityNodeBuilder();
        builder.withEntity("PolicyPeriod").create();

    }
}
