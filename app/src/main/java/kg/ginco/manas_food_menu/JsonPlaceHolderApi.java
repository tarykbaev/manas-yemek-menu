package kg.ginco.manas_food_menu;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi {

    @GET("food/{date}")
    Call<List<String>> getFoodList(@Path("date") String date);

    @GET("date")
    Call<List<String>> getDateList();
}
