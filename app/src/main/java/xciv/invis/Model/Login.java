package xciv.invis.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Betha on 8/22/2018.
 */

public class Login {
    private String id;
    private String password;
    public Login(String id, String password){
        this.id = id;
        this.password = password;
    }

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("status")
    @Expose
    private String status;

    public String getToken() {
        return token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
