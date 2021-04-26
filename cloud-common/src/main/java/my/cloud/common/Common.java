package my.cloud.common;

public enum Common {
    LS("ls "),
    DOWNLOAD("download "),
    UPLOAD("upload "),
    LOCAL_DIR("."),
    CLOUD_DIR(System.getenv("LOCALAPPDATA") + "//CloudProject"),
    FILES_LIST(LOCAL_DIR + "//Files//filesList.txt");

    private String value;

    Common(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}