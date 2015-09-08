package bit.weilytw1.sdhb_client;

/**
 * Created by weilytw1 on 4/09/2015.
 */
public class Contact
{
    private String contactName;
    private String contactNumber;

    public Contact(String contactName, String contactNumber)
    {
        this.setContactName(contactName);
        this.setContactNumber(contactNumber);
    }

    public String getContactName(){
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
