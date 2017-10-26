package milanroxe.inc.snocoins.withdraw;

/**
 * Created by milan sharma on 11-06-2017.
 */

public class TransactionModel {
    private int debited_coins;
    private String date;
    private String imagelink;




    public TransactionModel(int debited_coins,String date,String  imagelink)
    {
        this.debited_coins=debited_coins;
        this.date = date;
        this.imagelink = imagelink;
    }

    public TransactionModel()
    {

    }

    public void setDebited_coins(int debited_coins)
    {
     this.debited_coins=debited_coins;
    }

    public void setDate(String date)
    {
        this.date=date;
    }
    public void setImagelink(String imagelink)
    {
        this.imagelink=imagelink;
    }
    public int getSno_coins()
    {
        return debited_coins;
    }

    public String getDate()
    {
        return date;
    }

    public String getImagelink(){ return imagelink;};

    public String toString()
    {
        return debited_coins+"  --"+date+"  ---"+imagelink;
    }
}
