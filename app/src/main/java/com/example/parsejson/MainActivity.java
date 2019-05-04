package com.example.parsejson;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewResult;
    private RequestQueue mQueue;
    private TouchImageView map;

    public List<Integer> idx = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.whitemap1);
        final String ip = "10.50.212.69";//hiện ko sử dụng
        final int port = 80;//hiện ko sử dụng

        //final List<Integer> idx = new ArrayList<>();
        mTextViewResult = findViewById(R.id.text_view_result);
        Button buttonParse = findViewById(R.id.button_parse);
        mQueue = Volley.newRequestQueue(this);
        map = findViewById(R.id.imageView);

        buttonParse.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                //có bấm nút thì lấy json
                jsonParse(idx, src, ip, port);

                //in ra box text tren mTextViewResult LUC NÀY thi thay idx KO co cap nhat
                //mTextViewResult.append("idx, " + idx.size() +  "\n\n");

                //Sau khi lấy json, tui mong muốn là biến idx ấy có 1 chuỗi số nguyên
                //để bỏ vào hàm dưới xử lý pixels
                Bitmap b = replaceColor(src, idx, Color.BLACK);
                map.setImageBitmap(b);
            }
        });
    }
    //hàm này hiện ko dùng
    private static Uri buildURI(String ipAddress, int port) {
        Uri videoUri = Uri.parse("server.php");
        Uri.Builder builder = new Uri.Builder();
        return builder
                .scheme("http")
                .encodedAuthority(ipAddress + ":" + port)
                .encodedPath(videoUri.getEncodedPath())
                .build();
    }

    public void jsonParse(final List<Integer> idx, final Bitmap src, String ip, int port) {

        String url = "https://api.myjson.com/bins/13ozng";

        //link chua object 1 phan tu, key: "data", value: 1 array voi phan tu dau la toa do robot.
        //Moi phan tu khac (index > 0) trong array la 1 object toa do vat can.
        //Mục tiêu thay doi cac pixels co toa do vat can thanh Color.BLACK tren file bitmap whitemap1.

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                int x = data.getInt("x");
                                int y = data.getInt("y");
                                //chuyen tu mang 2 chieu thanh 1 chieu de sau nay xu ly pixel
                                idx.add(x + y * src.getWidth());

                            }

                            //in ra box text tren mTextViewResult LUC NÀY thi thay idx co cap nhat
                            mTextViewResult.append(" idx, " + idx.size() +  "\n\n");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    public Bitmap replaceColor(Bitmap src, List<Integer> idx, int replaceColor) {
        if(src == null) {
            return null;
        }
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int x = 0; x < pixels.length; ++x) {    //quét toàn bộ mảng 1 chiều
            for (int y = 0; y < idx.size(); ++y) {  //pixel thứ x nào = bất kì giá trị trong idx thì chuyển pixel đó thành Color.BLACK
                pixels[x] = (x == idx.get(y)) ? replaceColor : pixels[x];
            }
        }

        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }



}
