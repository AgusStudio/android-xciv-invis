package xciv.invis.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Betha on 8/22/2018.
 */

public class User {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("last_login")
    @Expose
    private String lastLogin;
    @SerializedName("last_host")
    @Expose
    private String lastHost;
    @SerializedName("rid")
    @Expose
    private String rid;
    @SerializedName("role_name")
    @Expose
    private String roleName;
    @SerializedName("status")
    @Expose
    private String status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastHost() {
        return lastHost;
    }

    public void setLastHost(String lastHost) {
        this.lastHost = lastHost;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getStatus() { return status;}
}
