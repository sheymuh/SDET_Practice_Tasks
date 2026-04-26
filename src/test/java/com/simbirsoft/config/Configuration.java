package com.simbirsoft.config;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:configurations/config.properties")
public interface Configuration extends Config {

    @Key("base.url")
    String baseUrl();

    @Key("base.path")
    @DefaultValue("")
    String basePath();
}
