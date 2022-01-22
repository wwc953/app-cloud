package com.example.apporder.util;

public enum FileType {
    /**
     * JEPG.
     */
    JPEG("FFD8FF"),

    /**
     * PNG.
     */
    PNG("89504E47"),

    /**
     * GIF.
     */
    GIF("47494638"),

    /**
     * Adobe Photoshop.
     */
    PSD("38425053"),

    /**
     * Rich Text Format.
     */
    RTF("7B5C727466"),

    /**
     * XML.
     */
    XML("3C3F786D6C"),

    /**
     * HTML.
     */
    HTML("68746D6C3E"),

    /**
     * CSS.
     */
    CSS("48544D4C207B0D0A0942"),

    /**
     * JS.
     */
    JS("696B2E71623D696B2E71"),

    /**
     * MS Word/Excel.
     */
    XLS("D0CF11E0"),

    XLSX("504B030414000600080000002100"),

    /**
     * WPS文字wps、表格et、演示dps都是一样的
     */
    WPS("d0cf11e0a1b11ae10000"),

    /**
     * torrent
     */
    TORRENT("6431303A637265617465"),

    /**
     * WordPerfect.
     */
    WPD("FF575043"),


    /**
     * Adobe Acrobat.
     */
    PDF("255044462D312E"),


    /**
     * ZIP Archive.
     */

    ZIP("504B0304"),

    /**
     * RAR Archive.
     */

    RAR("52617221"),

    /**
     * JAR Archive.
     */

    JAR("504B03040A000000"),

    /**
     * MF Archive.
     */
    MF("4D616E69666573742D56"),

    /**
     * CHM Archive.
     */

    CHM("49545346030000006000"),

    /*

     * INI("235468697320636F6E66"), SQL("494E5345525420494E54"), BAT(

     * "406563686F206f66660D"), GZ("1F8B0800000000000000"), PROPERTIES(

     * "6C6F67346A2E726F6F74"), MXP(

     * "04000000010000001300"),

     */

    /**
     * Wave.
     */
    WAV("57415645"),

    /**
     * AVI.
     */

    AVI("41564920"),

    /**
     * Real Audio.
     */
    RAM("2E7261FD"),

    /**
     * Real Media.
     */
    RM("2E524D46"),

    /**
     * MPEG (mpg).
     */
    MPG("000001BA"),

    /**
     * Quicktime.
     */
    MOV("6D6F6F76"),

    /**
     * MP4.
     */

    MP4("00000020667479706d70"),

    /**
     * MP3.
     */

    MP3("49443303000000002176"),

    /**
     * FLV.
     */

    FLV("464C5601050000000900");

    private String value = "";

    /**
     * Constructor.
     *
     * @param
     */
    private FileType(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
