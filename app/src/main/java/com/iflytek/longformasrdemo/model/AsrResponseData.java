package com.iflytek.longformasrdemo.model;

import java.util.Arrays;

//{"action":"started","code":"0","data":"","desc":"success","sid":"rta08404398@dx2f5f1a45c970000100"}
public class AsrResponseData {

    private int code;
    private String desc;
    private String sid;

    private String action;
    private String data;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return this.desc;
    }

    public String getSid() {
        return sid;
    }

    public String getData() {
        return data;
    }


    // Welcome7.java

    public static class Data {
        private CN cn;
        private long segID;

        public CN getCN() {
            return cn;
        }

        public void setCN(CN value) {
            this.cn = value;
        }

        public long getSegID() {
            return segID;
        }

        public void setSegID(long value) {
            this.segID = value;
        }
// CN.java

        public static class CN {
            private St st;

            public St getSt() {
                return st;
            }

            public void setSt(St value) {
                this.st = value;
            }

            @Override
            public String toString() {
                return "CN{" +
                        "st=" + st +
                        '}';
            }
        }

// St.java


        public static class St {
            private String bg;
            private String ed;
            private Rt[] rt;
            private String type;

            public String getBg() {
                return bg;
            }

            public void setBg(String value) {
                this.bg = value;
            }

            public String getEd() {
                return ed;
            }

            public void setEd(String value) {
                this.ed = value;
            }

            public Rt[] getRt() {
                return rt;
            }

            public void setRt(Rt[] value) {
                this.rt = value;
            }

            public String getType() {
                return type;
            }

            public void setType(String value) {
                this.type = value;
            }

            @Override
            public String toString() {
                return "St{" +
                        "bg='" + bg + '\'' +
                        ", ed='" + ed + '\'' +
                        ", rt=" + Arrays.toString(rt) +
                        ", type='" + type + '\'' +
                        '}';
            }
        }

// Rt.java


        public static class Rt {
            private W[] ws;

            public W[] getWs() {
                return ws;
            }

            public void setWs(W[] value) {
                this.ws = value;
            }

            @Override
            public String toString() {
                return "Rt{" +
                        "ws=" + Arrays.toString(ws) +
                        '}';
            }
        }

// W.java

        public static class W {
            private Cw[] cw;
            private long wb;
            private long we;

            public Cw[] getCw() {
                return cw;
            }

            public void setCw(Cw[] value) {
                this.cw = value;
            }

            public long getWb() {
                return wb;
            }

            public void setWb(long value) {
                this.wb = value;
            }

            public long getWe() {
                return we;
            }

            public void setWe(long value) {
                this.we = value;
            }

            @Override
            public String toString() {
                return "W{" +
                        "cw=" + Arrays.toString(cw) +
                        ", wb=" + wb +
                        ", we=" + we +
                        '}';
            }
        }

// Cw.java


        public static class Cw {
            private String w;
            private String wp;

            public String getW() {
                return w;
            }

            public void setW(String value) {
                this.w = value;
            }

            public String getWp() {
                return wp;
            }

            public void setWp(String value) {
                this.wp = value;
            }

            @Override
            public String toString() {
                return "Cw{" +
                        "w='" + w + '\'' +
                        ", wp='" + wp + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Data{" +
                    "cn=" + cn +
                    ", segID=" + segID +
                    '}';
        }
    }
}