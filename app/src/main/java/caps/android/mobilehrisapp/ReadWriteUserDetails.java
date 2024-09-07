package caps.android.mobilehrisapp;

public class ReadWriteUserDetails {
    public String doB;
    public String mobile;
    public String gender;

    //constructor
    public ReadWriteUserDetails(){

    }

    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile){

        this.doB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
