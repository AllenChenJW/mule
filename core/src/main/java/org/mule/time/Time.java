package org.mule.time;

import java.util.concurrent.TimeUnit;

public interface Time
{

    long getTime();

    TimeUnit getUnit();
}
