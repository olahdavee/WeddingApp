package hu.mobilalkfejl.weddingapp.recyclerview;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import hu.mobilalkfejl.weddingapp.R;
import hu.mobilalkfejl.weddingapp.activity.WeddingsActivity;
import hu.mobilalkfejl.weddingapp.model.Wedding;

public class WeddingElementAdapter extends RecyclerView.Adapter<WeddingElementAdapter.ViewHolder> {
    private final ArrayList<Wedding> weddings;
    private final Context context;
    private int lastPosition = -1;

    public WeddingElementAdapter(Context context, ArrayList<Wedding> weddings) {
        this.weddings = weddings;
        this.context = context;
    }

    @Override
    public WeddingElementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.wedding, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onBindViewHolder(WeddingElementAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bindTo(weddings.get(position));

        if (viewHolder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.pop);
            viewHolder.itemView.startAnimation(animation);
            lastPosition = viewHolder.getAdapterPosition();
        }

        viewHolder.cardView.setOnLongClickListener(view -> {
            viewHolder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.zoom));
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return weddings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView location;
        private final ImageView coverImage;
        private final FirebaseUser user;
        public CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.wedding_name);
            location = itemView.findViewById(R.id.wedding_location);
            coverImage = itemView.findViewById(R.id.wedding_image);

            user = FirebaseAuth.getInstance().getCurrentUser();

            cardView = itemView.findViewById(R.id.card);
        }

        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        void bindTo(Wedding wedding) {
            name.setText(wedding.getName());
            location.setText(wedding.getLocation());

            Glide.with(context).load(wedding.getImageResource()).into(coverImage);

            itemView.findViewById(R.id.reservation).setOnClickListener(view -> ((WeddingsActivity)context).reservation(wedding));
            itemView.findViewById(R.id.take_picture).setOnClickListener(view -> ((WeddingsActivity)context).takePicture());
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((WeddingsActivity)context).delete(wedding));

            itemView.findViewById(R.id.reservation).setVisibility(View.GONE);
            itemView.findViewById(R.id.take_picture).setVisibility(View.GONE);
            itemView.findViewById(R.id.delete).setVisibility(View.GONE);
            itemView.findViewById(R.id.anonymus_text).setVisibility(View.GONE);

            if ("admin@admin.com".equals(user.getEmail()))
                itemView.findViewById(R.id.delete).setVisibility(View.VISIBLE);

            if (user.isAnonymous())
                itemView.findViewById(R.id.anonymus_text).setVisibility(View.VISIBLE);

            if (!user.isAnonymous() && !"admin@admin.com".equals(user.getEmail())) {
                itemView.findViewById(R.id.reservation).setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.take_picture).setVisibility(View.VISIBLE);
            }

            if (!wedding.isAvailable())
                itemView.findViewById(R.id.reservation).setVisibility(View.GONE);
        }
    }
}
