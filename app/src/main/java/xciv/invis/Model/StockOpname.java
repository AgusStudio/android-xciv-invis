package xciv.invis.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Betha on 8/22/2018.
 */

public class StockOpname {
    @SerializedName("acc_id")
    @Expose
    private String accId;
    @SerializedName("sid")
    @Expose
    private String sId;
    @SerializedName("quantity")
    @Expose
    private double quantity;
    @SerializedName("status")
    @Expose
    private String status;

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getSId() { return sId;}

    public void setSId(String sId) {
        this.sId = sId;
    }

    public double getQuantity() { return quantity;}

    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getStatus() { return status;}

    public void setStatus(String status) { this.status = status; }

}
