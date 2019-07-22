package com.example.hk_pc.gmf.Map;

import com.example.hk_pc.gmf.Model.Results;
import com.example.hk_pc.gmf.Remote.IGoogleAPIService;
import com.example.hk_pc.gmf.Remote.RetrofitClient;
import com.example.hk_pc.gmf.Remote.RetrofitScalarsClient;

public class Common {

    public static Results currentResult;
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService( ){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }

    public static IGoogleAPIService getGoogleAPIServiceScalars( ){
        return RetrofitScalarsClient.getScalarClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }

}
