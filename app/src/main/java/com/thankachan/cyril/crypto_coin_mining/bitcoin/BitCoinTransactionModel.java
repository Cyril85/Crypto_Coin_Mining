package milanroxe.inc.snocoins.bitcoin;

/**
 * Created by milan sharma on 21-06-2017.
 */

public class BitCoinTransactionModel {
    private String date;
    private String type_of_deposit;
    private String image_link;
    private String reference_no;
    private String amount;
    private String added_to_bal;
    private String key;

    public String getRsbalance() {
        return rsbalance;
    }

    public void setRsbalance(String rsbalance) {
        this.rsbalance = rsbalance;
    }

    private String rsbalance;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAdded_to_bal() {
        return added_to_bal;
    }

    public void setAdded_to_bal(String added_to_bal) {
        this.added_to_bal = added_to_bal;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getReference_no() {
        return reference_no;
    }

    public void setReference_no(String reference_no) {
        this.reference_no = reference_no;
    }



    public String getType_of_deposit() {
        return type_of_deposit;
    }

    public void setType_of_deposit(String type_of_deposit) {
        this.type_of_deposit = type_of_deposit;
    }



    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public BitCoinTransactionModel(String date, String image_link, String reference_no, String amount)
    {
        this.amount=amount;
        this.image_link=image_link;
        this.date=date;
        this.reference_no=reference_no;

    }
    public BitCoinTransactionModel()
    {

    }

    public String toString() {
        return "Date:- " + date + " image_link=" + image_link + " amount:- " + amount;
    }


}
