package com.example.hk_pc.gmf.Information;

public class Information {
    private String gn;
    private String bn;

    public Information(String gn, String bn){
        this.setGn(gn);
        this.setBn(bn);
    }

    public String getGn( ) {
        return gn;
    }

    public void setGn(String gn) {
        this.gn = gn;
    }

    public String getBn( ) {
        return bn;
    }

    public void setBn(String bn) {
        this.bn = bn;
    }
}
