package com.fekrah.halan.models;

public class Tool {

    private String tool_name;
    private String tool_key;

    public Tool() {
    }

    public Tool(String tool_name, String tool_key) {
        this.tool_name = tool_name;
        this.tool_key = tool_key;
    }

    public String getTool_name() {
        return tool_name;
    }

    public void setTool_name(String tool_name) {
        this.tool_name = tool_name;
    }

    public String getTool_key() {
        return tool_key;
    }

    public void setTool_key(String tool_key) {
        this.tool_key = tool_key;
    }
}
