package kg.ginco.manas_food_menu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static kg.ginco.manas_food_menu.R.color.colorPrimary2;
import static kg.ginco.manas_food_menu.R.color.white;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView1, imageView2, imageView3, imageView4;
    private String url1, url2, url3, url4;
    private LinearLayout linearLayout;
    private TextView textView1, textView1k, textView2, textView2k, textView3, textView3k,
            textView4, textView4k, textAbove;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private Animation animation;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = findViewById(R.id.menu_image_view1);
        imageView2 = findViewById(R.id.menu_image_view2);
        imageView3 = findViewById(R.id.menu_image_view3);
        imageView4 = findViewById(R.id.menu_image_view4);
        linearLayout = findViewById(R.id.hor_layout);
        textView1 = findViewById(R.id.food_text_view1);
        textView2 = findViewById(R.id.food_text_view2);
        textView3 = findViewById(R.id.food_text_view3);
        textView4 = findViewById(R.id.food_text_view4);
        textView1k = findViewById(R.id.food_text_view1_k);
        textView2k = findViewById(R.id.food_text_view2_k);
        textView3k = findViewById(R.id.food_text_view3_k);
        textView4k = findViewById(R.id.food_text_view4_k);
        textAbove = findViewById(R.id.text_above);
        Button refBtn = findViewById(R.id.refresh_btn);
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://manas-menu-api.herokuapp.com/")
                //.baseUrl("http://192.168.43.199:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Main();

        refBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void Main() {
        if (haveNetwork()) {
            sifirlama();
            startAnimation();
            getFoodList("today");
            getDateList();
        } else {
            noInternet();
        }
    }

    private boolean haveNetwork() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }

    private void getFoodList(String today) {

        Call<List<String>> call1 = jsonPlaceHolderApi.getFoodList(today);

        call1.enqueue(new Callback<List<String>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> list = response.body();

                    if (list.get(0).equals("noFood")) {
                        textAbove.setText(R.string.no_food);
                        notFound();
                    } else {
                        textView1.setText(list.get(1));
                        textView1k.setText(list.get(2)+" kalori");

                        textView2.setText(list.get(3));
                        textView2k.setText(list.get(4)+" kalori");

                        textView3.setText(list.get(5));
                        textView3k.setText(list.get(6)+" kalori");

                        textView4.setText(list.get(7));
                        textView4k.setText(list.get(8)+" kalori");

                        textAbove.setText(list.get(0));
                        FireBaseConnection(list.get(1), list.get(3), list.get(5), list.get(7));
                    }
                } else {
                    notFound();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                notFound();
            }
        });

    }

    private void getDateList() {
        Call<List<String>> call2 = jsonPlaceHolderApi.getDateList();

        call2.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    createButton(response.body());
                } else {
                    notFound();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                notFound();
            }
        });
    }


    private void FireBaseConnection(final String menufood1, final String menufood2, final String menufood3,
                                    final String menufood4) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("Menu");

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            Resources res = getResources();

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    url1 = Objects.requireNonNull(dataSnapshot.child(menufood1).getValue()).toString();
                    imageView1.clearAnimation();
                    Picasso.get().load(url1).fit().into(imageView1);
                } catch (Exception e) {
                    imageView1.clearAnimation();
                    imageView1.setImageDrawable(res.getDrawable(R.drawable.notfound));
                }
                try {
                    url2 = Objects.requireNonNull(dataSnapshot.child(menufood2).getValue()).toString();
                    imageView2.clearAnimation();
                    Picasso.get().load(url2).fit().into(imageView2);
                } catch (Exception e) {
                    imageView2.clearAnimation();
                    imageView2.setImageDrawable(res.getDrawable(R.drawable.notfound));
                }
                try {
                    url3 = Objects.requireNonNull(dataSnapshot.child(menufood3).getValue()).toString();
                    imageView3.clearAnimation();
                    Picasso.get().load(url3).fit().into(imageView3);
                } catch (Exception e) {
                    imageView3.clearAnimation();
                    imageView3.setImageDrawable(res.getDrawable(R.drawable.notfound));
                }
                try {
                    url4 = Objects.requireNonNull(dataSnapshot.child(menufood4).getValue()).toString();
                    imageView4.clearAnimation();
                    Picasso.get().load(url4).fit().into(imageView4);
                } catch (Exception e) {
                    imageView4.clearAnimation();
                    imageView4.setImageDrawable(res.getDrawable(R.drawable.notfound));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                notFound();
            }
        });

    }

    private void notFound() {
        stopAnimation();
        Resources res = getResources();
        imageView1.setImageDrawable(res.getDrawable(R.drawable.notfound));
        imageView2.setImageDrawable(res.getDrawable(R.drawable.notfound));
        imageView3.setImageDrawable(res.getDrawable(R.drawable.notfound));
        imageView4.setImageDrawable(res.getDrawable(R.drawable.notfound));
        sifirlama();
    }

    private void stopAnimation() {
        imageView1.clearAnimation();
        imageView2.clearAnimation();
        imageView3.clearAnimation();
        imageView4.clearAnimation();
    }

    private void startAnimation() {
        Resources res = getResources();
        imageView1.setImageDrawable(res.getDrawable(R.drawable.loading));
        imageView2.setImageDrawable(res.getDrawable(R.drawable.loading));
        imageView3.setImageDrawable(res.getDrawable(R.drawable.loading));
        imageView4.setImageDrawable(res.getDrawable(R.drawable.loading));
        imageView1.startAnimation(animation);
        imageView2.startAnimation(animation);
        imageView3.startAnimation(animation);
        imageView4.startAnimation(animation);
    }

    private void noInternet() {
        stopAnimation();
        Resources res = getResources();
        imageView1.setImageDrawable(res.getDrawable(R.drawable.nointernet));
        imageView2.setImageDrawable(res.getDrawable(R.drawable.nointernet));
        imageView3.setImageDrawable(res.getDrawable(R.drawable.nointernet));
        imageView4.setImageDrawable(res.getDrawable(R.drawable.nointernet));
        sifirlama();
    }

    private void sifirlama() {
        textView1.setText("");
        textView1k.setText("");
        textView2.setText("");
        textView2k.setText("");
        textView3.setText("");
        textView3k.setText("");
        textView4.setText("");
        textView4k.setText("");
    }

    @SuppressLint("ResourceAsColor")
    private void createButton(List<String> dates) {
        linearLayout.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(4, 4, 4, 4);

        for (String date : dates) {
            Button btn = new Button(this);
            btn.setBackgroundColor(getResources().getColor(colorPrimary2));
            btn.setTextColor(getResources().getColor(white));
            btn.setLayoutParams(params);
            btn.setText(date);
            btn.setTag(date);
            btn.setOnClickListener(this);
            linearLayout.addView(btn);
        }
    }

    @Override
    public void onClick(View v) {
        if (haveNetwork()) {
            startAnimation();
            getFoodList((String) v.getTag());
            textAbove.setText((String) v.getTag());
        } else {
            noInternet();
        }
    }
}
