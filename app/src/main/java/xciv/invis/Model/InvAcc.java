package xciv.invis.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Betha on 8/22/2018.
 */

public class InvAcc {
    @SerializedName("acc_id")
    @Expose
    private String accId;
    @SerializedName("acc_code")
    @Expose
    private String accCode;
    @SerializedName("acc_name")
    @Expose
    private String accName;
    @SerializedName("status")
    @Expose
    private String status;

    public InvAcc(){

    }

    public InvAcc(String accId, String accCode, String accName){
        this.accId = accId;
        this.accCode = accCode;
        this.accName = accName;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getAccCode() {
        return accCode;
    }

    public void setAccCode(String accCode) {
        this.accCode = accCode;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getStatus() {
        return status;
    }
}
