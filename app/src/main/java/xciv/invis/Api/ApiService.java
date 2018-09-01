package xciv.invis.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import xciv.invis.Model.InvAcc;
import xciv.invis.Model.Login;
import xciv.invis.Model.StockBalance;
import xciv.invis.Model.StockOpname;
import xciv.invis.Model.User;

/**
 * Created by Betha on 8/22/2018.
 */

public interface ApiService {
    @FormUrlEncoded
    @POST("user/login")
    Call<Login> login(@Field("id") String username, @Field("password") String password);

    @GET("user/profile")
    Call<User> getProfile(@Header("Authorization") String authHeader);

    @GET("inventory/balance/acid/{accId}/sid/{sId}")
    Call<StockBalance> getBalance(@Path("accId") String accId, @Path("sId") String sId, @Header("Authorization") String authHeader);

    @FormUrlEncoded
    @POST("stock/opname")
    Call<StockOpname> opname(@Field("acc_id") String accId, @Field("sid") String sId, @Field("quantity") double qtyOpname, @Header("Authorization") String authHeader);

    @GET("inventory/account/mode/storage")
    Call<List<InvAcc>> getInvAcc(@Header("Authorization") String authHeader);
}
