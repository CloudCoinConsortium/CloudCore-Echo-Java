package com.cloudcore.echo.server;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Command {


    /* JSON Fields */

    @Expose
    @SerializedName("command")
    public String command;

    public String filename;
}
