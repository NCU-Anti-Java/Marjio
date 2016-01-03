package io.github.antijava.marjio.common.input;

import java.lang.annotation.*;

/**
 * Created by freyr on 2016/1/3.
 */


@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NetWorkData {
    Class[] value();
}
