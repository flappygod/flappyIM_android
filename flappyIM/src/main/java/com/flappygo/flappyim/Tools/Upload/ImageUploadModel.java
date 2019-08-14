package com.flappygo.flappyim.Tools.Upload;

public class ImageUploadModel {
    /**
     * id : 973b1267f867425c9d26420c476bddfd
     * size : 209374
     * msg : ä¸ä¼ æå
     * savePath : C:\\publish\\ysaas.web\\attached\\suggestion\false\11499d1c105f9e0d708cbd3f28b50762.jpg
     * title : wx_camera_1543053987719.jpg
     * date : 2018-12-04 17:28:06
     * saveUrl : http://chohelp.yuranos.com:8090/attached/suggestion/false/11499d1c105f9e0d708cbd3f28b50762.jpg
     */


    private String localUrl;

    private String id;
    private int size;
    private String msg;
    private String savePath;
    private String title;
    private String date;
    private String saveUrl;
    /**
     * ocr : {"state":true,"code":"","message":"","message_detail":"","timestamp":"","data":"TWCU2016224","total":0}
     */

    private OcrBean ocr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSaveUrl() {
        return saveUrl;
    }

    public void setSaveUrl(String saveUrl) {
        this.saveUrl = saveUrl;
    }

    public OcrBean getOcr() {
        return ocr;
    }

    public void setOcr(OcrBean ocr) {
        this.ocr = ocr;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public static class OcrBean {
        /**
         * state : true
         * code :
         * message :
         * message_detail :
         * timestamp :
         * data : TWCU2016224
         * total : 0
         */

        private boolean state;
        private String code;
        private String message;
        private String message_detail;
        private String timestamp;
        private String data;
        private int total;

        public boolean isState() {
            return state;
        }

        public void setState(boolean state) {
            this.state = state;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage_detail() {
            return message_detail;
        }

        public void setMessage_detail(String message_detail) {
            this.message_detail = message_detail;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
