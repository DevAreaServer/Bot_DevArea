package devarea.backend.controllers.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebRoleCount {

    @JsonIgnore
    protected String roleId;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("countMember")
    protected int countMember;
    @JsonProperty("color")
    protected String color;

    public WebRoleCount() {
    }

    public WebRoleCount(final int countMember, final String roleId, final String name) {
        this();
        this.roleId = roleId;
        this.countMember = countMember;
        this.name = name;
    }

    public WebRoleCount(final String roleId) {
        this.roleId = roleId;
    }

    @JsonIgnore
    public int getCountMember() {
        return countMember;
    }

    @JsonIgnore
    public String getRoleId() {
        return roleId;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @JsonIgnore
    public void setCountMember(int countMember) {
        this.countMember = countMember;
    }

    @JsonIgnore
    public void setColor(String color) {
        this.color = color;
    }

    @JsonIgnore
    public String getColor() {
        return color;
    }
}
