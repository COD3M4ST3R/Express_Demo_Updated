package orderprojectexpress.prototype.Express.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import orderprojectexpress.prototype.Express.Activity.OrderReceivedActivity;
import orderprojectexpress.prototype.Express.Class.GlobalVariables;
import orderprojectexpress.prototype.Express.Class.Item;
import com.prototype.Express.R;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import static android.content.Context.MODE_PRIVATE;


public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.MyViewHolder>
{
    // VARIABLES
    Context context;
    ArrayList<Item> mData;
    Button button_approve;
    double price_total;
    int item_quantity;

    // VIEWHOLDERS
    MyViewHolder myViewHolder;

    public BasketAdapter(Context context, ArrayList<Item> mData, Button button_approve)
    {
        this.context = context;
        this.mData = mData;
        this.button_approve = button_approve;
        button_approve.setVisibility(View.INVISIBLE);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.basket_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return  myViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position)
    {
        holder.name.setText(mData.get(holder.getAdapterPosition()).getName());
        Picasso.get().load(mData.get(holder.getAdapterPosition()).getPhoto()).into(holder.image);
        holder.description.setText(mData.get(holder.getAdapterPosition()).getDescription());
        holder.price.setText(String.valueOf(mData.get(holder.getAdapterPosition()).getPrice()) + "₺");
        holder.quantity.setText(String.valueOf(mData.get(holder.getAdapterPosition()).getQuantity()));
        item_quantity = mData.get(holder.getAdapterPosition()).getQuantity();

        price_total = price_total + (mData.get(holder.getAdapterPosition()).getPrice() * mData.get(holder.getAdapterPosition()).getQuantity());

        if(price_total != 0)
        {
            button_approve.setVisibility(View.VISIBLE);
            button_approve.setText("SİPARİŞİ ONAYLA: " + price_total + "₺");

            button_approve.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // EMIT DATA TO SOCKET SERVER
                    sendOrder();
                }
            });
        }
    }


    @Override
    public int getItemCount()
    {
        return mData.size();
    }




    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        // VARIABLES OF ITEM
        TextView name;
        TextView price;
        ConstraintLayout descriptionLayout;
        ImageView image;
        TextView description, quantity;
        ImageView remove;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            // CASTING VARIABLES OF ITEM
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            descriptionLayout = itemView.findViewById(R.id.descriptionLayout);
            image = itemView.findViewById(R.id.image);
            description = itemView.findViewById(R.id.description);
            quantity = itemView.findViewById(R.id.quantity);
            remove = itemView.findViewById(R.id.remove);
        }
    }





    // INTENTs
    public void open_OrderReceived()
    {
        Intent intent = new Intent(context, OrderReceivedActivity.class);
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    // FUNCTIONs
    private void sendOrder()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_token", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        try {
            IO.Options mOptions = new IO.Options();
            mOptions.query = "auth_token=" + token;
            Socket msocket = IO.socket("http://104.248.207.133:5000", mOptions);
            msocket.connect();

            int numberOfItems = GlobalVariables.getInstance().encounters.size();

            for(int i = 0; i < numberOfItems; i++)
            {
                String name = GlobalVariables.getInstance().encounters.get(i).getName();
                String id =  GlobalVariables.getInstance().encounters.get(i).get_id();

                JSONObject order = new JSONObject();
                try{
                    // Fill Order
                    order.put("name", name);
                    order.put("menuItem", id);

                    // Send Order
                    msocket.emit("phone-send", order);

                }catch (JSONException e){
                    System.out.print(e);
                }
            }


        } catch (URISyntaxException e) {
            System.out.print(e);
        }


        // WAIT FOR ASYNC VOLLEY TO FINISH
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            public void run()
            {
                open_OrderReceived();
            }
        }, 2000);   // 2 second
    }
}
