package com.agooday.baseapp.ad;


import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdRequest;

public class AdUtils {
    @NonNull
    public static AdRequest getDefaultAdRequest() {
        return new AdRequest.Builder()
                .addTestDevice("E52078E86DC54B185A3BBD3355041E5E")
                .addTestDevice("BA35A00DBC45CEC6C3EA8B10D9964C36")
                .addTestDevice("CB4955D674A50954B3D105B094BA74AA")
                .addTestDevice("8D52F5F808EB4E6F734A66D735968D65")
                .build();
    }
}
