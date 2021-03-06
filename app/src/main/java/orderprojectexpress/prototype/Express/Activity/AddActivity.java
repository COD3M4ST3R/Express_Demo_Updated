package orderprojectexpress.prototype.Express.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import orderprojectexpress.prototype.Express.Class.GlobalVariables;
import orderprojectexpress.prototype.Express.Class.Item;
import com.prototype.Express.R;
import com.squareup.picasso.Picasso;

public class AddActivity extends AppCompatActivity
{
    // XML VARIABLES
    TextView name, price, number, description;
    ImageView photo, more, less;
    Button button_approve;

    // VARIABLES
    String sname, sdescription, sphoto, sid;
    double sprice;
    int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // CASTING XML VARIABLES
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        photo = findViewById(R.id.photo);
        number = findViewById(R.id.number);
        more = findViewById(R.id.more);
        less = findViewById(R.id.less);
        description = findViewById(R.id.description);
        button_approve = findViewById(R.id.button_approve);

        // BUTTON START STATE
        button_approve.setVisibility(View.INVISIBLE);


        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            sphoto = bundle.getString("photo");
            sname = bundle.getString("name");
            sdescription = bundle.getString("description");
            sprice = bundle.getDouble("price");
            sid = bundle.getString("id");
        }

        Picasso.get().load(sphoto).into(photo);
        name.setText(sname);
        description.setText(sdescription);
        price.setText(String.valueOf(sprice));

        more.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v)
            {
                counter = counter + 1;
                number.setText(String.valueOf(counter));
                button_approve.setVisibility(View.VISIBLE);
            }
        });

        less.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(counter != 0)
                {
                    counter = counter - 1;
                    number.setText(String.valueOf(counter));
                }

                if(counter == 0)
                {
                    button_approve.setVisibility(View.INVISIBLE);
                }
            }
        });

        button_approve.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                open_MenuActivity(sname, sdescription, sprice, counter, sphoto, sid);
            }
        });
    }

    public void open_MenuActivity(String sname, String sdescription, double sprice, int counter, String sphoto, String sid)
    {
        Item item = new Item();
        item.setName(sname);
        item.setDescription(sdescription);
        item.setPrice(sprice);
        item.setQuantity(counter);
        item.setPhoto(sphoto);
        item.set_id(sid);

        GlobalVariables.getInstance().encounters.add(item);

        this.finish();
    }
}
