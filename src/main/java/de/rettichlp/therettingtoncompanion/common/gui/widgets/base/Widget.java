package de.rettichlp.therettingtoncompanion.common.gui.widgets.base;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@IndexAnnotated
public @interface Widget {

    String registryName();

    double defaultX() default 0.0;

    double defaultY() default 0.0;

    boolean defaultEnabled() default true;
}
