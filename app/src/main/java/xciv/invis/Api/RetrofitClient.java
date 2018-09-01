package xciv.invis.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Betha on 8/22/2018.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static Retrofit getClient(String URL_HEAD){
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(URL_HEAD)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static void Reset()
    {
        retrofit=null;
    }
    public static ApiService getApiService(String apiURL){
        return getClient(apiURL).create(ApiService.class);
    }
}
