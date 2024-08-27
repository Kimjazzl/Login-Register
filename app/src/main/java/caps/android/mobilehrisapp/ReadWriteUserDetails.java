package caps.android.mobilehrisapp;

public class ReadWriteUserDetails {
    public String fullName, doB, gender, mobile;

    public ReadWriteUserDetails(String textFullName,String textDoB, String textGender, String textMobile){
        this.fullName = textFullName;
        this.doB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
