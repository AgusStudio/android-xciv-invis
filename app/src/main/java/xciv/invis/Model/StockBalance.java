package xciv.invis.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Betha on 8/22/2018.
 */

public class StockBalance {
    @SerializedName("acc_id")
    @Expose
    private String accId;
    @SerializedName("stock_id")
    @Expose
    private String stockId;
    @SerializedName("stock_code")
    @Expose
    private String stockCode;
    @SerializedName("stock_name")
    @Expose
    private String stockName;
    @SerializedName("balance")
    @Expose
    private double balance;
    @SerializedName("unit_id")
    @Expose
    private String unitId;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("unit_size")
    @Expose
    private double unitSize;
    @SerializedName("status")
    @Expose
    private String status;

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public double getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(double unitSize) {
        this.unitSize = unitSize;
    }

    public String getStatus() { return status;}
}
