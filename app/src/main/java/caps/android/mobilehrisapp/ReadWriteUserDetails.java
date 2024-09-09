package caps.android.mobilehrisapp;

public class ReadWriteUserDetails {
    public String doB, gender, mobile, registerDate;

    //Constructor
    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile, String textRegisterDate){
        this.doB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
        this.registerDate = textRegisterDate;
    }

    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile){
        this.doB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
